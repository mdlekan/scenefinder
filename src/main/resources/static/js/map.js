/* global L, isAuthenticated */

let map;
let allLocations = [];
let markers = [];
let layerControl;

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

// Marker colors by season (shared by the markers and the legend)
const SEASON_COLORS = {
    'Spring':     '#27ae60',
    'Summer':     '#2980b9',
    'Fall':       '#e67e22',
    'Winter':     '#8e44ad',
    'Year-round': '#2c3e50'
};

// First season mentioned in free text like "Late Spring through Fall"
function primarySeason(text) {
    if (!text) return null;
    const lower = text.toLowerCase();
    let best = null;
    let bestIndex = Infinity;
    for (const s of ['Spring', 'Summer', 'Fall', 'Winter']) {
        const i = lower.indexOf(s.toLowerCase());
        if (i !== -1 && i < bestIndex) {
            best = s;
            bestIndex = i;
        }
    }
    return best || (lower.includes('year') ? 'Year-round' : null);
}

// Legend explaining the marker colors
function addLegend() {
    const legend = L.control({ position: 'bottomleft' });
    legend.onAdd = () => {
        const div = L.DomUtil.create('div', 'map-legend');
        div.innerHTML = '<h4>First season listed</h4>' +
            Object.entries(SEASON_COLORS).map(([season, color]) =>
                `<div class="legend-row"><span class="legend-swatch" style="background:${color}"></span>${season}</div>`
            ).join('');
        return div;
    };
    legend.addTo(map);
}

// Initialize map centered on Colorado
function initMap() {
    const COLORADO_BOUNDS = L.latLngBounds([35.5, -111.0], [42.5, -100.0]);

    const street = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 18
    });
    const topo = L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
        attribution: 'Map data © OpenStreetMap contributors, SRTM | Style © OpenTopoMap (CC-BY-SA)',
        maxNativeZoom: 17,
        maxZoom: 18
    });
    const satellite = L.tileLayer(
        'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
            attribution: 'Tiles © Esri, Maxar, Earthstar Geographics, and the GIS User Community',
            maxZoom: 18
        });

    map = L.map('map', {
        layers: [street],
        maxBounds: COLORADO_BOUNDS,
        maxBoundsViscosity: 1.0
    }).setView([39.0, -105.5], 7);

    map.setMinZoom(map.getBoundsZoom(COLORADO_BOUNDS));

    layerControl = L.control.layers(
        { 'Street': street, 'Topographic': topo, 'Satellite': satellite }
    ).addTo(map);

    L.control.scale({ metric: false, imperial: true, position: 'bottomleft' }).addTo(map);
    addLegend();

    loadLocations();
}

// Custom marker icon
function createMarkerIcon(season) {
    const color = SEASON_COLORS[season] || '#2c3e50';

    return L.divIcon({
        className: '',
        html: `<div style="
            width: 14px; height: 14px;
            background: ${color};
            border: 2.5px solid white;
            border-radius: 50%;
            box-shadow: 0 1px 4px rgba(0,0,0,0.4)">
        </div>`,
        iconSize: [14, 14],
        iconAnchor: [7, 7]
    });
}

// Load all locations from the API
function loadLocations() {
    fetch('/api/locations')
        .then(res => {
            console.log('API status:', res.status);
            return res.json();
        })
        .then(data => {
            console.log('Locations loaded:', data.length);
            console.log('First location sample:', data[0]);
            allLocations = data;
            populateRegionFilter(data);
            renderMarkers(data);
        })
        .catch(err => console.error('Failed to load locations:', err));
}

// Populate the region dropdown dynamically from data
function populateRegionFilter(locations) {
    const regions = [...new Set(locations.map(l => l.region).filter(Boolean))].sort();
    const select = document.getElementById('filter-region');
    regions.forEach(region => {
        const option = document.createElement('option');
        option.value = region;
        option.textContent = region;
        select.appendChild(option);
    });
}

// Render markers for a given set of locations
function renderMarkers(locations) {
    markers.forEach(m => map.removeLayer(m));
    markers = [];

    locations.forEach(loc => {
        // Skip if coordinates are missing
        if (!loc.latitude || !loc.longitude) {
            console.warn('Skipping location with missing coords:', loc.name);
            return;
        }

        const marker = L.marker([loc.latitude, loc.longitude], {
            icon: createMarkerIcon(primarySeason(loc.bestSeason))
        });

        marker.bindTooltip(escapeHtml(loc.name), {
            permanent: false,
            direction: 'top',
            offset: [0, -8]
        });

        marker.on('click', () => showDetail(loc));
        marker.addTo(map);
        markers.push(marker);
    });

    document.getElementById('location-count').textContent = locations.length;
}

