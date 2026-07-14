package stores;

import structures.*;

import interfaces.ICredits;

public class Credits implements ICredits{
    // reference to stores used by Stores.java
    Stores stores;
    // Hash table array of certain capacity, each slot holds head of single linked list of CreditNode objects (separate chaining).
    // A slot is null when no film has hashed to that bucket.
    // film ID -> (Cast + Crew)
    private CreditNode[] creditHashTable;
    // fixed number of buckets in hash table, used prime number (1999) to improve key distribution and reduce collisions.
    private int capacity = 1999;
    // number of movies stored across the whole hash table
    private int size = 0;


    /**
     * Initialises the Credits store by allocating the hash table backing array. All slots default to null (empty buckets). 
     * Credits are added incrementally via add().
     *
     * @param stores An object storing all the different key stores, including itself
     */
    public Credits (Stores stores) {
        this.stores = stores;
        creditHashTable = new CreditNode[capacity];
    }


    /**
     * Computes the bucket index for the given film ID. Uses abs(filmID) % capacity to map potentially negative IDs to a 
     * valid bucket index in hashtable from [0, capacity).
     *
     * @param filmID The unique TMDB film ID
     * @return A valid bucket index for creditHashTable
     */
    private int getCreditHash(int filmID) {
        return Math.abs(filmID) % capacity;
    }


    /**
     * Retrieves the CreditNode for the given film ID by traversing its bucket's linked list.
     * Central lookup helper used by all public methods that need to find a node by film ID (DRY principle). Returns null if
     * no entry for the film exists, which callers interpret as "not found".
     *
     * @param filmID The unique TMDB film ID to look up
     * @return The matching CreditNode, or null if not found
     */
    private CreditNode getCreditNode(int filmID) {
        int creditHashCode = getCreditHash(filmID);

        for (CreditNode creditNode = creditHashTable[creditHashCode]; creditNode != null; creditNode = creditNode.nextCredit) {
            if (creditNode.filmID == filmID) return creditNode;
        }
        return null;
    }


    /**
     * Adds data about the people who worked on a given film. The movie ID should be unique.
     * A duplicate check is performed via getCreditNode(int) before insertion; if a node already exists for this film ID 
     * the addition is rejected. The new node (if accepted) is inserted at the head of its bucket's linked list (O(1)) by 
     * pointing it at the current head before replacing it.
     * 
     * @param cast An array of all cast members that starred in the given film
     * @param crew An array of all crew members that worked on a given film
     * @param id   The (unique) movie ID
     * @return TRUE if the data able to be added, FALSE otherwise
     */
    @Override
    public boolean add(CastCredit[] cast, CrewCredit[] crew, int filmID) {
        if (getCreditNode(filmID) != null) return false;

        CreditNode creditNode = new CreditNode(filmID, cast, crew);

        int creditHashCode = getCreditHash(filmID);
        creditNode.nextCredit = creditHashTable[creditHashCode];
        creditHashTable[creditHashCode] = creditNode;
        size++;
        return true;
    }


    /**
     * Remove a given films data from the data structure
     * The bucket is located via the hash, then the linked list is traversed while tracking the predecessor node. When the
     * target is found its predecessor (or the bucket head) is relinked to skip over it.
     * 
     * @param id The movie ID
     * @return TRUE if the data was removed, FALSE otherwise
     */
    @Override
    public boolean remove(int filmID) {
        int creditHashCode = getCreditHash(filmID);

        CreditNode prevCreditNode = null;

        for (CreditNode creditNode = creditHashTable[creditHashCode]; creditNode != null; creditNode = creditNode.nextCredit) {
            if (creditNode.filmID == filmID) {
                if (prevCreditNode == null) {
                    creditHashTable[creditHashCode] = creditNode.nextCredit;
                } else {
                    prevCreditNode.nextCredit = creditNode.nextCredit;
                }
                size--;
                return true;
            }
            prevCreditNode = creditNode;
        }
        return false;
    }


