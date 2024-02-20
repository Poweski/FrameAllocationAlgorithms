package MyObjects;

import Helpful.FullMemoryException;

import java.util.LinkedList;

public class Memory {

    private final LinkedList<Integer> pages = new LinkedList<>();
    private int size;

    public Memory(int size) {
        this.size = size;
    }

    public LinkedList<Integer> getPages() {
        return pages;
    }

    public int getSize() {
        return size;
    }

    public boolean isFull() {
        return pages.size() >= size;
    }

    public boolean contains(Integer page) {
        return pages.contains(page);
    }

    public void addPage(Integer page) {
        if (isFull()) {
            throw new FullMemoryException("Memory is full! Element cannot be added!");
        }
        pages.addFirst(page);
    }

    public boolean removePage(Integer page) {
        return pages.remove(page);
    }

    public void increaseSize() {
        size++;
    }

    public void changeSize(int change) {
        int temp = size + change;
        if (temp < pages.size()) {
            throw new FullMemoryException("Cannot decrease size!");
        }
        size = temp;
    }
}