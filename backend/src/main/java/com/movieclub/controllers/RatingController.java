package com.movieclub.controllers;

import com.movieclub.models.Rating;
import com.movieclub.services.RatingService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class RatingController {
    
    private static final RatingService ratingService = new RatingService();

    public static void registerRoutes(Javalin app) {
        app.post("/api/ratings", RatingController::saveRating);
        app.delete("/api/ratings/{movieId}/{userId}", RatingController::deleteRating);
    }

    @SuppressWarnings("unchecked")
    private static void saveRating(Context ctx) {
        try {
            // take contents of request body and store in map, then extract values from request and store in variables
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            int movieId = (int) body.get("movieId");
            int userId = (int) body.get("userId");
            // we dont know if the rating will get saved as a double or int (could be 8.5 or 8), so store as a Number which covers both and then convert to double
            double rating = ((Number) body.get("rating")).doubleValue();

            // safety check to ensure valid rating
            if (rating < 0.0 || rating > 10.0) {
                ctx.status(400).json(Map.of("error", "Rating must be between 0 and 10"));
                return;
            }

            // save rating to database and report result of operation in response back to user
            Rating saved = ratingService.saveRating(movieId, userId, rating);

            if (saved == null) {
                ctx.status(500).json(Map.of("error", "Failed to save rating"));
                return;
            }

            ctx.status(200).json(saved);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    private static void deleteRating(Context ctx) {
        try {
            int movieId = Integer.parseInt(ctx.pathParam("movieId"));
            int userId = Integer.parseInt(ctx.pathParam("userId"));

            boolean deleted = ratingService.deleteRating(movieId, userId);

            if (deleted)
                ctx.status(200).json(Map.of("message", "Rating deleted successfully"));
            else
                ctx.status(400).json(Map.of("error", "Rating not found"));
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}