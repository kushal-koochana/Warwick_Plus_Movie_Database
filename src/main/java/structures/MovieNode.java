package structures;
import java.time.LocalDate;
import stores.Genre;
import stores.Company;

public class MovieNode {
    public int id;
    public String title;
    public String originalTitle;
    public String overview;
    public String tagline;
    public String status;
    public Genre[] genres;
    public LocalDate release;
    public long budget;
    public long revenue;
    public String[] languages;
    public String originalLanguage;
    public double runtime;
    public String homepage;
    public boolean adult;
    public boolean video;
    public String poster;

    public int collectionID = -1;
    
    public String imdbID;
    public double popularity = 0.0;

    public MyDynamicArray<Company> companies = new MyDynamicArray<>();
    public MyDynamicArray<String> countries = new MyDynamicArray<>();

    public double voteAverage = -1;
    public int voteCount = -1;

    public MovieNode next;

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
