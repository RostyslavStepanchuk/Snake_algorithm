package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class Route implements List<Point> {
    public Point getFirst() {
        return storage.getFirst();
    }

    public Point removeFirst() {
        return storage.removeFirst();
    }

    public Point removeLast() {
        return storage.removeLast();
    }

    public void addFirst(Point point) {
        storage.addFirst(point);
    }

    public void addLast(Point point) {
        storage.addLast(point);
    }

    public boolean remove(Object o) {
        return storage.remove(o);
    }

    public boolean addAll(int index, Collection<? extends Point> c) {
        return storage.addAll(index, c);
    }

    public void clear() {
        storage.clear();
    }

    public Point get(int index) {
        return storage.get(index);
    }

    public Point set(int index, Point element) {
        return storage.set(index, element);
    }

    public void add(int index, Point element) {
        storage.add(index, element);
    }

    public Point remove(int index) {
        return storage.remove(index);
    }

    public int indexOf(Object o) {
        return storage.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return storage.lastIndexOf(o);
    }

    public Point peek() {
        return storage.peek();
    }

    public Point element() {
        return storage.element();
    }

    public Point remove() {
        return storage.remove();
    }

    public boolean offer(Point point) {
        return storage.offer(point);
    }

    public boolean offerFirst(Point point) {
        return storage.offerFirst(point);
    }

    public boolean offerLast(Point point) {
        return storage.offerLast(point);
    }

    public Point peekFirst() {
        return storage.peekFirst();
    }

    public Point peekLast() {
        return storage.peekLast();
    }

    public Point pollFirst() {
        return storage.pollFirst();
    }

    public Point pollLast() {
        return storage.pollLast();
    }

    public void push(Point point) {
        storage.push(point);
    }

    public Point pop() {
        return storage.pop();
    }

    public boolean removeFirstOccurrence(Object o) {
        return storage.removeFirstOccurrence(o);
    }

    public boolean removeLastOccurrence(Object o) {
        return storage.removeLastOccurrence(o);
    }

    public ListIterator<Point> listIterator(int index) {
        return storage.listIterator(index);
    }

    public Iterator<Point> descendingIterator() {
        return storage.descendingIterator();
    }

    public Object[] toArray() {
        return storage.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return storage.toArray(a);
    }

    public Spliterator<Point> spliterator() {
        return storage.spliterator();
    }

    public ListIterator<Point> listIterator() {
        return storage.listIterator();
    }

    public List<Point> subList(int fromIndex, int toIndex) {
        return storage.subList(fromIndex, toIndex);
    }

    public boolean containsAll(Collection<?> c) {
        return storage.containsAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return storage.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return storage.retainAll(c);
    }

    public boolean removeIf(Predicate<? super Point> filter) {
        return storage.removeIf(filter);
    }

    public Stream<Point> stream() {
        return storage.stream();
    }

    public Stream<Point> parallelStream() {
        return storage.parallelStream();
    }

    public void replaceAll(UnaryOperator<Point> operator) {
        storage.replaceAll(operator);
    }

    public void sort(Comparator<? super Point> c) {
        storage.sort(c);
    }

    private final LinkedList<Point> storage;

    public Route() {
        this (new LinkedList<>());
    }

    public Route(Route route) {
        this (route.getStorage());
    }

    public Route(Collection<? extends Point> c) {
        storage = new LinkedList<>(c);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(",", "<<",">>");
        storage.forEach(item -> sj.add(item.toString()));
        return sj.toString();
    }

    private LinkedList<Point> getStorage() {
        return storage;
    }

    public Point poll() {
        return storage.poll();
    }

    public boolean add(Point point) {
        return storage.add(point);
    }

    public Point getLast() {
        return storage.getLast();
    }

    public int size() {
        return storage.size();
    }

    public boolean contains(Object o) {
        return storage.contains(o);
    }


    public void forEach(Consumer<? super Point> action) {
        storage.forEach(action);
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public Iterator<Point> iterator() {
        return storage.iterator();
    }

    public boolean addAll(Collection<? extends Point> c) {
        return storage.addAll(c);
    }
}
