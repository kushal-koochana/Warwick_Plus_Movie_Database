package stores;

import java.time.LocalDateTime;

import interfaces.IRatings;
import structures.*;

public class Ratings implements IRatings {
    Stores stores;
    private RatingNode[] userHashTable;
    private RatingNode[] movieHashTable;
    private int capacity = 1999;
    private int size = 0;

    public Ratings(Stores stores) {
        this.stores = stores;
        userHashTable = new RatingNode[capacity];
        movieHashTable = new RatingNode[capacity];
    }

    private int getUserHash(int userID) {
        return Math.abs(userID) % capacity;
    }

    private int getMovieHash(int movieID) {
        return Math.abs(movieID) % capacity;
    }

    private RatingNode getRatingNode(int userID, int movieID) {
        int userHashCode = getUserHash(userID);

        for(RatingNode ratingNode = userHashTable[userHashCode]; ratingNode != null; ratingNode = ratingNode.nextUser) {
            if (ratingNode.userID == userID && ratingNode.movieID == movieID) {
                return ratingNode;
            }
        }
        return null;
    }

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

    @Override
    public int size() {
        return size;
    }
}
