package com.movieclub.controllers;

import com.movieclub.models.Movie;
import com.movieclub.models.SeenIt;
import com.movieclub.models.Suggestion;
import com.movieclub.services.SuggestionService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SuggestionController {

    private static final SuggestionService suggestionService = new SuggestionService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/suggestions", SuggestionController::getAllSuggestions);
        app.post("/api/suggestions", SuggestionController::addSuggestion);
        app.delete("/api/suggestions/{id}", SuggestionController::deleteSuggestion);
        app.post("/api/suggestions/{id}/watched", SuggestionController::markAsWatched);
        app.post("/api/suggestions/{id}/seen-it", SuggestionController::toggleSeenIt);
        app.get("/api/suggestions/{id}/seen-it", SuggestionController::getSeenIt);
    }
    
    private static void getAllSuggestions(Context ctx) {
        try {
            List<Suggestion> suggestions = suggestionService.getAllSuggestions();
            ctx.status(200).json(suggestions);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void addSuggestion(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            // store data from request body as variables
            int tmdbId = (int) body.get("tmdbId");
            String title = (String) body.get("title");
            String posterUrl = (String) body.get("posterUrl");
            int year = (int) body.get("year");
            int suggestedBy = (int) body.get("suggestedBy");

            // safety check to ensure suggestion is valid
            if (title == null) {
                ctx.status(400).json(Map.of("error", "Title is required"));
                return;
            }

            // add suggestion to database, report result of operation to user through response
            Suggestion suggestion = suggestionService.addSuggestion(tmdbId, title, posterUrl, year, suggestedBy);

            // safety check
            if (suggestion == null) {
                ctx.status(400).json(Map.of("error", "Maximum 3 suggestions allowed"));
                return;
            }

            ctx.status(201).json(suggestion);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error" + e.getMessage()));
        }
    }

    private static void deleteSuggestion(Context ctx) {
        try {
            int suggestionId = Integer.parseInt(ctx.pathParam("id"));
            boolean deleted = suggestionService.deleteSuggestion(suggestionId);

            if (deleted) 
                ctx.status(200).json(Map.of("message", "suggestion deleted successfully"));
            else
                ctx.status(404).json(Map.of("error", "Suggestion not found"));
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void markAsWatched(Context ctx) {
        try {
            int suggestionId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            int chosenBy = (int) body.get("chosenBy");
            LocalDate dateWatched = LocalDate.parse((String) body.get("dateWatched"));

            // add watched movie to database, and report result of operation to user through response
            Movie movie = suggestionService.markAsWatched(suggestionId, chosenBy, dateWatched);

            // safety check
            if (movie == null) {
                ctx.status(404).json(Map.of("error", "Suggestion not found"));
                return;
            }

            ctx.status(200).json(movie);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void toggleSeenIt(Context ctx) {
        try {
            int suggestionId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);

            // extract the user and whether they have seen it or not from request body and store in variables
            int userId = (int) body.get("userId");
            boolean hasSeen = (boolean) body.get("hasSeen");

            // update database whether user has seen a movie, and report result of operation to user in response
            SeenIt seenIt = suggestionService.toggleSeenIt(suggestionId, userId, hasSeen);

            // safety check
            if (seenIt == null) {
                ctx.status(500).json(Map.of("error", "Failed to save response"));
                return;
            }

            ctx.status(200).json(seenIt);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    private static void getSeenIt(Context ctx) {
        try {
            int suggestionId = Integer.parseInt(ctx.pathParam("id"));
            List<SeenIt> responses = suggestionService.getSeenItForSuggestion(suggestionId);
            ctx.status(200).json(responses);
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}
