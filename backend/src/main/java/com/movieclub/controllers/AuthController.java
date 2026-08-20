// =============================================================================
// Author:  Joey Jaikaran
// Date:    August 20, 2026
// Purpose: REST controller handling authentication endpoints for the Movie Club
//          app. Registers routes for user login and PIN updates. Validates
//          request data, delegates business logic to AuthService, and returns
//          appropriate HTTP status codes and JSON responses. Returns user ID,
//          username, and pick order on successful login without exposing the
//          hashed PIN.
// =============================================================================

package com.movieclub.controllers;

import com.movieclub.models.User;
import com.movieclub.services.AuthService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Map;

public class AuthController {
    
    // create service object, logic lives in service so can just call methods of object
    private static final AuthService authService =  new AuthService();

    public static void registerRoutes(Javalin app) {
        // attach methods to appropriate HTTP requests and path
        app.post("/api/auth/login", AuthController::login);
        app.post("/api/auth/update-pin", AuthController::updatePin);
    }

    // context is the HTTP request converted to a usable object by Javalin
    @SuppressWarnings("unchecked")
    private static void login(Context ctx) {
        try {
            // take the context body, and convert to a map object, and then store the values from the map into variables
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String username = body.get("username");
            String pin = body.get("pin");

            // if username or pin do not exist then report error to user, and do not continue
            if (username == null || pin == null) {
                ctx.status(400).json(Map.of("error", "Username and PIN are required"));
                return;
            }

            // attempt login with provided information from request
            User user = authService.login(username, pin);

            // if login fails report error to user and do not continue
            if (user == null) {
                ctx.status(401).json(Map.of("error", "Invalid username or PIN"));
                return;
            }

            // if login successful then send successful status code and the user object as json back in the response
            ctx.status(200).json(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "pickOrder", user.getPickOrder()
            ));
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server Error: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void updatePin(Context ctx) {
        try {
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            int userId = (int) body.get("userId");
            String newPin = (String) body.get("newPin");

            // check to ensure valid pin has been provided
            if (newPin == null || newPin.length() < 4) {
                ctx.status(400).json(Map.of("error", "PIN must be at least 4 digits"));
                return;
            }

            // call service to update pin, send result in response
            boolean success = authService.updatePin(userId, newPin);

            if (success) {
                ctx.status(200).json(Map.of("message", "PIN updated successfully"));
            } 
            else {
                ctx.status(404).json(Map.of("error", "User not found"));
            }
        }
        catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Server Error: " + e.getMessage()));
        }
    }
}