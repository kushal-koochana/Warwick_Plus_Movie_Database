package stores;

import java.time.LocalDateTime;

import interfaces.IRatings;
import structures.*;

public class Ratings implements IRatings {
    // reference to stores used by Stores.java, used in cross-store existence checks e.g. getMovieAverageRating()
    Stores stores;
    // Hash table keyed on userID. Each slot holds the head of a singly-linked list of RatingNode objects linked via nextUser. 
    // Enables O(1) average retrieval of all ratings for a specific user.
    private RatingNode[] userHashTable;
    // Hash table keyed on {@code movieID}. Each slot holds the head of a singly-linked list of RatingNode objects linked via
    // nextMovie. Enables O(1) average retrieval of all ratings for a specific film.
    private RatingNode[] movieHashTable;
    // fixed number of buckets in hash table, used prime number (1999) to improve key distribution and reduce collisions.
    private int capacity = 1999;
    // number of ratings stored
    private int size = 0;


    /**
     * Initialises the Ratings store by allocating both hash table backing arrays. All slots default to null (empty buckets).
     * Ratings are added incrementally via add().
     * 
     * @param stores An object storing all the different key stores, including itself
     */
    public Ratings(Stores stores) {
        this.stores = stores;
        userHashTable = new RatingNode[capacity];
        movieHashTable = new RatingNode[capacity];
    }


    /**
     * Computes the bucket index in userHashTable for the given user ID.
     *
     * @param userID The unique user identifier (always > 0 per the spec)
     * @return A valid index in hashtable from [0, capacity)
     */
    private int getUserHash(int userID) {
        return Math.abs(userID) % capacity;
    }

    
    /**
     * Computes the bucket index in movieHashTable for the given film ID.
     *
     * @param movieID The unique TMDB film identifier
     * @return A valid index in hashtable from [0, capacity)
     */
    private int getMovieHash(int movieID) {
        return Math.abs(movieID) % capacity;
    }


    /**
     * Retrieves the RatingNode for the given (userID, movieID) pair by searching the user hash table's bucket chain.
     * Used as the central DRY lookup helper: checking both userID and movieID on each node ensures the correct rating is
     * returned even when multiple users share the same hash bucket.
     *
     * @param userID  The user who submitted the rating
     * @param movieID The film that was rated
     * @return The matching RatingNode, or null if not found
     */
    private RatingNode getRatingNode(int userID, int movieID) {
        int userHashCode = getUserHash(userID);

        for(RatingNode ratingNode = userHashTable[userHashCode]; ratingNode != null; ratingNode = ratingNode.nextUser) {
            if (ratingNode.userID == userID && ratingNode.movieID == movieID) {
                return ratingNode;
            }
        }
        return null;
    }


    /**
     * Removes the given RatingNode from the movie hash table's bucket chain.
     * Called as a sub-step of remove(int, int) after the node has already been unlinked from the user hash table. Compares
     * by object identity ( == ) rather than by value because the same physical node object is stored in both tables.
     *
     * @param targetRatingNode The exact node object to remove from the movie table
     */
    private void removeFromMovieHashTable(RatingNode targetRatingNode) {
        int movieHashCode = getMovieHash(targetRatingNode.movieID);
        RatingNode prevRatingNode = null;

        for (RatingNode ratingNode = movieHashTable[movieHashCode]; ratingNode != null; ratingNode = ratingNode.nextMovie) {
            if (ratingNode == targetRatingNode) {
                if (prevRatingNode == null) {
                    movieHashTable[movieHashCode] = ratingNode.nextMovie;
                } else {
                    prevRatingNode.nextMovie = ratingNode.nextMovie;
                }
                return;
            }
            prevRatingNode = ratingNode;
        }
    }


