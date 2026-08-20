// =============================================================================
// Author:  Joey Jaikaran
// Date:    August 20, 2026
// Purpose: Model class representing a watched movie in the Movie Club app.
//          Maps to the movies table in the database. Includes convenience
//          fields chosenByUsername and aggregateRating that are not stored
//          in the database but populated via SQL joins and aggregate functions
//          when fetching movies to avoid multiple round trips to the database.
// =============================================================================

package com.movieclub.models;

import java.time.LocalDate;

public class Movie {
    
    private int id;
    private int tmdbId;
    private String title;
    private String posterUrl;
    private int year;
    private LocalDate dateWatched;
    private int chosenBy;
    private String chosenByUsername;
    private double aggregateRating;

    public Movie() {}

    public Movie(int id, int tmdbId, String title, String posterUrl, int year, LocalDate dateWatched, int chosenBy) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.year = year;
        this.dateWatched = dateWatched;
        this.chosenBy = chosenBy;
    }

    public int getId() { return id; }
    public int getTmdbId() { return tmdbId; }
    public String getTitle() { return title; }
    public String getPosterUrl() { return posterUrl; }
    public int getYear() { return year; }
    public LocalDate getDateWatched() { return dateWatched; }
    public int getChosenBy() { return chosenBy; }
    public String getChosenByUsername() { return chosenByUsername; }
    public double getAggregateRating() { return aggregateRating; }

    public void setId(int id) { this.id = id; }
    public void setTmdbId(int tmdbId) { this.tmdbId = tmdbId; }
    public void setTitle(String title) { this.title = title; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setYear(int year) { this.year = year; }
    public void setDateWatched(LocalDate dateWatched) { this.dateWatched = dateWatched; }
    public void setChosenBy(int chosenBy) { this.chosenBy = chosenBy; }
    public void setChosenByUsername(String chosenByUsername) { this.chosenByUsername = chosenByUsername; }
    public void setAggregateRating(double aggregateRating) { this.aggregateRating = aggregateRating; }
}
