// =============================================================================
// Author:  Joey Jaikaran
// Date:    August 20, 2026
// Purpose: Service class handling authentication business logic for the Movie
//          Club app. Verifies login credentials by fetching the stored BCrypt
//          hashed PIN from the database and comparing it to the plain text PIN
//          provided by the user. Also handles PIN updates by hashing the new
//          PIN with BCrypt before storing it, ensuring plain text PINs are
//          never stored in the database.
// =============================================================================

package com.movieclub.services;

import com.movieclub.config.Database;
import com.movieclub.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    
    // method that will return a user object if login is successful
    public User login(String username, String pin) throws SQLException{

        // create query and store as string
        String sql = "SELECT id, username, pin, pick_order FROM users WHERE username = ?";

        // create connection to interact with the database
        Connection conn = Database.getConnection();
        // send the sql query to database as a prepared statement. This means that we only send the query once, the database remembers the template, and then we just need to fill in the "?" whenever we want to execute. This protects against SQL injection, if we just hard coded the username into out sql query then the user could put anything there and could change the query itself. With a prepared statement since we insert the username into the query the entire username gets treated as the value and the rest of the query structure could not be changed. Username interpreted as code vs value
        PreparedStatement stmt = conn.prepareStatement(sql);
        // replace first question mark placeholder with provided value
        stmt.setString(1, username);
        // using SELECT so use executeQuery, returns a result set that contains the rows that matched
        ResultSet rs = stmt.executeQuery();

        // move cursor to the first row and return true if true. usernames are unique so this will either return 1 or 0, if it returns 0 then .next() returns false, we skip this section and just return null
        if (rs.next()) {
            // get pin stored in row for this username, and store as string
            String storedPin = rs.getString("pin");
            // pin returned is hashed, use salt from this hashed pin to hash the user submitted pin, and if they match then create and return new user object using data from this row of table
            if (BCrypt.checkpw(pin, storedPin)) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPickOrder(rs.getInt("pick_order"));
                return user;
            }
        }

        return null;
    }

    public boolean updatePin(int userId, String newPin) throws SQLException {
        // has new pin before storing it
        String hashedPin = BCrypt.hashpw(newPin, BCrypt.gensalt());
        String sql = "UPDATE users SET pin = ? Where id = ?";

        Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, hashedPin);
        stmt.setInt(2, userId);

        // modifying data so use executeUpdate, returns an integer for the number of rows affected
        return stmt.executeUpdate() > 0;
    }
}
