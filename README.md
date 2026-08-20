# Movie Club App

A full-stack web application for a private movie club to track watched movies, submit ratings, view rankings, and manage monthly movie suggestions.

## Features

- **Watched Movies** — Browse all movies watched by the club with poster images, dates, and who chose each film
- **Ratings** — Members can submit and edit ratings from 0-10, with aggregate scores calculated across all members
- **Rankings** — Movies ranked by aggregate rating with individual member scores, plus a member leaderboard based on the average rating of their picks
- **Next Movie** — Current picker can search TMDB for movies and add up to three suggestions, members can vote on whether they have seen each suggestion, and the picker can mark a movie as watched to advance to the next person
- **Movie Night Banner** — Countdown to the next scheduled movie night displayed on every page
- **Change PIN** — Members can update their personal PIN at any time

## Tech Stack

**Frontend**
- HTML, CSS, JavaScript
- TMDB API for movie search and poster images
- Hosted on GitHub Pages

**Backend**
- Java with Javalin framework
- BCrypt for PIN hashing
- Hosted on Render

**Database**
- PostgreSQL via Supabase

## Running Locally

**Prerequisites**
- Java 21
- Maven
- A Supabase account with the database schema set up

**Backend**

1. Clone the repository
2. Create a `.env` file in the `backend/` folder:
```
DB_URL=your_supabase_jdbc_url
DB_USER=your_db_user
DB_PASSWORD=your_db_password
```
3. Run the server:
```bash
cd backend
mvn exec:java -Dexec.mainClass="com.movieclub.Main"
```

**Frontend**

1. Create `frontend/js/config.js` with your TMDB API key:
```javascript
const CONFIG = {
    TMDB_API_KEY: 'your_tmdb_api_key'
};
```
2. Update `API_URL` in `frontend/js/utils.js` to `http://localhost:8080`
3. Open `frontend/index.html` in your browser using Live Server

## Database Schema

Five tables — `users`, `movies`, `ratings`, `suggestions`, `seen_it`, and `settings`. See the Supabase SQL editor for the full schema.

## Author

Joey Jaikaran — Computer Programming & Analysis, Fanshawe College