    /**
     * Gets all the cast members for a given film
     * A defensive copy of the node's cast array is made before sorting so the stored data will never be mutated. Selection
     * sort is used for simplicity; cast arrays are typically small (tens of members), so O(n²) is acceptable.
     * 
     * @param filmID The movie ID
     * @return An array of CastCredit objects, one for each member of cast that is in the given film. The cast members should
     * be in "order" order. If there is no cast members attached to a film, or the film cannot be found in Credits, then return
     * an empty array
     */
    @Override
    public CastCredit[] getFilmCast(int filmID) {
        CreditNode creditNode = getCreditNode(filmID);

        if (creditNode == null || creditNode.cast == null || creditNode.cast.length == 0) {
            return new CastCredit[0];
        }

        CastCredit[] orderedFilmCastArray = new CastCredit[creditNode.cast.length];
        for (int i = 0; i < creditNode.cast.length; i++) {
            orderedFilmCastArray[i] = creditNode.cast[i];
        }

        for (int i = 0; i < orderedFilmCastArray.length; i++) {
            int minIndex = i;

            for (int j = i + 1; j < orderedFilmCastArray.length; j++) {
                if (orderedFilmCastArray[j].getOrder() < orderedFilmCastArray[minIndex].getOrder()) {
                    minIndex = j;
                }
            }

            CastCredit temp = orderedFilmCastArray[i];
            orderedFilmCastArray[i] = orderedFilmCastArray[minIndex];
            orderedFilmCastArray[minIndex] = temp;
        }

        return orderedFilmCastArray;
    }


    /**
     * Gets all the crew members for a given film
     * A defensive copy is made before sorting for the same reasons as getFilmCast(int). Selection sort is used for simplicity.
     * 
     * @param filmID The movie ID
     * @return An array of CrewCredit objects, one for each member of crew that is in the given film. The crew members should
     * be in "id" order (not "elementID"). If there is no crew members attached to a film, or the film cannot be found in Credits,
     * then return an empty array
     */
    @Override
    public CrewCredit[] getFilmCrew(int filmID) {
        CreditNode creditNode = getCreditNode(filmID);

        if (creditNode == null || creditNode.crew == null || creditNode.crew.length == 0) {
            return new CrewCredit[0];
        }

        // Copy array so we don't modify original
        CrewCredit[] orderedFilmCrewArray = new CrewCredit[creditNode.crew.length];
        for (int i = 0; i < creditNode.crew.length; i++) {
            orderedFilmCrewArray[i] = creditNode.crew[i];
        }

        // Selection sort by order (ascending)
        for (int i = 0; i < orderedFilmCrewArray.length; i++) {
            int minIndex = i;

            for (int j = i + 1; j < orderedFilmCrewArray.length; j++) {
                if (orderedFilmCrewArray[j].getID() < orderedFilmCrewArray[minIndex].getID()) minIndex = j;
            }

            // swap
            CrewCredit temp = orderedFilmCrewArray[i];
            orderedFilmCrewArray[i] = orderedFilmCrewArray[minIndex];
            orderedFilmCrewArray[minIndex] = temp;
        }

        return orderedFilmCrewArray;
    }


    /**
     * Gets the number of cast that worked on a given film
     * 
     * @param filmID The movie ID
     * @return The number of cast member that worked on a given film. If the film cannot be found in Credits, then return -1
     */
    @Override
    public int sizeOfCast(int filmID) {
        CreditNode creditNode = getCreditNode(filmID);
        if (creditNode == null) return -1;
        return (creditNode.cast == null) ? 0 : creditNode.cast.length;
    }


    /**
     * Gets the number of crew that worked on a given film
     * 
     * @param filmID The movie ID
     * @return The number of crew member that worked on a given film. If the film cannot be found in Credits, then return -1
     */
    @Override
    public int sizeOfCrew(int filmID) {
        CreditNode creditNode = getCreditNode(filmID);
        if (creditNode == null) return -1;
        return (creditNode.crew == null) ? 0 : creditNode.crew.length;
    }


