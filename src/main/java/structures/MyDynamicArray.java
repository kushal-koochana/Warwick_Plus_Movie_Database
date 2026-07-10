package structures;

public class MyDynamicArray<E> implements IList<E> {
    private static final int defaultCapacity = 10;  // initial default size of array
    private static final float growthScaleFactor = 1.5F; // the array capacity/length grows by this much when array's full
    private static final int shrinkThreshold = 4;  //  the array's capacity halved when size < capacity / shrinkThreshold i.e when fewer
    // than 25% of array slots are occupied

    private Object[] array;
    private int size;  // number of elements stored in array, not physical length of array (allocated capacity)
    private int capacity;  // actual physical length of array (allocated capacity)


    /**
     * Constructs an empty MyDynamicArray with the default initial capacity of 10.
     */
    public MyDynamicArray() {
        this.capacity = defaultCapacity;
        this.array = new Object[capacity];
        this.size = 0;
    }


    /**
     * Constructs an empty MyDynamicArray with the given initial capacity, it default to 10 if the supplied value is less 
     * than 1. Use this constructor when the approximate number of elements is known beforehand to avoid unnecessary resizing.
     *
     * @param initialCapacity The desired initial array's allocated capacity (must be greater than or equal to 1)
     */
    public MyDynamicArray(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = defaultCapacity;
        this.capacity = initialCapacity;
        this.array = new Object[capacity];
        this.size = 0;
    }


