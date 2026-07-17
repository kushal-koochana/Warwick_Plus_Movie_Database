package structures;

public interface IList<E> {

    public boolean add(E element);

    public void clear();

    public boolean contains(E element);

    public boolean isEmpty();

    public boolean remove(E element);

    public int size();

    public E get(int index);

    public int indexOf(E element);

    public E set(int index, E element);
        
}
