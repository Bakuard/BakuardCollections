package com.bakuard.collections;

import com.bakuard.collections.exceptions.NegativeSizeException;
import com.bakuard.collections.testUtil.BitsMutator;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class BitsTest {

    @DisplayName("Bits(other):")
    @ParameterizedTest(name = """
             origin is {0}
             => expected {1}
            """)
    @MethodSource("provideForCopyConstructor1")
    void Bits_copy1(Bits origin, Bits expected) {
        Bits actual = new Bits(origin);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Bits(other): origin and copy must be independent of each other")
    @ParameterizedTest(name = """
             origin is {0}
             => expected {1}
            """)
    @MethodSource("provideForCopyConstructor2")
    void Bits_copy2(Bits origin,
                    Bits expectedOrigin,
                    Bits expectedCopy,
                    BitsMutator originMutator,
                    BitsMutator expectedMutator) {
        Bits actualCopy = new Bits(origin);

        originMutator.mutate(origin);
        expectedMutator.mutate(actualCopy);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualCopy).isEqualTo(expectedCopy);
        assertions.assertThat(origin).isEqualTo(expectedOrigin);
        assertions.assertAll();
    }

    @Test
    @DisplayName("Bits(numberBits): numberBits < 0 => exception")
    void Bits_numberBits_exception() {
        Assertions.assertThatExceptionOfType(IndexOutOfBoundsException.class).
                isThrownBy(() -> new Bits(-1));
    }

    @DisplayName("Bits(numberBits):")
    @ParameterizedTest(name = """
             numberBits is {0}
             => expected {1}
            """)
    @MethodSource("provideForConstructorWithNumberBits")
    void Bits_numberBits(int numberBits, Bits expected) {
        Bits actual = new Bits(numberBits);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("get(index):")
    @ParameterizedTest(name = """
             bits is {0},
             index is {1}
             => exception
            """)
    @MethodSource("provideForMethodWithSingleIndexParam_ExceptionCases")
    void get_exception(Bits bits, int index) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> bits.get(index));
    }

    @Test
    @DisplayName("""
            set(index):
             index is correct
             => get(index) must return true
            """)
    void set() {
        Bits bits = new Bits(1000);
        Set<Integer> indexes = Set.of(0, 200, 317, 999);

        indexes.forEach(bits::set);

        SoftAssertions assertions = new SoftAssertions();
        for(int i = 0; i < bits.size(); i++) {
            boolean expect = indexes.contains(i);
            assertions.assertThat(bits.get(i)).as("expect %b for index %d", expect, i).isEqualTo(expect);
        }
        assertions.assertAll();
    }

    @DisplayName("set(index):")
    @ParameterizedTest(name = """
             bits is {0},
             index is {1}
             => exception
            """)
    @MethodSource("provideForMethodWithSingleIndexParam_ExceptionCases")
    void set_exception(Bits bits, int index) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> bits.set(index));
    }

    @DisplayName("setAll(indexes):")
    @ParameterizedTest(name = """
             origin is {0},
             indexes is {1}
             => exception, don't change bits
            """)
    @MethodSource("provideForMethodWithVarargsIndexesParam_ExceptionCases")
    void setAllWithArguments_exception(Bits origin, int[] indexes) {
        Bits expected = new Bits(origin);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.setAll(indexes));
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("setAll(indexes):")
    @ParameterizedTest(name = """
             origin is {0},
             indexes is {1}
             => expected is {2}
            """)
    @MethodSource("provideForSetAllWithArguments")
    void setAllWithArguments(Bits origin, int[] indexes, Bits expected) {
        origin.setAll(indexes);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("setRange(fromIndex, toIndex):")
    @ParameterizedTest(name = """
             fromIndex is {1},
             toIndex is {2},
             bits is {0}
             => expected bits {3}
            """)
    @MethodSource("provideForSetRange")
    void setRange(Bits origin, int fromIndex, int toIndex, Bits expected) {
        origin.setRange(fromIndex, toIndex);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("setRange(fromIndex, toIndex):")
    @ParameterizedTest(name = """
             bits is {0},
             fromIndex is {1},
             toIndex is {2}
             => exception, don't change bits
            """)
    @MethodSource("provideForMethodWithRange_ExceptionCase")
    void setRange_exception(Bits bits, int fromIndex, int toIndex) {
        Bits expected = new Bits(bits);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> bits.setRange(fromIndex, toIndex));
        assertions.assertThat(bits).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("setAll():")
    @ParameterizedTest(name = """
             bits is {0}
             => expected is {1}
            """)
    @MethodSource("provideForSetAll")
    void setAllWithoutArguments1(Bits origin, Bits expected) {
        origin.setAll();

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @Test
    @DisplayName("setAll(): call expandTo() after setAll() => bits greater or equal old value of size() is zero")
    void setAllWithoutArguments2() {
        Bits actual = new Bits(140);

        actual.setAll();
        actual.growToIndex(1000);

        for(int i = 0; i < actual.size(); i++) {
            if(i >= 140) Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("""
            clear(index):
             index is correct
             => get(index) must return false
            """)
    void clear() {
        Bits bits = Bits.filled(1000);
        Set<Integer> indexes = Set.of(0, 200, 317, 999);

        indexes.forEach(bits::clear);

        SoftAssertions assertions = new SoftAssertions();
        for(int i = 0; i < bits.size(); i++) {
            boolean expect = !indexes.contains(i);
            assertions.assertThat(bits.get(i)).as("expect %b for index %d", expect, i).isEqualTo(expect);
        }
        assertions.assertAll();
    }

    @DisplayName("clear(index):")
    @ParameterizedTest(name = """
             bits is {0},
             index is {1}
             => exception
            """)
    @MethodSource("provideForMethodWithSingleIndexParam_ExceptionCases")
    void clear_exception(Bits bits, int index) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> bits.clear(index));
    }

    @DisplayName("clearAll(indexes):")
    @ParameterizedTest(name = """
             bits is {0},
             indexes is {1}
             => exception, don't change bits
            """)
    @MethodSource("provideForMethodWithVarargsIndexesParam_ExceptionCases")
    void clearAllWithArguments_exception(Bits bits, int[] indexes) {
        Bits expected = new Bits(bits);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> bits.clearAll(indexes));
        assertions.assertThat(bits).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("clearAll(indexes):")
    @ParameterizedTest(name = """
             origin is {0},
             indexes is {1}
             => expected is {2}
            """)
    @MethodSource("provideForClearAllWithArguments")
    void clearAllWithArguments(Bits origin, int[] indexes, Bits expected) {
        origin.clearAll(indexes);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("clearRange(fromIndex, toIndex):")
    @ParameterizedTest(name = """
             fromIndex is {1},
             toIndex is {2},
             bits is {0}
             => expected bits {3}
            """)
    @MethodSource("provideForClearRange")
    void clearRange(Bits origin, int fromIndex, int toIndex, Bits expected) {
        origin.clearRange(fromIndex, toIndex);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("clearRange(fromIndex, toIndex):")
    @ParameterizedTest(name = """
             bits is {0},
             fromIndex is {1},
             toIndex is {2}
             => exception, don't change bits
            """)
    @MethodSource("provideForMethodWithRange_ExceptionCase")
    void clearRange_exception(Bits bits, int fromIndex, int toIndex) {
        Bits expected = new Bits(bits);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> bits.clearRange(fromIndex, toIndex));
        assertions.assertThat(bits).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("clearAll():")
    @ParameterizedTest(name = """
             bits is {0}
             => expected is {1}
            """)
    @MethodSource("provideForClearAll")
    void clearAllWithoutArguments(Bits origin, Bits expected) {
        origin.clearAll();

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("and(other):")
    @ParameterizedTest(name = """
             note: {3},
             firstOperand is {0},
             secondOperand is {1}
             => expected is {2}
            """)
    @MethodSource("provideForAnd")
    void and(Bits firstOperand, Bits secondOperand, Bits expected, String note) {
        firstOperand.and(secondOperand);

        Assertions.assertThat(firstOperand).isEqualTo(expected);
    }

    @DisplayName("and(other): commutative property")
    @ParameterizedTest(name = """
             note: {2},
             firstOperand is {0},
             secondOperand is {1}
             => firstOperand.and(secondOperand) = secondOperand.and(firstOperand)
            """)
    @MethodSource("provideForAnd_commutative")
    void and_commutative(Bits firstOperand, Bits secondOperand, String note) {
        Bits result1 = new Bits(firstOperand).and(secondOperand);
        Bits result2 = new Bits(secondOperand).and(firstOperand);

        Assertions.assertThat(result1).
                usingComparator(Bits::compareIgnoreSize).
                isEqualTo(result2);
    }

    @DisplayName("and(other): transitive property")
    @ParameterizedTest(name = """
             note: {3},
             firstOperand is {0},
             secondOperand is {1},
             thirdOperand is {2}
             => firstOperand.and(secondOperand) = secondOperand.and(firstOperand) = firstOperand.and(thirdOperand)
            """)
    @MethodSource("provideForAnd_transitive")
    void and_transitive(Bits firstOperand, Bits secondOperand, Bits thirdOperand, String note) {
        Bits result1 = new Bits(firstOperand).and(secondOperand);
        Bits result2 = new Bits(secondOperand).and(thirdOperand);
        Bits result3 = new Bits(firstOperand).and(thirdOperand);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(result1).usingComparator(Bits::compareIgnoreSize).isEqualTo(result2);
        assertions.assertThat(result2).usingComparator(Bits::compareIgnoreSize).isEqualTo(result3);
        assertions.assertAll();
    }

    @DisplayName("or(other):")
    @ParameterizedTest(name = """
             note: {3},
             firstOperand is {0},
             secondOperand is {1}
             => expected is {2}
            """)
    @MethodSource("provideForOr")
    void or(Bits firstOperand, Bits secondOperand, Bits expected, String note) {
        firstOperand.or(secondOperand);

        Assertions.assertThat(firstOperand).isEqualTo(expected);
    }

    @DisplayName("or(other): commutative property")
    @ParameterizedTest(name = """
             note: {2},
             firstOperand is {0},
             secondOperand is {1}
             => firstOperand.and(secondOperand) = secondOperand.and(firstOperand)
            """)
    @MethodSource("provideForOr_commutative")
    void or_commutative(Bits firstOperand, Bits secondOperand, String note) {
        Bits result1 = new Bits(firstOperand).or(secondOperand);
        Bits result2 = new Bits(secondOperand).or(firstOperand);

        Assertions.assertThat(result1).
                usingComparator(Bits::compareIgnoreSize).
                isEqualTo(result2);
    }

    @DisplayName("or(other): transitive property")
    @ParameterizedTest(name = """
             note: {3},
             firstOperand is {0},
             secondOperand is {1},
             thirdOperand is {2}
             => firstOperand.and(secondOperand) = secondOperand.and(firstOperand) = firstOperand.and(thirdOperand)
            """)
    @MethodSource("provideForOr_transitive")
    void or_transitive(Bits firstOperand, Bits secondOperand, Bits thirdOperand, String note) {
        Bits result1 = new Bits(firstOperand).or(secondOperand);
        Bits result2 = new Bits(secondOperand).or(thirdOperand);
        Bits result3 = new Bits(firstOperand).or(thirdOperand);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(result1).usingComparator(Bits::compareIgnoreSize).isEqualTo(result2);
        assertions.assertThat(result2).usingComparator(Bits::compareIgnoreSize).isEqualTo(result3);
        assertions.assertAll();
    }

    @DisplayName("xor(other):")
    @ParameterizedTest(name = """
             note: {3},
             firstOperand is {0},
             secondOperand is {1}
             => expected is {2}
            """)
    @MethodSource("provideForXor")
    void xor(Bits firstOperand, Bits secondOperand, Bits expected, String note) {
        firstOperand.xor(secondOperand);

        Assertions.assertThat(firstOperand).isEqualTo(expected);
    }

    @DisplayName("xor(other): commutative property")
    @ParameterizedTest(name = """
             note: {2},
             firstOperand is {0},
             secondOperand is {1}
             => firstOperand.and(secondOperand) = secondOperand.and(firstOperand)
            """)
    @MethodSource("provideForXor_commutative")
    void xor_commutative(Bits firstOperand, Bits secondOperand, String note) {
        Bits result1 = new Bits(firstOperand).xor(secondOperand);
        Bits result2 = new Bits(secondOperand).xor(firstOperand);

        Assertions.assertThat(result1).
                usingComparator(Bits::compareIgnoreSize).
                isEqualTo(result2);
    }

    @DisplayName("xor(other): transitive property")
    @ParameterizedTest(name = """
             note: {3},
             firstOperand is {0},
             secondOperand is {1},
             thirdOperand is {2}
             => firstOperand.and(secondOperand) = secondOperand.and(firstOperand) = firstOperand.and(thirdOperand)
            """)
    @MethodSource("provideForXor_transitive")
    void xor_transitive(Bits firstOperand, Bits secondOperand, Bits thirdOperand, String note) {
        Bits result1 = new Bits(firstOperand).xor(secondOperand);
        Bits result2 = new Bits(secondOperand).xor(thirdOperand);
        Bits result3 = new Bits(firstOperand).xor(thirdOperand);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(result1).usingComparator(Bits::compareIgnoreSize).isEqualTo(result2);
        assertions.assertThat(result2).usingComparator(Bits::compareIgnoreSize).isEqualTo(result3);
        assertions.assertAll();
    }

    @DisplayName("andNot(other):")
    @ParameterizedTest(name = """
             note: {3},
             firstOperand is {0},
             secondOperand is {1}
             => expected is {2}
            """)
    @MethodSource("provideForAndNot")
    void andNot(Bits firstOperand, Bits secondOperand, Bits expected, String note) {
        firstOperand.andNot(secondOperand);

        Assertions.assertThat(firstOperand).isEqualTo(expected);
    }

    @Test
    @DisplayName("not(): involution property")
    void not_involution() {
        Bits operand = new Bits(10000);
        operand.setRange(1200, 3400);
        operand.setRange(4577, 9898);
        Bits expected = new Bits(operand);

        Bits actual = operand.not().not();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("not():")
    @ParameterizedTest(name = """
             operand is {0}
             => expected is {1}
            """)
    @MethodSource("provideForNot")
    void not(Bits operand, Bits expected) {
        operand.not();

        Assertions.assertThat(operand).usingComparator(Bits::compareIgnoreSize).isEqualTo(expected);
    }

    @DisplayName("copyStateFrom(src):")
    @ParameterizedTest(name = """
             operand is {0},
             src is {1}
             => expected is {2}
            """)
    @MethodSource("provideForCopyFullStateFrom")
    void copyFullStateFrom(Bits origin, Bits src, Bits expected) {
        origin.copyFullStateFrom(src);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("copyRangeFrom(src, srcPos, destPos, length):")
    @ParameterizedTest(name = """
             srcPos is {2},
             destPos is {3},
             length is {4},
             origin is {0},
             src is {1}
             => exception
            """)
    @MethodSource("provideForCopyRangeFrom_ExceptionCase")
    void copyRangeFrom_exception(Bits origin,
                                 Bits src,
                                 int srcPos,
                                 int destPos,
                                 int length,
                                 Class<? extends Throwable> exceptionType) {
        Assertions.assertThatThrownBy(() -> origin.copyRangeFrom(src, srcPos, destPos, length))
                .isInstanceOf(exceptionType);
    }

    @DisplayName("copyRangeFrom(src, srcPos, destPos, length): src != dest")
    @ParameterizedTest(name = """
             note: {7},
             srcPos is {2},
             destPos is {3},
             length is {4},
             origin is {0},
             src is {1}
             => expected is {5},
                expectedReturnedResult is {6}
            """)
    @MethodSource("provideForCopyRangeFrom_SrcIsNotDest")
    void copyRangeFrom_srcIsNotDest(Bits origin,
                                    Bits src,
                                    int srcPos,
                                    int destPos,
                                    int length,
                                    Bits expected,
                                    int expectedReturnedResult,
                                    String note) {
        int rewritedBits = origin.copyRangeFrom(src, srcPos, destPos, length);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(rewritedBits).isEqualTo(expectedReturnedResult);
        assertions.assertAll();
    }

    @DisplayName("copyRangeFrom(src, srcPos, destPos, length): src == dest")
    @ParameterizedTest(name = """
             note: {6},
             srcPos is {1},
             destPos is {2},
             length is {3},
             origin is {0},
             => expected is {4},
                expectedReturnedResult is {5}
            """)
    @MethodSource("provideForCopyRangeFrom_SrcIsDest")
    void copyRangeFrom_srcIsDest(Bits origin,
                                 int srcPos,
                                 int destPos,
                                 int length,
                                 Bits expected,
                                 int expectedReturnedResult,
                                 String note) {
        int rewritedBits = origin.copyRangeFrom(origin, srcPos, destPos, length);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(rewritedBits).isEqualTo(expectedReturnedResult);
        assertions.assertAll();
    }

    @DisplayName("growTo(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => expected bits {2}
            """)
    @MethodSource("provideForGrowTo")
    void growTo(Bits origin, int index, Bits expected) {
        origin.growToIndex(index);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("growTo(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => exception
            """)
    @MethodSource("provideForCheckingIndexForNegative_ExceptionCase")
    void growTo_exception(Bits origin, int index) {
        Bits expected = new Bits(origin);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.growToIndex(index));
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("truncateTo(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => expected bits is {2},
                expectedHighBits is {3}
            """)
    @MethodSource("provideForTruncateTo")
    void truncateTo(Bits origin, int index, Bits expected, Bits expectedHighBits) {
        origin.truncateToSize(index);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(origin).usingComparator(Bits::compareIgnoreSize).isEqualTo(expectedHighBits);
        assertions.assertAll();
    }

    @DisplayName("truncateTo(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => exception
            """)
    @MethodSource("provideForCheckingIndexForNegative_ExceptionCase")
    void truncateTo_exception(Bits origin, int index) {
        Bits expected = new Bits(origin);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThatThrownBy(() -> origin.truncateToSize(index)).isInstanceOf(NegativeSizeException.class);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("cardinality():")
    @ParameterizedTest(name = """
             origin is {0}
             => expected is {1}
            """)
    @MethodSource("provideForCardinality")
    void cardinality(Bits origin, int expected) {
        int actual = origin.cardinality();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("getHighBitIndex():")
    @ParameterizedTest(name = """
             origin is {0}
             => expectedIndex is {1}
            """)
    @MethodSource("provideForGetHighBitIndex")
    void getHighBitIndex(Bits origin, int expectedIndex) {
        int actual = origin.getHighBitIndex();

        Assertions.assertThat(actual).isEqualTo(expectedIndex);
    }

    @DisplayName("isClear():")
    @ParameterizedTest(name = """
             origin is {0}
             => expected is {1}
            """)
    @MethodSource("provideForIsClear")
    void isClear(Bits origin, boolean expected) {
        boolean actual = origin.isClear();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("nextSetBit(fromIndex):")
    @ParameterizedTest(name = """
             origin is {0},
             fromIndex is {1}
             => exception
            """)
    @MethodSource("provideForCheckingIndexForNegative_ExceptionCase")
    void nextSetBit_exception(Bits origin, int fromIndex) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.nextSetBit(fromIndex));
    }

    @DisplayName("nextSetBit(fromIndex):")
    @ParameterizedTest(name = """
             origin is {0},
             fromIndex is {1}
             => expectedIndexSequence is {2}
            """)
    @MethodSource("provideForNextSetBit")
    void nextSetBit(Bits origin, int fromIndex, List<Integer> expectedIndexSequence) {
        List<Integer> actualIndexSequence = IntStream
                .concat(
                        IntStream.iterate(origin.nextSetBit(fromIndex),
                                index -> index != -1,
                                index -> origin.nextSetBit(index + 1)),
                        IntStream.of(-1)
                )
                .boxed()
                .toList();

        Assertions.assertThat(actualIndexSequence).isEqualTo(expectedIndexSequence);
    }

    @DisplayName("nextClearBit(fromIndex):")
    @ParameterizedTest(name = """
             origin is {0},
             fromIndex is {1}
             => exception
            """)
    @MethodSource("provideForCheckingIndexForNegative_ExceptionCase")
    void nextClearBit_exception(Bits origin, int fromIndex) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.nextClearBit(fromIndex));
    }

    @DisplayName("nextClearBit(fromIndex):")
    @ParameterizedTest(name = """
             origin is {0},
             fromIndex is {1}
             => expectedIndexSequence is {2}
            """)
    @MethodSource("provideForNextClearBit")
    void nextClearBit(Bits origin, int fromIndex, List<Integer> expectedIndexSequence) {
        List<Integer> actualIndexSequence = IntStream
                .concat(
                        IntStream.iterate(origin.nextClearBit(fromIndex),
                                index -> index != -1,
                                index -> origin.nextClearBit(index + 1)),
                        IntStream.of(-1)
                )
                .boxed()
                .toList();

        Assertions.assertThat(actualIndexSequence).isEqualTo(expectedIndexSequence);
    }

    @DisplayName("contains(other):")
    @ParameterizedTest(name = """
             origin is {0},
             other is {1}
             => expected is {2}
            """)
    @MethodSource("provideForContains")
    void contains(Bits origin, Bits other, boolean expected) {
        boolean actual = origin.contains(other);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("intersect(other):")
    @ParameterizedTest(name = """
             origin is {0},
             other is {1}
             => expected is {2}
            """)
    @MethodSource("provideForIntersect")
    void intersect(Bits origin, Bits other, boolean expected) {
        boolean actual = origin.intersect(other);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("equals(other):")
    @ParameterizedTest(name = """
             origin is {0},
             other is {1}
             => expected is {2}
            """)
    @MethodSource("provideForEquals")
    void equals(Bits origin, Bits other, boolean expected) {
        boolean actual = origin.equals(other);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("equals(Object o): idempotence property")
    @Test
    void equals_idempotence() {
        Bits origin = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(origin.equals(origin)).isTrue();
    }

    @DisplayName("equals(Object o): commutative property")
    @Test
    void equals_commutative() {
        Bits first = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);
        Bits second = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(first.equals(second) == second.equals(first)).isTrue();
    }

    @DisplayName("equals(Object o): transitive property")
    @Test
    void equals_transitive() {
        Bits first = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);
        Bits second = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);
        Bits third = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(first.equals(second) == second.equals(third) == first.equals(third))
                .isTrue();
    }

    @DisplayName("equalsIgnoreSize(other):")
    @ParameterizedTest(name = """
             origin is {0},
             other is {1}
             => expected is {2}
            """)
    @MethodSource("provideForEqualsIgnoreSize")
    void equalsIgnoreSize(Bits origin, Bits other, boolean expected) {
        boolean actual = origin.equalsIgnoreSize(other);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("equalsIgnoreSize(Object o): idempotence property => return true")
    @Test
    void equalsIgnoreSize_idempotence() {
        Bits origin = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(origin.equalsIgnoreSize(origin)).isTrue();
    }

    @DisplayName("equalsIgnoreSize(Object o): commutative property => return true")
    @Test
    void equalsIgnoreSize_commutative() {
        Bits first = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);
        Bits second = new Bits(600)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(first.equalsIgnoreSize(second) == second.equalsIgnoreSize(first)).isTrue();
    }

    @DisplayName("equalsIgnoreSize(Object o): transitive property")
    @Test
    void equalsIgnoreSize_transitive() {
        Bits first = new Bits(500)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);
        Bits second = new Bits(600)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);
        Bits third = new Bits(550)
                .setRange(120, 455)
                .clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(first.equalsIgnoreSize(second) == second.equalsIgnoreSize(third)
                        == first.equalsIgnoreSize(third))
                .isTrue();
    }

    @DisplayName("compareTo(other):")
    @ParameterizedTest(name = """
             origin is {0},
             other is {1}
             => expected is {2}
            """)
    @MethodSource("provideForCompareTo")
    void compareTo(Bits origin, Bits other, int expected) {
        int actual = origin.compareTo(other);

        Assertions.assertThat(Integer.compare(actual, 0)).isEqualTo(expected);
    }

    @DisplayName("""
            compareTo(Bits o):
             x.compareTo(y) == 0
             => x.compareTo(z) == y.compareTo(z)
            """)
    @Test
    void compareTo_chain() {
        Bits first = new Bits(500).setRange(100, 400);
        Bits second = new Bits(500).setRange(100, 400);
        Bits third = new Bits(450).setRange(100, 450);

        Assertions.assertThat(first.compareTo(third )== second.compareTo(third)).isTrue();
    }

    @DisplayName("""
            compareTo(Bits o):
             transitive property
            """)
    @Test
    void compareTo_transitive() {
        Bits first = new Bits(500).setRange(100, 400);
        Bits second = new Bits(500).setRange(95, 400);
        Bits third = new Bits(505).setRange(100, 450);

        Assertions.assertThat(first.compareTo(second) < 0 && second.compareTo(third) < 0 &&
                first.compareTo(third) < 0).isTrue();
    }

    @DisplayName("""
            compareTo(Bits o):
             x.compareTo(y) == -y.compareTo(x)
            """)
    @Test
    void compareTo_asymmetry() {
        Bits first = new Bits(500).setRange(100, 400);
        Bits second = new Bits(500).setRange(95, 400);

        Assertions.assertThat(first.compareTo(second) == -second.compareTo(first)).isTrue();
    }

    @DisplayName("compareIgnoreSize(other):")
    @ParameterizedTest(name = """
             origin is {0},
             other is {1}
             => expected is {2}
            """)
    @MethodSource("provideForCompareIgnoreSize")
    void compareIgnoreSize(Bits origin, Bits other, int expected) {
        int actual = origin.compareIgnoreSize(other);

        Assertions.assertThat(Integer.compare(actual, 0)).isEqualTo(expected);
    }

    @DisplayName("""
            compareIgnoreSize(Bits o):
             x.compareTo(y) == 0
             => x.compareTo(z) == y.compareTo(z)
            """)
    @Test
    void compareIgnoreSize_chain() {
        Bits first = new Bits(500).setRange(100, 400);
        Bits second = new Bits(700).setRange(100, 400);
        Bits third = new Bits(450).setRange(100, 450);

        Assertions.assertThat(first.compareIgnoreSize(third) == second.compareIgnoreSize(third)).isTrue();
    }

    @DisplayName("""
            compareIgnoreSize(Bits o):
             transitive property
            """)
    @Test
    void compareIgnoreSize_transitive() {
        Bits first = new Bits(500).setRange(100, 400);
        Bits second = new Bits(600).setRange(95, 400);
        Bits third = new Bits(700).setRange(100, 450);

        Assertions.assertThat(first.compareIgnoreSize(second) < 0 &&
                second.compareIgnoreSize(third) < 0 &&
                first.compareIgnoreSize(third) < 0).isTrue();
    }

    @DisplayName("""
            compareIgnoreSize(Bits o):
             x.compareTo(y) == -y.compareTo(x)
            """)
    @Test
    void compareIgnoreSize_asymmetry() {
        Bits first = new Bits(500).setRange(100, 400);
        Bits second = new Bits(600).setRange(95, 400);

        Assertions.assertThat(first.compareTo(second) == -second.compareTo(first)).isTrue();
    }

    @DisplayName("toBinaryString():")
    @ParameterizedTest(name = """
             origin is {0}
             => expected is {1}
            """)
    @MethodSource("provideForToBinaryString")
    void toBinaryString(Bits origin, String expected) {
        String actual = origin.toBinaryString();

        Assertions.assertThat(actual).isEqualTo(expected);
    }


    private static Stream<Arguments> provideForCopyConstructor1() {
        return Stream.of(
                Arguments.of(
                        new Bits(),
                        new Bits()
                ),
                Arguments.of(
                        Bits.of(1, 0),
                        Bits.of(1, 0)
                ),
                Arguments.of(
                        Bits.of(10, 0, 2, 4, 7, 8),
                        Bits.of(10, 0, 2, 4, 7, 8)
                )
        );
    }

    private static Stream<Arguments> provideForCopyConstructor2() {
        return Stream.of(
                Arguments.of(
                        Bits.of(10, 0, 1, 3, 4, 7, 9),
                        new Bits(10),
                        Bits.of(10, 0, 1, 3, 4, 7, 9),
                        (BitsMutator) Bits::clearAll,
                        (BitsMutator) bits -> {}
                ),
                Arguments.of(
                        Bits.of(10, 0, 1, 3, 4, 7, 9),
                        Bits.of(10, 0, 1, 3, 4, 7, 9),
                        new Bits(10),
                        (BitsMutator) bits -> {},
                        (BitsMutator) Bits::clearAll
                )
        );
    }

    private static Stream<Arguments> provideForConstructorWithNumberBits() {
        return Stream.of(
                Arguments.of(0, new Bits()),
                Arguments.of(1, new Bits(1)),
                Arguments.of(10000, new Bits(10000))
        );
    }

    private static Stream<Arguments> provideForMethodWithSingleIndexParam_ExceptionCases() {
        return Stream.of(
                Arguments.of(new Bits(), 0),
                Arguments.of(new Bits(), -1),
                Arguments.of(new Bits(), 1),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), -1),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), 10),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), 11)
        );
    }

    private static Stream<Arguments> provideForMethodWithVarargsIndexesParam_ExceptionCases() {
        return Stream.of(
                Arguments.of(new Bits(), new int[]{-1}),
                Arguments.of(new Bits(), new int[]{0}),
                Arguments.of(new Bits(), new int[]{1}),
                Arguments.of(new Bits(1), new int[]{-1}),
                Arguments.of(new Bits(1), new int[]{1}),
                Arguments.of(new Bits(1), new int[]{2}),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), new int[]{0, 1, 5, -1, 8}),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), new int[]{0, 1, 5, 10, 8}),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), new int[]{0, 1, 5, 11, 8})
        );
    }

    private static Stream<Arguments> provideForMethodWithRange_ExceptionCase() {
        return Stream.of(
                Arguments.of(new Bits(), -1, 0),
                Arguments.of(new Bits(), 0, 1),
                Arguments.of(new Bits(), 1, 0),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), -1, 9),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), 0, 11),
                Arguments.of(Bits.of(10, 0, 2, 3, 4, 5, 8, 9), 6, 5)
        );
    }

    private static Stream<Arguments> provideForSetRange() {
        Bits twoWords1 = new Bits(128);
        Bits twoWords2 = new Bits(128);
        Bits longWords1 = new Bits(1000);
        Bits longWords2 = new Bits(1000);
        for(int i = 0; i < 72; i++) twoWords1.set(i);
        for(int i = 57; i < 128; i++) twoWords2.set(i);
        for(int i = 0; i < 612; i++) longWords1.set(i);
        for(int i = 479; i < 1000; i++) longWords2.set(i);

        return Stream.of(
                Arguments.of(new Bits(1), 0, 1, Bits.of(1, 0)),
                Arguments.of(new Bits(10), 0, 5, Bits.of(10, 0, 1, 2, 3, 4)),
                Arguments.of(new Bits(10), 5, 10, Bits.of(10, 5, 6, 7, 8, 9)),
                Arguments.of(new Bits(128), 0, 72, twoWords1),
                Arguments.of(new Bits(128), 57, 128, twoWords2),
                Arguments.of(new Bits(1000), 0, 612, longWords1),
                Arguments.of(new Bits(1000), 479, 1000, longWords2),
                Arguments.of(new Bits(1000), 479, 479, new Bits(1000)),
                Arguments.of(new Bits(1000), 999, 999, new Bits(1000)),
                Arguments.of(new Bits(1000), 0, 0, new Bits(1000))
        );
    }

    private static Stream<Arguments> provideForClearRange() {
        Bits twoWords1 = Bits.filled(128);
        Bits twoWords2 = Bits.filled(128);
        Bits longWords1 = Bits.filled(1000);
        Bits longWords2 = Bits.filled(1000);
        for(int i = 0; i < 72; i++) twoWords1.clear(i);
        for(int i = 57; i < 128; i++) twoWords2.clear(i);
        for(int i = 0; i < 612; i++) longWords1.clear(i);
        for(int i = 479; i < 1000; i++) longWords2.clear(i);

        return Stream.of(
                Arguments.of(Bits.filled(1), 0, 1, new Bits(1)),
                Arguments.of(Bits.filled(10), 0, 5, Bits.of(10, 5, 6, 7, 8, 9)),
                Arguments.of(Bits.filled(10), 5, 10, Bits.of(10, 0, 1, 2, 3, 4)),
                Arguments.of(Bits.filled(128), 0, 72, twoWords1),
                Arguments.of(Bits.filled(128), 57, 128, twoWords2),
                Arguments.of(Bits.filled(1000), 0, 612, longWords1),
                Arguments.of(Bits.filled(1000), 479, 1000, longWords2),
                Arguments.of(Bits.filled(1000), 479, 479, Bits.filled(1000)),
                Arguments.of(Bits.filled(1000), 999, 999, Bits.filled(1000)),
                Arguments.of(Bits.filled(1000), 0, 0, Bits.filled(1000))
        );
    }

    private static Stream<Arguments> provideForSetAllWithArguments() {
        Bits bits1000_origin = new Bits(1000);
        IntStream.of(0, 100, 215, 317, 600, 999).forEach(bits1000_origin::set);
        Bits bits1000_expected = new Bits(1000);
        IntStream.of(0, 11, 100, 212, 215, 216, 256, 257, 317, 600, 601, 999).forEach(bits1000_expected::set);

        return Stream.of(
                Arguments.of(new Bits(0), new int[0], new Bits(0)),
                Arguments.of(new Bits(1), new int[]{0}, Bits.filled(1)),
                Arguments.of(new Bits(bits1000_origin), new int[0], new Bits(bits1000_origin)),
                Arguments.of(
                        new Bits(bits1000_origin),
                        new int[]{0, 11, 212, 215, 216, 256, 257, 601},
                        new Bits(bits1000_expected)
                ),
                Arguments.of(
                        new Bits(bits1000_origin),
                        new int[]{0, 0, 0, 11, 11, 11, 212, 215, 216, 256, 257, 601, 601, 601, 601},
                        new Bits(bits1000_expected)
                )
        );
    }

    private static Stream<Arguments> provideForSetAll() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0)),
                Arguments.of(new Bits(1), new Bits(1).setRange(0, 1)),
                Arguments.of(new Bits(34),  new Bits(34).setRange(0, 34)),
                Arguments.of(new Bits(1000), new Bits(1000).setRange(0, 1000)),
                Arguments.of(new Bits(1000).setRange(0, 140), new Bits(1000).setRange(0, 1000))
        );
    }

    private static Stream<Arguments> provideForClearAllWithArguments() {
        Bits bits1000_expected = Bits.filled(1000);
        IntStream.of(0, 100, 215, 317, 600, 999).forEach(bits1000_expected::clear);

        return Stream.of(
                Arguments.of(new Bits(0), new int[0], new Bits(0)),
                Arguments.of(Bits.filled(1), new int[0], Bits.filled(1)),
                Arguments.of(Bits.filled(1), new int[]{0}, new Bits(1)),
                Arguments.of(Bits.filled(34), new int[0], Bits.filled(34)),
                Arguments.of(
                        Bits.filled(34),
                        new int[]{0, 1, 6, 7, 8, 12, 13, 17, 18, 19, 23, 24, 25, 28, 29, 32, 33},
                        Bits.of(34, 2,3,4,5,9,10,11,14,15,16,20,21,22,26,27,30,31)
                ),
                Arguments.of(Bits.filled(1000), new int[0], Bits.filled(1000)),
                Arguments.of(Bits.filled(1000), new int[]{0, 100, 215, 317, 600, 999}, new Bits(bits1000_expected))
        );
    }

    private static Stream<Arguments> provideForClearAll() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0)),
                Arguments.of(new Bits(1), new Bits(1)),
                Arguments.of(Bits.filled(1), new Bits(1)),
                Arguments.of(Bits.filled(34), new Bits(34)),
                Arguments.of(Bits.filled(1000), new Bits(1000))
        );
    }

    private static Stream<Arguments> provideForAnd() {
        Bits theSameOperand = Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99);

        return Stream.of(
                Arguments.of(theSameOperand, theSameOperand, new Bits(theSameOperand),
                        "idempotence property, the same object"),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "idempotence property, not same object"
                ),
                Arguments.of(
                        new Bits(0), new Bits(0), new Bits(0),
                        "idempotence property, all operands have zero size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(100),
                        new Bits(100),
                        "zero property, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(200),
                        new Bits(100),
                        "zero property, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(50),
                        new Bits(100),
                        "zero property, first operand size > second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(0),
                        new Bits(100),
                        "zero property, second operand size = 0"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.filled(100),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "unit property, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.filled(200),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "unit property, first operand size < unit size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 1,14,17,22,35,41,51,66,69,71,73,99),
                        Bits.of(100, 22, 41, 51, 66, 71, 99),
                        "different object, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        Bits.of(100, 22, 41, 51, 66, 71, 99),
                        "different object, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(50, 1,14,17,22,35,41,42,43,45,49),
                        Bits.of(100, 22, 41),
                        "different object, first operand size > second operand size"
                )
        );
    }

    private static Stream<Arguments> provideForAnd_commutative() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0),
                        "all operands have zero size"),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        "first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "first operand size > second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 1,14,17,22,35,41,51,66,69,71,73,99),
                        "first operand size = second operand size"
                )
        );
    }

    private static Stream<Arguments> provideForAnd_transitive() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0), new Bits(0),
                        "all operands have zero size"),
                Arguments.of(
                        Bits.of(200, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(300, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "all operands have different size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "all operands have the same size"
                )
        );
    }

    private static Stream<Arguments> provideForOr() {
        Bits theSameOperand = Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99);

        return Stream.of(
                Arguments.of(theSameOperand, theSameOperand, new Bits(theSameOperand),
                        "idempotence property, the same object"),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "idempotence property, not same object"
                ),
                Arguments.of(
                        new Bits(0), new Bits(0), new Bits(0),
                        "idempotence property, all operands have zero size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(100),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "zero property, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(200),
                        Bits.of(200, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "zero property, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(50),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "zero property, first operand size > second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(0),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "zero property, second operand size = 0"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.filled(100),
                        Bits.filled(100),
                        "unit property, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.filled(200),
                        Bits.filled(200),
                        "unit property, first operand size < unit size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 1,14,17,22,35,41,51,66,69,71,73,99),
                        Bits.of(100, 0,1,14,15,16,17,22,34,35,41,51,66,69,70,71,72,73,99),
                        "different object, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        Bits.of(200, 0,1,14,15,16,17,22,34,35,41,51,66,69,70,71,72,73,99,100,114,115,120,144,177,199),
                        "different object, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(50, 1,14,17,22,35,41,42,43,45,49),
                        Bits.of(100, 0,1,14,15,16,17,22,34,35,41,42,43,45,49,51,66,70,71,72,99),
                        "different object, first operand size > second operand size"
                )
        );
    }

    private static Stream<Arguments> provideForOr_commutative() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0),
                        "all operands have zero size"),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        "first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "first operand size > second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 1,14,17,22,35,41,51,66,69,71,73,99),
                        "first operand size = second operand size"
                )
        );
    }

    private static Stream<Arguments> provideForOr_transitive() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0), new Bits(0),
                        "all operands have zero size"),
                Arguments.of(
                        Bits.of(200, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(300, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "all operands have different size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "all operands have the same size"
                )
        );
    }

    private static Stream<Arguments> provideForXor() {
        Bits theSameOperand = Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99);

        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0), new Bits(0),
                        "all arguments have zero size"),
                Arguments.of(theSameOperand, theSameOperand, new Bits(100),
                        "all operands are the same object"),
                Arguments.of(
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        new Bits(200),
                        "all operands are equal"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 1,14,17,22,35,41,51,66,69,71,73,99),
                        Bits.of(100, 0,1,14,15,16,17,34,35,69,70,72,73),
                        "different object, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        Bits.of(200, 0,1,14,15,16,17,34,35,69,70,72,73,100,114,115,120,144,177,199),
                        "different object, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(50, 1,14,17,22,35,41,42,43,45,49),
                        Bits.of(100, 0,1,14,15,16,17,34,35,42,43,45,49,51,66,70,71,72,99),
                        "different object, first operand size > second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(0),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "second operand has zero size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(50),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "second operand is empty, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(100),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "second operand is empty, first operand size = second operand size"
                )
        );
    }

    private static Stream<Arguments> provideForXor_commutative() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0),
                        "all operands have zero size"),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        "first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "first operand size > second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 1,14,17,22,35,41,51,66,69,71,73,99),
                        "first operand size = second operand size"
                )
        );
    }

    private static Stream<Arguments> provideForXor_transitive() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0), new Bits(0),
                        "all operands have zero size"),
                Arguments.of(
                        Bits.of(200, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(300, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "all operands have different size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "all operands have the same size"
                )
        );
    }

    private static Stream<Arguments> provideForAndNot() {
        Bits theSameOperand = Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99);

        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0), new Bits(0),
                        "all arguments have zero size"),
                Arguments.of(theSameOperand, theSameOperand, new Bits(100),
                        "all operands are the same object"),
                Arguments.of(
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        Bits.of(200, 1,14,17,22,35,41,51,66,69,71,73,99,100,114,115,120,144,177,199),
                        new Bits(200),
                        "all operands are equal"
                ),
                Arguments.of(
                        new Bits(0), Bits.filled(1000), new Bits(0),
                        "first operand has zero size"
                ),
                Arguments.of(
                        new Bits(100), Bits.filled(1000), new Bits(100),
                        "first operand is empty, first operand size < second operand size"
                ),
                Arguments.of(
                        new Bits(1000), Bits.filled(1000), new Bits(1000),
                        "first operand is empty, first operand size = second operand size"
                ),
                Arguments.of(
                        new Bits(1000), Bits.filled(100), new Bits(1000),
                        "first operand is empty, first operand size > second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(0),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "second operand has zero size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(200),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "second operand is empty, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(100),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "second operand is empty, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        new Bits(50),
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        "second operand is empty, first operand size > second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.filled(200),
                        new Bits(100),
                        "second is full, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,34,41,51,66,70,71,72,99),
                        Bits.filled(100),
                        new Bits(100),
                        "second is full, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,23,24,25,26,27,28,29,41,51,66,70,71,72,99),
                        Bits.of(200, 1,2,4,5,6,7,8,9,13,17,18,19,20,22,23,24,25,26,28,70,71,72,98,
                                100,150,151,159,167,180,181,182,193,198,199),
                        Bits.of(100, 0,15,16,27,29,41,51,66,99),
                        "operands are different, first operand size < second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,23,24,25,26,27,28,29,41,51,66,70,71,72,99),
                        Bits.of(100, 1,2,4,5,6,7,8,9,13,17,18,19,20,22,23,24,25,26,28,70,71,72,98),
                        Bits.of(100, 0,15,16,27,29,41,51,66,99),
                        "operands are different, first operand size = second operand size"
                ),
                Arguments.of(
                        Bits.of(100, 0,15,16,22,23,24,25,26,27,28,29,41,51,66,70,71,72,99),
                        Bits.of(30, 1,2,4,5,6,7,8,9,13,17,18,19,20,22,23,24,25,26,28),
                        Bits.of(100, 0,15,16,27,29,41,51,66,70,71,72,99),
                        "operands are different, first operand size > second operand size"
                )
        );
    }

    private static Stream<Arguments> provideForNot() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0)),
                Arguments.of(
                        new Bits(64).setRange(0, 15).setRange(38, 64),
                        new Bits(200).setRange(15, 38)
                ),
                Arguments.of(Bits.filled(1), new Bits(1)),
                Arguments.of(
                        new Bits(150).setRange(0, 60).setRange(117, 128),
                        new Bits(200).setRange(60, 117).setRange(128, 150)
                )
        );
    }

    private static Stream<Arguments> provideForGrowTo() {
        return Stream.of(
                Arguments.of(new Bits(0), 0, new Bits(1)),
                Arguments.of(new Bits(1), 0, new Bits(1)),
                Arguments.of(Bits.filled(1), 0, Bits.filled(1)),
                Arguments.of(Bits.filled(1), 1, Bits.of(2, 0)),
                Arguments.of(Bits.filled(64), 63, Bits.filled(64)),
                Arguments.of(Bits.filled(64), 64, new Bits(65).setRange(0, 64)),
                Arguments.of(Bits.filled(234), 499, new Bits(500).setRange(0, 234))
        );
    }

    private static Stream<Arguments> provideForCheckingIndexForNegative_ExceptionCase() {
        return Stream.of(
                Arguments.of(new Bits(0), -1),
                Arguments.of(new Bits(1), -1),
                Arguments.of(new Bits(64), -1),
                Arguments.of(new Bits(128), -1)
        );
    }

    private static Stream<Arguments> provideForCopyFullStateFrom() {
        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0), new Bits(0)),
                Arguments.of(
                        Bits.of(32, 0,1,2,3,4,9,12,26,31),
                        Bits.of(64, 1,2,3,4,12,14,35,47,63),
                        Bits.of(64, 1,2,3,4,12,14,35,47,63)
                ),
                Arguments.of(
                        Bits.of(64, 1,2,3,4,12,14,35,47,63),
                        Bits.of(64, 0,12,14,56,63),
                        Bits.of(64, 0,12,14,56,63)
                ),
                Arguments.of(
                        Bits.of(64, 1,2,3,4,12,14,35,47,63),
                        Bits.of(32, 0,1,2,3,4,9,12,26,31),
                        Bits.of(32, 0,1,2,3,4,9,12,26,31)
                ),
                Arguments.of(
                        Bits.of(200, 0,1,14,15,16,17,22,34,35,41,51,66,69,70,71,72,73,99,100,114,115,120,144,177,199),
                        Bits.of(100, 1,2,10,17,18,19,25,44,45,46,70,80,90,91,92,93,99),
                        Bits.of(100, 1,2,10,17,18,19,25,44,45,46,70,80,90,91,92,93,99)
                ),
                Arguments.of(
                        Bits.of(200, 0,1,14,15,16,17,22,34,35,41,51,66,69,70,71,72,73,99,100,114,115,120,144,177,199),
                        Bits.of(200, 1,2,10,17,18,19,25,44,45,46,70,80,90,91,92,93,99,165,169,170,171,186,196,198),
                        Bits.of(200, 1,2,10,17,18,19,25,44,45,46,70,80,90,91,92,93,99,165,169,170,171,186,196,198)
                ),
                Arguments.of(
                        Bits.of(100, 1,2,10,17,18,19,25,44,45,46,70,80,90,91,92,93,99),
                        Bits.of(200, 0,1,14,15,16,17,22,34,35,41,51,66,69,70,71,72,73,99,100,114,115,120,144,177,199),
                        Bits.of(200, 0,1,14,15,16,17,22,34,35,41,51,66,69,70,71,72,73,99,100,114,115,120,144,177,199)
                )
        );
    }

    private static Stream<Arguments> provideForCopyRangeFrom_ExceptionCase() {
        return Stream.of(
                Arguments.of(Bits.filled(100), null, 0, 0, 10,
                        NullPointerException.class),
                Arguments.of(Bits.filled(100), Bits.filled(150), -1, 0, 10,
                        IndexOutOfBoundsException.class),
                Arguments.of(Bits.filled(100), Bits.filled(150), 150, 0, 10,
                        IndexOutOfBoundsException.class),
                Arguments.of(Bits.filled(100), Bits.filled(150), 151, 0, 10,
                        IndexOutOfBoundsException.class),
                Arguments.of(Bits.filled(100), Bits.filled(150), 0, -1, 10,
                        IndexOutOfBoundsException.class),
                Arguments.of(Bits.filled(100), Bits.filled(150), 0, 100, 10,
                        IndexOutOfBoundsException.class),
                Arguments.of(Bits.filled(100), Bits.filled(150), 0, 101, 10,
                        IndexOutOfBoundsException.class),
                Arguments.of(Bits.filled(100), Bits.filled(150), 0, 0, -1,
                        IndexOutOfBoundsException.class)
        );
    }

    private static Stream<Arguments> provideForCopyRangeFrom_SrcIsNotDest() {
        return Stream.of(
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(0, 200),
                        0, 0, 100,
                        new Bits(500).setRange(0, 250),
                        100,
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(50, 200),
                        0, 400, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        100,
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(400, 450),
                        400, 0, 100,
                        new Bits(500).setRange(0, 50).setRange(100, 250),
                        100,
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(450, 500),
                        400, 400, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        100,
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(10, 250),
                        new Bits(500).setRange(450, 500),
                        300, 400, 0,
                        new Bits(500).setRange(10, 250),
                        0,
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(350, 500),
                        499, 0, 100,
                        new Bits(500).setRange(50, 250).setAll(0),
                        1,
                        "srcPos + length >= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        new Bits(500).setRange(350, 500),
                        490, 0, 100,
                        new Bits(500).setRange(0, 10).setRange(50, 250).setRange(450, 500),
                        10,
                        "srcPos + length >= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(300, 450),
                        400, 499, 100,
                        new Bits(500).setRange(50, 250).setAll(499),
                        1,
                        "srcPos + length <= src.size(), destPos + length >= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(300, 450),
                        400, 450, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        50,
                        "srcPos + length <= src.size(), destPos + length >= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(350, 500),
                        450, 499, 100,
                        new Bits(500).setRange(50, 250).setAll(499),
                        1,
                        "srcPos + length >= src.size(), destPos + length >= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(350, 500),
                        450, 450, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        50,
                        "srcPos + length >= src.size(), destPos + length >= dest.size(), src range == dest range"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(350, 500),
                        499, 450, 100,
                        new Bits(500).setRange(50, 250).setAll(450),
                        1,
                        "srcPos + length >= src.size(), destPos + length >= dest.size(), src range > dest range"
                )
        );
    }

    private static Stream<Arguments> provideForCopyRangeFrom_SrcIsDest() {
        return Stream.of(
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        0, 0, 100,
                        new Bits(500).setRange(50, 250),
                        100,
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        0, 400, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        100,
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(450, 500),
                        400, 0, 100,
                        new Bits(500).setRange(50, 100).setRange(450, 500),
                        100,
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(450, 500),
                        400, 400, 100,
                        new Bits(500).setRange(450, 500),
                        100,
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        45, 450, 0,
                        new Bits(500).setRange(50, 250),
                        0,
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 400),
                        499, 50, 100,
                        new Bits(500).setRange(51, 400),
                        1,
                        "srcPos + length >= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        440, 50, 100,
                        new Bits(500).setRange(60, 250).setRange(450, 500),
                        60,
                        "srcPos + length >= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        200, 499, 100,
                        new Bits(500).setRange(50, 250).setAll(499),
                        1,
                        "srcPos + length <= origin.size(), destPos + length >= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        200, 450, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        50,
                        "srcPos + length <= origin.size(), destPos + length >= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        200, 499, 400,
                        new Bits(500).setRange(50, 250).setAll(499),
                        1,
                        "srcPos + length >= origin.size(), destPos + length >= origin.size(), dest range < src range"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        200, 200, 400,
                        new Bits(500).setRange(50, 250),
                        300,
                        "srcPos + length >= origin.size(), destPos + length >= origin.size(), dest range == src range"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 500),
                        499, 0, 1000,
                        new Bits(500).setRange(50, 500).setAll(0),
                        1,
                        "srcPos + length >= origin.size(), destPos + length >= origin.size(), dest range > src range"
                )
        );
    }

    private static Stream<Arguments> provideForTruncateTo() {
        return Stream.of(
                Arguments.of(
                        Bits.filled(65),
                        64,
                        Bits.filled(64),
                        new Bits(128).setRange(0, 64)
                ),
                Arguments.of(
                        Bits.filled(65),
                        63,
                        Bits.filled(63),
                        new Bits(128).setRange(0, 63)
                ),
                Arguments.of(
                        Bits.filled(129),
                        128,
                        Bits.filled(128),
                        new Bits(256).setRange(0, 128)
                ),
                Arguments.of(
                        new Bits(500).setRange(230, 500),
                        501,
                        new Bits(500).setRange(230, 500),
                        new Bits(600).setRange(230, 500)
                ),
                Arguments.of(
                        new Bits(500).setRange(230, 500),
                        500,
                        new Bits(500).setRange(230, 500),
                        new Bits(600).setRange(230, 500)
                ),
                Arguments.of(
                        new Bits(500).setRange(230, 500),
                        499,
                        new Bits(499).setRange(230, 499),
                        new Bits(600).setRange(230, 499)
                ),
                Arguments.of(
                        new Bits(500).setRange(0, 50).setRange(230, 500),
                        1,
                        Bits.filled(1),
                        new Bits(600).setAll(0)
                ),
                Arguments.of(
                        new Bits(500).setRange(230, 500),
                        0,
                        new Bits(0),
                        new Bits(600)
                )
        );
    }

    private static Stream<Arguments> provideForCardinality() {
        return Stream.of(
                Arguments.of(new Bits(0), 0),
                Arguments.of(new Bits(1), 0),
                Arguments.of(new Bits(64), 0),
                Arguments.of(new Bits(128), 0),
                Arguments.of(new Bits(129), 0),
                Arguments.of(new Bits(500), 0),
                Arguments.of(Bits.filled(1), 1),
                Arguments.of(Bits.filled(64), 64),
                Arguments.of(Bits.filled(128), 128),
                Arguments.of(Bits.filled(129), 129),
                Arguments.of(Bits.filled(500), 500),
                Arguments.of(new Bits(500).setRange(0, 50), 50),
                Arguments.of(new Bits(500).setRange(63, 163).setAll(200, 241, 307, 344), 104)
        );
    }

    private static Stream<Arguments> provideForGetHighBitIndex() {
        return Stream.of(
                Arguments.of(new Bits(0), -1),
                Arguments.of(new Bits(500), -1),
                Arguments.of(Bits.filled(1), 0),
                Arguments.of(Bits.of(64, 0, 2, 12, 24, 25, 30, 31, 32, 33), 33),
                Arguments.of(Bits.of(64, 0, 13, 14, 15, 16, 23, 24, 25, 26, 54, 63), 63),
                Arguments.of(Bits.of(500, 0), 0),
                Arguments.of(new Bits(500).setRange(120, 256), 255),
                Arguments.of(new Bits(500).setRange(120, 256).setAll(456, 457, 499), 499)
        );
    }

    private static Stream<Arguments> provideForIsClear() {
        return Stream.of(
                Arguments.of(new Bits(0), true),
                Arguments.of(new Bits(500), true),
                Arguments.of(Bits.of(500, 1), false),
                Arguments.of(Bits.of(500, 300), false),
                Arguments.of(Bits.of(500, 499), false)
        );
    }

    private static Stream<Arguments> provideForNextSetBit() {
        return Stream.of(
                Arguments.of(new Bits(500), 0, List.of(-1)),
                Arguments.of(new Bits(500).setRange(250, 340), 340, List.of(-1)),
                Arguments.of(new Bits(500).setRange(250, 255), 250,
                        List.of(250, 251, 252, 253, 254, -1)),
                Arguments.of(new Bits(500).setRange(250, 500), 500, List.of(-1)),
                Arguments.of(new Bits(500).setRange(250, 500), 501, List.of(-1)),
                Arguments.of(
                        Bits.of(500, 0,7,12,22,23,27,30,50,51,52,53,54,100,250,251,400,495,499),
                        36,
                        List.of(50, 51, 52, 53, 54, 100, 250, 251, 400, 495, 499, -1)
                )
        );
    }

    private static Stream<Arguments> provideForNextClearBit() {
        return Stream.of(
                Arguments.of(Bits.filled(500), 0, List.of(-1)),
                Arguments.of(Bits.filled(500).clearRange(150, 307), 307, List.of(-1)),
                Arguments.of(Bits.filled(500).clearRange(250, 255), 250,
                        List.of(250, 251, 252, 253, 254, -1)),
                Arguments.of(Bits.filled(500).clearRange(250, 500), 500, List.of(-1)),
                Arguments.of(Bits.filled(500).clearRange(250, 500), 501, List.of(-1)),
                Arguments.of(
                        Bits.filled(500)
                                .clearAll(0,7,12,22,23,27,30,50,51,52,53,54,100,250,251,400,495,499),
                        36,
                        List.of(50, 51, 52, 53, 54, 100, 250, 251, 400, 495, 499, -1)
                )
        );
    }

    private static Stream<Arguments> provideForContains() {
        Bits theSameObject = new Bits(500).setRange(47, 300).setAll(0, 42, 43, 317, 404);

        return Stream.of(
                Arguments.of(new Bits(0), new Bits(0), true),
                Arguments.of(new Bits(0), new Bits(500), true),
                Arguments.of(new Bits(500), new Bits(0), true),
                Arguments.of(new Bits(500), new Bits(500), true),
                Arguments.of(new Bits(500).setRange(150, 230), new Bits(0), true),
                Arguments.of(new Bits(500).setRange(150, 230), new Bits(500), true),
                Arguments.of(new Bits(500).setRange(150, 230), new Bits(700), true),
                Arguments.of(new Bits(500).setRange(150, 230),
                        new Bits(300).setRange(177, 212),
                        true),
                Arguments.of(new Bits(500).setRange(150, 230),
                        new Bits(500).setRange(177, 212),
                        true),
                Arguments.of(new Bits(500).setRange(150, 230),
                        new Bits(700).setRange(177, 212),
                        true),
                Arguments.of(new Bits(500).setRange(150, 230),
                        new Bits(300).setRange(149, 229),
                        false),
                Arguments.of(new Bits(500).setRange(150, 230),
                        new Bits(500).setRange(149, 229),
                        false),
                Arguments.of(new Bits(500).setRange(150, 230),
                        new Bits(700).setRange(149, 229),
                        false),
                Arguments.of(theSameObject, theSameObject, true)
        );
    }

    private static Stream<Arguments> provideForIntersect() {
        Bits theSameObject = new Bits(500).setRange(47, 300).setAll(0, 42, 43, 317, 404);

        return Stream.of(
                Arguments.of(new Bits(300).setRange(40, 233),
                        new Bits(500).setRange(233, 414),
                        false),
                Arguments.of(new Bits(300).setRange(40, 233),
                        new Bits(500).setRange(232, 414),
                        true),
                Arguments.of(new Bits(500).setRange(40, 233),
                        new Bits(500).setRange(233, 414),
                        false),
                Arguments.of(new Bits(500).setRange(40, 233),
                        new Bits(500).setRange(232, 414),
                        true),
                Arguments.of(new Bits(700).setRange(40, 233),
                        new Bits(500).setRange(233, 414),
                        false),
                Arguments.of(new Bits(700).setRange(40, 233),
                        new Bits(500).setRange(232, 414),
                        true),
                Arguments.of(theSameObject, theSameObject, true),
                Arguments.of(new Bits(700).setRange(40, 233), new Bits(0), false),
                Arguments.of(new Bits(700).setRange(40, 233), new Bits(500), false),
                Arguments.of(new Bits(700).setRange(40, 233), new Bits(700), false)
        );
    }

    private static Stream<Arguments> provideForEquals() {
        return Stream.of(
                Arguments.of(new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        new Bits(400).setRange(169, 389).setAll(0, 47, 50, 101),
                        false),
                Arguments.of(new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        new Bits(400).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        false),
                Arguments.of(new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        new Bits(500).setRange(169, 389).setAll(0, 47, 50, 101),
                        false),
                Arguments.of(new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        true)
        );
    }

    private static Stream<Arguments> provideForEqualsIgnoreSize() {
        return Stream.of(
                Arguments.of(new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        new Bits(400).setRange(169, 389).setAll(0, 47, 50, 101),
                        false),
                Arguments.of(new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        new Bits(400).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        true),
                Arguments.of(new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        new Bits(500).setRange(169, 389).setAll(0, 47, 50, 101),
                        false),
                Arguments.of(new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        new Bits(500).setRange(170, 390).setAll(0, 47, 51, 91, 101),
                        true)
        );
    }

    private static Stream<Arguments> provideForCompareTo() {
        return Stream.of(
                Arguments.of(new Bits(500).setRange(170, 301),
                        new Bits(400).setRange(170, 300),
                        1),
                Arguments.of(new Bits(500).setRange(170, 300),
                        new Bits(400).setRange(170, 300),
                        1),
                Arguments.of(new Bits(500).setRange(170, 300),
                        new Bits(400).setRange(170, 301),
                        1),
                Arguments.of(new Bits(400).setRange(170, 301),
                        new Bits(500).setRange(170, 300),
                        -1),
                Arguments.of(new Bits(400).setRange(170, 300),
                        new Bits(500).setRange(170, 300),
                        -1),
                Arguments.of(new Bits(400).setRange(170, 300),
                        new Bits(500).setRange(170, 301),
                        -1),
                Arguments.of(new Bits(500).setRange(170, 301),
                        new Bits(500).setRange(170, 300),
                        1),
                Arguments.of(new Bits(500).setRange(170, 300),
                        new Bits(500).setRange(170, 300),
                        0),
                Arguments.of(new Bits(500).setRange(170, 300),
                        new Bits(500).setRange(170, 301),
                        -1)
        );
    }

    private static Stream<Arguments> provideForCompareIgnoreSize() {
        return Stream.of(
                Arguments.of(new Bits(500).setRange(170, 301),
                        new Bits(400).setRange(170, 300),
                        1),
                Arguments.of(new Bits(500).setRange(170, 300),
                        new Bits(400).setRange(170, 300),
                        0),
                Arguments.of(new Bits(500).setRange(170, 300),
                        new Bits(400).setRange(170, 301),
                        -1),
                Arguments.of(new Bits(400).setRange(170, 301),
                        new Bits(500).setRange(170, 300),
                        1),
                Arguments.of(new Bits(400).setRange(170, 300),
                        new Bits(500).setRange(170, 300),
                        0),
                Arguments.of(new Bits(400).setRange(170, 300),
                        new Bits(500).setRange(170, 301),
                        -1),
                Arguments.of(new Bits(500).setRange(170, 301),
                        new Bits(500).setRange(170, 300),
                        1),
                Arguments.of(new Bits(500).setRange(170, 300),
                        new Bits(500).setRange(170, 300),
                        0),
                Arguments.of(new Bits(500).setRange(170, 300),
                        new Bits(500).setRange(170, 301),
                        -1)
        );
    }

    private static Stream<Arguments> provideForToBinaryString() {
        return Stream.of(
                Arguments.of(new Bits(0), ""),
                Arguments.of(new Bits(1), "0"),
                Arguments.of(Bits.filled(1), "1"),
                Arguments.of(new Bits(64),
                        "0000000000000000000000000000000000000000000000000000000000000000"),
                Arguments.of(Bits.filled(64),
                        "1111111111111111111111111111111111111111111111111111111111111111"),
                Arguments.of(new Bits(65),
                        "00000000000000000000000000000000000000000000000000000000000000000"),
                Arguments.of(Bits.filled(65),
                        "11111111111111111111111111111111111111111111111111111111111111111"),
                Arguments.of(new Bits(128),
                        "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"),
                Arguments.of(Bits.filled(128),
                        "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"),
                Arguments.of(Bits.of(57, 0, 8, 16, 23, 24, 25, 26, 27, 42, 44, 50),
                        "000000100000101000000000000001111100000010000000100000001"),
                Arguments.of(Bits.of(94, 0, 2, 3, 4, 5, 7, 9, 13, 16, 18,
                                19, 20, 22, 23, 27, 28, 29, 33, 34, 36,
                                37, 38, 40, 41, 49, 54, 55, 56, 57, 58,
                                61, 63, 64, 66, 67, 70, 73, 74, 76, 77,
                                80, 81, 85, 88, 89, 91, 92, 93),
                        "1110110010001100110110010011011010011111000010000000110111011000111000110111010010001010111101"),
                Arguments.of(Bits.of(96, 0, 2, 3, 4, 5, 7, 9, 13, 16, 18,
                                19, 20, 22, 23, 27, 28, 29, 33, 34, 36,
                                37, 38, 40, 41, 49, 54, 55, 56, 57, 58),
                        "000000000000000000000000000000000000011111000010000000110111011000111000110111010010001010111101")
        );
    }
}
