package structures;

public class CollectionNode {
    public int id;
    public String name;
    public String posterPath;
    public String backdropPath;

    public MyDynamicArray<Integer> filmIDs = new MyDynamicArray<>();

    public CollectionNode(int id, String name, String posterPath, String backdropPath) {
        this.id = id;
        this.name = name;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
    }
}
