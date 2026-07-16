package stores;

import java.time.LocalDate;

import interfaces.IMovies;
import structures.*;

public class Movies implements IMovies{
    // reference to stores used by Stores.java
    Stores stores;
    // Hash table array of certain capacity, each slot holds head of single linked list of MovieNode objects (separate chaining).
    // A slot is null when no film has hashed to that bucket.
    private MovieNode[] hashTable;
    // fixed number of buckets in hash table, used prime number (1999) to improve key distribution and reduce collisions.
    private int capacity = 1999;
    // number of movies stored across the whole hash table
    private int size = 0;
    // Dynamic array of all known film collections. Looked up by linear scan on collection ID; kept separate from main hash
    // table, as film may belong to collection discovered only after film node itself has been inserted.
    private MyDynamicArray<CollectionNode> collections = new MyDynamicArray<>();


    /**
     * Initialises the Movies store by allocating capacity to the hash table array. All capacity slots are initialised to 
     * null by the JVM, representing empty buckets. No films are stored at this point; they are added incrementally via add().
     * 
     *  @param stores    An object storing all the different key stores, including itself
     */
    public Movies(Stores stores) {
        this.stores = stores;
        this.hashTable = new MovieNode[capacity];
    }


    /**
     * Computes the hash table bucket index for the given film ID. Uses a simple modular compression function: 
     * abs(id) % capacity. Taking the absolute value guards against negative IDs producing a negative index. The prime 
     * modulus (1999) distributes keys evenly.
     *
     * @param id The unique TMDB film ID
     * @return A valid bucket index in the range [0, capacity).
     */
    private int getHash(int id) {
        return Math.abs(id) % capacity;
    }


    /**
     * Retrieves the MovieNode for the given film ID by traversing its hash bucket's linked list. This helper encapsulates 
     * the common pattern of hashing to a bucket and walking the chain until a matching ID is found, following the DRY
     * principle. It is used internally by virtually all public getter and setter methods to avoid duplicating the traversal 
     * logic.
     *
     * @param id The unique TMDB film ID to look up
     * @return The matching MovieNode, or null if not found
     */
    private MovieNode getNode(int id) {
        int hashCode = getHash(id);
        
        for (MovieNode node = hashTable[hashCode]; node != null; node = node.next) {
            if (node.id == id) return node;
        }

        return null;
    }


    /**
     * Retrieves the CollectionNode for the given collection ID by performing a linear scan of the collections dynamic array.
     * Used by all collection-related methods to simplify code in those methods. Linear scan is acceptable here because the 
     * number of distinct collections is small relative to the total film count.
     *
     * @param collectionID The unique TMDB collection ID to search for
     * @return The matching CollectionNode, or null if not found
     */
    private CollectionNode getCollection(int collectionID) {
        for (int i = 0; i < collections.size(); i++) {
            CollectionNode collectionNode = collections.get(i);
            if (collectionNode.id == collectionID) return collectionNode;
        }
        return null;
    }


    /** 
     * Adds data about a film to the data structure.
     * The film ID is hashed to find the target bucket. A duplicate check is performed first by calling getNode(int); 
     * if a node with the same ID already exists the addition is rejected (returns false) to maintain uniqueness. Otherwise a
     * new MovieNode is created and inserted at the head of the bucket's linked list (O(1)). Optional metadata (IMDb ID, 
     * popularity, vote data, production companies/countries, collection membership) are not set here; they are provided via
     * the dedicated setter methods after the node is in the hash table.
     * 
     * @param id               The unique ID for the film
     * @param title            The English title of the film
     * @param originalTitle    The original language title of the film
     * @param overview         An overview of the film
     * @param tagline          The tagline for the film (empty string if there is no tagline)
     * @param status           Current status of the film
     * @param genres           An array of Genre objects related to the film
     * @param release          The release date for the film
     * @param budget           The budget of the film in US Dollars
     * @param revenue          The revenue of the film in US Dollars
     * @param languages        An array of ISO 639 language codes for the film
     * @param originalLanguage An ISO 639 language code for the original language of the film
     * @param runtime          The runtime of the film in minutes
     * @param homepage         The URL to the homepage of the film
     * @param adult            Whether the film is an adult film
     * @param video            Whether the film is a "direct-to-video" film
     * @param poster           The unique part of the URL of the poster (empty if the URL is not known)
     * @return TRUE if the data able to be added, FALSE otherwise
     */
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


