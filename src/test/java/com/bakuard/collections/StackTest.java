package com.bakuard.collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

class StackTest {

    @Test
    @DisplayName("""
            Stack(other):
             other is empty
             => copy is equivalent to original
            """)
    public void Stack_copy1() {
        Stack<Integer> expected = new Stack<>();

        Stack<Integer> actual = new Stack<>(expected);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Stack(other):
             other is not empty
             => copy is equivalent to original
            """)
    public void Stack_copy2() {
        Stack<Integer> expected = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        Stack<Integer> actual = new Stack<>(expected);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Stack(other):
             other is not empty
             => copy contains the same objects
            """)
    public void Stack_copy3() {
        Stack<Object> expected = Stack.of(new Object(), new Object(), new Object());

        Stack<Object> actual = new Stack<>(expected);

        Assertions.assertThat(actual.at(0)).isSameAs(expected.at(0));
        Assertions.assertThat(actual.at(1)).isSameAs(expected.at(1));
        Assertions.assertThat(actual.at(2)).isSameAs(expected.at(2));
    }

    @Test
    @DisplayName("""
            Stack(other):
             change the original must not the effect the copy
            """)
    public void Stack_copy4() {
        Stack<Integer> expected = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        Stack<Integer> original = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        Stack<Integer> copy = new Stack(original);
        copy.clear();
        copy.putAllOnLast(5, 6, 7, 8, 9);

        Assertions.assertThat(original).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Stack.of(...data):
             data[] is null
             => exception
            """)
    public void of1() {
        Assertions.assertThatNullPointerException().
                isThrownBy(() -> Stack.<Integer>of(null));
    }

    @Test
    @DisplayName("""
            Stack.of(...data):
             data[] is empty
             => return empty Stack
            """)
    public void of2() {
        Stack<Integer> stack = Stack.of();

        Assertions.assertThat(stack.size()).isZero();
    }

    @Test
    @DisplayName("""
            Stack.of(...data):
             data[] contains several items
             => return Stack with this item
            """)
    public void of3() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        Assertions.assertThat(stack).
                containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
    }

    @Test
    @DisplayName("""
            Stack.of(...data):
             change data[] after creating Stack
             => new object Stack don't change
            """)
    public void of4() {
        Integer[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        Stack<Integer> stack = Stack.of(data);
        Arrays.fill(data, 100);

        Assertions.assertThat(stack).
                containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
    }

    @Test
    @DisplayName("""
            putLast(value):
             => add item
            """)
    public void putLast1() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        Stack<Integer> expected = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100);

        actual.putLast(100);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            putLast(value):
             => increase size
            """)
    public void putLast2() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putLast(100);

