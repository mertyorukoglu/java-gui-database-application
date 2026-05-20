package model;

public class UserMovieRating {
    private int ratingID;
    private int userID;
    private int movieID;
    private int rating;
    private String comment;

    public UserMovieRating() {}

    public UserMovieRating(int ratingID, int userID, int movieID, int rating, String comment) {
        this.ratingID = ratingID;
        this.userID = userID;
        this.movieID = movieID;
        this.rating = rating;
        this.comment = comment;
    }

    public int getRatingID() { return ratingID; }
    public void setRatingID(int ratingID) { this.ratingID = ratingID; }
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
