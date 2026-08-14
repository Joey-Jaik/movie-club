package com.movieclub.services;

import com.movieclub.config.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsService {

    public String getNextMovieNight() throws SQLException {
        String sql = "SELECT value FROM settings WHERE key = 'next_movie_night'";
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        // if query is successful then return value from database, otherwise return null
        if(rs.next())
            return rs.getString("value");

        return null;
    }

    public boolean updateNextMovieNight(String date) throws SQLException {
        String sql = "UPDATE settings SET value = ? WHERE key = 'next_movie_night'";
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, date);

        return stmt.executeUpdate() > 0;
    }
    
}
