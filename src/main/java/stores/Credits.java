package stores;

import structures.*;

import interfaces.ICredits;

public class Credits implements ICredits{
    Stores stores;
    private CreditNode[] creditHashTable;
    private int capacity = 1999;
    private int size = 0;

    public Credits (Stores stores) {
        this.stores = stores;
        creditHashTable = new CreditNode[capacity];
    }

    private int getCreditHash(int filmID) {
        return Math.abs(filmID) % capacity;
    }

    private CreditNode getCreditNode(int filmID) {
        int creditHashCode = getCreditHash(filmID);

        for (CreditNode creditNode = creditHashTable[creditHashCode]; creditNode != null; creditNode = creditNode.nextCredit) {
            if (creditNode.filmID == filmID) return creditNode;
        }
        return null;
    }

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

    @Override
    public CrewCredit[] getFilmCrew(int filmID) {
        CreditNode creditNode = getCreditNode(filmID);

        if (creditNode == null || creditNode.crew == null || creditNode.crew.length == 0) {
            return new CrewCredit[0];
        }

        CrewCredit[] orderedFilmCrewArray = new CrewCredit[creditNode.crew.length];
        for (int i = 0; i < creditNode.crew.length; i++) {
            orderedFilmCrewArray[i] = creditNode.crew[i];
        }

        for (int i = 0; i < orderedFilmCrewArray.length; i++) {
            int minIndex = i;

            for (int j = i + 1; j < orderedFilmCrewArray.length; j++) {
                if (orderedFilmCrewArray[j].getID() < orderedFilmCrewArray[minIndex].getID()) minIndex = j;
            }

            CrewCredit temp = orderedFilmCrewArray[i];
            orderedFilmCrewArray[i] = orderedFilmCrewArray[minIndex];
            orderedFilmCrewArray[minIndex] = temp;
        }

        return orderedFilmCrewArray;
    }

    @Override
    public int sizeOfCast(int filmID) {
        CreditNode creditNode = getCreditNode(filmID);
        if (creditNode == null) return -1;
        return (creditNode.cast == null) ? 0 : creditNode.cast.length;
    }

    @Override
    public int sizeOfCrew(int filmID) {
        CreditNode creditNode = getCreditNode(filmID);
        if (creditNode == null) return -1;
        return (creditNode.crew == null) ? 0 : creditNode.crew.length;
    }

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

    @Override
    public int size() {
        return size;
    }
}