// Apply all active filters
function applyFilters() {
    const season     = document.getElementById('filter-season').value;
    const time       = document.getElementById('filter-time').value;
    const difficulty = document.getElementById('filter-difficulty').value;
    const region     = document.getElementById('filter-region').value;
    const tag        = document.getElementById('filter-tag').value.toLowerCase().trim();

    const filtered = allLocations.filter(loc => {
        if (season     && loc.bestSeason     !== season)     return false;
        if (time       && loc.bestTimeOfDay  !== time)       return false;
        if (difficulty && loc.difficulty     !== difficulty) return false;
        if (region     && loc.region         !== region)     return false;
        if (tag && !(loc.tags || []).some(t => t.toLowerCase().includes(tag))) return false;
        return true;
    });

    renderMarkers(filtered);
}

// Reset all filters
function resetFilters() {
    document.getElementById('filter-season').value     = '';
    document.getElementById('filter-time').value       = '';
    document.getElementById('filter-difficulty').value = '';
    document.getElementById('filter-region').value     = '';
    document.getElementById('filter-tag').value        = '';
    renderMarkers(allLocations);
}

// Show location detail panel
function showDetail(loc) {
    const panel = document.getElementById('detail-panel');
    const content = document.getElementById('detail-content');

    const tags = (loc.tags || []).map(t =>
        `<span class="tag">${escapeHtml(t)}</span>`).join('');

    const permit = loc.permitRequired
        ? `<div class="detail-meta-row">
               <span class="detail-meta-label">Permit</span>
               <span class="detail-meta-value" style="color:#e74c3c">Required</span>
           </div>`
        : '';

    const noteAction = isAuthenticated
        ? `<a href="/locations/${loc.id}/add-note" class="btn-primary">Add a note about this location</a>`
        : `<p class="detail-login-prompt"><a href="/login">Log in</a> to add notes about this location.</p>`;

    content.innerHTML = `
        <div class="detail-name">${escapeHtml(loc.name)}</div>
        <div class="detail-region">${escapeHtml(loc.region)}</div>
        <div class="detail-description">${escapeHtml(loc.description)}</div>
        <div class="detail-meta">
            <div class="detail-meta-row">
                <span class="detail-meta-label">Best season</span>
                <span class="detail-meta-value">${loc.bestSeason || '—'}</span>
            </div>
            <div class="detail-meta-row">
                <span class="detail-meta-label">Best time</span>
                <span class="detail-meta-value">${loc.bestTimeOfDay || '—'}</span>
            </div>
            <div class="detail-meta-row">
            	<span class="detail-meta-label">Sunrise</span>
            	<span class="detail-meta-value">${loc.sunriseTime || '—'}</span>
            </div>
            <div class="detail-meta-row">
            	<span class="detail-meta-label">Morning Golden Hour</span>
            	<span class="detail-meta-value">${loc.morningGoldenHour || '—'}</span>
            </div>
            <div class="detail-meta-row">
            	<span class="detail-meta-label">Evening Golden Hour</span>
            	<span class="detail-meta-value">${loc.eveningGoldenHour || '—'}</span>
            </div>
            <div class="detail-meta-row">
            	<span class="detail-meta-label">Solar Noon Time</span>
            	<span class="detail-meta-value">${loc.solarNoonTime || '—'}</span>
            </div>

            <div class="detail-meta-row">
                <span class="detail-meta-label">Difficulty</span>
                <span class="detail-meta-value">${loc.difficulty || '—'}</span>
            </div>
            <div class="detail-meta-row">
                <span class="detail-meta-label">Elevation</span>
                <span class="detail-meta-value">${loc.elevationFt ? loc.elevationFt.toLocaleString() + ' ft' : '—'}</span>
            </div>
            ${permit}
            <div class="detail-meta-row">
                <span class="detail-meta-label">Parking</span>
                <span class="detail-meta-value">${escapeHtml(loc.parkingNotes) || '—'}</span>
            </div>
            <div class="detail-meta-row">
                <span class="detail-meta-label">Access</span>
                <span class="detail-meta-value">${escapeHtml(loc.accessNotes) || '—'}</span>
            </div>
        </div>
        <div class="detail-tags">${tags}</div>
        <div class="detail-actions">${noteAction}</div>
    `;

    panel.classList.add('open');

    fetch(`/api/locations/${loc.id}/notes`)
        .then(res => res.json())
        .then(notes => {
            const notesHtml = notes.length
                ? notes.map(n => `
                    <div class="community-note">
                        <p>${escapeHtml(n.note)}</p>
                        ${n.sunriseDelayMinutes ? `<span class="note-meta">Sunrise delay: ${n.sunriseDelayMinutes} min</span>` : ''}
                        <span class="note-author">— ${escapeHtml(n.username)}</span>
                    </div>
                `).join('')
                : '<p class="no-notes">No community notes yet.</p>';

            document.getElementById('detail-content').insertAdjacentHTML('beforeend',
                `<div class="community-notes"><h3>Community notes</h3>${notesHtml}</div>`);
        })
        .catch(err => console.error('Failed to load notes:', err));
}

// Close detail panel
function closeDetail() {
    document.getElementById('detail-panel').classList.remove('open');
}

// Start everything
initMap();
