package dao;

import db.DatabaseManager;
import model.Watchlist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {
    private Connection conn;

    public WatchlistDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public List<Watchlist> getWatchlistForUser(int userId) {
        List<Watchlist> list = new ArrayList<>();
        String sql = """
            SELECT w.*, m.Title AS MovieTitle
            FROM Watchlist w
            JOIN Movie m ON w.MovieID = m.MovieID
            WHERE w.UserID=?
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Watchlist wl = new Watchlist(rs.getInt("WatchlistID"), rs.getInt("UserID"), rs.getInt("MovieID"));
                wl.setMovieTitle(rs.getString("MovieTitle"));
                list.add(wl);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addToWatchlist(int userId, int movieId) {
        
        String check = "SELECT COUNT(*) FROM Watchlist WHERE UserID=? AND MovieID=?";
        try (PreparedStatement stmt = conn.prepareStatement(check)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return false; 
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String sql = "INSERT INTO Watchlist (UserID, MovieID) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean removeFromWatchlist(int watchlistId) {
        String sql = "DELETE FROM Watchlist WHERE WatchlistID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, watchlistId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