    /**
     * Gets a list of all unique cast members present in the data structure
     * Iterates over the entire hash table and all bucket chains, collecting cast entries. A separate MyDynamicArray of 
     * already-seen person IDs is used to deduplicate: a person is added only if their ID has not been encountered before. A
     * corresponding Person object is constructed from the first occurrence of each unique ID.
     * Note: a person may appear under different names (e.g. a name change or alias) across films. Only the first-encountered
     * name is retained here.
     * 
     * @return An array of all unique cast members as Person objects. If there are no cast members, then return an empty array
     */
    @Override
    public Person[] getUniqueCast() {
        MyDynamicArray<Integer> seenCastIDs = new MyDynamicArray<>();
        MyDynamicArray<Person> allUniqueCast = new MyDynamicArray<>();

        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.cast == null) continue;
                for (CastCredit castCredit: creditNode.cast) {
                    if (seenCastIDs.indexOf(castCredit.getID()) == -1) {
                        seenCastIDs.add(castCredit.getID());
                        allUniqueCast.add(new Person(castCredit.getID(), castCredit.getName(), castCredit.getProfilePath()));
                    }
                }
            }
        }

        Person[] allUniqueCastArray = new Person[allUniqueCast.size()];
        for (int i = 0; i < allUniqueCast.size(); i++) {
            allUniqueCastArray[i] = allUniqueCast.get(i);
        }
        return allUniqueCastArray;
    }


    /**
     * Gets a list of all unique crew members present in the data structure
     * Uses the same deduplication strategy as getUniqueCast(): a seen-IDs list guards against the same person being added
     * twice.
     * 
     * @return An array of all unique crew members as Person objects. If there are no crew members, then return an empty array
     */
    @Override
    public Person[] getUniqueCrew() {
        MyDynamicArray<Integer> seenCrewIDs = new MyDynamicArray<>();
        MyDynamicArray<Person> allUniqueCrew = new MyDynamicArray<>();

        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.crew == null) continue;
                for (CrewCredit crewCredit: creditNode.crew) {
                    if (seenCrewIDs.indexOf(crewCredit.getID()) == -1) {
                        seenCrewIDs.add(crewCredit.getID());
                        allUniqueCrew.add(new Person(crewCredit.getID(), crewCredit.getName(), crewCredit.getProfilePath()));
                    }
                }
            }
        }

        Person[] allUniqueCrewArray = new Person[allUniqueCrew.size()];
        for (int i = 0; i < allUniqueCrew.size(); i++) {
            allUniqueCrewArray[i] = allUniqueCrew.get(i);
        }
        return allUniqueCrewArray;
    }


    /**
     * Get all the cast members that have the given string within their name
     * The search string is lower-cased once before the scan to avoid repeated conversion during comparisons. Deduplication
     * is performed via the same seen-IDs strategy used in getUniqueCast().
     * 
     * @param cast The string that needs to be found
     * @return An array of unique Person objects of all cast members that have the requested string in their name. If there are no matches, return an empty array
     */
    @Override
    public Person[] findCast(String cast) {
        if (cast == null) return new Person[0];
        cast = cast.toLowerCase();

        MyDynamicArray<Integer> seenCastIDs = new MyDynamicArray<>();
        MyDynamicArray<Person> foundCast = new MyDynamicArray<>();


        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.cast == null) continue;

                for (CastCredit castCredit : creditNode.cast) {
                    if (castCredit.getName() != null && castCredit.getName().toLowerCase().contains(cast)) {

                        if (seenCastIDs.indexOf(castCredit.getID()) == -1) {
                            seenCastIDs.add(castCredit.getID());
                            foundCast.add(new Person(castCredit.getID(), castCredit.getName(), castCredit.getProfilePath()));
                        }
                    }
                }
            }
        }

        Person[] foundCastArray = new Person[foundCast.size()];
        for (int i = 0; i < foundCast.size(); i++) {
            foundCastArray[i] = foundCast.get(i);
        }
        return foundCastArray;
    }


    /**
     * Get all the crew members that have the given string within their name
     * Uses the same approach as findCast(String): single lower-case normalisation followed by a full-table scan with
     * deduplication by person ID.
     * 
     * @param crew The string that needs to be found
     * @return An array of unique Person objects of all crew members that have the requested string in their name. If there are no matches, return an empty array
     */
    @Override
    public Person[] findCrew(String crew) {
        if (crew == null) return new Person[0];
        crew = crew.toLowerCase();

        MyDynamicArray<Integer> seenCrewIDs = new MyDynamicArray<>();
        MyDynamicArray<Person> foundCrew = new MyDynamicArray<>();

        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.crew == null) continue;

                for (CrewCredit crewCredit : creditNode.crew) {
                    if (crewCredit.getName() != null && crewCredit.getName().toLowerCase().contains(crew)) {
                        if (seenCrewIDs.indexOf(crewCredit.getID()) == -1) {
                            seenCrewIDs.add(crewCredit.getID());
                            foundCrew.add(new Person(crewCredit.getID(), crewCredit.getName(), crewCredit.getProfilePath()));
                        }
                    }
                }
            }
        }

        Person[] foundCrewArray = new Person[foundCrew.size()];
        for (int i = 0; i < foundCrew.size(); i++) {
            foundCrewArray[i] = foundCrew.get(i);
        }
        return foundCrewArray;
    }


    /**
     * Gets the Person object corresponding to the cast ID
     * Performs a full table scan because no secondary index by person ID is maintained. Returns the first matching entry
     * found; if the same person appears under multiple names only one is returned.
     * 
     * @param castID The cast ID of the person to be found
     * @return The Person object corresponding to the cast ID provided. If a person cannot be found, then return null
     */
    @Override
    public Person getCast(int castID) {
        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.cast == null) continue;
                for (CastCredit castCredit : creditNode.cast) {
                    if(castCredit.getID() == castID) {
                        return new Person(castCredit.getID(), castCredit.getName(), castCredit.getProfilePath());
                    }
                }
            }
        }
        return null;
    }


    /**
     * Gets the Person object corresponding to the crew ID
     * Performs a full table scan for the same reasons as getCast(int). Returns the first matching entry found.
     * 
     * @param crewID The crew ID of the person to be found
     * @return The Person object corresponding to the crew ID provided. If a person cannot be found, then return null
     */
    @Override
    public Person getCrew(int crewID){
        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.crew == null) continue;
                for (CrewCredit crewCredit : creditNode.crew) {
                    if(crewCredit.getID() == crewID) {
                        return new Person(crewCredit.getID(), crewCredit.getName(), crewCredit.getProfilePath());
                    }
                }
            }
        }
        return null;
    }

    
    /**
     * Get an array of film IDs where the cast member has starred in
     * Iterates over every film's credit node. When the cast member's ID is found within a film's cast array, the film ID 
     * is recorded and the inner loop breaks early (a person is counted at most once per film egardless of how many roles
     * they played, since we only need the film ID).
     * 
     * @param castID The cast ID of the person
     * @return An array of all the films the member of cast has starred in. If there are no films attached to the cast member, then return an empty array
     */
    @Override
    public int[] getCastFilms(int castID){
        MyDynamicArray<Integer> allCastMemberFilms = new MyDynamicArray<>();
        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.cast == null) continue;

                for (CastCredit castCredit : creditNode.cast) {
                    if (castCredit.getID() == castID) {
                        allCastMemberFilms.add(creditNode.filmID);
                        break;
                    }
                }
            }
        }
        return allCastMemberFilms.toIntArray();
    }


    /**
     * Get an array of film IDs where the crew member has starred in
     * Uses the same approach as getCastFilms(int): scan all credit nodes, add the film ID on the first match per film, then
     * break.
     * 
     * @param crewID The crew ID of the person
     * @return An array of all the films the member of crew has starred in. If there are no films attached to the crew member, then return an empty array
     */
    @Override
    public int[] getCrewFilms(int crewID) {
        MyDynamicArray<Integer> allCrewMemberFilms = new MyDynamicArray<>();
        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.crew == null) continue;

                for (CrewCredit crewCredit : creditNode.crew) {
                    if (crewCredit.getID() == crewID) {
                        allCrewMemberFilms.add(creditNode.filmID);
                        break;
                    }
                }
            }
        }
        return allCrewMemberFilms.toIntArray();
    }


    /**
     * Get the films that this cast member stars in (in the top 3 cast members/top 3 billing). This is determined by the order 
     * field in the CastCredit class).
     * Note: the order field isn't zero-indexed, so order 1 is top billing. "Top 3 cast" therefore means order values 1, 2,
     * and 3. Uses the same scan-and-break pattern as getCastFilms(int).
     * 
     * @param castID The cast ID of the cast member to be searched for
     * @return An array of film IDs where the the cast member stars in. If there are no films where the cast member has
     * starred in, or the cast member does not exist, return an empty array
     */
    @Override
    public int[] getCastStarsInFilms(int castID){
        MyDynamicArray<Integer> topCastFilms = new MyDynamicArray<>();

        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.cast == null) continue;

                for (CastCredit castCredit : creditNode.cast) {
                    if (castCredit.getID() == castID && castCredit.getOrder() <= 3) {
                        topCastFilms.add(creditNode.filmID);
                        break;
                    }
                }
            }
        }
        return topCastFilms.toIntArray();
    }


    /**
     * Get Person objects for cast members who have appeared in the most films. If the cast member has multiple roles within
     * the film, then they would get a credit per role played. For example, if a cast member performed as 2 roles in the same
     * film, then this would count as 2 credits. The list should be ordered by the highest to lowest number of credits.
     * A single pass over the entire table builds two parallel MyDynamicArray(s): one of person IDs and one of credit counts.
     * Because a person may have multiple roles in the same film, every CastCredit entry increments the count (not just unique
     * films). The parallel arrays are then sorted together in descending order by count using selection sort, and the top 
     * numResults persons are returned as Person objects resolved via getCast(int).
     * 
     * @param numResults The maximum number of elements that should be returned
     * @return An array of Person objects corresponding to the cast members with the most credits, ordered by the highest 
     * number of credits. If there are less cast members that the number required, then the list should be the same number of cast members found.
     */
    @Override
    public Person[] getMostCastCredits(int numResults) {
        MyDynamicArray<Integer> castMemberIDs = new MyDynamicArray<>();
        MyDynamicArray<Integer> totalCastMemberCredits = new MyDynamicArray<>();

        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.cast == null) continue;

                for (CastCredit castCredit : creditNode.cast) {
                    int index = castMemberIDs.indexOf(castCredit.getID());
                    if (index == -1) {
                        castMemberIDs.add(castCredit.getID());
                        totalCastMemberCredits.add(1);
                    } else {
                        totalCastMemberCredits.set(index, totalCastMemberCredits.get(index) + 1);
                    }
                }
            }
        }

        for (int i = 0; i < totalCastMemberCredits.size(); i++) {
            int max = i;
            for (int j = i + 1; j < totalCastMemberCredits.size(); j++) {
                if (totalCastMemberCredits.get(j) > totalCastMemberCredits.get(max)) max = j;
            }

            int tempC = totalCastMemberCredits.get(i);
            totalCastMemberCredits.set(i, totalCastMemberCredits.get(max));
            totalCastMemberCredits.set(max, tempC);

            int tempID = castMemberIDs.get(i);
            castMemberIDs.set(i, castMemberIDs.get(max));
            castMemberIDs.set(max, tempID);
        }

        int arraySize = Math.min(numResults, castMemberIDs.size());
        Person[] mostCastedMembersIDsArray = new Person[arraySize];

        for (int i = 0; i < arraySize; i++) {
            mostCastedMembersIDsArray[i] = getCast(castMemberIDs.get(i));
        }

        return mostCastedMembersIDsArray;
    }


    /**
     * Get the number of credits for a given cast member. If the cast member has multiple roles within the film, then they
     * would get a credit per role played. For example, if a cast member performed as 2 roles in the same film, then this 
     * would count as 2 credits.
     * Unlike getCastFilms(int), this method does not break early when it finds the cast member in a film's cast array, 
     * because the same person can appear multiple times in one film's cast list (multiple roles), and each appearance counts
     * as a separate credit.
     * 
     * @param castID A cast ID representing the cast member to be found
     * @return The number of credits the given cast member has. If the cast member cannot be found, return -1
     */
    @Override
    public int getNumCastCredits(int castID) {
        int numOfCredits = 0;
        boolean castFound = false;

        for (int i = 0; i < capacity; i++) {
            for (CreditNode creditNode = creditHashTable[i]; creditNode != null; creditNode = creditNode.nextCredit) {
                if (creditNode.cast == null) continue;

                for (CastCredit castCredit : creditNode.cast) {
                    if (castCredit.getID() == castID) {
                        numOfCredits++;
                        castFound = true;
                    }
                }
            }
        }
        return castFound ? numOfCredits : -1;
    }


    /**
     * Gets the number of films stored in this data structure
     * 
     * @return The number of films in the data structure
     */
    @Override
    public int size() {
        return size;
    }
}
