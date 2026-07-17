package stores;

import java.time.LocalDate;

import interfaces.IMovies;
import structures.*;

public class Movies implements IMovies{
    Stores stores;
    private MovieNode[] hashTable;
    private int capacity = 1999;
    private int size = 0;
    private MyDynamicArray<CollectionNode> collections = new MyDynamicArray<>();

    public Movies(Stores stores) {
        this.stores = stores;
        this.hashTable = new MovieNode[capacity];
    }

    private int getHash(int id) {
        return Math.abs(id) % capacity;
    }

    private MovieNode getNode(int id) {
        int hashCode = getHash(id);
        
        for (MovieNode node = hashTable[hashCode]; node != null; node = node.next) {
            if (node.id == id) return node;
        }

        return null;
    }

    private CollectionNode getCollection(int collectionID) {
        for (int i = 0; i < collections.size(); i++) {
            CollectionNode collectionNode = collections.get(i);
            if (collectionNode.id == collectionID) return collectionNode;
        }
        return null;
    }

    @Override
    public boolean add(int id, String title, String originalTitle, String overview, String tagline, String status, Genre[] genres, LocalDate release, long budget, long revenue, String[] languages, String originalLanguage, double runtime, String homepage, boolean adult, boolean video, String poster) {
        if (getNode(id) != null) return false;

        MovieNode node = new MovieNode(id, title, originalTitle, overview, tagline, status, genres, release, budget, revenue,
                            languages, originalLanguage, runtime, homepage, adult, video, poster);
        
        int hashCode = getHash(id);
        node.next = hashTable[hashCode];
        hashTable[hashCode] = node;
        size++;
        return true;
    }

    @Override
    public boolean remove(int id) {
        int hashCode = getHash(id);

        MovieNode prevNode = null;

        for (MovieNode node = hashTable[hashCode]; node != null; node = node.next) {
            if (node.id == id) {
                if (prevNode == null) {
                    hashTable[hashCode] = node.next;
                } else {
                    prevNode.next = node.next;
                }
                size--;
                return true;
            }
            prevNode = node;
        }
        return false;
    }

    @Override
    public int[] getAllIDs() {
        int[] idsArray = new int[size];
        int i = 0;

        for (int j = 0; j < capacity; j++) {
            for (MovieNode node = hashTable[j]; node != null; node = node.next) {
                idsArray[i++] = node.id;
            }
        }
        return idsArray;
    }

    @Override
    public int[] getAllIDsReleasedInRange(LocalDate start, LocalDate end) {
        int[] largeIDsInReleaseRangeArray = new int[size];
        int actualArraySize = 0;

        for (int i = 0; i < capacity; i++) {
            for (MovieNode node = hashTable[i]; node != null; node = node.next) {
                if (node.release == null) continue;

                if (node.release.isAfter(start) && node.release.isBefore(end)) {
                    largeIDsInReleaseRangeArray[actualArraySize++] = node.id;
                }
            }
        }

        int[] idsInReleaseRangeArray = new int[actualArraySize];
        for (int i = 0; i < actualArraySize; i++) {
            idsInReleaseRangeArray[i] = largeIDsInReleaseRangeArray[i];
        }
        return idsInReleaseRangeArray;
    }

    @Override
    public String getTitle(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.title;
    }

    @Override
    public String getOriginalTitle(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.originalTitle;
    }

    @Override
    public String getOverview(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.overview;
    }

    @Override
    public String getTagline(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.tagline;
    }

    @Override
    public String getStatus(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.status;
    }

    @Override
    public Genre[] getGenres(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.genres;
    }

    @Override
    public LocalDate getRelease(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.release;
    }

    @Override
    public long getBudget(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1 : node.budget;
    }

    @Override
    public long getRevenue(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1 : node.revenue;
    }

    @Override
    public String[] getLanguages(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.languages;
    }

    @Override
    public String getOriginalLanguage(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.originalLanguage;
    }

    @Override
    public double getRuntime(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1.0d : node.runtime;
    }

    @Override
    public String getHomepage(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.homepage;
    }

    @Override
    public boolean getAdult(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? false : node.adult;
    }

    @Override
    public boolean getVideo(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? false : node.video;
    }

    @Override
    public String getPoster(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.poster;
    }

