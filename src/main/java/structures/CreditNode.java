package structures;
import stores.CastCredit;
import stores.CrewCredit;

public class CreditNode {
    public int filmID;  // tmdbID
    public CastCredit[] cast;
    public CrewCredit[] crew;
    public CreditNode nextCredit;  // chaining for collisions


    /**
     * Constructs a new CreditNode storing the cast and crew for the given film. New nodes are inserted at the head 
     * of their bucket's linked list in Credits.add, so nextCredit is set externally immediately after construction.
     *
     * @param filmID The unique TMDB film ID that associated with node
     * @param cast   Array of cast members for the film (may be empty, not null)
     * @param crew   Array of crew members for the film (may be empty, not null)
     */
    public CreditNode(int filmID, CastCredit[] cast, CrewCredit[] crew) {
        this.filmID = filmID;
        this.cast = cast;
        this.crew = crew;
    }
}