package structures;
import java.time.LocalDateTime;

public class RatingNode {
    public int userID;
    // public int movieLensID; (this isn't used in application, so disregarded)
    public int movieID; // tmdbID
    public float rating;
    public LocalDateTime timestamp;

    public RatingNode nextUser;  // used for linked list of user table
    public RatingNode nextMovie;  // used for linked list of movie table


    /**
     * Constructs a new RatingNode with all fields initialised. Both nextUser and nextMovie default to null; they are 
     * modified by Ratings.add immediately after construction when the node is inserted at the head of each table's bucket 
     * chain.
     *
     * @param userID    The ID of the user submitting the rating (greater than 0)
     * @param movieID   The TMDB film ID being rated
     * @param rating    The star rating, between 0 and 5 inclusive
     * @param timestamp The date and time the rating was recorded
     */
    public RatingNode(int userID, int movieID, float rating, LocalDateTime timestamp) {
        this.userID = userID;
        this.movieID = movieID;
        this.rating = rating;
        this.timestamp = timestamp;
    }
}