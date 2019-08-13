package com.jnape.palatable.lambda.internal.iteration;

import java.util.Iterator;

public class SpliceSourceState<A> {
    private int startOffset;
    private int replaceCount;
    private Iterator<A> source;
    private boolean sourceExhausted;
    private SpliceSourceState<A> next;

    public SpliceSourceState(int startOffset, int replaceCount, Iterator<A> source, SpliceSourceState<A> next) {
        this.startOffset = startOffset;
        this.replaceCount = replaceCount;
        this.source = source;
        this.sourceExhausted = false;
        this.next = next;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getReplaceCount() {
        return replaceCount;
    }

    public void setReplaceCount(int replaceCount) {
        this.replaceCount = replaceCount;
    }

    public Iterator<A> getSource() {
        return source;
    }

    public boolean isSourceExhausted() {
        return sourceExhausted;
    }

    public void setSourceExhausted(boolean sourceExhausted) {
        this.sourceExhausted = sourceExhausted;
    }

    public SpliceSourceState<A> getNext() {
        return next;
    }

    public void setNext(SpliceSourceState<A> next) {
        this.next = next;
    }
}
