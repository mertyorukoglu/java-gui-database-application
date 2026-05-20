package dao;

import db.DatabaseManager;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class WatchedLogDAO {
    private Connection conn;

    public WatchedLogDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public boolean logWatched(int userId, int movieId) {
        
        String check = "SELECT COUNT(*) FROM WatchedLog WHERE UserID=? AND MovieID=?";
        try (PreparedStatement stmt = conn.prepareStatement(check)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String sql = "INSERT INTO WatchedLog (UserID, MovieID, WatchedDate) VALUES (?, ?, date('now'))";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean removeWatched(int userId, int movieId) {
        String sql = "DELETE FROM WatchedLog WHERE UserID=? AND MovieID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean isWatched(int userId, int movieId) {
        String sql = "SELECT COUNT(*) FROM WatchedLog WHERE UserID=? AND MovieID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int countWatchedByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM WatchedLog WHERE UserID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Map<Integer, Integer> getMostWatchedMovies() {
        Map<Integer, Integer> map = new HashMap<>();
        String sql = "SELECT MovieID, COUNT(*) as cnt FROM WatchedLog GROUP BY MovieID ORDER BY cnt DESC";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getInt("MovieID"), rs.getInt("cnt"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public Map<String, Integer> getWatchCountPerChild() {
        Map<String, Integer> map = new HashMap<>();
        String sql = """
            SELECT u.Username, COUNT(wl.LogID) as cnt
            FROM User u
            LEFT JOIN WatchedLog wl ON u.UserId = wl.UserID
            WHERE u.UserType = 2
            GROUP BY u.UserId, u.Username
        """;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("Username"), rs.getInt("cnt"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
}