    /**
     * Removes a film from the data structure, and any data added through this class related to the film
     * The bucket for the film's hash is located, then the linked list is traversed to find the target node while tracking its
     * predecessor. When found, the predecessor's next pointer (or the bucket head) is redirected to bypass the removed node, 
     * which then allows for garbage collection.
     * 
     * @param id The film ID
     * @return TRUE if the film has been removed successfully, FALSE otherwise
     */
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


    /**
     * Gets all the IDs for all films
     * Iterates over every bucket and every node in each chain, collecting IDs into a pre-allocated array of length size. 
     * The order of IDs in the returned array is not defined (it depends on bucket distribution).
     * 
     * @return An array of all film IDs stored
     */
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


    /**
     * Finds the film IDs of all films released within a given range. If a film is released either on the start or end dates,
     * then that film should not be included.
     * The range is exclusive on both ends: films released exactly on the 'start' or exactly on 'end' are not included. Films
     * with no recorded release date (node.release == null) are silently skipped. A large working array of length size is
     * allocated for the worst case, then a correctly-sized array is copied and returned.
     * 
     * @param start The start point of the range of dates
     * @param end   The end point of the range of dates
     * @return An array of film IDs that were released between start and end
     */
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


    /**
     * Gets the title of a particular film, given the ID number of that film.
     * 
     * @param id The movie ID
     * @return The title of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String getTitle(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.title;
    }


    /**
     * Gets the original title of a particular film, given the ID number of that film.
     * This may be equal to getTitle(int), but not always the case.
     * 
     * @param id The movie ID
     * @return The original title of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String getOriginalTitle(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.originalTitle;
    }


    /**
     * Gets the overview of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The overview of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String getOverview(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.overview;
    }


    /**
     * Gets the tagline of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The tagline of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String getTagline(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.tagline;
    }


    /**
     * Gets the status of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The status of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String getStatus(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.status;
    }


    /**
     * Gets the genres of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The genres of the requested film. If the film cannot be found, then return null
     */
    @Override
    public Genre[] getGenres(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.genres;
    }


    /**
     * Gets the release date of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The release date of the requested film. If the film cannot be found, then return null
     */
    @Override
    public LocalDate getRelease(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.release;
    }


    /**
     * Gets the budget of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The budget of the requested film. If the film cannot be found, then return -1
     */
    @Override
    public long getBudget(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1 : node.budget;
    }


    /**
     * Gets the revenue of a particular film, given the ID number of that film.
     * 
     * @param id The movie ID
     * @return The revenue of the requested film. If the film cannot be found, then return -1
     */
    @Override
    public long getRevenue(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1 : node.revenue;
    }


    /**
     * Gets the languages of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The languages of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String[] getLanguages(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.languages;
    }

    
    /**
     * Gets the original language of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The original language of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String getOriginalLanguage(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.originalLanguage;
    }


    /**
     * Gets the runtime of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The runtime of the requested film. If the film cannot be found, then return -1.0d
     */
    @Override
    public double getRuntime(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1.0d : node.runtime;
    }


    /**
     * Gets the homepage of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The homepage of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String getHomepage(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.homepage;
    }


    /**
     * Gets whether a particular film is classed as "adult", given the ID number of that film
     * 
     * @param id The movie ID
     * @return The "adult" status of the requested film. If the film cannot be found, then return false
     */
    @Override
    public boolean getAdult(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? false : node.adult;
    }


    /**
     * Gets weather a particular film is classed as "direct-to-video", given the ID number of that film
     * 
     * @param id The movie ID
     * @return The "direct-to-video" status of the requested film. If the film cannot be found, then return false
     */
    @Override
    public boolean getVideo(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? false : node.video;
    }


