package com.jnape.palatable.lambda.internal.iteration;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.coproduct.CoProduct3;
import com.jnape.palatable.lambda.functions.Fn1;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.lang.Math.max;
import static java.lang.Math.min;

public abstract class SpliceDirective<A> implements CoProduct3<SpliceDirective.Take<A>,
        SpliceDirective.Drop<A>,
        SpliceDirective.Splice<A>,
        SpliceDirective<A>> {

    public Maybe<SpliceDirective<A>> combine(SpliceDirective<A> other) {
        return nothing();
    }

    private SpliceDirective() {

    }

    public static class Take<A> extends SpliceDirective<A> {
        private final int count;

        private Take(int count) {
            this.count = count;
        }

        @Override
        public <R> R match(Fn1<? super Take<A>, ? extends R> aFn, Fn1<? super Drop<A>, ? extends R> bFn, Fn1<? super Splice<A>, ? extends R> cFn) {
            return aFn.apply(this);
        }

        @Override
        public Maybe<SpliceDirective<A>> combine(SpliceDirective<A> other) {
            return other.match(
                    t -> just(new Take<>(min(count, t.getCount()))),
                    __ -> nothing(),
                    __ -> nothing());
        }

        public int getCount() {
            return count;
        }
    }

    public static class Drop<A> extends SpliceDirective<A> {
        private final int count;

        private Drop(int count) {
            this.count = count;
        }

        @Override
        public <R> R match(Fn1<? super Take<A>, ? extends R> aFn, Fn1<? super Drop<A>, ? extends R> bFn, Fn1<? super Splice<A>, ? extends R> cFn) {
            return bFn.apply(this);
        }

        @Override
        public Maybe<SpliceDirective<A>> combine(SpliceDirective<A> other) {
            return other.match(
                    __ -> nothing(),
                    d -> just(new Drop<>(count + d.getCount())),
                    __ -> nothing());
        }

        public int getCount() {
            return count;
        }
    }

    public static class Splice<A> extends SpliceDirective<A> {

        private final int startOffset;
        private final int replaceCount;
        private final Iterable<A> source;

        private Splice(int startOffset, int replaceCount, Iterable<A> source) {
            this.source = source;
            this.startOffset = startOffset;
            this.replaceCount = replaceCount;
        }

        @Override
        public <R> R match(Fn1<? super Take<A>, ? extends R> aFn, Fn1<? super Drop<A>, ? extends R> bFn, Fn1<? super Splice<A>, ? extends R> cFn) {
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

    public static <A> Take<A> take(int count) {
        return new Take<>(max(0, count));
    }

    public static <A> Drop<A> drop(int count) {
        return new Drop<>(max(0, count));
    }

    public static <A> Splice<A> splice(int startOffset, int replaceCount, Iterable<A> source) {
        if (replaceCount < 0) {
            startOffset = startOffset - replaceCount;
            replaceCount = -replaceCount;
        }
        return new Splice<>(max(0, startOffset), replaceCount, source);
    }

}
