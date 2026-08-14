const API_URL = 'http://localhost:8080';

async function loadMovieNightBanner() {
    try {
        const res = await fetch(`${API_URL}/api/settings/movie-night`);
        const data = await res.json();

        if (!data.date) return;

        // store date for next movie night and current date in variables
        const movieNight = new Date(data.date + 'T00:00:00');
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        // calculate the time diffence between current day and next movie night and display to user
        const diffTime = movieNight - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        let message = '';
        if (diffDays < 0)
            message = `Last movie night was ${Math.abs(diffDays)} days ago`;
        else if (diffDays == 0)
           message =  `🎬 Movie night is TODAY!`;
        else if (diffDays == 1)
            message = `🎬 Movie night is TOMORROW!`;
        else 
            message = `🎬 Next movie night: ${movieNight.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric' })} — ${diffDays} days away`;

        // create banner element to display movie night countdown
        const banner = document.createElement('div');
        banner.id = 'movie-night-banner';
        banner.textContent = message;
        banner.style.cssText = `
            background-color: var(--bg-card);
            border-bottom: 1px solid var(--border);
            padding: 0.6rem 2rem;
            text-align: center;
            font-size: 0.85rem;
            color: var(--accent);
            letter-spacing: 0.03em;
        `;

        const nav = document.querySelector('nav');
        nav.insertAdjacentElement('afterend', banner);
    }
    catch (error) {
        console.error('Could not load movie night date:', error);
    }
}