    /**
     * Adds a rating to the data structure. The rating is made unique by its user ID and its movie ID
     * A (userID, movieID) pair must be unique; duplicate entries are rejected via getRatingNode. The new node is inserted
     * at the head of the relevant bucket chain in both tables (O(1)), connecting up nextUser and nextMovie before replacing the respective
     * heads.
     * 
     * @param userID    The user ID
     * @param movieID   The movie ID
     * @param rating    The rating gave to the film by this user (between 0 and 5 inclusive)
     * @param timestamp The time at which the rating was made
     * @return TRUE if the data able to be added, FALSE otherwise
     */
    @Override
    public boolean add(int userID, int movieID, float rating, LocalDateTime timestamp) {
        if (getRatingNode(userID, movieID) != null) return false;

        RatingNode ratingNode = new RatingNode(userID, movieID, rating, timestamp);
        int userHashCode = getUserHash(userID);
        int movieHashCode = getMovieHash(movieID);

        ratingNode.nextUser = userHashTable[userHashCode];
        userHashTable[userHashCode] = ratingNode;
        ratingNode.nextMovie = movieHashTable[movieHashCode];
        movieHashTable[movieHashCode] = ratingNode;

        size++;
        return true;
    }


    /**
     * Removes a given rating, using the user ID and the movie ID as the unique identifier
     * The user table is searched first; once the target node is found and unlinked from the user chain, 
     * removeFromMovieHashTable(RatingNode) removes it from the movie chain using the same object reference.
     * 
     * @param userID  The user ID
     * @param movieID The movie ID
     * @return TRUE if the data was removed successfully, FALSE otherwise
     */
    @Override
    public boolean remove(int userID, int movieID) {
        int userHashCode = getUserHash(userID);
        
        RatingNode prevRatingNode = null;

        for (RatingNode ratingNode = userHashTable[userHashCode]; ratingNode != null; ratingNode = ratingNode.nextUser) {
            if (ratingNode.userID == userID && ratingNode.movieID == movieID) {
                if (prevRatingNode == null) {
                    userHashTable[userHashCode] = ratingNode.nextUser;
                } else {
                    prevRatingNode.nextUser = ratingNode.nextUser;
                }
                removeFromMovieHashTable(ratingNode);
                size--;
                return true;
            }
            prevRatingNode = ratingNode;
        }
        return false;
    }


    /**
     * Sets a rating for a given user ID and movie ID. Therefore, should the given user have already rated the given movie,
     * the new data should overwrite the existing rating. However, if the given user has not already rated the given movie,
     * then this rating should be added to the data structure
     * If the rating already exists, only the rating and timestamp fields on the existing node are updated (O(1) after lookup),
     * avoiding a remove-then-insert cycle that would require two hash table modifications.
     * 
     * @param userID    The user ID
     * @param movieID   The movie ID
     * @param rating    The new rating to be given to the film by this user (between 0 and 5 inclusive)
     * @param timestamp The time at which the new rating was made
     * @return TRUE if the data able to be added/updated, FALSE otherwise
     */
    @Override
    public boolean set(int userID, int movieID, float rating, LocalDateTime timestamp) {
        RatingNode ratingNode = getRatingNode(userID, movieID);
        if (ratingNode != null) {
            ratingNode.rating = rating;
            ratingNode.timestamp = timestamp;
            return true;
        }
        return add(userID, movieID, rating, timestamp);
    }



    /**
     * Get all the ratings for a given film
     * The movie hash table is used for an O(chain-length) scan. Two passes are made: the first counts matching entries to 
     * allocate an exactly-sized array, and the second populates it. This avoids the overhead of a dynamic array when the 
     * result size is known after the count pass.
     * 
     * @param movieID The movie ID
     * @return An array of ratings. If there are no ratings or the film cannot be found in Ratings, then return an empty array
     */
    @Override
    public float[] getMovieRatings(int movieID) {
        int arraySize = 0;
        int movieHashCode = getMovieHash(movieID);

        for (RatingNode ratingNode = movieHashTable[movieHashCode]; ratingNode != null; ratingNode = ratingNode.nextMovie) {
            if (ratingNode.movieID == movieID) arraySize++;
        }
        
        float[] movieRatingsArray = new float[arraySize];
        int i = 0;
        for (RatingNode ratingNode = movieHashTable[movieHashCode]; ratingNode != null; ratingNode = ratingNode.nextMovie) {
            if (ratingNode.movieID == movieID) movieRatingsArray[i++] = ratingNode.rating;
        }
        return movieRatingsArray;
    }


