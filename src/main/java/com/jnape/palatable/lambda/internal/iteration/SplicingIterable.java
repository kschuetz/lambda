package com.jnape.palatable.lambda.internal.iteration;

import com.jnape.palatable.lambda.adt.Maybe;

import java.util.Iterator;

public final class SplicingIterable<A> implements Iterable<A> {

    private final ImmutableQueue<SpliceDirective<A>> segments;

    public SplicingIterable(ImmutableQueue<SpliceDirective<A>> segments) {
        this.segments = segments;
    }

    @Override
    public Iterator<A> iterator() {
        return new SplicingIterator<>(segments);
    }

    public SplicingIterable<A> concat(Iterable<A> other) {
        if (other instanceof SplicingIterable<?>) {
            return new SplicingIterable<>(segments.concat(((SplicingIterable<A>) other).segments));
        } else {
            return new SplicingIterable<>(segments.pushBack(SpliceDirective.splice(0, 0, other)));
        }
    }

    public SplicingIterable<A> take(int n) {
        return prepend(SpliceDirective.take(n));
    }

    public SplicingIterable<A> drop(int n) {
        return prepend(SpliceDirective.drop(n));
    }

    public SplicingIterable<A> splice(int startIndex, int replaceCount, Iterable<A> source) {
        return prepend(SpliceDirective.splice(startIndex, replaceCount, source));
    }

    private SplicingIterable<A> prepend(SpliceDirective<A> directive) {
        Maybe<SpliceDirective<A>> spliceDirectiveMaybe = segments.head().flatMap(h -> h.combine(directive));

        ImmutableQueue<SpliceDirective<A>> newSegments = spliceDirectiveMaybe
                .match(__ -> segments.pushFront(directive),
                        newDirective -> segments.tail().pushFront(newDirective));

        return new SplicingIterable<>(newSegments);
    }

    public static <A> SplicingIterable<A> splicingIterable(Iterable<A> initialSource) {
        if (initialSource instanceof SplicingIterable<?>) {
            return (SplicingIterable<A>) initialSource;
        } else {
            return new SplicingIterable<A>(ImmutableQueue.<SpliceDirective<A>>empty()
                    .pushFront(SpliceDirective.splice(0, 0, initialSource)));
        }
    }

}
