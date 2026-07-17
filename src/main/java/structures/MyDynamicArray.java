package structures;

public class MyDynamicArray<E> implements IList<E> {
    private static final int defaultCapacity = 10;
    private static final float growthScaleFactor = 1.5F;
    private static final int shrinkThreshold = 4;

    private Object[] array;
    private int size;
    private int capacity;

    public MyDynamicArray() {
        this.capacity = defaultCapacity;
        this.array = new Object[capacity];
        this.size = 0;
    }

    public MyDynamicArray(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = defaultCapacity;
        this.capacity = initialCapacity;
        this.array = new Object[capacity];
        this.size = 0;
    }

    private void resize(int newCapacity) {
        Object[] temp = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            temp[i] = array[i];
        }
        array = temp;
        capacity = newCapacity;
    }

    private void grow() {
        int newCapacity = (int) (capacity * growthScaleFactor);
        if (newCapacity <= capacity) newCapacity = capacity + 1;
        resize(newCapacity);
    }

    private void maybeShrink() {
        if (capacity > defaultCapacity && size < capacity / shrinkThreshold) {
            int newCapacity = Math.max(capacity / 2, defaultCapacity);
            resize(newCapacity);
        }
    }

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

    @Override
    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(array[i])) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.capacity = defaultCapacity;
        this.array = new Object[capacity];
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        return (E) array[index];
    }

    @Override
    public int indexOf(E element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(array[i])) return i;
        }
        return -1;
    }

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

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > capacity) {
            int newCapacity = capacity;
            while (newCapacity < minCapacity) {
                newCapacity = (int) (newCapacity * growthScaleFactor);
                if (newCapacity <= capacity) newCapacity = minCapacity;
            }
            resize(newCapacity);
        }
    }

    public void trimToSize() {
        if (size < capacity) {
            resize(Math.max(size, 1));
        }
    }

    public int[] toIntArray() {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = (Integer) array[i];
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public float[] toFloatArray() {
        float[] result = new float[size];
        for (int i = 0; i < size; i++) {
            result[i] = (Float) array[i];
        }
        return result;
    }

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
