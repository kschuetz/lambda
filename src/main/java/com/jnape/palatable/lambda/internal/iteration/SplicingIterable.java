package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Cons.cons;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldRight.foldRight;
import static com.jnape.palatable.lambda.functor.builtin.Lazy.lazy;
import static java.util.Collections.singletonList;

public class SplicingIterable<A> implements Iterable<A> {
    private final Iterable<SpliceSource<A>> sources;

    public SplicingIterable(Iterable<SpliceSource<A>> sources) {
        this.sources = sources;
    }

    @Override
    public Iterator<A> iterator() {
        return new SplicingIterator<>(foldRight((s, acc) -> acc.fmap(s::initState),
                lazy((SpliceSourceState<A>) null),
                sources)
                .value());
    }

    public SplicingIterable<A> splice(int startIndex, int replaceCount, Iterable<A> source) {
        return new SplicingIterable<A>(cons(new SpliceSource<A>(source, startIndex, replaceCount), sources));
    }

    public static <A> SplicingIterable<A> splicingIterable(Iterable<A> initialSource) {
        if (initialSource instanceof SplicingIterable<?>) {
            return (SplicingIterable<A>) initialSource;
        } else {
            return new SplicingIterable<>(singletonList(new SpliceSource<A>(initialSource, 0, 0)));
        }
    }
}
