package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;

public final class SplicingIterable<A> implements Iterable<A> {

    private final ImmutableQueue<SpliceSegment<A>> segments;

    public SplicingIterable(ImmutableQueue<SpliceSegment<A>> segments) {
        this.segments = segments;
    }

    @Override
    public Iterator<A> iterator() {
        return new SplicingIterator<>(segments);
    }

    public SplicingIterable<A> splice(int startIndex, int replaceCount, Iterable<A> source) {
        return new SplicingIterable<>(segments.pushFront(new SpliceSegment<>(startIndex, replaceCount, source)));
    }

    public static <A> SplicingIterable<A> splicingIterable(Iterable<A> initialSource) {
        if (initialSource instanceof SplicingIterable<?>) {
            return (SplicingIterable<A>) initialSource;
        } else {
            return new SplicingIterable<>(ImmutableQueue.<SpliceSegment<A>>empty()
                    .pushFront(new SpliceSegment<>(0, 0, initialSource)));
        }
    }

}
