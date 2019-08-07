package com.jnape.palatable.lambda.internal.iteration;

import java.util.ArrayList;

import static com.jnape.palatable.lambda.internal.iteration.SpliceSegment.takeAll;

public class SplicePlan<A> {
    private final ArrayList<Iterable<A>> sources;
    private final ArrayList<SpliceSegment<Integer>> segments;

    private SplicePlan(ArrayList<Iterable<A>> sources, ArrayList<SpliceSegment<Integer>> segments) {
        this.sources = sources;
        this.segments = segments;
    }

    public ArrayList<Iterable<A>> getSources() {
        return sources;
    }

    public ArrayList<SpliceSegment<Integer>> getSegments() {
        return segments;
    }

    public static <A> SplicePlan<A> splicePlan(Iterable<A> initialSource) {
        ArrayList<Iterable<A>> sources = new ArrayList<>();
        sources.add(initialSource);
        ArrayList<SpliceSegment<Integer>> segments = new ArrayList<>();
        segments.add(takeAll(0));
        return new SplicePlan<>(sources, segments);
    }
}
