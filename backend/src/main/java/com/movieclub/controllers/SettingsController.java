// =============================================================================
// Author:  Joey Jaikaran
// Date:    August 20, 2026
// Purpose: REST controller handling settings endpoints for the Movie Club app.
//          Registers routes for getting and updating the next movie night date,
//          and for getting and incrementing the current picker index. The picker
//          index is stored in the database and incremented each time a movie is
//          marked as watched to advance the pick order through the five members.
// =============================================================================

package com.movieclub.controllers;

import com.movieclub.services.SettingsService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class SettingsController {
    
    private static final SettingsService settingsService = new SettingsService();

    public static void registerRoutes(Javalin app) {
        app.get("/api/settings/movie-night", SettingsController::getMovieNight);
        app.post("/api/settings/movie-night", SettingsController::updateMovieNight);
        app.get("/api/settings/current-picker", SettingsController::getCurrentPicker);
        app.post("/api/settings/increment-picker", SettingsController::incrementPicker);
    }

    private static void getMovieNight(Context ctx) {
        try {
            String date = settingsService.getNextMovieNight();
            ctx.status(200).json(Map.of("date", date != null ? date : ""));
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void updateMovieNight(Context ctx){
        try{
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String date = body.get("date");

            // safety check
            if (date == null || date.isEmpty()) {
                ctx.status(400).json(Map.of("error", "Date is required"));
                return;
            }

            // update next movie night in database and report reuslt to user
            boolean updated = settingsService.updateNextMovieNight(date);
            if (updated)
                ctx.status(200).json(Map.of("message", "Movie night updated", "date", date));
            else
                ctx.status(500).json(Map.of("error", "Failed to update"));
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    private static void getCurrentPicker(Context ctx) {
        try {
            int index = settingsService.getCurrentPickerIndex();
            ctx.status(200).json(Map.of("index", index));
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    private static void incrementPicker(Context ctx) {
        try {
            boolean updated = settingsService.incrementPickerIndex();

            if (updated) {
                int newIndex = settingsService.getCurrentPickerIndex();
                ctx.status(200).json(Map.of("index", newIndex));
            }
            else
                ctx.status(500).json(Map.of("error", "Failed to increment picker"));
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}
