package com.jnape.palatable.lambda.internal.iteration;

import com.jnape.palatable.lambda.adt.coproduct.CoProduct3;
import com.jnape.palatable.lambda.functions.Fn1;

public abstract class SpliceSegment<Source> implements CoProduct3<SpliceSegment.Take<Source>,
        SpliceSegment.Drop<Source>,
        SpliceSegment.TakeAll<Source>,
        SpliceSegment<Source>> {

    public abstract Source getSource();

    public static class Take<Source> extends SpliceSegment<Source> {
        private final int count;
        private final Source source;

        private Take(int count, Source source) {
            this.count = count;
            this.source = source;
        }

        public int getCount() {
            return count;
        }

        @Override
        public Source getSource() {
            return source;
        }

        @Override
        public <R> R match(Fn1<? super Take<Source>, ? extends R> aFn, Fn1<? super Drop<Source>, ? extends R> bFn, Fn1<? super TakeAll<Source>, ? extends R> cFn) {
            return aFn.apply(this);
        }

    }

    public static class Drop<Source> extends SpliceSegment<Source> {
        private final int count;
        private final Source source;

        private Drop(int count, Source source) {
            this.count = count;
            this.source = source;
        }

        public int getCount() {
            return count;
        }

        @Override
        public Source getSource() {
            return source;
        }

        @Override
        public <R> R match(Fn1<? super Take<Source>, ? extends R> aFn, Fn1<? super Drop<Source>, ? extends R> bFn, Fn1<? super TakeAll<Source>, ? extends R> cFn) {
            return bFn.apply(this);
        }

    }

    public static class TakeAll<Source> extends SpliceSegment<Source> {
        private final Source source;

        private TakeAll(Source source) {
            this.source = source;
        }

        @Override
        public Source getSource() {
            return source;
        }

        @Override
        public <R> R match(Fn1<? super Take<Source>, ? extends R> aFn, Fn1<? super Drop<Source>, ? extends R> bFn, Fn1<? super TakeAll<Source>, ? extends R> cFn) {
            return cFn.apply(this);
        }
    }

    public static <Source> Take<Source> take(int count, Source source) {
        return new Take<>(count, source);
    }

    public static <Source> Drop<Source> drop(int count, Source source) {
        return new Drop<>(count, source);
    }

    public static <Source> TakeAll<Source> takeAll(Source source) {
        return new TakeAll<>(source);
    }

}
