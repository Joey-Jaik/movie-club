package com.movieclub.services;

import com.movieclub.config.Database;
import com.movieclub.models.SeenIt;
import com.movieclub.models.Suggestion;
import com.movieclub.models.Movie;
//import com.movieclub.services.MovieService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuggestionService {
    
    public List<Suggestion> getAllSuggestions() throws SQLException {
        // create a SQL query that will get all movie suggestions for that month from database
        String sql = """
                SELECT s.id, s.tmdb_id, s.title, s.poster_url, s.year,
                       s.suggested_by, s.created_at, u.username AS suggested_by_username
                FROM suggestions s
                JOIN users u ON s.suggested_by = u.id
                ORDER BY s.created_at ASC
                """;

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        // take each returned row in result set and conver to suggestion object, add to list holding all movie suggestions for that month and return list
        List<Suggestion> suggestions = new ArrayList<>();
        while (rs.next()) {
            Suggestion suggestion = mapRowToSuggestion(rs);
            suggestions.add(suggestion);
        }

        return suggestions;
    }

    public Suggestion addSuggestion(int tmdb, String title, String posterUrl, int year, int suggestedBy) throws SQLException {
        // ensure user has not already reached limit of 3 suggestions, if they have then do not insert new suggestion and just return null
        String sql = """
                SELECT COUNT(*) FROM suggestions WHERE suggested_by = ?
                """;

        Connection conn = Database.getConnection();
        PreparedStatement countStmt = conn.prepareStatement(sql);
        countStmt.setInt(1, suggestedBy);
        ResultSet countRs = countStmt.executeQuery();

        if (countRs.next() && countRs.getInt(1) >= 3)
            return null;

        // create SQL query to insert new suggestion into database
        String insertSql = """
                INSERT INTO suggestions (tmdb_id, title, poster_url, year, suggested_by)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id, tmdb_id, title, poster_url, year, suggested_by, created_at
                """;

        PreparedStatement insertStmt = conn.prepareStatement(insertSql);
        insertStmt.setInt(1, tmdb);
        insertStmt.setString(2, title);
        insertStmt.setString(3, posterUrl);
        insertStmt.setInt(4, year);
        insertStmt.setInt(5, suggestedBy);

        // execute query, if insert is successful row is returned, conver row to suggestion object and return object, if insertion unsuccessful return null
        ResultSet rs = insertStmt.executeQuery();
        if (rs.next()) {
            return mapRowToSuggestion(rs);
        }

        return null;
    }

    public boolean deleteSuggestion(int suggestionId) throws SQLException {
        // create SQL query to delete suggestion from database
        String sql = "DELETE FROM suggestions WHERE id = ?";

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, suggestionId);

        return stmt.executeUpdate() > 0;
    }

    public boolean clearAllSuggestions() throws SQLException {
        // create SQL query to delete all suggestions from database
        String sql = "DELETE FROM suggestions";

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.executeUpdate();
        return true;
    }

    public Movie markAsWatched(int suggestionId, int chosenBy, java.time.LocalDate dateWatched) throws SQLException {
        // create SQL query that will get all the data for the suggested movie that got watched
        String getSql = "SELECT * FROM suggestions WHERE id = ?";

        Connection conn = Database.getConnection();
        PreparedStatement getStmt = conn.prepareStatement(getSql);
        getStmt.setInt(1, suggestionId);
        ResultSet rs = getStmt.executeQuery();

        // return null if suggestion is not found, safety check
        if (!rs.next()) return null;

        // take result and extract movie data, create a movie service object and use its add movie method with extracted data to add the watched suggested movie to database
        int tmdbId = rs.getInt("tmdb_id");
        String title = rs.getString("title");
        String posterUrl = rs.getString("poster_url");
        int year = rs.getInt("year");

        MovieService movieService = new MovieService();
        Movie movie = movieService.addMovie(tmdbId, title, posterUrl, year, dateWatched, chosenBy);

        // movie has been watched, suggestions get reset for next users turn
        clearAllSuggestions();

        return movie;
    }

    public SeenIt toggleSeenIt(int suggestionId, int userId, boolean hasSeen) throws SQLException {
        // create SQL query to update database that a user has marked they have seen a suggested movie
        String sql = """
                INSERT INTO seen_it (suggestion_id, user_id, has_seen)
                VALUES (?, ?, ?)
                ON CONFLICT (suggestion_id, user_id)
                DO UPDATE SET has_seen = EXCLUDED.has_seen
                RETURNING id, suggestion_id, user_id, has_seen, created_at
                """;

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, suggestionId);
        stmt.setInt(2, userId);
        stmt.setBoolean(3, hasSeen);

        // execute query, if successfull take row that gets return and use data to create and return a seen it object, if update not successfull then return  null
        ResultSet seenRs = stmt.executeQuery();
        if (seenRs.next()) {
            SeenIt seenIt = new SeenIt();
            seenIt.setId(seenRs.getInt("id"));
            seenIt.setSuggestionId(seenRs.getInt("suggestion_id"));
            seenIt.setUserId(seenRs.getInt("user_id"));
            seenIt.setHasSeen(seenRs.getBoolean("has_seen"));
            return seenIt;
        }

        return null;
    }

    public List<SeenIt> getSeenItForSuggestion(int suggestionId) throws SQLException {
        // create an SQL query that will get all the seen it data for a specifed suggested movie
        String sql = """
                SELECT si.id, si.suggestion_id, si.user_id,
                       si.has_seen, si.created_at, u.username
                FROM seen_it si
                JOIN users u ON si.user_id = u.id
                WHERE si.suggestion_id = ?
                """;

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, suggestionId);
        ResultSet rs = stmt.executeQuery();

        // for each row returned from query convert into seen it object, store object in list and return list
        List<SeenIt> responses = new ArrayList<>();
        while (rs.next()) {
            SeenIt seenIt = new SeenIt();
            seenIt.setId(rs.getInt("id"));
            seenIt.setSuggestionId(rs.getInt("suggestion_id"));
            seenIt.setUserId(rs.getInt("user_id"));
            seenIt.setHasSeen(rs.getBoolean("has_seen"));
            seenIt.setUsername(rs.getString("username"));
            responses.add(seenIt);
        }

        return responses;
    }

    private Suggestion mapRowToSuggestion(ResultSet rs) throws SQLException {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(rs.getInt("id"));
        suggestion.setTmdbId(rs.getInt("tmdb_id"));
        suggestion.setTitle(rs.getString("title"));
        suggestion.setPosterUrl(rs.getString("poster_url"));
        suggestion.setYear(rs.getInt("year"));
        suggestion.setSuggestedBy(rs.getInt("suggested_by"));
        suggestion.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        try {
            suggestion.setSuggestedByUsername(rs.getString("suggested_by_username"));
        } catch (SQLException e) {

        }

        return suggestion;
    }
}