    /**
     * Get all the ratings for a given user
     * Uses the user hash table for an efficient bucket-scoped scan. Same two-pass approach as getMovieRatings(int).
     * 
     * @param userID The user ID
     * @return An array of ratings. If there are no ratings or the user cannot be found in Ratings, then return an empty array
     */
    @Override
    public float[] getUserRatings(int userID) {
        int arraySize = 0;
        int userHashCode = getUserHash(userID);

        for (RatingNode ratingNode = userHashTable[userHashCode]; ratingNode != null; ratingNode = ratingNode.nextUser) {
            if (ratingNode.userID == userID) arraySize++;
        }
        
        float[] userRatingsArray = new float[arraySize];
        int i = 0;
        for (RatingNode ratingNode = userHashTable[userHashCode]; ratingNode != null; ratingNode = ratingNode.nextUser) {
            if (ratingNode.userID == userID) userRatingsArray[i++] = ratingNode.rating;
        }
        return userRatingsArray;
    }


    /**
     * Get the average rating for a given film
     * Three cases are distinguished, as required by the interface contract:
     * * The film has at least one rating → return the arithmetic mean.
     * * No ratings exist but the film is in the Movies store → return 0.0f
     * * Neither condition holds → return -1.0f (film unknown).
     * The movie hash table is used to confine the scan to a single bucket, keeping the operation O(chain-length) rather than
     * O(total ratings).
     * 
     * @param movieID The movie ID
     * @return Produces the average rating for a given film. If the film cannot be found in Ratings, but does exist in the 
     * Movies store, return 0.0f.  If the film cannot be found in Ratings or Movies stores, return -1.0f.
     */
    @Override
    public float getMovieAverageRating(int movieID) {
        int movieHashCode = getMovieHash(movieID);
        float sum = 0;
        int numberOfMovieRatings = 0;

        for (RatingNode ratingNode = movieHashTable[movieHashCode]; ratingNode != null; ratingNode = ratingNode.nextMovie) {
            if (ratingNode.movieID == movieID) {
                sum += ratingNode.rating;
                numberOfMovieRatings++;
            }
        }
        
        if (numberOfMovieRatings > 0) {
            return sum / numberOfMovieRatings;
        }

        if (stores.getMovies().getTitle(movieID) != null) return 0.0f;
        return -1.0f;
    }


    /**
     * Get the average rating for a given user
     * The user hash table is used for a bucket-scoped scan. If the user has submitted no ratings (or does not exist) -1.0f 
     * is returned.
     * 
     * @param userID The user ID
     * @return Produces the average rating for a given user. If the user cannot be found in Ratings, or there are no rating,
     * return -1.0f
     */
    @Override
    public float getUserAverageRating(int userID) {
        int userHashCode = getUserHash(userID);
        float sum = 0;
        int numberOfUserRatings = 0;
        
        for (RatingNode ratingNode = userHashTable[userHashCode]; ratingNode != null; ratingNode = ratingNode.nextUser) {
            if (ratingNode.userID == userID) {
                sum += ratingNode.rating;
                numberOfUserRatings++;
            }
        }
        if (numberOfUserRatings == 0) return -1.0f;
        return sum / numberOfUserRatings;
    }


