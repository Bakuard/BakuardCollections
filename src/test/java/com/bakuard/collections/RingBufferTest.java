package com.bakuard.collections;

import com.bakuard.collections.exception.NegativeSizeException;
import com.bakuard.collections.testUtil.Fabric;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RingBufferTest {

    @DisplayName("RingBuffer(other):")
    @ParameterizedTest(name = """
             origin is {0}
             => expected {1}
            """)
    @MethodSource("provideForCopyConstructor1")
    void RingBuffer_copy(RingBuffer<Integer> origin, RingBuffer<Integer> expected) {
        RingBuffer<Integer> actual = new RingBuffer<>(origin);

        Assertions.assertThat(actual).isEqualTo(expected);
     }

    @DisplayName("RingBuffer(other): origin and copy must be independent of each other")
    @ParameterizedTest(name = """
             origin is {0},
             change origin and copy after creation
             => expectedOrigin {1},
                expectedCopy {2}
            """)
    @MethodSource("provideForCopyConstructor2")
    void RingBuffer_copy_doNotChangeOrigin(RingBuffer<Integer> origin,
                                           RingBuffer<Integer> expectedOrigin,
                                           RingBuffer<Integer> expectedCopy,
                                           Consumer<RingBuffer<Integer>> originMutator,
                                           Consumer<RingBuffer<Integer>> copyMutator) {
        RingBuffer<Integer> actualCopy = new RingBuffer<>(origin);

        originMutator.accept(origin);
        copyMutator.accept(actualCopy);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualCopy).isEqualTo(expectedCopy);
        assertions.assertThat(origin).isEqualTo(expectedOrigin);
        assertions.assertAll();
    }

    @DisplayName("RingBuffer(iterable):")
    @ParameterizedTest(name = """
             iterable is {0}
             => expected {1}
            """)
    @MethodSource("provideForIterableConstructor1")
    void RingBuffer_iterable1(Iterable<Integer> iterable, RingBuffer<Integer> expected) {
        RingBuffer<Integer> actual = new RingBuffer<>(iterable);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("RingBuffer(iterable): iterable and buffer must be independent of each other")
    @ParameterizedTest(name = """
             iterable is {0},
             change iterable and buffer after creation
             => expectedIterable {1},
                expectedBuffer {2}
            """)
    @MethodSource("provideForIterableConstructor2")
    void RingBuffer_iterable2(Iterable<Integer> iterable,
                              Iterable<Integer> expectedIterable,
                              RingBuffer<Integer> expectedBuffer,
                              Consumer<Iterable<Integer>> iterableMutator,
                              Consumer<RingBuffer<Integer>> bufferMutator) {
        RingBuffer<Integer> actual = new RingBuffer<>(iterable);

        iterableMutator.accept(iterable);
        bufferMutator.accept(actual);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).isEqualTo(expectedBuffer);
        assertions.assertThat(iterable).isEqualTo(expectedIterable);
        assertions.assertAll();
    }

    @DisplayName("of(maxSize, ...data):")
    @ParameterizedTest(name = """
             maxSize is {0},
             data is {1}
             => expected is {2}
            """)
    @MethodSource("provideForOf1")
    void of(int maxSize, Integer[] data, RingBuffer<Integer> expected) {
        RingBuffer<Integer> actual = RingBuffer.of(maxSize, data);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("of(maxSize, ...data): data and ring buffer must be independent of each other")
    @ParameterizedTest(name = """
             maxSize is {0},
             data is {1},
             change data and ring buffer after creation
             => expected data {2},
                expected ring buffer {3}
            """)
    @MethodSource("provideForOf2")
    void of_doNotChangeOrigin(int maxSize,
                              Integer[] data,
                              Integer[] expectedData,
                              RingBuffer<Integer> expectedBuffer,
                              Consumer<Integer[]> dataMutator,
                              Consumer<RingBuffer<Integer>> bufferMutator) {
        RingBuffer<Integer> actual = RingBuffer.of(maxSize, data);

        dataMutator.accept(data);
        bufferMutator.accept(actual);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).isEqualTo(expectedBuffer);
        assertions.assertThat(data).isEqualTo(expectedData);
        assertions.assertAll();
    }

    @DisplayName("putLastOrReplace(value):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             addedValue is {1}
             => expected buffer is {2},
                expected value is {3}
            """)
    @MethodSource("provideForPutLastOrReplace")
    void putLastOrReplace(RingBuffer<Integer> origin,
                          Integer addedValue,
                          RingBuffer<Integer> expected,
                          Integer expectedReturnedValue) {
        Integer actualReturnedValue = origin.putLastOrReplace(addedValue);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValue).isEqualTo(expectedReturnedValue);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putAllOnLastOrReplace(iterable):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             added values is {1}
             => expected buffer is {2},
                expected values is {3}
            """)
    @MethodSource("provideForPutAllOnLastOrReplace_iterable")
    void putAllOnLastOrReplace_iterable(RingBuffer<Integer> origin,
                                        DynamicArray<Integer> addedValues,
                                        RingBuffer<Integer> expected,
                                        DynamicArray<Integer> expectedReturnedValues) {
        DynamicArray<Integer> actualReturnedValues = origin.putAllOnLastOrReplace(addedValues);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValues).isEqualTo(expectedReturnedValues);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putAllOnLastOrReplace(data):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             added values is {1}
             => expected buffer is {2},
                expected values is {3}
            """)
    @MethodSource("provideForPutAllOnLastOrReplace_array")
    void putAllOnLastOrReplace_array(RingBuffer<Integer> origin,
                                     Integer[] addedValues,
                                     RingBuffer<Integer> expected,
                                     DynamicArray<Integer> expectedReturnedValues) {
        DynamicArray<Integer> actualReturnedValues = origin.putAllOnLastOrReplace(addedValues);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValues).isEqualTo(expectedReturnedValues);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putLastOrSkip(value):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             addedValue is {1}
             => expected buffer is {2},
                expected value is {3}
            """)
    @MethodSource("provideForPutLastOrSkip")
    void putLastOrSkip(RingBuffer<Integer> origin,
                       Integer addedValue,
                       RingBuffer<Integer> expected,
                       boolean expectedReturnedValue) {
        boolean actualReturnedValue = origin.putLastOrSkip(addedValue);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValue).isEqualTo(expectedReturnedValue);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putAllOnLastOrSkip(iterable):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             added values is {1}
             => expected buffer is {2},
                expected values is {3}
            """)
    @MethodSource("provideForPutAllOnLastOrSkip_iterable")
    void putAllOnLastOrSkip_iterable(RingBuffer<Integer> origin,
                                     DynamicArray<Integer> addedValues,
                                     RingBuffer<Integer> expected,
                                     int expectedReturnedValues) {
        int actualReturnedValues = origin.putAllOnLastOrSkip(addedValues);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValues).isEqualTo(expectedReturnedValues);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putAllOnLastOrSkip(data):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             added values is {1}
             => expected buffer is {2},
                expected values is {3}
            """)
    @MethodSource("provideForPutAllOnLastOrSkip_array")
    void putAllOnLastOrSkip_array(RingBuffer<Integer> origin,
                                  Integer[] addedValues,
                                  RingBuffer<Integer> expected,
                                  int expectedReturnedValues) {
        int actualReturnedValues = origin.putAllOnLastOrSkip(addedValues);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValues).isEqualTo(expectedReturnedValues);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("removeFirst():")
    @ParameterizedTest(name = """
             origin buffer is {0}
             => expected buffer is {1},
                expected value is {2}
            """)
    @MethodSource("provideForRemoveFirst")
    void removeFirst(RingBuffer<Integer> origin,
                     RingBuffer<Integer> expected,
                     Integer expectedReturnedValue) {
        Integer actualReturnedValue = origin.removeFirst();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(actualReturnedValue).isEqualTo(expectedReturnedValue);
        assertions.assertAll();
    }

    @DisplayName("growToSize(newSize):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             newSize is {1}
             => expected buffer is {2}
            """)
    @MethodSource("provideForGrowToSize")
    void growToSize(RingBuffer<Integer> origin, int newSize, RingBuffer<Integer> expected) {
        origin.growToSize(newSize);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("""
            growToSize(newSize):
                newSize < 0
                => exception
            """)
    @Test
    void growToSize_exception() {
        RingBuffer<Integer> origin = RingBuffer.of(10, 1, 2, 3, 4, 5);

        Assertions.assertThatThrownBy(() -> origin.growToSize(-1))
                .isInstanceOf(NegativeSizeException.class);
    }


    private static Stream<Arguments> provideForCopyConstructor1() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        RingBuffer.of(1, 1),
                        RingBuffer.of(1, 1)
                ),
                Arguments.of(
                        new RingBuffer<>(100),
                        new RingBuffer<>(100)
                )
        );
    }

    private static Stream<Arguments> provideForCopyConstructor2() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(10, 6, 7, 8, 9, 10),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        (Consumer<RingBuffer<Integer>>) origin -> {
                            for(int i = 0; i < 5; i++) origin.removeFirst();
                        },
                        (Consumer<RingBuffer<Integer>>) copy -> {}
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(10, 6, 7, 8, 9, 10),
                        (Consumer<RingBuffer<Integer>>) origin -> {},
                        (Consumer<RingBuffer<Integer>>) copy -> {
                            for(int i = 0; i < 5; i++) copy.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForIterableConstructor1() {
        return Stream.of(
                Arguments.of(
                        List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        List.of(1),
                        RingBuffer.of(1, 1)
                ),
                Arguments.of(
                        List.of(),
                        new RingBuffer<>(0)
                )
        );
    }

    private static Stream<Arguments> provideForIterableConstructor2() {
        return Stream.of(
                Arguments.of(
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        DynamicArray.of(null, null, null, null, null, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        (Consumer<DynamicArray<Integer>>) iterable -> {
                            for(int i = 0; i < 5; i++) iterable.replace(i, null);
                        },
                        (Consumer<RingBuffer<Integer>>) buffer -> {}
                ),
                Arguments.of(
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(15, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        (Consumer<DynamicArray<Integer>>) iterable -> {},
                        (Consumer<RingBuffer<Integer>>) buffer -> {
                            for(int i = 0; i < 5; i++) buffer.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForOf1() {
        Fabric<Integer, RingBuffer<Integer>> fabric = (size, data) -> {
            RingBuffer<Integer> buffer = new RingBuffer<>(size);
            for(Integer value : data) buffer.putLastOrSkip(value);
            return buffer;
        };

        return Stream.of(
                Arguments.of(
                        15,
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        fabric.createWithSize(15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        15,
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        fabric.createWithSize(15, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                ),
                Arguments.of(
                        1,
                        new Integer[]{1},
                        fabric.create(1)
                ),
                Arguments.of(
                        0,
                        new Integer[0],
                        new RingBuffer<>(0)
                )
        );
    }

    private static Stream<Arguments> provideForOf2() {
        Fabric<Integer, RingBuffer<Integer>> fabric = (size, data) -> {
            RingBuffer<Integer> buffer = new RingBuffer<>(size);
            for(Integer value : data) buffer.putLastOrSkip(value);
            return buffer;
        };

        return Stream.of(
                Arguments.of(
                        10,
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new Integer[]{null, null, null, null, null, 6, 7, 8, 9, 10},
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        (Consumer<Integer[]>) data -> {
                            for(int i = 0; i < 5; i++) data[i] = null;
                        },
                        (Consumer<RingBuffer<Integer>>) buffer -> {}
                ),
                Arguments.of(
                        10,
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        fabric.createWithSize(10, 6, 7, 8, 9, 10),
                        (Consumer<Integer[]>) data -> {},
                        (Consumer<RingBuffer<Integer>>) buffer -> {
                            for(int i = 0; i < 5; i++) buffer.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForPutLastOrReplace() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(0),
                        1,
                        RingBuffer.of(0),
                        1
                ),
                Arguments.of(
                        RingBuffer.of(1, 1),
                        2,
                        RingBuffer.of(1, 2),
                        1
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        11,
                        RingBuffer.of(10, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        1
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        1,
                        RingBuffer.of(10, 1),
                        null
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5),
                        6,
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6),
                        null
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastOrReplace_iterable() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of(),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of()
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of(11),
                        RingBuffer.of(10, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        DynamicArray.of(1)
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of(11, 12, 13, 14, 15),
                        RingBuffer.of(10, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        DynamicArray.of(1, 2, 3, 4, 5)
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
                        RingBuffer.of(10, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        DynamicArray.of(1, 2, 3, 4, 5, 6),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6),
                        DynamicArray.of()
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of()
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(10, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        DynamicArray.of(1, 2, 3, 4, 5)
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5),
                        DynamicArray.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                        RingBuffer.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                ),
                Arguments.of(
                        RingBuffer.of(0),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(0),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastOrReplace_array() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[0],
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of()
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11},
                        RingBuffer.of(10, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        DynamicArray.of(1)
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11, 12, 13, 14, 15},
                        RingBuffer.of(10, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        DynamicArray.of(1, 2, 3, 4, 5)
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25},
                        RingBuffer.of(10, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6},
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6),
                        DynamicArray.of()
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of()
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        RingBuffer.of(10, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        DynamicArray.of(1, 2, 3, 4, 5)
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5),
                        new Integer[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20},
                        RingBuffer.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                ),
                Arguments.of(
                        RingBuffer.of(0),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        RingBuffer.of(0),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                )
        );
    }

    private static Stream<Arguments> provideForPutLastOrSkip() {
        Fabric<Integer, RingBuffer<Integer>> fabric = (size, data) -> {
            RingBuffer<Integer> buffer = new RingBuffer<>(size);
            for(Integer value : data) buffer.putLastOrReplace(value);
            return buffer;
        };

        return Stream.of(
                Arguments.of(
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        11,
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        false
                ),
                Arguments.of(
                        fabric.createWithSize(1, 1),
                        2,
                        fabric.createWithSize(1, 1),
                        false
                ),
                Arguments.of(
                        fabric.createWithSize(0),
                        1,
                        fabric.createWithSize(0),
                        false
                ),
                Arguments.of(
                        fabric.createWithSize(10, 1, 2, 3, 4, 5),
                        6,
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6),
                        true
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        1,
                        fabric.createWithSize(1, 1),
                        true
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastOrSkip_iterable() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of(),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of(11),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of(11, 12, 13, 14, 15),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        DynamicArray.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        DynamicArray.of(1, 2, 3, 4, 5, 6),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6),
                        6
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10
                ),
                Arguments.of(
                        RingBuffer.of(11, 1, 2, 3, 4, 5),
                        DynamicArray.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(11, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        6
                ),
                Arguments.of(
                        new RingBuffer<>(0),
                        DynamicArray.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new RingBuffer<>(0),
                        0
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastOrSkip_array() {
        Fabric<Integer, RingBuffer<Integer>> fabric = (size, data) -> {
            RingBuffer<Integer> buffer = new RingBuffer<>(size);
            for(Integer value : data) buffer.putLastOrSkip(value);
            return buffer;
        };

        return Stream.of(
                Arguments.of(
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[0],
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11},
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11, 12, 13, 14, 15},
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25},
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6},
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6),
                        6
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        fabric.createWithSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10
                ),
                Arguments.of(
                        fabric.createWithSize(11, 1, 2, 3, 4, 5),
                        new Integer[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        fabric.createWithSize(11, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        6
                ),
                Arguments.of(
                        new RingBuffer<>(0),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new RingBuffer<>(0),
                        0
                )
        );
    }

    private static Stream<Arguments> provideForRemoveFirst() {
        return Stream.of(
                Arguments.of(
                        new RingBuffer<>(0),
                        new RingBuffer<>(0),
                        null
                ),
                Arguments.of(
                        RingBuffer.of(1, 1),
                        new RingBuffer<>(1),
                        1
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(10, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        1
                ),
                Arguments.of(
                        RingBuffer.of(1, new Integer[]{null}),
                        new RingBuffer<>(1),
                        null
                ),
                Arguments.of(
                        RingBuffer.of(10, null, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(10, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        null
                )
        );
    }

    private static Stream<Arguments> provideForGrowToSize() {
        return Stream.of(
                Arguments.of(new RingBuffer<>(0), 0, new RingBuffer<>(0)),
                Arguments.of(new RingBuffer<>(0), 10, new RingBuffer<>(10)),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0,
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10,
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                ),
                Arguments.of(
                        RingBuffer.of(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        50,
                        RingBuffer.of(50, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                )
        );
    }
}
