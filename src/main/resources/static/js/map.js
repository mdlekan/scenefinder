let map;
let allLocations = [];
let markers = [];

// Initialize map centered on Colorado
function initMap() {
    map = L.map('map').setView([39.0, -105.5], 7);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 18
    }).addTo(map);

    loadLocations();
}

// Custom marker icon
function createMarkerIcon(season) {
    const colors = {
        'Spring':     '#27ae60',
        'Summer':     '#2980b9',
        'Fall':       '#e67e22',
        'Winter':     '#8e44ad',
        'Year-round': '#2c3e50'
    };
    const color = colors[season] || '#2c3e50';

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
            icon: createMarkerIcon(loc.bestSeason)
        });

        marker.bindTooltip(loc.name, {
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
        `<span class="tag">${t}</span>`).join('');

    const permit = loc.permitRequired
        ? `<div class="detail-meta-row">
               <span class="detail-meta-label">Permit</span>
               <span class="detail-meta-value" style="color:#e74c3c">Required</span>
           </div>`
        : '';

    content.innerHTML = `
        <div class="detail-name">${loc.name}</div>
        <div class="detail-region">${loc.region || ''}</div>
        <div class="detail-description">${loc.description || ''}</div>
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
                <span class="detail-meta-value">${loc.parkingNotes || '—'}</span>
            </div>
            <div class="detail-meta-row">
                <span class="detail-meta-label">Access</span>
                <span class="detail-meta-value">${loc.accessNotes || '—'}</span>
            </div>
        </div>
        <div class="detail-tags">${tags}</div>
    `;

    panel.classList.add('open');
}

// Close detail panel
function closeDetail() {
    document.getElementById('detail-panel').classList.remove('open');
}

// Start everything
initMap();