    /**
     * Gets the top N movies with the most ratings, in order from most to least
     * A single pass over the movie hash table builds two parallel MyDynamicArray(s): one of film IDs and one of rating counts.
     * The arrays are then sorted together in descending order by count via selection sort, and the top 'num' IDs are returned.
     * 
     * @param num The number of movies that should be returned
     * @return A sorted array of movie IDs with the most ratings. The array should be no larger than num. If there are less
     * than num movies in the store, then the array should be the same length as the number of movies in Ratings.
     */
    @Override
    public int[] getMostRatedMovies(int num) {
        MyDynamicArray<Integer> movieIDs = new MyDynamicArray<>();
        MyDynamicArray<Integer> ratingsCount = new MyDynamicArray<>();

        for (int i = 0; i < capacity; i++) {
            for(RatingNode ratingNode = movieHashTable[i]; ratingNode != null; ratingNode = ratingNode.nextMovie) {
                int movieID = ratingNode.movieID;
                int index = movieIDs.indexOf(movieID);

                if (index == -1) {
                    movieIDs.add(movieID);
                    ratingsCount.add(1);
                } else {
                    ratingsCount.set(index, ratingsCount.get(index) + 1);
                }
            }
        }
        
        for (int i = 0; i < ratingsCount.size(); i++) {
            int maxIndex = i;
            for (int j = i + 1; j < ratingsCount.size(); j++) {
                if (ratingsCount.get(j) > ratingsCount.get(maxIndex)) maxIndex = j;
            }

            int tempCount = ratingsCount.get(i);
            ratingsCount.set(i, ratingsCount.get(maxIndex));
            ratingsCount.set(maxIndex, tempCount);

            int tempID = movieIDs.get(i);
            movieIDs.set(i, movieIDs.get(maxIndex));
            movieIDs.set(maxIndex, tempID);
        }

        int arraySize = Math.min(num, movieIDs.size());
        int[] mostRatedMoviesArray = new int[arraySize];

        for (int i = 0; i < arraySize; i++) {
            mostRatedMoviesArray[i] = movieIDs.get(i);
        }
        return mostRatedMoviesArray;
    }


    /**
     * Gets the top N users with the most ratings, in order from most to least
     * Uses the same parallel-array and selection-sort approach as getMostRatedMovies(int), but iterates over the user hash 
     * table and counts per user instead of per film.
     * 
     * @param num The number of users that should be returned
     * @return A sorted array of user IDs with the most ratings. The array should be no larger than num. If there are less 
     * than num users in the store, then the array should be the same length as the number of users in Ratings.
     */
    @Override
    public int[] getMostRatedUsers(int num) {
        MyDynamicArray<Integer> userIDs = new MyDynamicArray<>();
        MyDynamicArray<Integer> ratingsCount = new MyDynamicArray<>();

        for (int i = 0; i < capacity; i++) {
            for(RatingNode ratingNode = userHashTable[i]; ratingNode != null; ratingNode = ratingNode.nextUser) {
                int userID = ratingNode.userID;
                int index = userIDs.indexOf(userID);

                if (index == -1) {
                    userIDs.add(userID);
                    ratingsCount.add(1);
                } else {
                    ratingsCount.set(index, ratingsCount.get(index) + 1);
                }
            }
        }
        
        for (int i = 0; i < ratingsCount.size(); i++) {
            int maxIndex = i;
            for (int j = i + 1; j < ratingsCount.size(); j++) {
                if (ratingsCount.get(j) > ratingsCount.get(maxIndex)) maxIndex = j;
            }

            int tempCount = ratingsCount.get(i);
            ratingsCount.set(i, ratingsCount.get(maxIndex));
            ratingsCount.set(maxIndex, tempCount);

            int tempID = userIDs.get(i);
            userIDs.set(i, userIDs.get(maxIndex));
            userIDs.set(maxIndex, tempID);
        }

        int arraySize = Math.min(num, userIDs.size());
        int[] mostRatedUsersArray = new int[arraySize];

        for (int i = 0; i < arraySize; i++) {
            mostRatedUsersArray[i] = userIDs.get(i);
        }
        return mostRatedUsersArray;
    }


