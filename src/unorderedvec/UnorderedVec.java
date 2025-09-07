package unorderedvec;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class UnorderedVec<E> implements Iterable<E> {
    private E[] elements;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public UnorderedVec(){
        elements = (E[]) new Object[16];
    }

    public void add(E element){
        Objects.requireNonNull(element);
        if(size >= elements.length){
            if(elements.length == Integer.MAX_VALUE - 16){
                return;
            }
            var i = elements.length * 2;
            if(i < 0){
                i = Integer.MAX_VALUE - 16;
            }
            elements = Arrays.copyOf(elements, i);
        }
        elements[size] = element;
        size++;
    }

    public boolean remove(Object value){
        Objects.requireNonNull(value);
        for(var i = 0; i < size; i++){
            if (elements[i].equals(value)) {
                if(i == size - 1){
                    elements[size - 1] = null;
                    size--;
                    return true;
                }
                var lastElement = elements[size - 1];
                elements[size - 1] = null;
                elements[i] = lastElement;
                size--;
                return true;
            }
        }
        return false;
    }

    public int size(){
        return size;
    }

    private static int start(int size) {
        return size == 0 ? 0 : (int) ((size * 0x5DEECE66DL + 11) & 0x7FFFFFFF) % size;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            int position = start(size);
            int count = 0;
            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public E next() {
                if(!hasNext()){
                    throw new NoSuchElementException();
                }
                var element = elements[position];
                position++;
                count++;
                if(position == size){
                    position = 0;
                }
                return element;
            }
        };
    }
}