    /**
     * Replaces an existing array with some capacity with a new one of the specified capacity, copying all current elements 
     * across. This is a useful helper method for: grow(), maybeShrink(), ensureCapacity(int), trimToSize()
     *
     * @param newCapacity The desired physical size of the new array
     */
    private void resize(int newCapacity) {
        Object[] temp = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            temp[i] = array[i];
        }
        array = temp;
        capacity = newCapacity;
    }


    /**
     * Expands the array capacity when it is full, by applying the 1.5× growth scale factor. A guard ensures the new capacity is
     * always strictly greater than the old one, preventing an infinite loop i.e when capacity is 1 (integer truncation of
     * 1 * 1.5 == 1).
     */
    private void grow() {
        int newCapacity = (int) (capacity * growthScaleFactor);
        if (newCapacity <= capacity) newCapacity = capacity + 1;
        resize(newCapacity);
    }


    /**
     * Shrinks the array capacity after a removal if number of filled in elements compared to capacity drops below 25%.
     * The array is halved, but never below defaultCapacity, to avoid problems like when elements are repeatedly added
     * and removed near shrink boundary.
     */
    private void maybeShrink() {
        if (capacity > defaultCapacity && size < capacity / shrinkThreshold) {
            int newCapacity = Math.max(capacity / 2, defaultCapacity);
            resize(newCapacity);
        }
    }


    /**
     * Adds the given element to the end of the array. If the array is full, grow() is called first to expand it before the 
     * element is stored.
     *
     * @param element The element to add
     * @return true if the element was added successfully; false if an unexpected exception occurred
     */
    @Override
    public boolean add(E element) {
        try {
            if (size >= capacity) grow();
            array[size++] = element;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Returns true if the array contains the given element, using .equals() for comparison. It performs a linear scan of
     * array. O(n) is the worst case.
     *
     * @param element The element to search for
     * @return true if found; false otherwise
     */
    @Override
    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(array[i])) return true;
        }
        return false;
    }


    /**
     * Removes all elements from the array and resets capacity to the default capacity. The array is replaced entirely, 
     * releasing references held by the old array to allow garbage collection.
     */
    @Override
    public void clear() {
        this.capacity = defaultCapacity;
        this.array = new Object[capacity];
        this.size = 0;
    }


    /**
     * Returns true if the array contains no elements stored.
     *
     * @return true when size == 0
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * Returns the number of elements currently stored in the array.
     *
     * @return The logical size of the array
     */
    @Override
    public int size() {
        return size;
    }


    /**
     * Returns the element at the specified index. No bounds checking beyond what the JVM provides; callers are
     * responsible for ensuring 0 <= index < size().
     *
     * @param index Zero-based position of the desired element
     * @return The element at index
     */
    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        return (E) array[index];
    }


    /**
     * Returns the index of the first occurrence of the given element, or -1 if the element is not present. Uses .equals()
     * for comparison; O(n) in the worst case.
     *
     * @param element The element to find
     * @return Zero-based index of the first match, or -1 if not found
     */
    @Override
    public int indexOf(E element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(array[i])) return i;
        }
        return -1;
    }


    /**
     * Removes the first occurrence of the given element from the array. Elements to the right of the removed position are
     * shifted one place left to fill the gap. The vacated last slot is nulled to release the object reference. maybeShrink()
     * is called afterwards to optimise memory (by reducing capacity) if usage has dropped below 25%.
     *
     * @param element The element to remove
     * @return true if the element was found and removed; false if it was not present
     */
    @Override
    public boolean remove(E element) {
        int index = indexOf(element);
        if (index < 0) return false;
        
        for (int i = index + 1; i < size; i++) {
            array[i - 1] = array[i];
        }
        array[--size] = null;
        maybeShrink();
        return true;
    }


    /**
     * Replaces the element at the specified index with the given element and returns the element that was previously at that
     * position.
     *
     * @param index   Zero-based position to update (must be less than array logical size)
     * @param element The new value to store at index
     * @return The element that was previously at index
     * @throws ArrayIndexOutOfBoundsException if index >= size()
     */
    @Override
    public E set(int index, E element) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(
                    "index >= size: " + index + " >= " + size);
        }
        @SuppressWarnings("unchecked")
        E replaced = (E) array[index];
        array[index] = element;
        return replaced;
    }


    /**
     * Ensures the array can hold at least the minCapacity elements without resizing. If the current capacity 
     * already meets the requirement this method does nothing. Otherwise the array is grown (using the same 1.5× factor) 
     * until it is large enough. This is useful when the approximate number of elements to be added is known beforehand, 
     * avoiding repeated incremental resizes.
     *
     * @param minCapacity The minimum capacity required
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > capacity) {
            int newCapacity = capacity;
            while (newCapacity < minCapacity) {
                newCapacity = (int) (newCapacity * growthScaleFactor);
                if (newCapacity <= capacity) newCapacity = minCapacity; // guard
            }
            resize(newCapacity);
        }
    }


    /**
     * Compacts the backing array to the exact number of elements stored (size), removing any unused capacity. Useful before
     * converting the MyDynamicArray to a plain array (e.g. via toIntArray()), when no further additions expected, ensuring
     * minimum amount of memory used.
     */
    public void trimToSize() {
        if (size < capacity) {
            resize(Math.max(size, 1));
        }
    }


    /**
     * Converts the MyDynamicArray contents to a primitive int[] array. This avoids use of continuous for loop to unbox
     * in middle of code to cast each element into primitive array via get(int), when element type is know to be Integer
     * It is useful in many places in the stores (e.g. getFilmsInCollection()) to return results as plain int[] arrays with
     * no intermediate boxing step.
     *
     * @return A new int[] array of length size() containing each element cast to int.
     */
    public int[] toIntArray() {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = (Integer) array[i];
        }
        return result;
    }


    /**
     * Converts the MyDynamicArray contents to a primitive float[] array. Analogous to toIntArray() but for Float elements;
     * avoids every element's unboxing overhead.
     *
     * @return A new float[] array of length size() containing each element cast to float.
     */
    @SuppressWarnings("unchecked")
    public float[] toFloatArray() {
        float[] result = new float[size];
        for (int i = 0; i < size; i++) {
            result[i] = (Float) array[i];
        }
        return result;
    }


    /**
     * Returns a human readable representation of the array contents, formatted as [e0, e1, ..., en-1], where n is the
     * size of elements.
     *
     * @return String representation of the array
     */
    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(array[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }
}