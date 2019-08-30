package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.lang.Math.min;

public final class SplicingIterator<A> implements Iterator<A> {

    private enum Status {NOT_CACHED, CACHED, DONE}

    private Node<A> head;
    private A cachedElement;
    private Status status;

    public static int count = 0;

    public SplicingIterator(Iterable<SpliceDirective<A>> sources) {
        count += 1;
        Node<A> prev = null;
        for (SpliceDirective<A> source : sources) {
            Node<A> node = source.match(taking -> new Node<>(taking.getCount(), -1, null),
                    dropping -> new Node<>(0, dropping.getCount(), null),
                    splicing -> new Node<>(splicing.getStartOffset(), splicing.getReplaceCount(),
                            splicing.getSource()));
            if (prev == null) {
                this.head = node;
            } else {
                prev.next = node;
            }
            node.prev = prev;
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

        do {
            while (current != null) {
                if (current.delay == 0) {
                    if (current.haveMoreElements()) {
                        break;
                    }

                    if (current.replaceCount < 0) {
                        // This is a 'take' node;  terminate immediately
                        cachedElement = null;
                        head = null;
                        return false;
                    } else if (current.replaceCount == 0 || current.next == null) {
                        // discard
                        Node<A> prev = current.prev;
                        Node<A> next = current.next;
                        if (prev == null) {
                            head = next;
                        } else {
                            prev.next = next;
                        }
                        if (next != null) {
                            next.prev = prev;
                        }
                    }
                }
                current = current.next;
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
            cachedElement = current.nextElement();

            current = current.prev;

            // go backwards, decrementing delays, until stopped by a node with a replace count
            while (current != null) {
                if (current.delay > 0) {
                    current.delay = current.delay - 1;
                } else if (current.replaceCount > 0) {
                    current.replaceCount = current.replaceCount - 1;
                    cachedElement = null;
                    break;
                }
                current = current.prev;
            }

            if (current == null) {
                return true;
            }

        } while (true);
    }

    private static <A> Node<A> normalizeDelays(Node<A> head) {
        if (head == null) {
            return null;
        }
        Node<A> first = head;

        while (first != null && !first.haveMoreElements()) {
            first = first.next;
        }

        if (first == null) {
            return null;
        }

        int minDelay = first.delay;
        Node<A> current = first.next;
        while (current != null && current.delay > 0 && current.haveMoreElements()) {
            minDelay = min(minDelay, current.delay);
            current = current.next;
        }
        current = first;
        while (current != null && current.delay > 0 && current.haveMoreElements()) {
            current.delay = current.delay - minDelay;
            current = current.next;
        }
        return head;
    }

    private static final class Node<A> {
        private Iterable<A> source;
        private Iterator<A> iterator;
        int delay;
        int replaceCount;
        Node<A> prev;
        Node<A> next;

        Node(int delay, int replaceCount, Iterable<A> source) {
            this.delay = delay;
            this.replaceCount = replaceCount;
            this.source = source;
            this.iterator = null;
            this.prev = null;
            this.next = null;
        }

        boolean haveMoreElements() {
            if (iterator == null) {
                if (source == null) {
                    return false;
                } else {
                    iterator = source.iterator();
                    source = null;
                }
            }

            if (iterator.hasNext()) {
                return true;
            } else {
                iterator = null;
                return false;
            }
        }

        A nextElement() {
            A result = iterator.next();
            if (!iterator.hasNext()) {
                iterator = null;
            }
            return result;
        }

        @Override
        public String toString() {
            return "[" + delay + ":" + replaceCount + ":" +
                    ((iterator != null && iterator.hasNext()) ? "+" : "_") + "]";
        }
    }

}
