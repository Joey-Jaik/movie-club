// =============================================================================
// Author:  Joey Jaikaran
// Date:    August 20, 2026
// Purpose: Entry point for the Movie Club backend server. Creates and configures
//          the Javalin web server with CORS enabled and Jackson JSR310 date
//          support. Registers all API routes from the four controllers and
//          starts the server on the port provided by the environment or
//          defaults to 8080 for local development.
// =============================================================================

package com.movieclub;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.movieclub.controllers.AuthController;
import com.movieclub.controllers.MovieController;
import com.movieclub.controllers.RatingController;
import com.movieclub.controllers.SuggestionController;
import com.movieclub.controllers.SettingsController;

public class Main {
    
    public static void main(String[] args) {

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });

            config.jsonMapper(new JavalinJackson(mapper, true));

        }).start(port);

        AuthController.registerRoutes(app);
        MovieController.registerRoutes(app);
        RatingController.registerRoutes(app);
        SuggestionController.registerRoutes(app);
        SettingsController.registerRoutes(app);

        System.out.println("Movie Club server running on port " + port);
    }
}
