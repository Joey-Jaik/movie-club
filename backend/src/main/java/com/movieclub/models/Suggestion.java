// =============================================================================
// Author:  Joey Jaikaran
// Date:    August 20, 2026
// Purpose: Model class representing a movie suggestion on the next movie page
//          of the Movie Club app. Maps to the suggestions table in the database.
//          Includes convenience fields for the suggester's username, seen it
//          counts, and a list of seen it responses that are populated from
//          joined queries rather than stored directly in the suggestions table.
// =============================================================================

package com.movieclub.models;

import java.time.LocalDateTime;
import java.util.List;

public class Suggestion {
    
    private int id;
    private int tmdbId;
    private String title;
    private String posterUrl;
    private int year;
    private int suggestedBy;
    private String suggestedByUsername;
    private LocalDateTime createdAt;
    private int seenItCount;
    private int notSeenItCount;
    private List<SeenIt> seenItResponses;

    public Suggestion() {}

    public Suggestion(int id, int tmdbId, String title, String posterUrl, int year, int suggestedBy) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.year = year;
        this.suggestedBy = suggestedBy;
    }

    public int getId() { return id; }
    public int getTmdbId() { return tmdbId; }
    public String getTitle() { return title; }
    public String getPosterUrl() { return posterUrl; }
    public int getYear() { return year; }
    public int getSuggestedBy() { return suggestedBy; }
    public String getSuggestedByUsername() { return suggestedByUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getSeenItCount() { return seenItCount; }
    public int getNotSeenItCount() { return notSeenItCount; }
    public List<SeenIt> getSeenItResponses() { return seenItResponses; }

    public void setId(int id) { this.id = id; }
    public void setTmdbId(int tmdbId) { this.tmdbId = tmdbId; }
    public void setTitle(String title) { this.title = title; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setYear(int year) { this.year = year; }
    public void setSuggestedBy(int suggestedBy) { this.suggestedBy = suggestedBy; }
    public void setSuggestedByUsername(String suggestedByUsername) { this.suggestedByUsername = suggestedByUsername; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setSeenItCount(int seenItCount) { this.seenItCount = seenItCount; }
    public void setNotSeenItCount(int notSeenItCount) { this.notSeenItCount = notSeenItCount; }
    public void setSeenItResponses(List<SeenIt> seenItResponses) { this.seenItResponses = seenItResponses; }
}
