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
}
