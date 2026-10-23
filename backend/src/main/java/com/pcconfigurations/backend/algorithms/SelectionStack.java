package com.pcconfigurations.backend.algorithms;

/**
 * Custom Stack for undo support in the PC Builder.
 *
 * Supports undoing the most recent component selection action.
 *
 * Time Complexity:
 *   - push: O(1)
 *   - pop:  O(1)
 *   - peek: O(1)
 *   - isEmpty: O(1)
 *
 * Space Complexity: O(n) where n = number of actions pushed
 */
public class SelectionStack<T> {

    private Object[] data;
    private int top;
    private static final int DEFAULT_CAPACITY = 32;

    public SelectionStack() {
        data = new Object[DEFAULT_CAPACITY];
        top = -1;
    }

    /** Push an action onto the stack. */
    public void push(T item) {
        if (top == data.length - 1) {
            // grow
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, data.length);
            data = bigger;
        }
        data[++top] = item;
    }

    /** Remove and return the top action. Throws if empty. */
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty — no actions to undo");
        }
        T item = (T) data[top];
        data[top--] = null;
        return item;
    }

    /** Return but do not remove the top action. */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return (T) data[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    public void clear() {
        for (int i = 0; i <= top; i++) data[i] = null;
        top = -1;
    }
}