    /**
     * Get the highest average rated film IDs, in order of there average rating (highest first).
     * Three parallel MyDynamicArray(s) are maintained: film IDs, running sums, and rating counts. After the single 
     * accumulation pass over the movie hash table, averages are computed into a fourth array, which is then sorted together
     * with the ID array via selection sort. The approach avoids computing the average on every comparison (which would 
     * trigger repeated division) by pre-computing all averages once.
     * 
     * @param numResults The maximum number of results to be returned
     * @return An array of the film IDs with the highest average ratings, highest first. If there are less than num movies in the store, then the array should be the same length as the number of movies in Ratings
     */
    @Override
    public int[] getTopAverageRatedMovies(int numResults) {
        MyDynamicArray<Integer> movieIDs = new MyDynamicArray<>();
        MyDynamicArray<Float> sumsOfMovieRatings = new MyDynamicArray<>();
        MyDynamicArray<Integer> numberOfUserRatings = new MyDynamicArray<>();

        for (int i = 0; i < capacity; i++) {
            for (RatingNode ratingNode = movieHashTable[i]; ratingNode != null; ratingNode = ratingNode.nextMovie) {
                int movieID = ratingNode.movieID;
                int index = movieIDs.indexOf(movieID);

                if (index == -1) {
                    movieIDs.add(movieID);
                    sumsOfMovieRatings.add(ratingNode.rating);
                    numberOfUserRatings.add(1);
                } else {
                    sumsOfMovieRatings.set(index, sumsOfMovieRatings.get(index) + ratingNode.rating);
                    numberOfUserRatings.set(index, numberOfUserRatings.get(index) + 1);
                }
            }
        }

        MyDynamicArray<Float> ratingsAverages = new MyDynamicArray<>();
        for (int i = 0; i < movieIDs.size(); i++) {
            ratingsAverages.add(sumsOfMovieRatings.get(i)/ numberOfUserRatings.get(i));
        }

        for (int i = 0; i < ratingsAverages.size(); i++) {
            int maxIndex = i;

            for (int j = i + 1; j < ratingsAverages.size(); j++) {
                if (ratingsAverages.get(j) > ratingsAverages.get(maxIndex)) maxIndex = j;
            }

            float tempAvg = ratingsAverages.get(i);
            ratingsAverages.set(i, ratingsAverages.get(maxIndex));
            ratingsAverages.set(maxIndex, tempAvg);

            int tempID = movieIDs.get(i);
            movieIDs.set(i, movieIDs.get(maxIndex));
            movieIDs.set(maxIndex, tempID);
        }

        int arraySize = Math.min(numResults, movieIDs.size());
        int[] topAverageRatingMoviesArray = new int[arraySize];

        for (int i = 0; i < arraySize; i++) {
            topAverageRatingMoviesArray[i] = movieIDs.get(i);
        }
        return topAverageRatingMoviesArray;
    }


    /**
     * Get the number of ratings that a movie has
     * Three cases match the method signature/design:
     * * Ratings exist for the film → return the count
     * * No ratings but the film exists in Movies → return 0
     * * Film unknown in both stores → return -1
     * 
     * @param movieid The movie id to be found
     * @return The number of ratings the specified movie has. If the movie exists in the Movies store, but there are no ratings
     * for it, then return 0. If the movie does not exist in the Ratings or Movies store, then return -1.
     */
    @Override
    public int getNumRatings(int movieID) {
        int movieHashCode = getMovieHash(movieID);
        int numberOfMovieRatings = 0;

        for (RatingNode ratingNode = movieHashTable[movieHashCode]; ratingNode != null; ratingNode = ratingNode.nextMovie) {
            if (ratingNode.movieID == movieID) numberOfMovieRatings++;
        }

        if (numberOfMovieRatings > 0) return numberOfMovieRatings;
        if (stores.getMovies().getTitle(movieID) != null) return 0;
        return -1;
    }

    /**
     * Gets the number of ratings in the data structure
     * 
     * @return The number of ratings in the data structure
     */
    @Override
    public int size() {
        return size;
    }
}
