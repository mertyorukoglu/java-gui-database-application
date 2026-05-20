package dao;

import db.DatabaseManager;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM User WHERE Username=? AND Password=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM User";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<User> getChildren() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE UserType = 2";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addUser(User u) {
        String sql = "INSERT INTO User (Username, Password, UserType, Email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPassword());
            stmt.setInt(3, u.getUserType());
            stmt.setString(4, u.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateUser(User u) {
        String sql = "UPDATE User SET Username=?, Password=?, UserType=?, Email=? WHERE UserId=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPassword());
            stmt.setInt(3, u.getUserType());
            stmt.setString(4, u.getEmail());
            stmt.setInt(5, u.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean resetPassword(int userId, String newPassword) {
        String sql = "UPDATE User SET Password=? WHERE UserId=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteUser(int userId) {
        try {
            
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM Watchlist WHERE UserID=?")) {
                s.setInt(1, userId); s.executeUpdate();
            }
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM WatchedLog WHERE UserID=?")) {
                s.setInt(1, userId); s.executeUpdate();
            }
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM UserMovieRating WHERE UserID=?")) {
                s.setInt(1, userId); s.executeUpdate();
            }
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM User WHERE UserId=?")) {
                s.setInt(1, userId);
                return s.executeUpdate() > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("UserId"),
            rs.getString("Username"),
            rs.getString("Password"),
            rs.getInt("UserType"),
            rs.getString("Email")
        );
    }
}
