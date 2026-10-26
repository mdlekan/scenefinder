// Provided by the Thymeleaf page (inline script sets this before map.js loads)
declare const isAuthenticated: boolean;

type Season = 'Spring' | 'Summer' | 'Fall' | 'Winter' | 'Year-round';

// Mirrors LocationDTO on the server
interface Location {
    id: number;
    name: string;
    description: string;
    latitude: number;
    longitude: number;
    region: string;
    bestSeason: string | null;
    bestTimeOfDay: string | null;
    difficulty: string | null;
    elevationFt: number | null;
    permitRequired: boolean | string | null;
    parkingNotes: string | null;
    accessNotes: string | null;
    tags: string[] | null;
    todaySunrise: string | null;
    todayMorningGoldenHour: string | null;
    todayEveningGoldenHour: string | null;
    todaySolarNoon: string | null;
    todaySunset: string | null;
}

// Mirrors LocationNoteResponseDTO
interface LocationNote {
    note: string;
    username: string;
    sunriseDelayMinutes: number | null;
}

let map: L.Map;
let allLocations: Location[] = [];
let markers: L.Marker[] = [];
let currentLocationId: number | null = null;

const COLORADO_BOUNDS = L.latLngBounds([35.5, -111.0], [42.5, -100.0]);

const SEASON_COLORS: Record<Season, string> = {
    'Spring': '#27ae60',
    'Summer': '#2980b9',
    'Fall': '#e67e22',
    'Winter': '#8e44ad',
    'Year-round': '#2c3e50'
};

function byId<T extends HTMLElement = HTMLElement>(id: string): T {
    const el = document.getElementById(id);
    if (!el) throw new Error(`Missing element #${id}`);
    return el as T;
}

