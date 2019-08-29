package com.jnape.palatable.lambda.internal.iteration;

import com.jnape.palatable.lambda.adt.Maybe;

import java.util.Iterator;

import static java.util.Collections.singleton;

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
        return addToFront(SpliceDirective.take(n));
    }

    public SplicingIterable<A> drop(int n) {
        return addToFront(SpliceDirective.drop(n));
    }

    public SplicingIterable<A> splice(int startIndex, int replaceCount, Iterable<A> source) {
        return addToFront(SpliceDirective.splice(startIndex, replaceCount, source));
    }

    public SplicingIterable<A> prepend(A element) {
        return addToFront(SpliceDirective.splice(0, 0, singleton(element)));
    }

    public SplicingIterable<A> append(A element) {
        return addToBack(SpliceDirective.splice(0, 0, singleton(element)));
    }

    private SplicingIterable<A> addToFront(SpliceDirective<A> directive) {
        Maybe<SpliceDirective<A>> maybeCombined = segments.head().flatMap(h -> h.combine(directive));

        ImmutableQueue<SpliceDirective<A>> newSegments = maybeCombined
                .match(__ -> segments.pushFront(directive),
                        newDirective -> segments.tail().pushFront(newDirective));

        return new SplicingIterable<>(newSegments);
    }

    private SplicingIterable<A> addToBack(SpliceDirective<A> directive) {
        return new SplicingIterable<>(segments.pushBack(directive));
    }

    public static <A> SplicingIterable<A> splicingIterable(Iterable<A> initialSource) {
        if (initialSource instanceof SplicingIterable<?>) {
            return (SplicingIterable<A>) initialSource;
        } else {
            return new SplicingIterable<>(ImmutableQueue.<SpliceDirective<A>>empty()
                    .pushFront(SpliceDirective.splice(0, 0, initialSource)));
        }
    }

}
