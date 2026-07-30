package com.movieclub.controllers;

import com.movieclub.models.Movie;
import com.movieclub.models.Rating;
import com.movieclub.services.MovieService;
import com.movieclub.services.RatingService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MovieController {
    
    private static final MovieService movieService = new MovieService();
    private static final RatingService ratingService =  new RatingService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/movies", MovieController::getAllMovies);
        app.get("/api/movies/ranked", MovieController::getRankedMovies);
        app.post("/api/movies", MovieController::addMovie);
        app.delete("/api/movies/{id}", MovieController::deleteMovie);
        app.get("/api/movies/{id}/ratings", MovieController::getMovieRatings);
    }

    private static void getAllMovies(Context ctx) {
        try {
            List<Movie> movies = movieService.getAllMovies();
            ctx.status(200).json(movies);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    private static void getRankedMovies(Context ctx) {
        try {
            List<Movie> movies = movieService.getRankedMovies();
            ctx.status(200).json(movies);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void addMovie(Context ctx) {
        try {
            // map the request body to an object, and store individual attributes in variables
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            int tmdbId = (int) body.get("tmdbId");
            String title = (String) body.get("title");
            String posterUrl = (String) body.get("posterUrl");
            int year = (int) body.get("year");
            LocalDate dateWatched = LocalDate.parse((String) body.get("dateWatched"));
            int chosenBy = (int) body.get("chosenBy");

            // safety check
            if (title == null || dateWatched == null) {
                ctx.status(400).json(Map.of("error", "Title and date watched are required"));
                return;
            }

            // use data sent in request to create new movie object, report status of opertion back in response
            Movie movie = movieService.addMovie(tmdbId, title, posterUrl, year, dateWatched, chosenBy);

            if (movie == null) {
                ctx.status(500).json(Map.of("error", "Failed to add movie"));
                return;
            }

            // 201 used when a new resource is created
            ctx.status(201).json(movie);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    private static void deleteMovie(Context ctx) {
        try {
            int movieId = Integer.parseInt(ctx.pathParam("id"));
            boolean deleted = movieService.deleteMovie(movieId);

            if (deleted)
                ctx.status(200).json(Map.of("message", "Movie deleted successfully"));
            else
                ctx.status(404).json(Map.of("error", "Movie not found"));
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    private static void getMovieRatings(Context ctx) {
        try {
            int movieId = Integer.parseInt(ctx.pathParam("id"));
            List<Rating> ratings = ratingService.getRatingsForMovie(movieId);
            ctx.status(200).json(ratings);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}