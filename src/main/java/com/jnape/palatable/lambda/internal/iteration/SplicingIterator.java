package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.lang.Math.min;
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
                    dropping -> new Node<>(0, dropping.getCount(), emptyIterator()),
                    splicing -> new Node<>(splicing.getStartOffset(), splicing.getReplaceCount(),
                            splicing.getSource().iterator()));
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
                    if (current.source.hasNext()) {
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
            cachedElement = current.source.next();

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

        while (first != null && !first.source.hasNext()) {
            first = first.next;
        }

        if (first == null) {
            return null;
        }

        int minDelay = first.delay;
        Node<A> current = first.next;
        while (current != null && current.delay > 0 && current.source.hasNext()) {
            minDelay = min(minDelay, current.delay);
            current = current.next;
        }
        current = first;
        while (current != null && current.delay > 0 && current.source.hasNext()) {
            current.delay = current.delay - minDelay;
            current = current.next;
        }
        return head;
    }

    private static final class Node<A> {
        final Iterator<A> source;
        int delay;
        int replaceCount;
        Node<A> prev;
        Node<A> next;

        Node(int delay, int replaceCount, Iterator<A> source) {
            this.delay = delay;
            this.replaceCount = replaceCount;
            this.source = source;
            this.prev = null;
            this.next = null;
        }

        @Override
        public String toString() {
            return "[" + delay + ":" + replaceCount + ":" +
                    (source.hasNext() ? "+" : "_") + "]";
        }
    }

}
