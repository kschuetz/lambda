package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Cons.cons;
import static java.util.Collections.singletonList;

public final class SplicingIterable<A> implements Iterable<A> {

    private final Iterable<SpliceSegment<A>> segments;

    public SplicingIterable(Iterable<SpliceSegment<A>> segments) {
        this.segments = segments;
    }

    @Override
    public Iterator<A> iterator() {
        return new SplicingIterator<>(segments);
    }

    public SplicingIterable<A> splice(int startIndex, int replaceCount, Iterable<A> source) {
        return new SplicingIterable<>(cons(new SpliceSegment<>(startIndex, replaceCount, source), segments));
    }

    public static <A> SplicingIterable<A> splicingIterable(Iterable<A> initialSource) {
        if (initialSource instanceof SplicingIterable<?>) {
            return (SplicingIterable<A>) initialSource;
        } else {
            return new SplicingIterable<>(singletonList(new SpliceSegment<>(0, 0, initialSource)));
        }
    }

}
