// =============================================================================
// Author:  Joey Jaikaran
// Date:    August 20, 2026
// Purpose: Model class representing a member's seen it response for a movie
//          suggestion in the Movie Club app. Maps to the seen_it table in the
//          database with fields for suggestion ID, user ID, and a boolean
//          indicating whether the member has seen the suggested movie.
//          Includes a convenience username field populated via SQL join.
// =============================================================================

package com.movieclub.models;

import java.time.LocalDateTime;

public class SeenIt {
    
    private int id;
    private int suggestionId;
    private int userId;
    private boolean hasSeen;
    private LocalDateTime createdAt;
    private String username;

    public SeenIt() {}

    public SeenIt(int id, int suggestId, int userId, boolean hasSeen) {
        this.id = id;
        this.suggestionId = suggestId;
        this.userId = userId;
        this.hasSeen = hasSeen;
    }

    public int getId() { return id; }
    public int getSuggestionId() { return suggestionId; }
    public int getUserId() { return userId; }
    public boolean isHasSeen() { return hasSeen; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getUsername() { return username; }

    public void setId(int id) { this.id = id; }
    public void setSuggestionId(int suggestionId) { this.suggestionId = suggestionId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setHasSeen(boolean hasSeen) { this.hasSeen = hasSeen; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUsername(String username) { this.username = username; }
}
