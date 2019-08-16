package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class SplicingIterator<A> implements Iterator<A> {

    private enum Status {NOT_CACHED, CACHED, DONE}

    private Node<A> head;
    private A cachedElement;
    private Status status;

    public SplicingIterator(Iterable<SpliceSegment<A>> sources) {
        Node<A> prev = null;
        for (SpliceSegment<A> source : sources) {
            Node<A> node = new Node<>(source.getStartOffset(), source.getReplaceCount(),
                    source.getSource().iterator(),
                    null);
            if (prev == null) {
                this.head = node;
            } else {
                prev.setNext(node);
            }
            prev = node;
        }
        this.status = Status.NOT_CACHED;
    }

    @Override
    public boolean hasNext() {
        if (status == Status.NOT_CACHED) {
            status = readNextElement() ? Status.CACHED : Status.DONE;
        }
        return status == Status.CACHED;
    }

    @Override
    public A next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        status = Status.NOT_CACHED;
        A result = cachedElement;
        cachedElement = null;
        return result;
    }

    private boolean readNextElement() {
        long skipCount = 0;
        Node<A> prev = null;
        Node<A> current = head;

        while (current != null) {
            int delay = current.getDelay();
            if (delay > 0) {
                current.setDelay(delay - 1);
                prev = current;
                current = current.getNext();
                continue;
            }

            Iterator<A> source = current.getSource();
            if (skipCount > 0 && source.hasNext()) {
                source.next();
                skipCount -= 1;
                prev = null;
                current = head;
                continue;
            }

            if (source.hasNext()) {
                cachedElement = source.next();
                return true;
            } else {
                skipCount += current.getReplaceCount();

                Node<A> next = current.getNext();
                if (prev == null) {
                    head = next;
                } else {
                    prev.setNext(next);
                }
                current = next;

                if (current == null) {
                    head = normalizeDelays(head);
                    prev = null;
                    current = head;
                }
            }
        }

        return false;
    }

    private static <A> Node<A> normalizeDelays(Node<A> first) {
        if (first == null) {
            return null;
        }
        int minDelay = first.getDelay();
        Node<A> current = first.getNext();
        while (current != null && current.getDelay() > 0) {
            minDelay = Math.min(minDelay, current.getDelay());
            current = current.getNext();
        }
        current = first;
        while (current != null && current.getDelay() > 0) {
            current.setDelay(current.getDelay() - minDelay);
            current = current.getNext();
        }
        return first;
    }

    private static final class Node<A> {
        private int delay;
        private final int replaceCount;
        private Iterator<A> source;
        private Node<A> next;

        Node(int delay, int replaceCount, Iterator<A> source, Node<A> next) {
            this.delay = delay;
            this.replaceCount = replaceCount;
            this.source = source;
            this.next = next;
        }

        int getDelay() {
            return delay;
        }

        void setDelay(int delay) {
            this.delay = delay;
        }

        int getReplaceCount() {
            return replaceCount;
        }

        Iterator<A> getSource() {
            return source;
        }

        Node<A> getNext() {
            return next;
        }

        void setNext(Node<A> next) {
            this.next = next;
        }

    }

}