    /**
     * Gets the poster URL of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The poster URL of the requested film. If the film cannot be found, then return null
     */
    @Override
    public String getPoster(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.poster;
    }


    /**
     * Sets the average IMDb score and the number of reviews used to generate this score, for a particular film
     * These values reflect the overall public rating on the film at the time the dataset was collected. Both fields are
     * updated on the same node so they remain consistent with one another. If the film already has vote data, the previous
     * values are overwritten.
     * 
     * @param id          The movie ID
     * @param voteAverage The average score on IMDb for the film
     * @param voteCount   The number of reviews on IMDb that were used to generate the average score for the film
     * @return TRUE if the data able to be added, FALSE otherwise
     */
    @Override
    public boolean setVote(int id, double voteAverage, int voteCount) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.voteAverage = voteAverage;
        node.voteCount = voteCount;
        return true;
    }


    /**
     * Gets the average score for IMDb reviews of a particular film, given the ID number of that film
     * 
     * @param id The movie ID
     * @return The average score for IMDb reviews of the requested film. If the film cannot be found, then return -1.0d
     */
    @Override
    public double getVoteAverage(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1.0d : node.voteAverage;
    }


    /**
     * Gets the amount of IMDb reviews used to generate the average score of a particular film, given the ID number of that
     * film.
     * 
     * @param id The movie ID
     * @return The amount of IMDb reviews used to generate the average score of the requested film. If the film cannot be found, then return -1
     */
    @Override
    public int getVoteCount(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1 : node.voteCount;
    }


    /**
     * Adds a given film to a collection. The collection is required to have an ID number, a name, and a URL to a poster for 
     * the collection
     * The method first checks that the film exists. It then updates the film's collectionID field and looks up (or creates)
     * the corresponding CollectionNode. A duplicate-ID guard prevents the same film from being added to the collection's
     * film list more than once.
     * 
     * @param filmID                 The movie ID
     * @param collectionID           The collection ID
     * @param collectionName         The name of the collection
     * @param collectionPosterPath   The URL where the poster can be found
     * @param collectionBackdropPath The URL where the backdrop can be found
     * @return TRUE if the data able to be added, FALSE otherwise
     */
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


    /**
     * Get all films that belong to a given collection
     * 
     * @param collectionID The collection ID to be searched for
     * @return An array of film IDs that correspond to the given collection ID. If there are no films in the collection ID,
     * or if the collection ID is not valid, return an empty array.
     */
    @Override
    public int[] getFilmsInCollection(int collectionID) {
        CollectionNode collectionNode = getCollection(collectionID);
        if (collectionNode == null) return new int[0];
        return collectionNode.filmIDs.toIntArray();
    }


    /**
     * Gets the name of a given collection
     * 
     * @param collectionID The collection ID
     * @return The name of the collection. If the collection cannot be found, then return null
     */
    @Override
    public String getCollectionName(int collectionID) {
        CollectionNode collectionNode = getCollection(collectionID);
        return (collectionNode == null) ? null : collectionNode.name;
    }


    /**
     * Gets the poster URL for a given collection
     * 
     * @param collectionID The collection ID
     * @return The poster URL of the collection. If the collection cannot be found, then return null
     */
    @Override
    public String getCollectionPoster(int collectionID) {
        CollectionNode collectionNode = getCollection(collectionID);
        return (collectionNode == null) ? null : collectionNode.posterPath;
    }


    /**
     * Gets the backdrop URL for a given collection
     * 
     * @param collectionID The collection ID
     * @return The backdrop URL of the collection. If the collection cannot be found, then return null
     */
    @Override
    public String getCollectionBackdrop(int collectionID) {
        CollectionNode collectionNode = getCollection(collectionID);
        return (collectionNode == null) ? null : collectionNode.backdropPath;
    }


    /**
     * Gets the collection ID of a given film
     * 
     * @param filmID The movie ID
     * @return The collection ID for the requested film. If the film cannot be found, then return -1
     */
    @Override
    public int getCollectionID(int filmID) {
        MovieNode node = getNode(filmID);
        return (node == null) ? -1 : node.collectionID;
    }


    /**
     * Sets the IMDb ID for a given film.
     * The IMDb ID is the unique path segment of the film's IMDb URL. No format validation is performed; the caller is 
     * responsible for supplying a correctly formatted value.
     * 
     * @param filmID The movie ID
     * @param imdbID The IMDB ID
     * @return TRUE if the data able to be set, FALSE otherwise
     */
    @Override
    public boolean setIMDB(int id, String imdbID) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.imdbID = imdbID;
        return true;
    }


    /**
     * Gets the IMDb ID for a given film
     * 
     * @param filmID The movie ID
     * @return The IMDb ID for the requested film. If the film cannot be found, return null
     */
    @Override
    public String getIMDB(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? null : node.imdbID;
    }


    /**
     * Sets the popularity of a given film. If the popularity for a film already exists, replace it with the new value 
     * If a popularity value has already been  recorded it is overwritten. The popularity score is always greater than or
     * equal to 0 per the specification; no range validation is performed here.
     * 
     * @param id         The movie ID
     * @param popularity The popularity of the film
     * @return TRUE if the data able to be set, FALSE otherwise
     */
    @Override
    public boolean setPopularity(int id, double popularity) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.popularity = popularity;
        return true;
    }


    /**
     * Gets the popularity of a given film
     * 
     * @param id The movie ID
     * @return The popularity value of the requested film. If the film cannot be found, then return -1.0d. If the popularity
     * has not been set, return 0.0
     */
    @Override
    public double getPopularity(int id) {
        MovieNode node = getNode(id);
        return (node == null) ? -1.0d : node.popularity;
    }


    /**
     * Adds a production company to a given film.
     * Uses the MyDynamicArray stored on the node so that companies can be added one at a time as the data loader processes
     * them. No duplicate checking is performed; callers are assumed to supply valid, non-duplicate Company objects.
     * 
     * @param id      The movie ID
     * @param company A Company object that represents the details on a production company
     * @return TRUE if the data able to be added, FALSE otherwise
     */
    @Override
    public boolean addProductionCompany(int id, Company company) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.companies.add(company);
        return true;
    }


    /**
     * Adds a production country to a given film's country list.
     * Uses the MyDynamicArray stored on the node so that countries can be added one at a time as the data loader processes
     * them. No duplicate checking is performed; callers are assumed to supply valid, non-duplicate Country objects.
     * 
     * @param id      The movie ID
     * @param country A ISO 3166 string containing the 2-character country code
     * @return TRUE if the data able to be added, FALSE otherwise
     */
    @Override
    public boolean addProductionCountry(int id, String country) {
        MovieNode node = getNode(id);
        if (node == null) return false;
        node.countries.add(country);
        return true;
    }


    /**
     * Gets all the production companies for a given film
     * Converts the node's internal MyDynamicArray into a plain Company[] to satisfy the method signature and to avoid
     * exposing the internal MyDynamicArray structure to callers.
     * 
     * @param id The movie ID
     * @return An array of Company objects that represent all the production companies that worked on the requested film. If
     * the film cannot be found, then return null
     */
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


    /**
     * Gets all the production countries for a given film.
     * Converts the node's internal MyDynamicArray into a plain String[] for the same reasons as getProductionCompanies(int).
     * 
     * @param id The movie ID
     * @return An array of Strings that represent all the production countries (in ISO 3166 format) that worked on the requested
     * film. If the film cannot be found, then return null
     */
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


    /**
     * States the number of movies stored in the data structure
     * 
     * @return The number of movies stored in the data structure
     */
    @Override
    public int size() {
        return size;
    }


    /**
     * Produces a list of movie IDs that have the search term in their title, original title or their overview.
     * The search term is converted to lower-case once before the scan to avoid repeated case conversion during comparisons.
     * A large working array (worst-case all films match) is allocated, then trimmed to the actual number of matches before
     * returning.
     * 
     * @param searchTerm The term that needs to be checked
     * @return An array of movie IDs that have the search term in their title, original title or their overview. If no movies
     * have this search term, then an empty array should be returned.
     */
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
