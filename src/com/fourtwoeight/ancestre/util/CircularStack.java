package com.fourtwoeight.ancestre.util;

import java.util.Stack;

public class CircularStack<E> extends Stack<E> {

    // Public Methods ==================================================================================================

    /**
     * Constructor
     * @param size the size of the stack
     */
    public CircularStack(int size){
        super();
        this.maxSize = size;
    }

    /**
     * Sets the size of the stack. Removes any extra elements if new size is smaller than previous size.
     * @param size the size of the Stack
     */
    public void setSize(int size){
        if(size > 1){
            this.maxSize = size;

            // remove any extra elements
            while(size() > maxSize){
                removeElementAt(size() - 1);
            }
        }
    }

    /**
     * Pushes an element onto a stack, removes any elements that are larger than the max size
     * @param item the item to add
     * @return the item added
     */
    @Override
    public E push(E item) {
        E element = super.push(item);

        while(size() > maxSize){
            removeElementAt(size() - 1);
        }

        return element;
    }

    // Private Fields ==================================================================================================

    private int maxSize;
}
