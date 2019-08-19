package com.jnape.palatable.lambda.internal.iteration;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.coproduct.CoProduct3;
import com.jnape.palatable.lambda.functions.Fn1;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.lang.Math.min;

public abstract class SpliceDirective<A> implements CoProduct3<SpliceDirective.Taking<A>,
        SpliceDirective.Dropping<A>,
        SpliceDirective.Splicing<A>,
        SpliceDirective<A>> {

    public Maybe<SpliceDirective<A>> prepend(SpliceDirective<A> other) {
        return nothing();
    }

    private SpliceDirective() {

    }

    public static class Taking<A> extends SpliceDirective<A> {
        private final int count;

        private Taking(int count) {
            this.count = count;
        }

        @Override
        public <R> R match(Fn1<? super Taking<A>, ? extends R> aFn, Fn1<? super Dropping<A>, ? extends R> bFn, Fn1<? super Splicing<A>, ? extends R> cFn) {
            return aFn.apply(this);
        }

        @Override
        public Maybe<SpliceDirective<A>> prepend(SpliceDirective<A> other) {
            return other.match(
                    t -> just(new Taking<>(min(count, t.getCount()))),
                    __ -> nothing(),
                    __ -> nothing());
        }

        public int getCount() {
            return count;
        }
    }

    public static class Dropping<A> extends SpliceDirective<A> {
        private final int count;

        private Dropping(int count) {
            this.count = count;
        }

        @Override
        public <R> R match(Fn1<? super Taking<A>, ? extends R> aFn, Fn1<? super Dropping<A>, ? extends R> bFn, Fn1<? super Splicing<A>, ? extends R> cFn) {
            return bFn.apply(this);
        }

        @Override
        public Maybe<SpliceDirective<A>> prepend(SpliceDirective<A> other) {
            return other.match(
                    __ -> nothing(),
                    d -> just(new Dropping<>(count + d.getCount())),
                    __ -> nothing());
        }

        public int getCount() {
            return count;
        }
    }

    public static class Splicing<A> extends SpliceDirective<A> {

        private final int startOffset;
        private final int replaceCount;
        private final Iterable<A> source;

        private Splicing(int startOffset, int replaceCount, Iterable<A> source) {
            this.source = source;
            this.startOffset = startOffset;
            this.replaceCount = replaceCount;
        }

        @Override
        public <R> R match(Fn1<? super Taking<A>, ? extends R> aFn, Fn1<? super Dropping<A>, ? extends R> bFn, Fn1<? super Splicing<A>, ? extends R> cFn) {
            return cFn.apply(this);
        }

        public Iterable<A> getSource() {
            return source;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public int getReplaceCount() {
            return replaceCount;
        }
    }

    public static <A> SpliceDirective.Taking<A> taking(int count) {
        return new Taking<>(count);
    }

    public static <A> SpliceDirective.Dropping<A> dropping(int count) {
        return new Dropping<>(count);
    }

    public static <A> SpliceDirective.Splicing<A> splicing(int startOffset, int replaceCount, Iterable<A> source) {
        return new Splicing<>(startOffset, replaceCount, source);
    }

}
