package com.pcconfigurations.backend.algorithms;

/**
 * Custom Singly Linked List for managing selected PC build components.
 *
 * Time Complexity:
 * - add: O(n) to traverse (or O(1) if we maintain tail)
 * - remove: O(n)
 * - find: O(n)
 * - display: O(n)
 *
 * Space Complexity: O(n)
 */
public class ComponentLinkedList<T> {

    private Node<T> head;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    /** Add a component to the end of the list. */
    public void add(T component) {
        Node<T> newNode = new Node<>(component);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    /** Remove the first occurrence of the component. Returns true if removed. */
    public boolean remove(T component) {
        if (head == null)
            return false;

        if (head.data.equals(component)) {
            head = head.next;
            size--;
            return true;
        }

        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(component)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /** Find a component by equality. Returns the component or null. */
    public T find(T component) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(component)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    /** Return all components as a java.util.List. */
    public java.util.List<T> toList() {
        java.util.List<T> result = new java.util.ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null)
                sb.append(" -> ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
