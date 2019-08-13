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

        SpliceSourceState<A> prev = null;
        SpliceSourceState<A> current = state;

        while (state != null && state.getStartOffset() > 0) {
            state.setStartOffset(state.getStartOffset() - 1);
            prev = state;
            state = state.getNext();
        }

        if (state != null) {
            if (state.getStartOffset() == 0) {
                if (state.getSource().hasNext()) {
                    cache = state.getSource().next();
                    return true;
                }
            }


        }

        return false;
    }
}
