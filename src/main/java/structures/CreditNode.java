package structures;
import stores.CastCredit;
import stores.CrewCredit;

public class CreditNode {
    public int filmID;
    public CastCredit[] cast;
    public CrewCredit[] crew;
    public CreditNode nextCredit;

    public CreditNode(int filmID, CastCredit[] cast, CrewCredit[] crew) {
        this.filmID = filmID;
        this.cast = cast;
        this.crew = crew;
    }
}
