package structures;
import java.time.LocalDate;
import stores.Genre;
import stores.Company;

public class MovieNode {
    public int id;  // tmdbID
    public String title;
    public String originalTitle;
    public String overview;
    public String tagline;
    public String status;
    public Genre[] genres;
    public LocalDate release;  // releaseDate
    public long budget;
    public long revenue;
    public String[] languages;  // spokenLanguages
    public String originalLanguage;
    public double runtime;
    public String homepage;
    public boolean adult;
    public boolean video;
    public String poster; // posterPath

    public int collectionID = -1;  // if part of a collection, then it'd have a collection ID not equal to -1
    
    public String imdbID;
    public double popularity = 0.0;

    public MyDynamicArray<Company> companies = new MyDynamicArray<>();  // productionCompanies
    public MyDynamicArray<String> countries = new MyDynamicArray<>();  // productionCountries

    public double voteAverage = -1;
    public int voteCount = -1;

    public MovieNode next;   // for chaining (hash collisions)


    /**
     * Constructs a fully initialised MovieNode from the core film metadata. Optional fields (collectionID, imdbID, 
     * popularity, voteAverage, voteCount) still have their default values and must be set via the corresponding 
     * setter methods in Movies.java after construction. The companies and countries dynamic arrays are also empty when 
     * first created.
     *
     * @param id               The unique TMDB film ID
     * @param title            The English-language title
     * @param originalTitle    The title in the film's original language
     * @param overview         A short plot summary
     * @param tagline          The poster tagline (empty string if none)
     * @param status           The current production/release status
     * @param genres           Array of genres associated with the film
     * @param release          The theatrical release date (may be null)
     * @param budget           Production budget in USD (0 if unknown)
     * @param revenue          Box-office revenue in USD (0 if unknown)
     * @param languages        ISO 639-1 codes for all available languages
     * @param originalLanguage ISO 639-1 code for the original production language
     * @param runtime          Running time in minutes (0.0 if unknown)
     * @param homepage         URL of the film's official homepage (empty if none)
     * @param adult            Whether the film is classified as adult content
     * @param video            Whether the film is a direct-to-video release
     * @param poster           Unique URL path segment for the film poster (empty if none)
     */
    public MovieNode(int id, String title, String originalTitle, String overview, String tagline, String status, Genre[] genres,
                    LocalDate release, long budget, long revenue, String[] languages, String originalLanguage, double runtime, 
                    String homepage, boolean adult, boolean video, String poster) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.tagline = tagline;
        this.status = status;
        this.genres = genres;
        this.release = release;
        this.budget = budget;
        this.revenue = revenue;
        this.languages = languages;
        this.originalLanguage = originalLanguage;
        this.runtime = runtime;
        this.homepage = homepage;
        this.adult = adult;
        this.video = video;
        this.poster = poster;
    }
}