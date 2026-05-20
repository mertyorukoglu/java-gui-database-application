package dao;

import db.DatabaseManager;
import model.UserMovieRating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {
    private Connection conn;

    public RatingDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public List<UserMovieRating> getRatingsForMovie(int movieId) {
        List<UserMovieRating> list = new ArrayList<>();
        String sql = "SELECT * FROM UserMovieRating WHERE MovieID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public UserMovieRating getUserRatingForMovie(int userId, int movieId) {
        String sql = "SELECT * FROM UserMovieRating WHERE UserID=? AND MovieID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean addOrUpdateRating(int userId, int movieId, int rating, String comment) {
        UserMovieRating existing = getUserRatingForMovie(userId, movieId);
        if (existing != null) {
            String sql = "UPDATE UserMovieRating SET Rating=?, Comment=? WHERE RatingID=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, rating);
                stmt.setString(2, comment);
                stmt.setInt(3, existing.getRatingID());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        } else {
            String sql = "INSERT INTO UserMovieRating (UserID, MovieID, Rating, Comment) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, movieId);
                stmt.setInt(3, rating);
                stmt.setString(4, comment);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        }
    }

    public boolean deleteRating(int ratingId) {
        String sql = "DELETE FROM UserMovieRating WHERE RatingID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ratingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<UserMovieRating> getAllRatings() {
        List<UserMovieRating> list = new ArrayList<>();
        String sql = "SELECT * FROM UserMovieRating";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private UserMovieRating mapRow(ResultSet rs) throws SQLException {
        return new UserMovieRating(
            rs.getInt("RatingID"),
            rs.getInt("UserID"),
            rs.getInt("MovieID"),
            rs.getInt("Rating"),
            rs.getString("Comment")
        );
    }
}
