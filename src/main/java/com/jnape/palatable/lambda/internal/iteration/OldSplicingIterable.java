package com.jnape.palatable.lambda.internal.iteration;

import java.util.ArrayList;
import java.util.Iterator;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Snoc.snoc;
import static com.jnape.palatable.lambda.internal.iteration.SpliceSegment.takeAll;
import static java.util.Collections.singletonList;

public class OldSplicingIterable<A> implements Iterable<A> {
    private final Iterable<Iterable<A>> sources;
    private final int sourceCount;
    private final ArrayList<SpliceSegment<Integer>> segments;

    private OldSplicingIterable(Iterable<Iterable<A>> sources, int sourceCount, ArrayList<SpliceSegment<Integer>> segments) {
        this.sources = sources;
        this.sourceCount = sourceCount;
        this.segments = segments;
    }

    @Override
    public Iterator<A> iterator() {
        return new SplicePlanIterator<>(sources, segments.iterator());
    }

    public OldSplicingIterable<A> splice(int startIndex, int replaceCount, Iterable<A> source) {
        int sourceIndex = sourceCount;

        // TODO: merge segments

        return new OldSplicingIterable<A>(snoc(source, sources), sourceCount + 1, segments);
    }

    public static <A> OldSplicingIterable<A> splicingIterable(Iterable<A> initialSource) {
        if (initialSource instanceof OldSplicingIterable<?>) {
            return (OldSplicingIterable<A>) initialSource;
        } else {
            ArrayList<SpliceSegment<Integer>> segments = new ArrayList<>();
            segments.add(takeAll(0));
            return new OldSplicingIterable<>(singletonList(initialSource), 1, segments);
        }
    }
}
