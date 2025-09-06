package unorderedvec;

import java.util.Objects;

public final class UnorderedVec<E> {
    private E[] elements;
    private int index = 0;

    @SuppressWarnings("unchecked")
    public UnorderedVec(){
        elements = (E[]) new Object[16];
    }

    public void add(E element){
        Objects.requireNonNull(element);
        elements[index] = element;
        index++;
    }
    public int size(){
        return index;
    }

    private static int start(int size) {
        return size == 0 ? 0 : (int) ((size * 0x5DEECE66DL + 11) & 0x7FFFFFFF) % size;
    }
}
