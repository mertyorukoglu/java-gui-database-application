package db;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:moviecritics.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            initializeSchema();
            seedData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeSchema() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS Person (
                PersonID INTEGER PRIMARY KEY AUTOINCREMENT,
                FirstName VARCHAR(100) NOT NULL,
                LastName VARCHAR(100) NOT NULL,
                DateOfBirth DATE,
                Nationality VARCHAR(100)
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS Movie (
                MovieID INTEGER PRIMARY KEY AUTOINCREMENT,
                Title VARCHAR(255) NOT NULL,
                ReleaseDate DATE,
                Language VARCHAR(100),
                CountryOfOrigin VARCHAR(100),
                Genre VARCHAR(100),
                DirectorId INTEGER,
                Watched BOOLEAN DEFAULT 0,
                LeadActorId INTEGER,
                SupportActorId INTEGER,
                About TEXT,
                Rating INTEGER DEFAULT 0,
                Comments TEXT,
                Poster VARCHAR(255),
                ParentalRestriction BOOLEAN DEFAULT 0,
                FOREIGN KEY (DirectorId) REFERENCES Person(PersonID),
                FOREIGN KEY (LeadActorId) REFERENCES Person(PersonID),
                FOREIGN KEY (SupportActorId) REFERENCES Person(PersonID)
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS User (
                UserId INTEGER PRIMARY KEY AUTOINCREMENT,
                Username VARCHAR(100) UNIQUE NOT NULL,
                Password VARCHAR(100) NOT NULL,
                UserType INTEGER NOT NULL,
                Email VARCHAR(255)
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS Watchlist (
                WatchlistID INTEGER PRIMARY KEY AUTOINCREMENT,
                UserID INTEGER NOT NULL,
                MovieID INTEGER NOT NULL,
                FOREIGN KEY (UserID) REFERENCES User(UserId),
                FOREIGN KEY (MovieID) REFERENCES Movie(MovieID)
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS UserMovieRating (
                RatingID INTEGER PRIMARY KEY AUTOINCREMENT,
                UserID INTEGER NOT NULL,
                MovieID INTEGER NOT NULL,
                Rating INTEGER NOT NULL,
                Comment TEXT,
                FOREIGN KEY (UserID) REFERENCES User(UserId),
                FOREIGN KEY (MovieID) REFERENCES Movie(MovieID)
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS WatchedLog (
                LogID INTEGER PRIMARY KEY AUTOINCREMENT,
                UserID INTEGER NOT NULL,
                MovieID INTEGER NOT NULL,
                WatchedDate DATE,
                FOREIGN KEY (UserID) REFERENCES User(UserId),
                FOREIGN KEY (MovieID) REFERENCES Movie(MovieID)
            )
        """);

        stmt.close();
    }

    private void seedData() throws SQLException {
        
        Statement check = connection.createStatement();
        ResultSet rs = check.executeQuery("SELECT COUNT(*) FROM Person");
        if (rs.getInt(1) > 0) {
            rs.close();
            check.close();
            return;
        }
        rs.close();
        check.close();

        Statement stmt = connection.createStatement();

        stmt.execute("INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES ('Christopher', 'Nolan', '1970-07-30', 'British')");
        stmt.execute("INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES ('Leonardo', 'DiCaprio', '1974-11-11', 'American')");
        stmt.execute("INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES ('Quentin', 'Tarantino', '1963-03-27', 'American')");
        stmt.execute("INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES ('Natalie', 'Portman', '1981-06-09', 'Israeli-American')");
        stmt.execute("INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES ('Steven', 'Spielberg', '1946-12-18', 'American')");
        stmt.execute("INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES ('Tom', 'Hanks', '1956-07-09', 'American')");
        stmt.execute("INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES ('James', 'Cameron', '1954-08-16', 'Canadian')");
        stmt.execute("INSERT INTO Person (FirstName, LastName, DateOfBirth, Nationality) VALUES ('Meryl', 'Streep', '1949-06-22', 'American')");

        stmt.execute("""
            INSERT INTO Movie (Title, ReleaseDate, Language, CountryOfOrigin, Genre, DirectorId, Watched,
                LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction)
            VALUES ('Inception', '2010-07-16', 'English', 'USA', 'Sci-Fi/Thriller', 1, 1,
                2, 4, 'A thief who steals corporate secrets through dream-sharing technology.', 5,
                'Mind-blowing masterpiece!', 'posters/inception.jpg', 0)
        """);
        stmt.execute("""
            INSERT INTO Movie (Title, ReleaseDate, Language, CountryOfOrigin, Genre, DirectorId, Watched,
                LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction)
            VALUES ('Pulp Fiction', '1994-10-14', 'English', 'USA', 'Crime/Drama', 3, 0,
                2, 6, 'The lives of two mob hitmen, a boxer, and a pair of diner bandits intertwine.', 5,
                'Classic Tarantino!', 'posters/pulp_fiction.jpg', 1)
        """);
        stmt.execute("""
            INSERT INTO Movie (Title, ReleaseDate, Language, CountryOfOrigin, Genre, DirectorId, Watched,
                LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction)
            VALUES ('Forrest Gump', '1994-07-06', 'English', 'USA', 'Drama/Romance', 5, 1,
                6, 8, 'The presidencies of Kennedy and Nixon through the eyes of an Alabama man.', 5,
                'Life is like a box of chocolates!', 'posters/forrest_gump.jpg', 0)
        """);
        stmt.execute("""
            INSERT INTO Movie (Title, ReleaseDate, Language, CountryOfOrigin, Genre, DirectorId, Watched,
                LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction)
            VALUES ('Titanic', '1997-12-19', 'English', 'USA', 'Romance/Drama', 7, 1,
                2, 8, 'A seventeen-year-old aristocrat falls in love with a kind but poor artist.', 4,
                'Epic love story!', 'posters/titanic.jpg', 0)
        """);
        stmt.execute("""
            INSERT INTO Movie (Title, ReleaseDate, Language, CountryOfOrigin, Genre, DirectorId, Watched,
                LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction)
            VALUES ('Interstellar', '2014-11-07', 'English', 'USA', 'Sci-Fi/Adventure', 1, 0,
                6, 4, 'A team of explorers travel through a wormhole in space to save humanity.', 5,
                'Visually stunning!', 'posters/interstellar.jpg', 0)
        """);
        stmt.execute("""
            INSERT INTO Movie (Title, ReleaseDate, Language, CountryOfOrigin, Genre, DirectorId, Watched,
                LeadActorId, SupportActorId, About, Rating, Comments, Poster, ParentalRestriction)
            VALUES ('Django Unchained', '2012-12-25', 'English', 'USA', 'Western/Action', 3, 0,
                2, 6, 'A freed slave teams with a bounty hunter to rescue his wife from a plantation.', 4,
                'Great action sequences!', 'posters/django_unchained.jpg', 1)
        """);

        stmt.execute("INSERT INTO User (Username, Password, UserType, Email) VALUES ('parent1', 'pass123', 1, 'parent1@family.com')");
        stmt.execute("INSERT INTO User (Username, Password, UserType, Email) VALUES ('parent2', 'pass456', 1, 'parent2@family.com')");
        stmt.execute("INSERT INTO User (Username, Password, UserType, Email) VALUES ('child1', 'child123', 2, 'child1@family.com')");
        stmt.execute("INSERT INTO User (Username, Password, UserType, Email) VALUES ('child2', 'child456', 2, 'child2@family.com')");
        stmt.execute("INSERT INTO User (Username, Password, UserType, Email) VALUES ('child3', 'child789', 2, 'child3@family.com')");

        stmt.execute("INSERT INTO UserMovieRating (UserID, MovieID, Rating, Comment) VALUES (1, 1, 5, 'Absolutely mind-bending!')");
        stmt.execute("INSERT INTO UserMovieRating (UserID, MovieID, Rating, Comment) VALUES (2, 3, 5, 'A timeless classic.')");
        stmt.execute("INSERT INTO UserMovieRating (UserID, MovieID, Rating, Comment) VALUES (3, 1, 4, 'Really enjoyed it!')");
        stmt.execute("INSERT INTO UserMovieRating (UserID, MovieID, Rating, Comment) VALUES (4, 4, 5, 'So romantic and sad.')");
        stmt.execute("INSERT INTO UserMovieRating (UserID, MovieID, Rating, Comment) VALUES (5, 5, 5, 'The science was amazing!')");

        stmt.execute("INSERT INTO WatchedLog (UserID, MovieID, WatchedDate) VALUES (3, 1, '2026-01-10')");
        stmt.execute("INSERT INTO WatchedLog (UserID, MovieID, WatchedDate) VALUES (3, 3, '2026-02-15')");
        stmt.execute("INSERT INTO WatchedLog (UserID, MovieID, WatchedDate) VALUES (4, 4, '2026-01-20')");
        stmt.execute("INSERT INTO WatchedLog (UserID, MovieID, WatchedDate) VALUES (5, 5, '2026-03-01')");

        stmt.close();
    }
}
