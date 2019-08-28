package com.jnape.palatable.lambda.functions.builtin.fn2;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.traitor.annotations.TestTraits;
import com.jnape.palatable.traitor.runners.Traits;
import org.junit.Test;
import org.junit.runner.RunWith;
import testsupport.traits.EmptyIterableSupport;
import testsupport.traits.FiniteIteration;
import testsupport.traits.ImmutableIteration;
import testsupport.traits.Laziness;

import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Drop.drop;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Iterate.iterate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.monoid.builtin.Concat.concat;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertThat;
import static testsupport.matchers.IterableMatcher.iterates;

@RunWith(Traits.class)
public class TakeTest {

    @TestTraits({FiniteIteration.class, EmptyIterableSupport.class, ImmutableIteration.class, Laziness.class})
    public Fn1<Iterable<Object>, Iterable<Object>> createTestSubject() {
        return take(10);
    }

    @Test
    public void takesElementsUpToLimit() {
        Iterable<String> names = asList("Moe", "Larry", "Curly", "Shemp");
        assertThat(take(3, names), iterates("Moe", "Larry", "Curly"));
    }

    @Test
    public void iteratesEntireIterableIfLessElementsThanLimit() {
        Iterable<Integer> oneTwoThree = asList(1, 2, 3);
        assertThat(take(4, oneTwoThree), iterates(1, 2, 3));
    }

    @Test
    public void takesNothingFromEmptyIterable() {
        assertThat(take(1, emptyList()), iterates());
    }

    // new

    @Test
    public void stackSafety() {
        int stackBlowingNumber = 10_000;
        Iterable<Integer> subject = iterate(acc -> acc + 1, 0);
        for (int i = 1; i <= stackBlowingNumber; i++) {
            subject = take(i, subject);
        }
        assertThat(subject, iterates(0));
    }

    @Test
    public void whenCombinedTakesMinimum() {
        List<Integer> source = asList(1, 2, 3, 4, 5, 6);
        assertThat(take(5, take(4, take(3, take(2, take(1, source))))),
                iterates(1));
        assertThat(take(1, take(2, take(3, take(4, take(5, source))))),
                iterates(1));
        assertThat(take(1, take(2, take(0, take(3, take(4, source))))),
                iterates());
    }

    @Test
    public void compoundDrop() {
        Iterable<Integer> source = drop(0, drop(1, drop(2, drop(3, asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)))));
        assertThat(take(0, source), iterates());
        assertThat(take(1, source), iterates(7));
        assertThat(take(3, source), iterates(7, 8, 9));
        assertThat(take(100, source), iterates(7, 8, 9, 10));
    }

    @Test
    public void compoundConcat() {
        Iterable<Integer> source = concat(asList(1, 2), concat(asList(3, 4), asList(5, 6)));
        assertThat(take(0, source), iterates());
        assertThat(take(1, source), iterates(1));
        assertThat(take(5, source), iterates(1, 2, 3, 4, 5));
        assertThat(take(100, source), iterates(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void compoundConcatDrop() {
        Iterable<Integer> source = concat(drop(0, asList(1, 2, 3, 4)),
                drop(1, concat(drop(2, asList(3, 4, 5, 6)),
                        drop(3, asList(7, 8, 9, 10, 11, 12)))));
        assertThat(take(0, source), iterates());
        assertThat(take(1, source), iterates(1));
        assertThat(take(6, source), iterates(1, 2, 3, 4, 6, 10));
        assertThat(take(100, source), iterates(1, 2, 3, 4, 6, 10, 11, 12));
    }

    // drop
    // concat
    // concat/drop
    // drop/take
    // concat/take
    // concat/drop/take

}
