package com.movieclub.models;

import java.time.LocalDateTime;

public class Rating {
    
    private int id;
    private int movieId;
    private int userId;
    private double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;

    public Rating() {}

    public Rating(int id, int movieId, int userId, double rating) {
        this.id = id;
        this.movieId = movieId;
        this.userId = userId;
        this.rating = rating;
    }

    public int getId() { return id; }
    public int getMovieId() { return movieId; }
    public int getUserId() { return userId; }
    public double getRating() { return rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getUsername() { return username; }
    
    public void setId(int id) { this.id = id; }
    public void setMovieId(int movieId) { this.movieId = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setRating(double rating) { this.rating = rating; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setUsername(String username) { this.username = username; }
}
