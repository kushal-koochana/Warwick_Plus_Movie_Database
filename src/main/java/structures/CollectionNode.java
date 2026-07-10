package structures;

public class CollectionNode {
    public int id;
    public String name;
    public String posterPath;
    public String backdropPath;

    public MyDynamicArray<Integer> filmIDs = new MyDynamicArray<>();


    /**
     * Constructs a new CollectionNode with the supplied metadata. The filmIDS array is initially empty; films are added
     * separately via Movies.addToCollection
     *
     * @param id             The unique TMDB collection ID
     * @param name           The display name of the collection
     * @param posterPath     The URL path segment for the collection poster
     * @param backdropPath   The URL path segment for the collection backdrop
     */
    public CollectionNode(int id, String name, String posterPath, String backdropPath) {
        this.id = id;
        this.name = name;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
    }
}