package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SplicingIterator<A> implements Iterator<A> {
    private SpliceSourceState<A> state;
    private A cache;
    private boolean cached;

    public SplicingIterator(SpliceSourceState<A> state) {
        this.state = state;
    }

    @Override
    public boolean hasNext() {
        if (cached) {
            return true;
        } else {
            cached = computeNext();
            return cached;
        }
    }

    @Override
    public A next() {
        if (hasNext()) {
            cached = false;
            A result = cache;
            cache = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }

    private boolean computeNext() {
        if (state == null) {
            return false;
        }

        long skipCount = 0;
        SpliceSourceState<A> prev = null;
        SpliceSourceState<A> current = state;

        while (current != null) {
            int startOffset = current.getStartOffset();
            if (startOffset > 0) {
                current.decStartOffset();
                prev = current;
                current = current.getNext();
                continue;
            }
            // startOffset is 0
            Iterator<A> source = current.getSource();
            while (skipCount > 0 && source.hasNext()) {
                source.next();
                skipCount -= 1;
            }

            if (source.hasNext()) {
                cache = source.next();
                return true;
            } else {
                skipCount += current.getReplaceCount();

                SpliceSourceState<A> next = current.getNext();
                if (prev == null) {
                    state = next;
                } else {
                    prev.setNext(next);
                }
                current = next;

                if (current == null) {
                    state = normalizeStates(state);
                    current = state;
                }
            }
        }

        return false;
    }

    private static <A> SpliceSourceState<A> normalizeStates(SpliceSourceState<A> first) {
        if (first == null) {
            return null;
        }
        int minOffset = first.getStartOffset();
        SpliceSourceState<A> current = first.getNext();
        while (current != null) {
            minOffset = Math.min(minOffset, current.getStartOffset());
            current = current.getNext();
        }
        current = first;
        while (current != null) {
            current.setStartOffset(current.getStartOffset() - minOffset);
            current = current.getNext();
        }
        return first;
    }
}
