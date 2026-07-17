package interfaces;

public abstract class AbstractStores {
    protected ICredits credits;
    protected IKeywords keywords;
    protected IMovies movies;
    protected IRatings ratings;

    public ICredits  getCredits()  { return this.credits; }
    public IKeywords getKeywords() { return this.keywords; }
    public IMovies   getMovies()   { return this.movies; }
    public IRatings  getRatings()  { return this.ratings; }
}
