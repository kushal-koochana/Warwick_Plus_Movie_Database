package structures;
import java.time.LocalDateTime;

public class RatingNode {
    public int userID;
    public int movieID;
    public float rating;
    public LocalDateTime timestamp;

    public RatingNode nextUser;
    public RatingNode nextMovie;

    public RatingNode(int userID, int movieID, float rating, LocalDateTime timestamp) {
        this.userID = userID;
        this.movieID = movieID;
        this.rating = rating;
        this.timestamp = timestamp;
    }
}
