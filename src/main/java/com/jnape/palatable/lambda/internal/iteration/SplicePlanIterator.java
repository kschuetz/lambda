package com.jnape.palatable.lambda.internal.iteration;

import java.util.ArrayList;
import java.util.Iterator;

import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;

public final class SplicePlanIterator<A> extends ImmutableIterator<A> {
    private final ArrayList<Iterable<A>> sources;
    private final Iterator<SpliceSegment<Integer>> segmentIterator;
    private final ArrayList<Iterator<A>> sourceIterators;

    public SplicePlanIterator(Iterable<Iterable<A>> sources, Iterator<SpliceSegment<Integer>> segmentIterator) {
        this.sources = toCollection(ArrayList::new, sources);
        this.segmentIterator = segmentIterator;
        sourceIterators = new ArrayList<>(this.sources.size());
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public A next() {
        return null;
    }

    private Iterator<A> getSource(int index) {
        Iterator<A> result = sourceIterators.get(index);
        if (result == null) {
            synchronized (this) {
                result = sourceIterators.get(index);
                if (result == null) {
                    result = sources.get(index).iterator();
                    sourceIterators.set(index, result);
                }
            }
        }
        return result;
    }
}
