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
            dumpState(state, "  state = ");
            dumpState(current, "  current = ");
            int startOffset = current.getStartOffset();
            if (startOffset > 0) {
                if (debugging) {
                    System.out.println("  s: " + startOffset);
                }
                current.decStartOffset();
                prev = current;
                current = current.getNext();
                continue;
            }
            // startOffset is 0
            Iterator<A> source = current.getSource();
//            while (skipCount > 0 && source.hasNext()) {
//
//                A skippedValue = source.next();
//                System.out.println("  skip " + skipCount + "; skipped " + skippedValue);
//                skipCount -= 1;
//            }
            if (skipCount > 0 && source.hasNext()) {

                A skippedValue = source.next();
                if (debugging) {
                    System.out.println("  skip " + skipCount + "; skipped " + skippedValue);
                }
                skipCount -= 1;
                prev = null;
                current = state;
                continue;
            }

            if (source.hasNext()) {
                cache = source.next();
                dumpState(state, "Yielding " + cache + ": ");
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
//                    skipCount = 0;
                    state = normalizeStates(state);
                    prev = null;
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
        dumpState(first, "** BEFORE NORMALIZE");
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
        dumpState(first, "** AFTER NORMALIZE");
        return first;
    }

    private static <A> void dumpState(SpliceSourceState<A> state, String message) {
        if (!debugging) {
            return;
        }
        System.out.print(message);
        SpliceSourceState<A> current = state;
        while (current != null) {
            System.out.print(current);
            current = current.getNext();
        }
        System.out.println();
    }

    private static <A> void dumpState(SpliceSourceState<A> state) {
        dumpState(state, "\n");
    }

    public static boolean debugging = false;
}
