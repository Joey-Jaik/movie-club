package com.movieclub.services;

import com.movieclub.config.Database;
import com.movieclub.models.Movie;

//import net.bytebuddy.dynamic.scaffold.MethodRegistry.Prepared;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieService {
    
    public List<Movie> getAllMovies() throws SQLException {
        // create sql query that will fetch al movie data needed
        String sql = """
                SELECT m.id, m.tmdb_id, m.title, m.poster_url, m.year,
                       m.date_watched, m.chosen_by, u.username AS chosen_by_username,
                       COALESCE(AVG(r.rating), 0) AS aggregate_rating
                FROM movies m
                JOIN users u ON m.chosen_by = u.id
                LEFT JOIN ratings r ON m.id = r.movie_id
                GROUP BY m.id, m.tmdb_id, m.title, m.poster_url, m.year,
                         m.date_watched, m.chosen_by, u.username
                ORDER BY m.date_watched DESC
                """;

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        // take each row of movie data retrieved from database, turn into movie object and add to list
        List<Movie> movies = new ArrayList<>();
        while (rs.next()) {
            Movie movie = mapRowToMovie(rs);
            movies.add(movie);
        }

        return movies;
    }

    public Movie addMovie(int tmdbId, String title, String posterUrl, int year, LocalDate dateWatched, int chosenBy) throws SQLException {
        String sql = """
                INSERT INTO movies (tmdb_id, title, poster_url, year, date_watched, chosen_by)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id, tmdb_id, title, poster_url, year, date_watched, chosen_by
                """;

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, tmdbId);
        stmt.setString(2, title);
        stmt.setString(3, posterUrl);
        stmt.setInt(4, year);
        stmt.setDate(5, Date.valueOf(dateWatched));
        stmt.setInt(6, chosenBy);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return mapRowToMovie(rs);
        }

        return null;
    }

    public boolean deleteMovie(int movieId) throws SQLException {
        String sql = "DELETE FROM movies WHERE id = ?";

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, movieId);

        return stmt.executeUpdate() > 0;
    }

    public List<Movie> getRankedMovies() throws SQLException {
        String sql = """
                SELECT m.id, m.tmdb_id, m.title, m.poster_url, m.year,
                       m.date_watched, m.chosen_by, u.username AS chosen_by_username,
                       COALESCE(AVG(r.rating), 0) AS aggregate_rating
                FROM movies m
                JOIN users u ON m.chosen_by = u.id
                LEFT JOIN ratings r ON m.id = r.movie_id
                GROUP BY m.id, m.tmdb_id, m.title, m.poster_url, m.year,
                         m.date_watched, m.chosen_by, u.username
                HAVING COUNT(r.id) > 0
                ORDER BY aggregate_rating DESC
                """;

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        List<Movie> movies = new ArrayList<>();
        while (rs.next()) {
            Movie movie = mapRowToMovie(rs);
            movies.add(movie);
        }

        return movies;
    }

    private Movie mapRowToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getInt("id"));
        movie.setTmdbId(rs.getInt("tmdb_id"));
        movie.setTitle(rs.getString("title"));
        movie.setPosterUrl(rs.getString("poster_url"));
        movie.setYear(rs.getInt("year"));
        movie.setDateWatched(rs.getDate("date_watched").toLocalDate());
        movie.setChosenBy(rs.getInt("chosen_by"));

        // if these fields dont exist in result set then skip them, not all queries will ask for them
        try{
            movie.setChosenByUsername(rs.getString("chosen_by_username"));
            movie.setAggregateRating(rs.getDouble("aggregate_rating"));
        } catch (SQLException e){

        }

        return movie;
    }
}
