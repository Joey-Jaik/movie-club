package com.movieclub.services;

import com.movieclub.config.Database;
import com.movieclub.models.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingService {
    
    public List<Rating> getRatingsForMovie(int movieId) throws SQLException {
        // create SQL query that will get rating infor for desired movie
        String sql = """
                SELECT r.id, r.movie_id, r.user_id, r.rating,
                       r.created_at, r.updated_at, u.username
                FROM ratings r
                JOIN users u ON r.user_id = u.id
                WHERE r.movie_id = ?
                ORDER BY u.username ASC
                """;

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, movieId);
        ResultSet rs = stmt.executeQuery();

        // go through every rating for the specified rating, and then create and return a list that contains all ratings for that movie
        List<Rating> ratings = new ArrayList<>();
        while (rs.next()) {
            Rating rating = new Rating();
            rating.setId(rs.getInt("id"));
            rating.setMovieId(rs.getInt("movie_id"));
            rating.setUserId(rs.getInt("user_id"));
            rating.setRating(rs.getDouble("rating"));
            rating.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            rating.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            rating.setUsername(rs.getString("username"));
            ratings.add(rating);
        }

        return ratings;
    }

    public Rating saveRating(int movieId, int userId, double rating) throws SQLException {
        // create SQL query to update ratings table with a new rating
        String sql = """
                INSERT INTO ratings (movie_id, user_id, rating)
                VALUES (?, ?, ?)
                ON CONFLICT (movie_id, user_id)
                DO UPDATE SET rating = EXCLUDED.rating,
                              updated_at = now()
                RETURNING id, movie_id, user_id, rating, created_at, updated_at
                """;

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, movieId);
        stmt.setInt(2, userId);
        stmt.setDouble(3, rating);

        // execute the update, and if it is successful then updated rows will be returned, create rating objects with return row and return it, if update is unsuccessful then no rows are returned and just return null
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Rating r = new Rating();
            r.setId(rs.getInt("id"));
            r.setMovieId(rs.getInt("movie_id"));
            r.setUserId(rs.getInt("user_id"));
            r.setRating(rs.getDouble("rating"));
            r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            r.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return r;
        }

        return null;
    }

    public boolean deleteRating(int movieId, int userId) throws SQLException {
        // create SQL query to delete rating from database
        String sql = "DELETE FROM ratings WHERE movie_id = ? AND user_id = ?";

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, movieId);
        stmt.setInt(2, userId);

        return stmt.executeUpdate() > 0;
    }
}
