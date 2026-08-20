// =============================================================================
// Author:  Joey Jaikaran
// Date:    August 20, 2026
// Purpose: Service class handling all settings business logic for the Movie
//          Club app. Retrieves and updates the next movie night date stored
//          in the settings table. Also retrieves and increments the current
//          picker index which determines whose turn it is to choose the next
//          movie. The index cycles through 0-4 using modulo arithmetic to
//          rotate through the five club members in alphabetical order.
// =============================================================================

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
    
    public int getCurrentPickerIndex() throws SQLException {
        String sql = "SELECT value FROM settings WHERE key = 'current_picker_index'";
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        if (rs.next())
            return Integer.parseInt(rs.getString("value"));

        return 0;
    }

    public boolean incrementPickerIndex() throws SQLException {
        String sql = "UPDATE settings SET value = CAST((CAST(value AS INTEGER) + 1) % 5 AS TEXT) WHERE key = 'current_picker_index'";
        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        return stmt.executeUpdate() > 0;
    }
}