    @Override
    public boolean setVote(int id, double voteAverage, int voteCount) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.voteAverage = voteAverage;
        node.voteCount = voteCount;
        return true;
    }

    @Override
    public double getVoteAverage(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1.0d : node.voteAverage;
    }

    @Override
    public int getVoteCount(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1 : node.voteCount;
    }

    @Override
    public boolean addToCollection(int filmID, int collectionID, String collectionName, String collectionPosterPath, String collectionBackdropPath) {
        MovieNode node = getNode(filmID);
        if (node == null) return false;
        
        node.collectionID = collectionID;
        CollectionNode collectionNode = getCollection(collectionID);

        if (collectionNode == null) {
            collectionNode = new CollectionNode(collectionID, collectionName, collectionPosterPath, collectionBackdropPath);
            collections.add(collectionNode);
        }

        if (!collectionNode.filmIDs.contains(filmID)) {
            collectionNode.filmIDs.add(filmID);
        }
        return true;
    }

    @Override
    public int[] getFilmsInCollection(int collectionID) {
        CollectionNode collectionNode = getCollection(collectionID);
        if (collectionNode == null) return new int[0];
        return collectionNode.filmIDs.toIntArray();
    }

    @Override
    public String getCollectionName(int collectionID) {
        CollectionNode collectionNode = getCollection(collectionID);
        return (collectionNode == null) ? null : collectionNode.name;
    }

    @Override
    public String getCollectionPoster(int collectionID) {
        CollectionNode collectionNode = getCollection(collectionID);
        return (collectionNode == null) ? null : collectionNode.posterPath;
    }

    @Override
    public String getCollectionBackdrop(int collectionID) {
        CollectionNode collectionNode = getCollection(collectionID);
        return (collectionNode == null) ? null : collectionNode.backdropPath;
    }

    @Override
    public int getCollectionID(int filmID) {
        MovieNode node = getNode(filmID);
        return (node == null) ? -1 : node.collectionID;
    }

    @Override
    public boolean setIMDB(int id, String imdbID) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.imdbID = imdbID;
        return true;
    }

    @Override
    public String getIMDB(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.imdbID;
    }

    @Override
    public boolean setPopularity(int id, double popularity) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.popularity = popularity;
        return true;
    }

    @Override
    public double getPopularity(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1.0d : node.popularity;
    }

    @Override
    public boolean addProductionCompany(int id, Company company) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.companies.add(company);
        return true;
    }

    @Override
    public boolean addProductionCountry(int id, String country) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.countries.add(country);
        return true;
    }

    @Override
    public Company[] getProductionCompanies(int id) {
        MovieNode node = getNode(id);
        if (node == null) return null;
        int companiesListSize = node.companies.size();
        Company[] companiesArray = new Company[companiesListSize];
        for (int i = 0; i < companiesListSize; i++) {
            companiesArray[i] = node.companies.get(i);
        }
        return companiesArray;
    }

    @Override
    public String[] getProductionCountries(int id) {
        MovieNode node = getNode(id);
        if (node == null) return null;
        int countriesListSize = node.countries.size();
        String[] countriesArray = new String[countriesListSize];
        for (int i = 0; i < countriesListSize; i++) {
            countriesArray[i] = node.countries.get(i);
        }
        return countriesArray;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int[] findFilms(String searchTerm) {
        if (searchTerm == null) return new int[0];
        searchTerm = searchTerm.toLowerCase();

        int[] largeFoundIDsArray = new int[size];
        int actualArraySize = 0;

        for (int i = 0; i < capacity; i++) {
            for (MovieNode node = hashTable[i]; node != null; node = node.next) {
                boolean searchTermInTitle = node.title != null && node.title.toLowerCase().contains(searchTerm);
                boolean searchTermInOriginalTitle = node.originalTitle != null && node.originalTitle.toLowerCase().contains(searchTerm);
                boolean searchTermInOverview = node.overview != null && node.overview.toLowerCase().contains(searchTerm);

                if (searchTermInTitle || searchTermInOriginalTitle || searchTermInOverview) {
                    largeFoundIDsArray[actualArraySize++] = node.id; 
                }
            }
        }

        int[] foundIDsArray = new int[actualArraySize];
        for (int i = 0; i < actualArraySize; i++) {
            foundIDsArray[i] = largeFoundIDsArray[i];
        }
        return foundIDsArray;
    }
}