function escapeHtml(value: unknown): string {
    const map: Record<string, string> = {
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    };
    return String(value ?? '').replace(/[&<>"']/g, c => map[c]);
}

function timeRange(start: string | null, end: string | null): string {
    if (!start || !end) return '—';
    return `${escapeHtml(start)} – ${escapeHtml(end)}`;
}

// First season mentioned in free text like "Late Spring through Fall"
function primarySeason(text: string | null): Season | null {
    if (!text) return null;
    const lower = text.toLowerCase();
    let best: Season | null = null;
    let bestIndex = Infinity;
    for (const s of ['Spring', 'Summer', 'Fall', 'Winter'] as const) {
        const i = lower.indexOf(s.toLowerCase());
        if (i !== -1 && i < bestIndex) {
            best = s;
            bestIndex = i;
        }
    }
    return best ?? (lower.includes('year') ? 'Year-round' : null);
}

function addLegend(): void {
    const legend = new L.Control({ position: 'bottomleft' });
    legend.onAdd = () => {
        const div = L.DomUtil.create('div', 'map-legend');
        div.innerHTML = '<h4>First season listed</h4>' +
            (Object.entries(SEASON_COLORS) as [Season, string][]).map(([season, color]) =>
                `<div class="legend-row"><span class="legend-swatch" style="background:${color}"></span>${season}</div>`
            ).join('');
        return div;
    };
    legend.addTo(map);
}

function fitMinZoom(): void {
    map.setMinZoom(map.getBoundsZoom(COLORADO_BOUNDS));
}

function initMap(): void {
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

    fitMinZoom();
    map.on('resize', fitMinZoom);

    L.control.layers({ 'Street': street, 'Topographic': topo, 'Satellite': satellite }).addTo(map);
    L.control.scale({ metric: false, imperial: true, position: 'bottomleft' }).addTo(map);
    addLegend();

    void loadLocations();
}

function createMarkerIcon(season: Season | null): L.DivIcon {
    const color = season ? SEASON_COLORS[season] : '#2c3e50';
    return L.divIcon({
        className: '',
        html: `<div style="
            width: 14px; height: 14px;
            background: ${color};
            border: 2.5px solid white;
            border-radius: 50%;
            box-shadow: 0 1px 4px rgba(0,0,0,0.4)"></div>`,
        iconSize: [14, 14],
        iconAnchor: [7, 7]
    });
}

async function loadLocations(): Promise<void> {
    try {
        const res = await fetch('/api/locations');
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        allLocations = await res.json() as Location[];
        populateRegionFilter(allLocations);
        renderMarkers(allLocations);
    } catch (err) {
        console.error('Failed to load locations:', err);
    }
}

function populateRegionFilter(locations: Location[]): void {
    const regions = [...new Set(locations.map(l => l.region).filter(Boolean))].sort();
    const select = byId<HTMLSelectElement>('filter-region');
    for (const region of regions) {
        const option = document.createElement('option');
        option.value = region;
        option.textContent = region;
        select.appendChild(option);
    }
}

function renderMarkers(locations: Location[]): void {
    markers.forEach(m => map.removeLayer(m));
    markers = [];

    for (const loc of locations) {
        if (!loc.latitude || !loc.longitude) {
            console.warn('Skipping location with missing coords:', loc.name);
            continue;
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
    }

    byId('location-count').textContent = String(locations.length);
}

function applyFilters(): void {
    const season = byId<HTMLSelectElement>('filter-season').value;
    const time = byId<HTMLSelectElement>('filter-time').value;
    const difficulty = byId<HTMLSelectElement>('filter-difficulty').value;
    const region = byId<HTMLSelectElement>('filter-region').value;
    const tag = byId<HTMLInputElement>('filter-tag').value.toLowerCase().trim();

    const filtered = allLocations.filter(loc => {
        // Match on the first season mentioned, so "Fall (aspen color) and Summer" counts as Fall
        if (season && primarySeason(loc.bestSeason) !== season) return false;
        if (time && loc.bestTimeOfDay !== time) return false;
        if (difficulty && loc.difficulty !== difficulty) return false;
        if (region && loc.region !== region) return false;
        if (tag && !(loc.tags ?? []).some(t => t.toLowerCase().includes(tag))) return false;
        return true;
    });

    renderMarkers(filtered);
}

function resetFilters(): void {
    for (const id of ['filter-season', 'filter-time', 'filter-difficulty', 'filter-region', 'filter-tag']) {
        (byId(id) as HTMLInputElement | HTMLSelectElement).value = '';
    }
    renderMarkers(allLocations);
}

function metaRow(label: string, value: string): string {
    return `<div class="detail-meta-row">
        <span class="detail-meta-label">${label}</span>
        <span class="detail-meta-value">${value}</span>
    </div>`;
}

function showDetail(loc: Location): void {
    currentLocationId = loc.id;

    const tags = (loc.tags ?? []).map(t => `<span class="tag">${escapeHtml(t)}</span>`).join('');

    const permitRequired = loc.permitRequired === true || loc.permitRequired === 'true';
    const permit = permitRequired
        ? metaRow('Permit', '<span style="color:#e74c3c">Required</span>')
        : '';

    const noteAction = isAuthenticated
        ? `<a href="/locations/${loc.id}/add-note" class="btn-primary">Add a note about this location</a>`
        : `<p class="detail-login-prompt"><a href="/login">Log in</a> to add notes about this location.</p>`;

    const dash = (v: unknown): string => escapeHtml(v) || '—';

    byId('detail-content').innerHTML = `
        <div class="detail-name">${escapeHtml(loc.name)}</div>
        <div class="detail-region">${escapeHtml(loc.region)}</div>
        <div class="detail-description">${escapeHtml(loc.description)}</div>
        <div class="detail-meta">
            ${metaRow('Best season', dash(loc.bestSeason))}
            ${metaRow('Best time', dash(loc.bestTimeOfDay))}
            ${metaRow('Sunrise', dash(loc.todaySunrise))}
            ${metaRow('Morning golden hour', timeRange(loc.todaySunrise, loc.todayMorningGoldenHour))}
            ${metaRow('Evening golden hour', timeRange(loc.todayEveningGoldenHour, loc.todaySunset))}
            ${metaRow('Solar Noon', dash(loc.todaySolarNoon))}
            ${metaRow('Sunset', dash(loc.todaySunset))}
            ${metaRow('Difficulty', dash(loc.difficulty))}
            ${metaRow('Elevation', loc.elevationFt ? loc.elevationFt.toLocaleString() + ' ft' : '—')}
            ${permit}
            ${metaRow('Parking', dash(loc.parkingNotes))}
            ${metaRow('Access', dash(loc.accessNotes))}
        </div>
        <div class="detail-tags">${tags}</div>
        <div class="detail-actions">${noteAction}</div>
    `;

    byId('detail-panel').classList.add('open');
    void loadNotes(loc.id);
}

async function loadNotes(locationId: number): Promise<void> {
    try {
        const res = await fetch(`/api/locations/${locationId}/notes`);
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const notes = await res.json() as LocationNote[];

        // User clicked another marker while this request was in flight
        if (currentLocationId !== locationId) return;

        const notesHtml = notes.length
            ? notes.map(n => `
                <div class="community-note">
                    <p>${escapeHtml(n.note)}</p>
                    ${n.sunriseDelayMinutes ? `<span class="note-meta">Sunrise delay: ${n.sunriseDelayMinutes} min</span>` : ''}
                    <span class="note-author">— ${escapeHtml(n.username)}</span>
                </div>`).join('')
            : '<p class="no-notes">No community notes yet.</p>';

        byId('detail-content').insertAdjacentHTML('beforeend',
            `<div class="community-notes"><h3>Community notes</h3>${notesHtml}</div>`);
    } catch (err) {
        console.error('Failed to load notes:', err);
    }
}

function closeDetail(): void {
    currentLocationId = null;
    byId('detail-panel').classList.remove('open');
}

initMap();
