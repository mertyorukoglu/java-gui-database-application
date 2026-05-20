package dao;

import db.DatabaseManager;
import model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    private Connection conn;

    public MovieDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public List<Movie> getAllMovies() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM Movie";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Movie> getAllMoviesForChild() {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM Movie WHERE ParentalRestriction = 0";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Movie> searchMovies(String genre, String directorName, String year, boolean childOnly) {
        List<Movie> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT m.* FROM Movie m LEFT JOIN Person p ON m.DirectorId = p.PersonID WHERE 1=1"
        );
        if (childOnly) sql.append(" AND m.ParentalRestriction = 0");
        if (genre != null && !genre.isEmpty())        sql.append(" AND LOWER(m.Genre) LIKE ?");
        if (directorName != null && !directorName.isEmpty()) sql.append(" AND LOWER(p.FirstName || ' ' || p.LastName) LIKE ?");
        if (year != null && !year.isEmpty())          sql.append(" AND m.ReleaseDate LIKE ?");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (genre != null && !genre.isEmpty())              stmt.setString(idx++, "%" + genre.toLowerCase() + "%");
            if (directorName != null && !directorName.isEmpty()) stmt.setString(idx++, "%" + directorName.toLowerCase() + "%");
            if (year != null && !year.isEmpty())                stmt.setString(idx++, year + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM Movie WHERE MovieID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean addMovie(Movie m) {
        String sql = """
            INSERT INTO Movie (Title, ReleaseDate, Language, CountryOfOrigin, Genre, DirectorId, Watched,
                LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getTitle());
            stmt.setString(2, m.getReleaseDate());
            stmt.setString(3, m.getLanguage());
            stmt.setString(4, m.getCountryOfOrigin());
            stmt.setString(5, m.getGenre());
            stmt.setInt(6, m.getDirectorId());
            stmt.setBoolean(7, m.isWatched());
            stmt.setInt(8, m.getLeadActorId());
            stmt.setInt(9, m.getSupportActorId());
            stmt.setString(10, m.getAbout());
            stmt.setInt(11, m.getRating());
            stmt.setString(12, m.getComments());
            stmt.setString(13, m.getPoster());
            stmt.setBoolean(14, m.isParentalRestriction());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateMovie(Movie m) {
        String sql = """
            UPDATE Movie SET Title=?, ReleaseDate=?, Language=?, CountryOfOrigin=?, Genre=?,
                DirectorId=?, Watched=?, LeadActorId=?, SupportActorId=?, About=?,
                Rating=?, Comments=?, Poster=?, ParentalRestriction=?
            WHERE MovieID=?
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getTitle());
            stmt.setString(2, m.getReleaseDate());
            stmt.setString(3, m.getLanguage());
            stmt.setString(4, m.getCountryOfOrigin());
            stmt.setString(5, m.getGenre());
            stmt.setInt(6, m.getDirectorId());
            stmt.setBoolean(7, m.isWatched());
            stmt.setInt(8, m.getLeadActorId());
            stmt.setInt(9, m.getSupportActorId());
            stmt.setString(10, m.getAbout());
            stmt.setInt(11, m.getRating());
            stmt.setString(12, m.getComments());
            stmt.setString(13, m.getPoster());
            stmt.setBoolean(14, m.isParentalRestriction());
            stmt.setInt(15, m.getMovieID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteMovie(int id) {
        try {
            
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM UserMovieRating WHERE MovieID=?")) {
                s.setInt(1, id); s.executeUpdate();
            }
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM Watchlist WHERE MovieID=?")) {
                s.setInt(1, id); s.executeUpdate();
            }
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM WatchedLog WHERE MovieID=?")) {
                s.setInt(1, id); s.executeUpdate();
            }
            try (PreparedStatement s = conn.prepareStatement("DELETE FROM Movie WHERE MovieID=?")) {
                s.setInt(1, id); return s.executeUpdate() > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean setParentalRestriction(int movieId, boolean value) {
        String sql = "UPDATE Movie SET ParentalRestriction=? WHERE MovieID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, value);
            stmt.setInt(2, movieId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        return new Movie(
            rs.getInt("MovieID"),
            rs.getString("Title"),
            rs.getString("ReleaseDate"),
            rs.getString("Language"),
            rs.getString("CountryOfOrigin"),
            rs.getString("Genre"),
            rs.getInt("DirectorId"),
            rs.getBoolean("Watched"),
            rs.getInt("LeadActorId"),
            rs.getInt("SupportActorId"),
            rs.getString("About"),
            rs.getInt("Rating"),
            rs.getString("Comments"),
            rs.getString("Poster"),
            rs.getBoolean("ParentalRestriction")
        );
    }
}
