package model;

public class Watchlist {
    private int watchlistID;
    private int userID;
    private int movieID;
    private String movieTitle; 

    public Watchlist() {}

    public Watchlist(int watchlistID, int userID, int movieID) {
        this.watchlistID = watchlistID;
        this.userID = userID;
        this.movieID = movieID;
    }

    public int getWatchlistID() { return watchlistID; }
    public void setWatchlistID(int watchlistID) { this.watchlistID = watchlistID; }
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
}
