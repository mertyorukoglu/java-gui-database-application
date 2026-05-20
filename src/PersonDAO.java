package dao;

import db.DatabaseManager;
import model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO {
    private Connection conn;

    public PersonDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public List<Person> getAllPersons() {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT * FROM Person";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Person getPersonById(int id) {
        String sql = "SELECT * FROM Person WHERE PersonID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean addPerson(Person p) {
        String sql = "INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getFirstName());
            stmt.setString(2, p.getLastName());
            stmt.setString(3, p.getDateOfBirth());
            stmt.setString(4, p.getNationality());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Person mapRow(ResultSet rs) throws SQLException {
        return new Person(
            rs.getInt("PersonID"),
            rs.getString("FirstName"),
            rs.getString("LastName"),
            rs.getString("DateOfBirth"),
            rs.getString("Nationality")
        );
    }
}
