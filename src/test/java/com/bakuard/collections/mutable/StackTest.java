package com.bakuard.collections.mutable;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
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

        Assertions.assertThat(actual.getAny(0)).isSameAs(expected.getAny(0));
        Assertions.assertThat(actual.getAny(1)).isSameAs(expected.getAny(1));
        Assertions.assertThat(actual.getAny(2)).isSameAs(expected.getAny(2));
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
        copy.putAllOnTop(5, 6, 7, 8, 9);

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

        Assertions.assertThat(stack.getLength()).isZero();
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
                containsExactly(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
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
                containsExactly(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
    }

    @Test
    @DisplayName("""
            putTop(value):
             => add item
            """)
    public void putTop1() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        Stack<Integer> expected = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100);

        actual.putTop(100);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            putTop(value):
             => increase length
            """)
    public void putTop2() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putTop(100);

        Assertions.assertThat(actual.getLength()).isEqualTo(17);
    }

    @Test
    @DisplayName("""
            putTop(value):
             add several items
            """)
    public void putTop3() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3);

        for(int i = 4; i < 1000; i++) actual.putTop(i);

        Assertions.assertThat(actual).
                hasSameElementsAs(IntStream.range(0, 1000).boxed().toList());
        Assertions.assertThat(actual.getLength()).isEqualTo(1000);
    }

    @Test
    @DisplayName("""
            putAllOnTop(stack):
             added stack is null
             => exception
            """)
    public void putAllOnTop_Stack1() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatNullPointerException().
                isThrownBy(() -> stack.putAllOnTop((Stack<Integer>) null));
    }

    @Test
    @DisplayName("""
            putAllOnTop(stack):
             added stack is empty,
             current stack is empty
             => do nothing
            """)
    public void putAllOnTop_Stack2() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnTop(new Stack<>());

        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            putAllOnTop(stack):
             added stack is empty,
             current stack is not empty
             => do nothing
            """)
    public void putAllOnTop_Stack3() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putAllOnTop(new Stack<>());

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnTop(stack):
             added stack is not empty,
             current stack is empty
             => add items
            """)
    public void putAllOnTop_Stack4() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnTop(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnTop(stack):
             added stack is not empty,
             current stack is not empty
             => add items, doesn't change existed items in current stack
            """)
    public void putAllOnTop_Stack5() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putAllOnTop(Stack.of(6, 7, 8, 9, 10, 11));

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 6, 7, 8, 9, 10, 11));
    }

    @Test
    @DisplayName("""
            putAllOnTop(stack):
             added stack has single item,
             current stack has single item
             => add item
            """)
    public void putAllOnTop_Stack6() {
        Stack<Integer> actual = Stack.of(100);

        actual.putAllOnTop(Stack.of(200));

        Assertions.assertThat(actual).isEqualTo(Stack.of(100, 200));
    }

    @Test
    @DisplayName("""
            putAllOnTop(array):
             added array is null
             => exception
            """)
    public void putAllOnTop_Array1() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatNullPointerException().
                isThrownBy(() -> stack.putAllOnTop((Array<Integer>) null));
    }

    @Test
    @DisplayName("""
            putAllOnTop(array):
             added array is empty,
             current stack is empty
             => do nothing
            """)
    public void putAllOnTop_Array2() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnTop(new Array<>());

        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            putAllOnTop(array):
             added array is empty,
             current stack is not empty
             => do nothing
            """)
    public void putAllOnTop_Array3() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putAllOnTop(new Array<>());

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnTop(array):
             added array is not empty,
             current stack is empty
             => add items
            """)
    public void putAllOnTop_Array4() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnTop(Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnTop(array):
             added array is not empty,
             current stack is not empty
             => add items, doesn't change existed items in current stack
            """)
    public void putAllOnTop_Array5() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putAllOnTop(Array.of(6, 7, 8, 9, 10, 11));

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 6, 7, 8, 9, 10, 11));
    }

    @Test
    @DisplayName("""
            putAllOnTop(array):
             added array has single item,
             current stack has single item
             => add item
            """)
    public void putAllOnTop_Array6() {
        Stack<Integer> actual = Stack.of(100);

        actual.putAllOnTop(Array.of(200));

        Assertions.assertThat(actual).isEqualTo(Stack.of(100, 200));
    }

    @Test
    @DisplayName("""
            putAllOnTop(data):
             added data is null
             => exception
            """)
    public void putAllOnTop_Data1() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatNullPointerException().
                isThrownBy(() -> stack.putAllOnTop((Integer[]) null));
    }

    @Test
    @DisplayName("""
            putAllOnTop(data):
             added data is empty,
             current stack is empty
             => do nothing
            """)
    public void putAllOnTop_Data2() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnTop();

        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            putAllOnTop(data):
             added data is empty,
             current stack is not empty
             => do nothing
            """)
    public void putAllOnTop_Data3() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        actual.putAllOnTop();

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnTop(array):
             added data is not empty,
             current stack is empty
             => add items
            """)
    public void putAllOnTop_Data4() {
        Stack<Integer> actual = new Stack<>();

        actual.putAllOnTop(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
    }

    @Test
    @DisplayName("""
            putAllOnTop(data):
             added data is not empty,
             current stack is not empty
             => add items, doesn't change existed items in current stack
            """)
    public void putAllOnTop_Data5() {
        Stack<Integer> actual = Stack.of(0, 1, 2, 3, 4, 5);

        actual.putAllOnTop(6, 7, 8, 9, 10, 11);

        Assertions.assertThat(actual).isEqualTo(Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
    }

    @Test
    @DisplayName("""
            putAllOnTop(data):
             added data has single item,
             current stack has single item
             => add item
            """)
    public void putAllOnTop_Data6() {
        Stack<Integer> actual = Stack.of(100);

        actual.putAllOnTop(200);

        Assertions.assertThat(actual).isEqualTo(Stack.of(100, 200));
    }

    @Test
    @DisplayName("""
            removeTop():
             stack is empty
             => return null
            """)
    public void removeTop1() {
        Stack<Integer> stack = new Stack<>();

        Integer actual = stack.removeTop();

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("""
            removeTop():
             stack has single item,
             remove all item
             => return this item and than null
            """)
    public void removeTop2() {
        Stack<Integer> stack = Stack.of(100);

        Assertions.assertThat(stack.removeTop()).isEqualTo(100);
        Assertions.assertThat(stack.removeTop()).isNull();
    }

    @Test
    @DisplayName("""
            removeTop():
             stack has several items
             => return all items in LIFO order
            """)
    public void removeTop3() {
        Stack<Integer> stack = Stack.of(0, null, 2, null, 4);

        Array<Integer> actual = new Array<>();
        for(int i = 0; i < 5; i++) {
            actual.append(stack.removeTop());
        }

        Assertions.assertThat(actual).isEqualTo(Array.of(4, null, 2, null, 0));
    }

    @Test
    @DisplayName("""
            removeTop():
             stack has single item,
             remove all item
             => stack must be empty
            """)
    public void removeTop4() {
        Stack<Integer> stack = Stack.of(100);

        stack.removeTop();

        Assertions.assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            removeTop():
             stack has several items,
             remove several items
             => change stack after method call
            """)
    public void removeTop5() {
        Stack<Integer> stack = Stack.of(0, null, 2, null, 4);

        stack.removeTop();
        stack.removeTop();
        stack.removeTop();

        Assertions.assertThat(stack).isEqualTo(Stack.of(0, null));
    }

    @Test
    @DisplayName("""
            tryRemoveTop():
             stack is empty
             => throw exception
            """)
    public void tryRemoveTop1() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(stack::tryRemoveTop);
    }

    @Test
    @DisplayName("""
            tryRemoveTop():
             stack has single item,
             remove this item
             => return this item and than throw exception
            """)
    public void tryRemoveTop2() {
        Stack<Integer> stack = Stack.of(100);

        Assertions.assertThat(stack.tryRemoveTop()).isEqualTo(100);
        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(stack::tryRemoveTop);
    }

    @Test
    @DisplayName("""
            tryRemoveTop():
             stack has several items
             => return all items in LIFO order
            """)
    public void tryRemoveTop3() {
        Stack<Integer> stack = Stack.of(0, null, 2, null, 4);

        Array<Integer> actual = new Array<>();
        for(int i = 0; i < 5; i++) {
            actual.append(stack.tryRemoveTop());
        }

        Assertions.assertThat(actual).isEqualTo(Array.of(4, null, 2, null, 0));
    }

    @Test
    @DisplayName("""
            tryRemoveTop():
             stack has single item,
             remove this item
             => stack must be empty
            """)
    public void tryRemoveTop4() {
        Stack<Integer> stack = Stack.of(100);

        stack.tryRemoveTop();

        Assertions.assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("""
            tryRemoveTop():
             stack has several items,
             remove several items
             => change stack after method call
            """)
    public void tryRemoveTop5() {
        Stack<Integer> stack = Stack.of(0, null, 2, null, 4);

        stack.tryRemoveTop();
        stack.tryRemoveTop();
        stack.tryRemoveTop();

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
             => stack length after `clear()` must be zero
            """)
    public void clear3() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        stack.clear();

        Assertions.assertThat(stack.getLength()).isZero();
    }

    @Test
    @DisplayName("""
            getTop():
             stack is empty
             => return null
            """)
    public void getTop1() {
        Stack<Integer> stack = new Stack<>();

        Integer actual = stack.getTop();

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("""
            getTop():
             stack not empty
             => return top
            """)
    public void getTop2() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 100);

        Integer actual = stack.getTop();

        Assertions.assertThat(actual).isEqualTo(100);
    }

    @Test
    @DisplayName("""
            getTop():
             stack not empty,
             top item is null
             => return null
            """)
    public void getTop3() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 100, null);

        Integer actual = stack.getTop();

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index = stack.getLength()
             => exception
            """)
    public void getAny1() {
        Stack<Integer> stack = Stack.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> stack.getAny(9));
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index > stack.getLength()
             => exception
            """)
    public void getAny2() {
        Stack<Integer> stack = Stack.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> stack.getAny(10));
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index < -stack.getLength()
             => exception
            """)
    public void getAny3() {
        Stack<Integer> stack = Stack.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> stack.getAny(-10));
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is empty,
             index is not negative
             => exception
            """)
    public void getAny4() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> stack.getAny(0));
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is empty,
             index is negative
             => exception
            """)
    public void getAny5() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> stack.getAny(-1));
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index = 0
             => return top
            """)
    public void getAny6() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = stack.getAny(0);

        Assertions.assertThat(actual).isEqualTo(8);
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index = stack.getLength() - 1
             => return bottom
            """)
    public void getAny7() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = stack.getAny(8);

        Assertions.assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index for middle item,
             index is positive
             => return correct item
            """)
    public void getAny8() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = stack.getAny(3);

        Assertions.assertThat(actual).isEqualTo(5);
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index = -1
             => return bottom
            """)
    public void getAny9() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = stack.getAny(-1);

        Assertions.assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index = -stack.getLength()
             => return top
            """)
    public void getAny10() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = stack.getAny(-9);

        Assertions.assertThat(actual).isEqualTo(8);
    }

    @Test
    @DisplayName("""
            getAny(index):
             stack is not empty,
             index for middle item,
             index is negative
             => return correct item
            """)
    public void getAny11() {
        Stack<Integer> stack = Stack.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = stack.getAny(-3);

        Assertions.assertThat(actual).isEqualTo(2);
    }

    @Test
    @DisplayName("""
            getLength():
             stack is empty
             => return 0
            """)
    public void getLength1() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThat(stack.getLength()).isZero();
    }

    @Test
    @DisplayName("""
            getLength():
             stack is empty,
             add the item
             => return 0, than return 1
            """)
    public void getLength2() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThat(stack.getLength()).isZero();
        stack.putTop(100);
        Assertions.assertThat(stack.getLength()).isOne();
    }

    @Test
    @DisplayName("""
            getLength():
             stack not empty,
             add multiple items in sequence,
             delete multiple elements sequentially
             => return 0, than return correct length after every element added or removed
            """)
    public void getLength3() {
        Stack<Integer> stack = Stack.of(1, 2, 3, 4, 5);

        for(int i = 6; i < 1000; i++) {
            stack.putTop(i);
            Assertions.assertThat(stack.getLength()).isEqualTo(i);
        }

        for(int i = stack.getLength(); i > 6; --i) {
            stack.removeTop();
            Assertions.assertThat(stack.getLength()).isEqualTo(i - 1);
        }
    }

    @Test
    @DisplayName("linearSearch(value): stack is empty => return -1")
    public void linearSearch1() {
        Stack<Integer> stack = new Stack<>();

        int actual = stack.linearSearch(100);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("linearSearch(value): stack contains null item => return index first null value")
    public void linearSearch2() {
        Stack<Integer> stack = Stack.of(0, 1, 2, null, 4, 5, null, 7, null, 8);

        int actual = stack.linearSearch((Integer)null);

        Assertions.assertThat(actual).isEqualTo(1);
    }

    @Test
    @DisplayName("linearSearch(value): stack contains several equal items => return index first item equal value")
    public void linearSearch3() {
        Stack<Integer> stack = Stack.of(0, 1, 7, 3, 4, 7, 6, 7, 8, 9);

        int actual = stack.linearSearch(7);

        Assertions.assertThat(actual).isEqualTo(2);
    }

    @Test
    @DisplayName("linearSearch(value): stack is not empty, stack don't contain item => return -1")
    public void linearSearch4() {
        Stack<Integer> stack = Stack.of(0, 1, 7, 3, 4, 7, 6, 7, 8, 9);

        int actual = stack.linearSearch(100);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("linearSearch(predicate): stack empty => return -1")
    public void linearSearchByPredicate1() {
        Stack<Integer> stack = new Stack<>();

        int actual = stack.linearSearch(item -> item == 100);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("linearSearch(predicate): stack contains several items for predicate => return index first item by predicate")
    public void linearSearchByPredicate2() {
        Stack<Integer> stack = Stack.of(10, 20, null, null, 30, null, 40, null, 50);

        int actual = stack.linearSearch(item -> item != null && item <= 20);

        Assertions.assertThat(actual).isEqualTo(7);
    }

    @Test
    @DisplayName("linearSearch(predicate): stack is not empty, stack don't contain item for predicate => return -1")
    public void linearSearchByPredicate3() {
        Stack<Integer> stack = Stack.of(10, 20, 30, 40, 50, 90);

        int actual = stack.linearSearch(item -> item == 45);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("""
            frequency(predicate):
             stack not contains items for predicate
             => return 0
            """)
    public void frequency1() {
        Stack<Integer> stack = Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110);

        int actual = stack.frequency(i -> i < 0);

        Assertions.assertThat(actual).isZero();
    }

    @Test
    @DisplayName("""
            frequency(predicate):
             stack is empty
             => return 0
            """)
    public void frequency2() {
        Stack<Integer> stack = new Stack<>();

        int actual = stack.frequency(i -> i > 0);

        Assertions.assertThat(actual).isZero();
    }

    @Test
    @DisplayName("""
            frequency(predicate):
             stack contains items for predicate
             => return correct result
            """)
    public void frequency3() {
        Stack<Integer> stack = Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110);

        int actual = stack.frequency(i -> i % 20 == 0);

        Assertions.assertThat(actual).isEqualTo(4);
    }

    @Test
    @DisplayName("iterator(), Iterator#hasNext(): stack is empty => return false")
    public void iterator1() {
        Stack<Integer> array = new Stack<>();

        Assertions.assertThat(array.iterator().hasNext()).isFalse();
    }

    @Test
    @DisplayName("iterator(), Iterator#hasNext(): stack contains several items => return correct result")
    public void iterator2() {
        Stack<Integer> stack = Stack.of(1, 2, 3, 4, 5);

        Iterator<Integer> iterator = stack.iterator();
        Array<Boolean> actual = new Array<>();
        for(int i = 0; i < 10; i++) {
            actual.append(iterator.hasNext());
            if(iterator.hasNext()) iterator.next();
        }

        Assertions.assertThat(actual).
                containsExactly(true, true, true, true, true, false, false, false, false, false);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): stack is empty => exception")
    public void iterator3() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(() -> stack.iterator().next());
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): stack contains several item => return all items in correct order")
    public void iterator4() {
        Stack<Integer> stack = Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120);
        Array<Integer> actual = new Array<>();

        for(Integer value : stack) actual.append(value);

        Assertions.assertThat(actual).
                containsExactly(120, 110, 100, 90, 80, 70, 60, 50, 40, 30, 20, 10);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): stack contains several items, next after last item => exception")
    public void iterator5() {
        Stack<Integer> stack = Stack.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = stack.iterator();

        while(iterator.hasNext()) iterator.next();

        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("forEach(): empty stack => do not anything")
    public void forEach1() {
        Stack<Integer> expected = new Stack<>();
        Stack<Integer> actual = new Stack<>();

        expected.forEach(actual::putTop);

        Assertions.assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("forEach(): stack contains several item => get each item in correct order")
    public void forEach2() {
        Stack<Integer> stack = Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120);
        Array<Integer> actual = new Array<>();

        stack.forEach(actual::append);

        Assertions.assertThat(actual).
                containsExactly(120, 110, 100, 90, 80, 70, 60, 50, 40, 30, 20, 10);
    }

    @Test
    @DisplayName("equals(Object o): first operand don't equal second => return false")
    public void equals1() {
        Stack<Integer> firstOperand = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> secondOperand = Stack.of(10, 20, null, 50, 40);

        Assertions.assertThat(firstOperand).isNotEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): first operand equal second, entry arrays length is equal => return true")
    public void equals2() {
        Stack<Integer> firstOperand = Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120);
        Stack<Integer> secondOperand = Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): first operand equal second, entry array length isn't equal => return true")
    public void equals3() {
        Stack<Integer> firstOperand = new Stack<>();
        Stack<Integer> secondOperand = Stack.of(10, 20, 30, 40, 50);

        for(int i = 0; i < 1000; i++) firstOperand.putTop(i);
        for(int i = 0; i < 1000; i++) firstOperand.removeTop();
        firstOperand.putAllOnTop(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): idempotence property")
    public void equals4() {
        Stack<Integer> firstOperand = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(firstOperand);
    }

    @Test
    @DisplayName("equals(Object o): commutative property")
    public void equals5() {
        Stack<Integer> firstOperand = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> secondOperand = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
        Assertions.assertThat(secondOperand).isEqualTo(firstOperand);
    }

    @Test
    @DisplayName("equals(Object o): transitive property")
    public void equals6() {
        Stack<Integer> firstOperand = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> secondOperand = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> thirdOperand = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
        Assertions.assertThat(secondOperand).isEqualTo(thirdOperand);
        Assertions.assertThat(firstOperand).isEqualTo(thirdOperand);
    }

}