        Assertions.assertThat(actual.size()).isEqualTo(17);
    }

    @Test
    @DisplayName("""
            putLast(value):
             add several items
            """)
    public void putLast3() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3);

        for(int i = 4; i < 1000; i++) actual.putLast(i);

        Assertions.assertThat(actual).
                hasSameElementsAs(IntStream.range(0, 1000).boxed().toList());
        Assertions.assertThat(actual.size()).isEqualTo(1000);
    }

    @Test
    @DisplayName("""
            putAllOnLast(iterable):
             added iterable is null
             => exception
            """)
    public void putAllOnLast_Iterable1() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatNullPointerException().
                isThrownBy(() -> stack.putAllOnLast((Iterable<Integer>) null));
    }

    @Test
    @DisplayName("""
            putAllOnLast(iterable):
             added iterable is empty,
             current stack is empty
             => do nothing
            """)
    public void putAllOnLast_Iterable2() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnLast(new Array<>());

        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            putAllOnLast(iterable):
             added iterable is empty,
             current stack is not empty
             => do nothing
            """)
    public void putAllOnLast_Iterable3() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putAllOnLast(new Array<>());

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnLast(iterable):
             added iterable is not empty,
             current stack is empty
             => add items
            """)
    public void putAllOnLast_Iterable4() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnLast(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnLast(iterable):
             added iterable is not empty,
             current stack is not empty
             => add items, doesn't change existed items in current stack
            """)
    public void putAllOnLast_Iterable5() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putAllOnLast(List.of(6, 7, 8, 9, 10, 11));

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 6, 7, 8, 9, 10, 11));
    }

    @Test
    @DisplayName("""
            putAllOnLast(iterable):
             added iterable has single item,
             current stack has single item
             => add item
            """)
    public void putAllOnLast_Iterable6() {
        Stack<Integer> actual = Stack.of(100);

        actual.putAllOnLast(List.of(200));

        Assertions.assertThat(actual).isEqualTo(Stack.of(100, 200));
    }
    
    @Test
    @DisplayName("""
            putAllOnLast(data):
             added data is null
             => exception
            """)
    public void putAllOnLast_Data1() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatNullPointerException().
                isThrownBy(() -> stack.putAllOnLast((Integer[]) null));
    }

    @Test
    @DisplayName("""
            putAllOnLast(data):
             added data is empty,
             current stack is empty
             => do nothing
            """)
    public void putAllOnLast_Data2() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnLast();

        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            putAllOnLast(data):
             added data is empty,
             current stack is not empty
             => do nothing
            """)
    public void putAllOnLast_Data3() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putAllOnLast();

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnLast(array):
             added data is not empty,
             current stack is empty
             => add items
            """)
    public void putAllOnLast_Data4() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnLast(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnLast(data):
             added data is not empty,
             current stack is not empty
             => add items, doesn't change existed items in current stack
            """)
    public void putAllOnLast_Data5() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5);

        actual.putAllOnLast(6, 7, 8, 9, 10, 11);

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
    }

    @Test
    @DisplayName("""
            putAllOnLast(data):
             added data has single item,
             current stack has single item
             => add item
            """)
    public void putAllOnLast_Data6() {
        Stack<Integer> actual = Stack.of(100);

        actual.putAllOnLast(200);

        Assertions.assertThat(actual).isEqualTo(Stack.of(100, 200));
    }

    @Test
    @DisplayName("""
            removeLast():
             stack is empty
             => return null
            """)
    public void removeLast1() {
        Stack<Integer> stack = new Stack<>();

        Integer actual = stack.removeLast();

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("""
            removeLast():
             stack has single item,
             remove all item
             => return this item and than null
            """)
    public void removeLast2() {
        Stack<Integer> stack = Stack.of(100);

        Assertions.assertThat(stack.removeLast()).isEqualTo(100);
        Assertions.assertThat(stack.removeLast()).isNull();
    }

    @Test
    @DisplayName("""
            removeLast():
             stack has several items
             => return all items in LIFO order
            """)
    public void removeLast3() {
        Stack<Integer> stack = Stack.of(0, null, 2, null, 4);

        Array<Integer> actual = new Array<>();
        for(int i = 0; i < 5; i++) {
            actual.append(stack.removeLast());
        }

        Assertions.assertThat(actual).isEqualTo(Array.of(4, null, 2, null, 0));
    }

    @Test
    @DisplayName("""
            removeLast():
             stack has single item,
             remove all item
             => stack must be empty
            """)
    public void removeLast4() {
        Stack<Integer> stack = Stack.of(100);

        stack.removeLast();

        Assertions.assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            removeLast():
             stack has several items,
             remove several items
             => change stack after method call
            """)
    public void removeLast5() {
        Stack<Integer> stack = Stack.of(0, null, 2, null, 4);

        stack.removeLast();
        stack.removeLast();
        stack.removeLast();

        Assertions.assertThat(stack).isEqualTo(Stack.of(0, null));
    }

    @Test
    @DisplayName("""
            tryRemoveLast():
             stack is empty
             => throw exception
            """)
    public void tryRemoveLast1() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(stack::tryRemoveLast);
    }

    @Test
    @DisplayName("""
            tryRemoveLast():
             stack has single item,
             remove this item
             => return this item and than throw exception
            """)
    public void tryRemoveLast2() {
        Stack<Integer> stack = Stack.of(100);

        Assertions.assertThat(stack.tryRemoveLast()).isEqualTo(100);
        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(stack::tryRemoveLast);
    }

    @Test
    @DisplayName("""
            tryRemoveLast():
             stack has several items
             => return all items in LIFO order
            """)
    public void tryRemoveLast3() {
        Stack<Integer> stack = Stack.of(0, null, 2, null, 4);

        Array<Integer> actual = new Array<>();
        for(int i = 0; i < 5; i++) {
            actual.append(stack.tryRemoveLast());
        }

        Assertions.assertThat(actual).isEqualTo(Array.of(4, null, 2, null, 0));
    }

    @Test
    @DisplayName("""
            tryRemoveLast():
             stack has single item,
             remove this item
             => stack must be empty
            """)
    public void tryRemoveLast4() {
        Stack<Integer> stack = Stack.of(100);

        stack.tryRemoveLast();

        Assertions.assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            tryRemoveLast():
             stack has several items,
             remove several items
             => change stack after method call
            """)
    public void tryRemoveLast5() {
        Stack<Integer> stack = Stack.of(0, null, 2, null, 4);

        stack.tryRemoveLast();
        stack.tryRemoveLast();
        stack.tryRemoveLast();

        Assertions.assertThat(stack).isEqualTo(Stack.of(0, null));
    }

    @Test
    @DisplayName("""
            clear():
             stack is empty
             => do nothing
            """)
    public void clear1() {
        Stack<Integer> stack = new Stack<>();

        stack.clear();

        Assertions.assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            clear():
             stack has several items
             => remove all items
            """)
    public void clear2() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        stack.clear();

        Assertions.assertThat(stack).isEqualTo(new Stack<>());
    }

    @Test
    @DisplayName("""
            clear():
             stack has several items
             => stack size after `clear()` must be zero
            """)
    public void clear3() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        stack.clear();

        Assertions.assertThat(stack.size()).isZero();
    }

    @Test
    @DisplayName("equals(Object o): first operand don't equal second => return false")
    public void equals1() {
        Stack<Integer> firstOperand = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> secondOperand = Stack.of(10, 20, null, 50, 40);

        Assertions.assertThat(firstOperand).isNotEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): first operand equal second, entry arrays size is equal => return true")
    public void equals2() {
        Stack<Integer> firstOperand = Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120);
        Stack<Integer> secondOperand = Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): first operand equal second, entry array size isn't equal => return true")
    public void equals3() {
        Stack<Integer> firstOperand = new Stack<>();
        Stack<Integer> secondOperand = Stack.of(10, 20, 30, 40, 50);

        for(int i = 0; i < 1000; i++) firstOperand.putLast(i);
        for(int i = 0; i < 1000; i++) firstOperand.removeLast();
        firstOperand.putAllOnLast(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): idempotence property")
    public void equals4() {
        Stack<Integer> origin = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(origin.equals(origin)).isTrue();
    }

    @Test
    @DisplayName("equals(Object o): commutative property")
    public void equals5() {
        Stack<Integer> first = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> second = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(first.equals(second) == second.equals(first)).isTrue();
    }

    @Test
    @DisplayName("equals(Object o): transitive property")
    public void equals6() {
        Stack<Integer> first = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> second = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> third = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(first.equals(second) == second.equals(third) == first.equals(third)).
                isTrue();
    }

}