package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyIterator;

public final class SplicingIterator<A> implements Iterator<A> {

    private enum Status {NOT_CACHED, CACHED, DONE}

    private Node<A> head;
    private A cachedElement;
    private Status status;

    public SplicingIterator(Iterable<SpliceDirective<A>> sources) {
        Node<A> prev = null;
        for (SpliceDirective<A> source : sources) {
            Node<A> node = source.match(taking -> new Node<A>(taking.getCount(), -1, emptyIterator()),
                    dropping -> new Node<A>(0, dropping.getCount(), emptyIterator()),
                    splicing -> new Node<A>(splicing.getStartOffset(), splicing.getReplaceCount(),
                            splicing.getSource().iterator()));
            if (prev == null) {
                this.head = node;
            } else {
                prev.setNext(node);
            }
            node.setPrev(prev);
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
        if (head == null) {
            return false;
        }

        Node<A> current = head;

        while (true) {
            while (current != null) {
                if (current.getDelay() == 0) {
                    if (current.getSource().hasNext()) {
                        break;
                    }

                    if (current.getReplaceCount() < 0) {
                        // taking node;  done
                        cachedElement = null;
                        head = null;
                        return false;
                    } else if (current.getReplaceCount() == 0 || current.getNext() == null) {
                        // discard
                        Node<A> prev = current.getPrev();
                        Node<A> next = current.getNext();
                        if (prev == null) {
                            head = next;
                        } else {
                            prev.setNext(next);
                        }
                        if (next != null) {
                            next.setPrev(prev);
                        }
                    }
                }
                current = current.getNext();
            }

            if (current == null) {
                head = normalizeDelays(head);
                current = head;
                if (head == null) {
                    cachedElement = null;
                    return false;
                }
                continue;
            }

            // current has 0 delay and has next
            cachedElement = current.getSource().next();

            current = current.getPrev();
            // go backwards
            while (current != null) {
                if (current.getDelay() > 0) {
                    current.setDelay(current.getDelay() - 1);
                } else if (current.getReplaceCount() > 0) {
                    current.setReplaceCount(current.getReplaceCount() - 1);
                    break;
                }
                current = current.getPrev();
            }

            if (current == null) {
                return true;
            }
        }
    }

    private static <A> Node<A> normalizeDelays(Node<A> first) {
        while (first != null && !first.getSource().hasNext()) {
            first = first.getNext();
        }

        if (first == null) {
            return null;
        }

        int minDelay = first.getDelay();
        Node<A> current = first.getNext();
        while (current != null && current.getDelay() > 0 && current.getSource().hasNext()) {
            minDelay = Math.min(minDelay, current.getDelay());
            current = current.getNext();
        }
        current = first;
        while (current != null && current.getDelay() > 0 && current.getSource().hasNext()) {
            current.setDelay(current.getDelay() - minDelay);
            current = current.getNext();
        }
        return first;
    }

    private static final class Node<A> {
        private int delay;
        private int replaceCount;
        private Iterator<A> source;
        private Node<A> prev;
        private Node<A> next;

        Node(int delay, int replaceCount, Iterator<A> source) {
            this.delay = delay;
            this.replaceCount = replaceCount;
            this.source = source;
            this.prev = null;
            this.next = null;
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

        void setReplaceCount(int replaceCount) {
            this.replaceCount = replaceCount;
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

        Node<A> getPrev() {
            return prev;
        }

        void setPrev(Node<A> prev) {
            this.prev = prev;
        }

        @Override
        public String toString() {
            return "[" + delay + ":" + replaceCount + ":" +
                    (source.hasNext() ? "+" : "_") + "]";
        }
    }

}
