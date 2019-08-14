package com.jnape.palatable.lambda.functions.builtin.fn4;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection;
import com.jnape.palatable.traitor.annotations.TestTraits;
import com.jnape.palatable.traitor.runners.Traits;
import org.junit.Test;
import org.junit.runner.RunWith;
import testsupport.traits.EmptyIterableSupport;
import testsupport.traits.FiniteIteration;
import testsupport.traits.ImmutableIteration;
import testsupport.traits.Laziness;

import java.util.ArrayList;
import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Repeat.repeat;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functions.builtin.fn4.Splice.splice;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertThat;
import static testsupport.matchers.IterableMatcher.iterates;

@RunWith(Traits.class)
public class SpliceTest {

    @TestTraits({FiniteIteration.class, EmptyIterableSupport.class, ImmutableIteration.class, Laziness.class})
    public Fn1<Iterable<Object>, Iterable<Object>> createTestSubject() {
        return splice(5, 3, asList(100, 200, 300, 400, 500));
    }

    @Test
    public void spliceIsAttachedToEndIfOriginalIsNotLongEnough() {
        assertThat(splice(100, 0, asList(4, 5, 6), asList(1, 2, 3)),
                iterates(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void splicingIntoEmptyIterableEqualsReplacement() {
        assertThat(splice(100, 100, asList(1, 2, 3), emptyList()),
                iterates(1, 2, 3));
    }

    @Test
    public void splicesEmptyWithoutReplacing() {
        assertThat(splice(3, 0, emptyList(), asList(1, 2, 3, 4, 5)),
                iterates(1, 2, 3, 4, 5));
    }

    @Test
    public void spliceEmptyAndReplaces() {
        assertThat(splice(2, 2, emptyList(), asList(1, 2, 3, 4, 5)),
                iterates(1, 2, 5));
    }

    @Test
    public void splicesOntoFrontWithoutReplacing() {
        assertThat(splice(0, 0, asList(100, 200, 300), asList(1, 2, 3, 4, 5, 6, 7, 8)),
                iterates(100, 200, 300, 1, 2, 3, 4, 5, 6, 7, 8));
    }

    @Test
    public void splicesOntoFrontAndReplaces() {
        assertThat(splice(0, 5, asList(100, 200, 300), asList(1, 2, 3, 4, 5, 6, 7, 8)),
                iterates(100, 200, 300, 6, 7, 8));
    }

    @Test
    public void splicesIntoMiddleWithoutReplacing() {
        assertThat(splice(3, 0, asList(100, 200, 300), asList(1, 2, 3, 4, 5, 6, 7, 8)),
                iterates(1, 2, 3, 100, 200, 300, 4, 5, 6, 7, 8));
    }

    @Test
    public void splicesIntoMiddleAndReplaces() {
        assertThat(splice(3, 3, asList(100, 200, 300), asList(1, 2, 3, 4, 5, 6, 7, 8)),
                iterates(1, 2, 3, 100, 200, 300, 7, 8));
    }

    @Test
    public void splicesOntoEndWithoutReplacing() {
        assertThat(splice(0, 0, asList(100, 200, 300), asList(1, 2, 3, 4, 5, 6, 7, 8)),
                iterates(100, 200, 300, 1, 2, 3, 4, 5, 6, 7, 8));
    }

    @Test
    public void spliceReplacesEntireOriginal() {
        assertThat(splice(0, 100, asList(100, 200, 300), asList(1, 2, 3, 4, 5, 6, 7, 8)),
                iterates(100, 200, 300));
    }

    @Test
    public void spliceInfiniteIntoFiniteOriginal() {
        assertThat(take(10, splice(5, 0, repeat(100), asList(1, 2, 3, 4, 5, 6, 7, 8))),
                iterates(1, 2, 3, 4, 5, 100, 100, 100, 100, 100));
    }

    @Test
    public void spliceFiniteIntoInfiniteOriginal() {
        assertThat(take(10, splice(3, 0, asList(1, 2, 3), repeat(100))),
                iterates(100, 100, 100, 1, 2, 3, 100, 100, 100, 100));
    }

    @Test
    public void spliceInfiniteIntoInfiniteOriginal() {
        assertThat(take(10, splice(3, 0, repeat(100), repeat(1))),
                iterates(1, 1, 1, 100, 100, 100, 100, 100, 100, 100));
    }

    @Test
    public void compoundSplice() {
        List<Integer> list1 = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> list2 = asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        List<Integer> list3 = asList(21, 22, 23, 24, 25);
        List<Integer> list4 = asList(26, 27, 28, 29, 30);
        List<Integer> list5 = asList(31, 32, 33, 34);
        List<Integer> list6 = asList(35, 36, 37, 38);
        List<Integer> list7 = asList(39, 40);

        System.out.println("----------");
        Iterable<Integer> result1 = splice(4, 0, list2, list1);
        //System.out.println(ToCollection.<Integer, ArrayList<Integer>>toCollection(ArrayList::new, result1));
        assertThat(result1, iterates(1, 2, 3, 4, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 5, 6, 7, 8, 9, 10));

        System.out.println("---- START ----");
        Iterable<Integer> result2 = splice(3, 1, list3, result1);
        System.out.println(ToCollection.<Integer, ArrayList<Integer>>toCollection(ArrayList::new, result2));
        assertThat(result2, iterates(1, 2, 3, 21, 22, 23, 24, 25, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 5, 6, 7, 8, 9, 10));

//        Iterable<Integer> result3 = splice(2, 2, list4, result2);
//        System.out.println(ToCollection.<Integer, ArrayList<Integer>>toCollection(ArrayList::new, result3));
//        Iterable<Integer> result4 = splice(1, 3, list5, result3);
//        System.out.println(ToCollection.<Integer, ArrayList<Integer>>toCollection(ArrayList::new, result4));
//        Iterable<Integer> result5 = splice(0, 4, list6, result4);
//        System.out.println(ToCollection.<Integer, ArrayList<Integer>>toCollection(ArrayList::new, result5));
//        Iterable<Integer> result6 = splice(100, 100, list7, result5);
//        System.out.println(ToCollection.<Integer, ArrayList<Integer>>toCollection(ArrayList::new, result6));

    }
}
