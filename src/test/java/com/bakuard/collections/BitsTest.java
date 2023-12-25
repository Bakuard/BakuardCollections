package com.bakuard.collections;

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
        actual.growTo(1000);

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
             note: {6},
             srcPos is {2},
             destPos is {3},
             length is {4},
             origin is {0},
             src is {1}
             => expected is {5}
            """)
    @MethodSource("provideForCopyRangeFrom_SrcIsNotDest")
    void copyRangeFrom_srcIsNotDest(Bits origin,
                                    Bits src,
                                    int srcPos,
                                    int destPos,
                                    int length,
                                    Bits expected,
                                    String note) {
        origin.copyRangeFrom(src, srcPos, destPos, length);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("copyRangeFrom(src, srcPos, destPos, length): src == dest")
    @ParameterizedTest(name = """
             note: {5},
             srcPos is {1},
             destPos is {2},
             length is {3},
             origin is {0},
             => expected is {4}
            """)
    @MethodSource("provideForCopyRangeFrom_SrcIsDest")
    void copyRangeFrom_srcIsDest(Bits origin,
                                 int srcPos,
                                 int destPos,
                                 int length,
                                 Bits expected,
                                 String note) {
        origin.copyRangeFrom(origin, srcPos, destPos, length);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             length == 0
             => return 0
            """)
    void copyRange31() {
        Bits dest = new Bits(500);
        dest.setRange(50,250);
        Bits src = new Bits(500);
        src.setRange(350, 450);

        int actual = dest.copyRangeFrom(src, 350, 300, 0);

        Assertions.assertThat(actual).isZero();
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             src range is within src,
             dest range is within dest
             => return correct result
            """)
    void copyRange32() {
        Bits dest = new Bits(500);
        dest.setRange(50,250);
        Bits src = new Bits(500);
        src.setRange(350, 450);

        int actual = dest.copyRangeFrom(src, 350, 300, 100);

        Assertions.assertThat(actual).isEqualTo(100);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             src range is out of src,
             dest range is within dest
             => return correct result
            """)
    void copyRange33() {
        Bits dest = new Bits(500);
        dest.setRange(250, 350);
        Bits src = new Bits(500);
        src.setRange(450, 500);

        int actual = dest.copyRangeFrom(src, 499, 0, 100);

        Assertions.assertThat(actual).isOne();
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             src range is within src,
             dest range is out of dest
             => return correct result
            """)
    void copyRange34() {
        Bits dest = new Bits(500);
        dest.setRange(250, 350);
        Bits src = new Bits(500);
        src.setRange(450, 500);

        int actual = dest.copyRangeFrom(src, 450, 499, 10);

        Assertions.assertThat(actual).isOne();
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             src range is out of src,
             dest range is out of dest,
             src range < dest range
             => return correct result
            """)
    void copyRange35() {
        Bits dest = new Bits(500);
        dest.setRange(350, 450);
        Bits src = new Bits(500);
        src.setRange(400, 500);

        int actual = dest.copyRangeFrom(src, 499, 495, 10);

        Assertions.assertThat(actual).isOne();
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             src range is out of src,
             dest range is out of dest,
             src range == dest range
             => return correct result
            """)
    void copyRange36() {
        Bits dest = new Bits(500);
        dest.setRange(350, 450);
        Bits src = new Bits(500);
        src.setRange(400, 500);

        int actual = dest.copyRangeFrom(src, 499, 499, 10);

        Assertions.assertThat(actual).isOne();
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             src range is out of src,
             dest range is out of dest,
             src range > dest range
             => return correct result
            """)
    void copyRange37() {
        Bits dest = new Bits(500);
        dest.setRange(350, 450);
        Bits src = new Bits(500);
        src.setRange(400, 500);

        int actual = dest.copyRangeFrom(src, 480, 499, 50);

        Assertions.assertThat(actual).isOne();
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             src range cross dest range,
             src range before dest range
             => correct result
            """)
    void copyRange38() {
        Bits dest = new Bits(500);
        dest.setRange(50, 100);
        Bits expected = new Bits(500);
        expected.setRange(100, 150);

        dest.copyRangeFrom(dest, 0, 50, 100);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             src range cross dest range,
             src range equal dest range,
             src and dest range is within object,
             => result equal dest
            """)
    void copyRange39() {
        Bits dest = new Bits(500);
        dest.setRange(50, 100);
        Bits expected = new Bits(dest);

        dest.copyRangeFrom(dest, 50, 50, 100);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             src range cross dest range,
             src range after dest range
             => result equal dest
            """)
    void copyRange40() {
        Bits dest = new Bits(500);
        dest.setRange(50, 100);
        dest.setRange(150, 200);
        Bits expected = new Bits(500);
        expected.setRange(25, 50);
        expected.setRange(100, 125);
        expected.setRange(150, 200);

        dest.copyRangeFrom(dest, 75, 25, 100);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @DisplayName("growTo(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => expected bits {2}
            """)
    @MethodSource("provideForGrowTo")
    void growTo(Bits origin, int index, Bits expected) {
        origin.growTo(index);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("growTo(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => exception
            """)
    @MethodSource("provideForGrowTo_ExceptionCase")
    void growTo_exception(Bits origin, int index) {
        Bits expected = new Bits(origin);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.growTo(index));
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @Test
    @DisplayName("compressTo(newSize): newSize > bits size => return false")
    void compressTo1() {
        Bits actual = new Bits(500);
        actual.setRange(230, 490);

        Assertions.assertThat(actual.compressTo(501)).isFalse();
    }

    @Test
    @DisplayName("compressTo(newSize): newSize == bits size => return false")
    void compressTo2() {
        Bits actual = new Bits(500);
        actual.setRange(230, 490);

        Assertions.assertThat(actual.compressTo(500)).isFalse();
    }

    @Test
    @DisplayName("compressTo(newSize): newSize < bits size => return true")
    void compressTo3() {
        Bits actual = new Bits(500);
        actual.setRange(230, 490);

        Assertions.assertThat(actual.compressTo(499)).isTrue();
    }

    @Test
    @DisplayName("compressTo(newSize): newSize > bits size => don't change target object")
    void compressTo4() {
        Bits actual = new Bits(500);
        actual.setRange(150, 405);
        Bits expected = new Bits(actual);

        actual.compressTo(501);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("compressTo(newSize): newSize == bits size => don't change target object")
    void compressTo5() {
        Bits actual = new Bits(500);
        actual.setRange(150, 405);
        Bits expected = new Bits(actual);

        actual.compressTo(500);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("compressTo(newSize): newSize < bits size => compress target object")
    void compressTo6() {
        Bits actual = new Bits(500);
        actual.setRange(150, 405);
        Bits expected = new Bits(499);
        expected.setRange(150, 405);

        actual.compressTo(499);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("compressTo(newSize): newSize < bits size => clear all bits out of new range")
    void compressTo7() {
        Bits actual = new Bits(500);
        actual.setAll();

        actual.compressTo(70);
        actual.growTo(500);

        for(int i = 70; i < 500; i++) {
            Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("compressTo(newSize): newSize == 0 => result is empty")
    void compressTo8() {
        Bits actual = new Bits(500);
        actual.setAll();
        Bits empty = new Bits();

        actual.compressTo(0);

        Assertions.assertThat(actual).isEqualTo(empty);
    }

    @Test
    @DisplayName("compressTo(newSize): newSize == 0 => clear all bits out of new range")
    void compressTo9() {
        Bits actual = new Bits(500);
        actual.setAll();

        actual.compressTo(0);
        actual.growTo(500);

        for(int i = 0; i < 500; i++) {
            Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("compressTo(newSize): newSize < 0 => return false")
    void compressTo10() {
        Bits actual = new Bits(500);
        actual.setRange(150, 450);

        Assertions.assertThat(actual.compressTo(-1)).isFalse();
    }

    @Test
    @DisplayName("compressTo(newSize): newSize < 0 => don't change target object")
    void compressTo11() {
        Bits actual = new Bits(500);
        actual.setRange(150, 450);
        Bits expected = new Bits(actual);

        actual.compressTo(-1);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("cardinality(): empty bits => return 0")
    void cardinality1() {
        Bits empty = new Bits(500);
        
        Assertions.assertThat(empty.cardinality()).isZero();
    }

    @Test
    @DisplayName("cardinality(): one word, is not empty => correct result")
    void cardinality2() {
        Bits actual = new Bits(63);
        actual.setAll(0, 15, 16, 17, 18, 19, 45, 46, 55);

        Assertions.assertThat(actual.cardinality()).isEqualTo(9);
    }

    @Test
    @DisplayName("cardinality(): several word, is not empty => correct result")
    void cardinality3() {
        Bits actual = new Bits(500);
        actual.setAll(0, 12, 56, 123, 124);
        actual.setRange(250, 500);

        Assertions.assertThat(actual.cardinality()).isEqualTo(255);
    }

    @Test
    @DisplayName("getHighBitIndex(): bits is empty, size == 0 => return -1")
    void getHighBitIndex1() {
        Bits empty = new Bits();

        Assertions.assertThat(empty.getHighBitIndex()).isEqualTo(-1);
    }

    @Test
    @DisplayName("getHighBitIndex(): bits is empty, size > 0 => return -1")
    void getHighBitIndex2() {
        Bits actual = new Bits(500);

        Assertions.assertThat(actual.getHighBitIndex()).isEqualTo(-1);
    }

    @Test
    @DisplayName("getHighBitIndex(): one word, first bit is high => return 0")
    void getHighBitIndex3() {
        Bits actual = new Bits(63);
        actual.set(0);

        Assertions.assertThat(actual.getHighBitIndex()).isZero();
    }

    @Test
    @DisplayName("getHighBitIndex(): one word, high bit in middle => return correct result")
    void getHighBitIndex4() {
        Bits actual = new Bits(63);
        actual.setAll(0, 2, 12, 24, 25, 30, 31, 32, 33);

        Assertions.assertThat(actual.getHighBitIndex()).isEqualTo(33);
    }

    @Test
    @DisplayName("getHighBitIndex(): one word, high bit is last => return last bit")
    void getHighBitIndex5() {
        Bits actual = new Bits(63);
        actual.setAll(0, 13, 14, 15, 16, 23, 24, 25, 26, 54, 62);

        Assertions.assertThat(actual.getHighBitIndex()).isEqualTo(62);
    }

    @Test
    @DisplayName("getHighBitIndex(): several word, first bit is high => return 0")
    void getHighBitIndex6() {
        Bits actual = new Bits(500);
        actual.set(0);

        Assertions.assertThat(actual.getHighBitIndex()).isZero();
    }

    @Test
    @DisplayName("getHighBitIndex(): several word, high bit in middle => return correct result")
    void getHighBitIndex7() {
        Bits actual = new Bits(500);
        actual.setRange(12, 256);

        Assertions.assertThat(actual.getHighBitIndex()).isEqualTo(255);
    }

    @Test
    @DisplayName("getHighBitIndex(): several word, high bit is last => return last bit")
    void getHighBitIndex8() {
        Bits actual = new Bits(500);
        actual.setRange(12, 300);
        actual.setAll(456, 457, 499);

        Assertions.assertThat(actual.getHighBitIndex()).isEqualTo(499);
    }

    @Test
    @DisplayName("isClean(): bits is empty, size == 0 => return true")
    void isClean1() {
        Bits empty = new Bits();

        Assertions.assertThat(empty.isClear()).isTrue();
    }

    @Test
    @DisplayName("isClean(): bits is empty, size > 0 => return true")
    void isClean2() {
        Bits empty = new Bits(500);

        Assertions.assertThat(empty.isClear()).isTrue();
    }

    @Test
    @DisplayName("isClean(): one bit is set, first bit is set => return false")
    void isClean3() {
        Bits actual = new Bits(500);
        actual.set(0);

        Assertions.assertThat(actual.isClear()).isFalse();
    }

    @Test
    @DisplayName("isClean(): one bit is set, unit bit in middle => return false")
    void isClean4() {
        Bits actual = new Bits(500);
        actual.set(300);

        Assertions.assertThat(actual.isClear()).isFalse();
    }

    @Test
    @DisplayName("isClean(): one bit is set, high bit is set => return false")
    void isClean5() {
        Bits actual = new Bits(500);
        actual.set(499);

        Assertions.assertThat(actual.isClear()).isFalse();
    }

    @Test
    @DisplayName("nextSetBit(fromIndex): bits is empty => return -1")
    void nextSetBit1() {
        Bits empty = new Bits(500);

        Assertions.assertThat(empty.nextSetBit(0)).isEqualTo(-1);
    }

    @Test
    @DisplayName("nextSetBit(fromIndex): fromIndex bigger than high one bit => return -1")
    void nextSetBit2() {
        Bits actual = new Bits(500);
        actual.setRange(23, 355);

        Assertions.assertThat(actual.nextSetBit(355)).isEqualTo(-1);
    }

    @Test
    @DisplayName("nextSetBit(fromIndex): fromIndex is one bit => return fromIndex")
    void nextSetBit3() {
        Bits actual = new Bits(500);
        actual.setRange(100, 250);

        Assertions.assertThat(actual.nextSetBit(100)).isEqualTo(100);
    }

    @Test
    @DisplayName("nextSetBit(fromIndex): bits contains several one bits => correct iteration")
    void nextSetBit4() {
        Bits bits = new Bits(500);
        bits.setAll(12, 50, 51, 52, 53, 54, 100, 250, 251, 400, 495, 499);
        List<Integer> actual = new ArrayList<>();
        List<Integer> expected = List.of(12, 50, 51, 52, 53, 54, 100, 250, 251, 400, 495, 499);

        int index = bits.nextSetBit(0);
        while(index != -1) {
            actual.add(index);
            index = bits.nextSetBit(index + 1);
        }

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("nextSetBit(fromIndex): fromIndex < 0 => exception")
    void nextSetBit5() {
        Bits actual = new Bits(500);
        actual.setRange(100, 200);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.nextSetBit(-1));
    }

    @Test
    @DisplayName("nextSetBit(fromIndex): fromIndex == size => return -1")
    void nextSetBit6() {
        Bits actual = new Bits(500);
        actual.setRange(100, 200);

        Assertions.assertThat(actual.nextSetBit(500)).isEqualTo(-1);
    }

    @Test
    @DisplayName("nextSetBit(fromIndex): fromIndex > size => return -1")
    void nextSetBit7() {
        Bits actual = new Bits(500);
        actual.setRange(100, 200);

        Assertions.assertThat(actual.nextSetBit(501)).isEqualTo(-1);
    }

    @Test
    @DisplayName("nextClearBit(fromIndex): bits is full => return -1")
    void nextClearBit1() {
        Bits full = new Bits(500);
        full.setAll();

        Assertions.assertThat(full.nextClearBit(0)).isEqualTo(-1);
    }

    @Test
    @DisplayName("nextClearBit(fromIndex): fromIndex bigger than high zero bit => return -1")
    void nextClearBit2() {
        Bits actual = new Bits(500);
        actual.setAll();
        actual.clearRange(0, 355);

        Assertions.assertThat(actual.nextClearBit(355)).isEqualTo(-1);
    }

    @Test
    @DisplayName("nextClearBit(fromIndex): fromIndex is zero bit => fromIndex")
    void nextClearBit3() {
        Bits actual = new Bits(500);
        actual.setAll();
        actual.clearRange(250, 500);

        Assertions.assertThat(actual.nextClearBit(250)).isEqualTo(250);
    }

    @Test
    @DisplayName("nextClearBit(fromIndex): bits contains several zero bits => correct iteration")
    void nextClearBit4() {
        Bits bits = new Bits(500);
        bits.setAll();
        bits.clearAll(12, 50, 51, 52, 53, 54, 100, 250, 251, 400, 495, 499);
        List<Integer> actual = new ArrayList<>();
        List<Integer> expected = List.of(12, 50, 51, 52, 53, 54, 100, 250, 251, 400, 495, 499);

        int index = bits.nextClearBit(0);
        while(index != -1) {
            actual.add(index);
            index = bits.nextClearBit(index + 1);
        }

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("nextClearBit(fromIndex): fromIndex < 0 => exception")
    void nextClearBit5() {
        Bits actual = new Bits(500);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.nextClearBit(-1));
    }

    @Test
    @DisplayName("nextClearBit(fromIndex): fromIndex == size => return -1")
    void nextClearBit6() {
        Bits actual = new Bits(500);

        Assertions.assertThat(actual.nextClearBit(500)).isEqualTo(-1);
    }

    @Test
    @DisplayName("nextClearBit(fromIndex): fromIndex > size => return -1")
    void nextClearBit7() {
        Bits actual = new Bits(500);

        Assertions.assertThat(actual.nextClearBit(501)).isEqualTo(-1);
    }

    @Test
    @DisplayName("""
            contains(other):
             first and second operand are empty,
             first operand size == 0,
             second operand size == 0
             => return true
            """)
    void contains1() {
        Bits firstOperand = new Bits();
        Bits secondOperand = new Bits();

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first and second operand are empty,
             first operand size == 0,
             second operand size > 0
             => return true
            """)
    void contains2() {
        Bits firstOperand = new Bits();
        Bits secondOperand = new Bits(500);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first and second operand are empty,
             first operand size > 0,
             second operand size == 0
             => return true
            """)
    void contains3() {
        Bits firstOperand = new Bits(500);
        Bits secondOperand = new Bits();

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first and second operand are empty,
             first operand > 0,
             second operand > 0,
             => return true
            """)
    void contains4() {
        Bits firstOperand = new Bits(500);
        Bits secondOperand = new Bits(500);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             second operand is empty,
             first operand size > second operand size
             => return true
            """)
    void contains5() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 470);
        Bits secondOperand = new Bits();

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             second operand is empty,
             first operand size == second operand size
             => return true
            """)
    void contains6() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 477);
        Bits secondOperand = new Bits(500);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             secondOperand is emtpy,
             first operand size > second operand size
             => return true
            """)
    void contains7() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(127, 399);
        Bits secondOperand = new Bits(700);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             second operand is not empty,
             first operand contains second,
             first operand size > second operand size
             => return true
            """)
    void contains8() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(107, 423);
        Bits secondOperand = new Bits(200);
        secondOperand.setRange(107, 189);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             second operand is not empty,
             first operand contains second,
             first operand size == second operand size
             => return true
            """)
    void contains9() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(107, 405);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(107, 370);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             second operand is not empty,
             first operand contains second,
             first operand size < second operand size
             => return true
            """)
    void contains10() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(110, 401);
        Bits secondOperand = new Bits(600);
        secondOperand.setRange(110, 383);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             second operand is not empty,
             first operand doesn't contain second,
             first operand size > second operand size
             => return false
            """)
    void contains11() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(95, 404);
        Bits secondOperand = new Bits(300);
        secondOperand.setRange(90, 300);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             second operand is not empty,
             first operand doesn't contain second,
             first operand size == second operand size
             => return false
            """)
    void contains12() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(150, 450);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            contains(other):
             first operand is not empty,
             second operand is not empty,
             first operand doesn't contain second,
             first operand size < second operand size
             => return false
            """)
    void contains13() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 350);
        Bits secondOperand = new Bits(600);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.contains(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            contains(other):
             first and same operand is same object,
             => return true
            """)
    void contains14() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(77, 451);

        Assertions.assertThat(firstOperand.contains(firstOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            intersect(other):
             first operand size < second operand size,
             first operand doesn't intersect second
             => return false
            """)
    void intersect1() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 240);
        Bits secondOperand = new Bits(600);
        secondOperand.setRange(240, 500);

        Assertions.assertThat(firstOperand.intersect(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            intersect(other):
             first operand size < second operand size,
             first operand intersect second
             => return true
            """)
    void intersect2() {
        Bits firstOperand = new Bits(300);
        firstOperand.setRange(150, 300);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(299, 500);

        Assertions.assertThat(firstOperand.intersect(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            intersect(other):
             first operand size == second operand size,
             first operand doesn't intersect second
             => return false
            """)
    void intersect3() {
        Bits firstOperand = new Bits(300);
        firstOperand.setRange(10, 150);
        Bits secondOperand = new Bits(300);
        secondOperand.setRange(150, 300);

        Assertions.assertThat(firstOperand.intersect(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            intersect(other):
             first operand size == second operand size,
             first operand intersect second
             => return true
            """)
    void intersect4() {
        Bits firstOperand = new Bits(300);
        firstOperand.setRange(10, 150);
        Bits secondOperand = new Bits(300);
        secondOperand.setRange(149, 300);

        Assertions.assertThat(firstOperand.intersect(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            intersect(other):
             first operand size > second operand size,
             first operand doesn't intersect second
             => return false
            """)
    void intersect5() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 456);
        Bits secondOperand = new Bits(300);
        secondOperand.setRange(0, 120);

        Assertions.assertThat(firstOperand.intersect(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            intersect(other):
             first and second operand are same object
             => return true
            """)
    void intersect6() {
        Bits firstOperand = new Bits(400);
        firstOperand.setRange(120, 370);

        Assertions.assertThat(firstOperand.intersect(firstOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            intersect(other):
             second operand is empty,
             second operand size == 0
             => return false
            """)
    void intersect7() {
        Bits firstOperand = new Bits(400);
        firstOperand.setRange(100, 394);
        Bits secondOperand = new Bits();

        Assertions.assertThat(firstOperand.intersect(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            equals(Object o):
             first operand size != second operand size,
             first operand bits don't equal second operand bits
             => return false
            """)
    void equals1() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 450);
        Bits secondOperand = new Bits(450);
        secondOperand.setRange(200, 401);

        Assertions.assertThat(firstOperand).isNotEqualTo(secondOperand);
        Assertions.assertThat(firstOperand).isNotEqualTo(secondOperand);
    }

    @Test
    @DisplayName("""
            equals(Object o):
             first operand size != second operand size,
             first operand bits equal second operand bits
             => return false
            """)
    void equals2() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 436);
        Bits secondOperand = new Bits(501);
        secondOperand.setRange(120, 436);

        Assertions.assertThat(firstOperand).isNotEqualTo(secondOperand);
    }

    @Test
    @DisplayName("""
            equals(Object o):
             first operand size == second operand size,
             first operand bits don't equal second operand bits
             => return false
            """)
    void equals3() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 468);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(119, 467);

        Assertions.assertThat(firstOperand).isNotEqualTo(secondOperand);
    }

    @Test
    @DisplayName("""
            equals(Object o):
             first operand size == second operand size,
             first operand bits equal second operand bits
             => return true
            """)
    void equals4() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 455);
        firstOperand.clearAll(134, 233, 236, 400, 425);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(120, 455);
        secondOperand.clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): idempotence property")
    void equals5() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 455);
        firstOperand.clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(firstOperand).isEqualTo(firstOperand);
    }

    @Test
    @DisplayName("equals(Object o): commutative property")
    void equals6() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 455);
        firstOperand.clearAll(134, 233, 236, 400, 425);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(120, 455);
        secondOperand.clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
        Assertions.assertThat(secondOperand).isEqualTo(firstOperand);
    }

    @Test
    @DisplayName("equals(Object o): transitive property")
    void equals7() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 455);
        firstOperand.clearAll(134, 233, 236, 400, 425);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(120, 455);
        secondOperand.clearAll(134, 233, 236, 400, 425);
        Bits threadOperand = new Bits(500);
        threadOperand.setRange(120, 455);
        threadOperand.clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
        Assertions.assertThat(secondOperand).isEqualTo(threadOperand);
        Assertions.assertThat(firstOperand).isEqualTo(threadOperand);
    }

    @Test
    @DisplayName("""
            equalsIgnoreSize(Object o):
             first operand size != second operand size,
             first operand bits don't equal second operand bits
             => return false
            """)
    void equalsIgnoreSize1() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 450);
        Bits secondOperand = new Bits(450);
        secondOperand.setRange(200, 401);

        Assertions.assertThat(firstOperand.equalsIgnoreSize(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            equalsIgnoreSize(Object o):
             first operand size != second operand size,
             first operand bits equal second operand bits
             => return true
            """)
    void equalsIgnoreSize2() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 436);
        Bits secondOperand = new Bits(501);
        secondOperand.setRange(120, 436);

        Assertions.assertThat(firstOperand.equalsIgnoreSize(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            equalsIgnoreSize(Object o):
             first operand size == second operand size,
             first operand bits don't equal second operand bits
             => return false
            """)
    void equalsIgnoreSize3() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 468);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(119, 467);

        Assertions.assertThat(firstOperand.equalsIgnoreSize(secondOperand)).isFalse();
    }

    @Test
    @DisplayName("""
            equalsIgnoreSize(Object o):
             first operand size == second operand size,
             first operand bits equal second operand bits
             => return true
            """)
    void equalsIgnoreSize4() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 455);
        firstOperand.clearAll(134, 233, 236, 400, 425);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(120, 455);
        secondOperand.clearAll(134, 233, 236, 400, 425);
        
        Assertions.assertThat(firstOperand.equalsIgnoreSize(secondOperand)).isTrue();
    }

    @Test
    @DisplayName("equalsIgnoreSize(Object o): idempotence property => return true")
    void equalsIgnoreSize5() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 455);
        firstOperand.clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(firstOperand.equalsIgnoreSize(firstOperand)).isTrue();
    }

    @Test
    @DisplayName("equalsIgnoreSize(Object o): commutative property => return true")
    void equalsIgnoreSize6() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 455);
        firstOperand.clearAll(134, 233, 236, 400, 425);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(120, 455);
        secondOperand.clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(firstOperand.equalsIgnoreSize(secondOperand)).isTrue();
        Assertions.assertThat(secondOperand.equalsIgnoreSize(firstOperand)).isTrue();
    }

    @Test
    @DisplayName("equalsIgnoreSize(Object o): transitive property")
    void equalsIgnoreSize7() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(120, 455);
        firstOperand.clearAll(134, 233, 236, 400, 425);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(120, 455);
        secondOperand.clearAll(134, 233, 236, 400, 425);
        Bits threadOperand = new Bits(500);
        threadOperand.setRange(120, 455);
        threadOperand.clearAll(134, 233, 236, 400, 425);

        Assertions.assertThat(firstOperand.equalsIgnoreSize(secondOperand)).isTrue();
        Assertions.assertThat(secondOperand.equalsIgnoreSize(threadOperand)).isTrue();
        Assertions.assertThat(firstOperand.equalsIgnoreSize(threadOperand)).isTrue();
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size > second operand size,
             first operand bits > second operand bits as unsigned number
             => return positive number
            """)
    void compareTo1() {
         Bits firstOperand = new Bits(500);
         firstOperand.setRange(100, 400);
         Bits secondOperand = new Bits(400);
         secondOperand.setRange(100, 399);

         Assertions.assertThat(firstOperand.compareTo(secondOperand)).isGreaterThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size > second operand size,
             first operand bits == second operand bits as unsigned number
             => return positive number
            """)
    void compareTo2() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(400);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isGreaterThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size > second operand size,
             first operand bits < second operand bits as unsigned number
             => return positive number
            """)
    void compareTo3() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 399);
        Bits secondOperand = new Bits(400);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isGreaterThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size < second operand size,
             first operand bits > second operand bits as unsigned number
             => return negative number
            """)
    void compareTo4() {
        Bits firstOperand = new Bits(400);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(100, 399);
        
        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size < second operand size,
             first operand bits == second operand bits as unsigned number
             => return negative number
            """)
    void compareTo5() {
        Bits firstOperand = new Bits(400);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size < second operand size,
             first operand bits < second operand bits as unsigned number
             => return negative number
            """)
    void compareTo6() {
        Bits firstOperand = new Bits(400);
        firstOperand.setRange(100, 399);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size == second operand size,
             first operand bits < second operand bits as unsigned number
             => return negative value
            """)
    void compareTo7() {
        Bits firstOperand = new Bits(400);
        firstOperand.setRange(100, 399);
        Bits secondOperand = new Bits(400);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size == second operand size,
             first operand bits == second operand bits as unsigned number
             => return 0
            """)
    void compareTo8() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isZero();
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             first operand size == second operand size,
             first operand bits > second operand bits as unsigned number
             => return positive number
            """)
    void compareTo9() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(101, 400);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isGreaterThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             x.compareTo(y) == 0
             => x.compareTo(z) == y.compareTo(z)
            """)
    void compareTo10() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(firstOperand);
        Bits threadOperand = new Bits(450);
        threadOperand.setRange(100, 450);

        Assertions.assertThat(firstOperand.compareTo(threadOperand)).
                isEqualTo(secondOperand.compareTo(threadOperand));
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             transitive property
            """)
    void compareTo11() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(95, 400);
        Bits threadOperand = new Bits(505);
        threadOperand.setRange(100, 450);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).isLessThan(0);
        Assertions.assertThat(secondOperand.compareTo(threadOperand)).isLessThan(0);
        Assertions.assertThat(firstOperand.compareTo(threadOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareTo(Bits o):
             x.compareTo(y) == -y.compareTo(x)
            """)
    void compareTo12() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(95, 400);

        Assertions.assertThat(firstOperand.compareTo(secondOperand)).
                isEqualTo(-secondOperand.compareTo(firstOperand));
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size > second operand size,
             first operand bits > second operand bits as unsigned number
             => return positive number
            """)
    void compareIgnoreSize1() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(400);
        secondOperand.setRange(101, 399);
        
        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).
                isGreaterThan(0);
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size > second operand size,
             first operand bits == second operand bits as unsigned number
             => return 0
            """)
    void compareIgnoreSize2() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(400);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).isZero();
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size > second operand size,
             first operand bits < second operand bits as unsigned number
             => return negative number
            """)
    void compareIgnoreSize3() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 340);
        Bits secondOperand = new Bits(400);
        secondOperand.setRange(95, 340);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size < second operand size,
             first operand bits > second operand bits as unsigned number
             => return positive number
            """)
    void compareIgnoreSize4() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 450);
        Bits secondOperand = new Bits(600);
        secondOperand.setRange(100, 440);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).
                isGreaterThan(0);
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size < second operand size,
             first operand bits == second operand bits as unsigned number
             => return 0
            """)
    void compareIgnoreSize5() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(600);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).isZero();
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size < second operand size,
             first operand bits < second operand bits as unsigned number
             => return negative value
            """)
    void compareIgnoreSize6() {
        Bits firstOperand = new Bits(400);
        firstOperand.setRange(100, 390);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(90, 390);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size == second operand size,
             first operand bits > second operand bits as unsigned number
             => return positive number
            """)
    void compareIgnoreSize7() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(140, 400);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).
                isGreaterThan(0);
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size == second operand size,
             first operand bits == second operand bits as unsigned number
             => return 0
            """)
    void compareIgnoreSize8() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(100, 400);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).isZero();
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             first operand size == second operand size,
             first operand bits < second operand bits as unsigned number
             => return negative number
            """)
    void compareIgnoreSize9() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(100, 401);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             x.compareTo(y) == 0
             => x.compareTo(z) == y.compareTo(z)
            """)
    void compareIgnoreSize10() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 430);
        Bits secondOperand = new Bits(firstOperand);
        Bits thirdOperand = new Bits(500);
        thirdOperand.setRange(80, 430);

        Assertions.assertThat(firstOperand.compareIgnoreSize(thirdOperand)).
                isEqualTo(secondOperand.compareIgnoreSize(thirdOperand));
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             transitive property
            """)
    void compareIgnoreSize11() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(95, 400);
        Bits threadOperand = new Bits(500);
        threadOperand.setRange(100, 450);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).isLessThan(0);
        Assertions.assertThat(secondOperand.compareIgnoreSize(threadOperand)).isLessThan(0);
        Assertions.assertThat(firstOperand.compareIgnoreSize(threadOperand)).isLessThan(0);
    }

    @Test
    @DisplayName("""
            compareIgnoreSize(Bits o):
             x.compareTo(y) == -y.compareTo(x)
            """)
    void compareIgnoreSize12() {
        Bits firstOperand = new Bits(500);
        firstOperand.setRange(100, 400);
        Bits secondOperand = new Bits(500);
        secondOperand.setRange(95, 400);

        Assertions.assertThat(firstOperand.compareIgnoreSize(secondOperand)).
                isEqualTo(-secondOperand.compareIgnoreSize(firstOperand));
    }

    @Test
    @DisplayName("toBinaryString(): size == 0 => return empty String")
    void toBinaryString1() {
        Bits bits = new Bits();

        Assertions.assertThat(bits.toBinaryString()).isEmpty();
    }

    @Test
    @DisplayName("""
            toBinaryString():
             size > 0
             => return a string with length = Bits.size
            """)
    void toBinaryString2() {
        Bits bits = new Bits(1017);
        bits.setRange(230, 543);

        String actual = bits.toBinaryString();

        Assertions.assertThat(actual).hasSize(1017);
    }

    @Test
    @DisplayName("""
            toBinaryString():
             size > 0,
             there are not bits set to one
             => return a string containing only zeros
            """)
    void toBinaryString3() {
        Bits bits = new Bits(1017);

        String actual = bits.toBinaryString();

        Assertions.assertThat(actual).
                hasSize(1017).
                containsPattern("^0+$");
    }

    @Test
    @DisplayName("""
            toBinaryString():
             size > 0,
             all bits set to one
             => return a string containing only ones
            """)
    void toBinaryString4() {
        Bits bits = new Bits(1017);
        bits.setAll();

        String actual = bits.toBinaryString();

        Assertions.assertThat(actual).
                hasSize(1017).
                containsPattern("^1+$");
    }

    @Test
    @DisplayName("""
            toBinaryString():
             size > 0 && size < 64,
             there are bits set to one and set to zero,
             => return correct result
            """)
    void toBinaryString5() {
        Bits bits = new Bits(57);
        bits.setAll(0, 8, 16, 23, 24, 25, 26, 27, 42, 44, 50);

        String actual = bits.toBinaryString();

        Assertions.assertThat(actual).
                isEqualTo("000000100000101000000000000001111100000010000000100000001");
    }

    @Test
    @DisplayName("""
            toBinaryString():
             size > 64,
             there are bits set to one and set to zero,
             => return correct result
            """)
    void toBinaryString6() {
        Bits bits = new Bits(94);
        bits.setAll(0, 2, 3, 4, 5, 7, 9, 13, 16, 18,
                19, 20, 22, 23, 27, 28, 29, 33, 34, 36,
                37, 38, 40, 41, 49, 54, 55, 56, 57, 58,
                61, 63, 64, 66, 67, 70, 73, 74, 76, 77,
                80, 81, 85, 88, 89, 91, 92, 93);

        String actual = bits.toBinaryString();

        Assertions.assertThat(actual).
                isEqualTo("1110110010001100110110010011011010011111000010000000110111011000111000110111010010001010111101");
    }

    @Test
    @DisplayName("""
            toBinaryString():
             size > 64,
             leading bits are zeros
             => return correct result
            """)
    void toBinaryString7() {
        Bits bits = new Bits(96);
        bits.setAll(0, 2, 3, 4, 5, 7, 9, 13, 16, 18,
                19, 20, 22, 23, 27, 28, 29, 33, 34, 36,
                37, 38, 40, 41, 49, 54, 55, 56, 57, 58);

        String actual = bits.toBinaryString();

        Assertions.assertThat(actual).
                isEqualTo("000000000000000000000000000000000000011111000010000000110111011000111000110111010010001010111101");
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

    private static Stream<Arguments> provideForGrowTo_ExceptionCase() {
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
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(50, 200),
                        0, 400, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(400, 450),
                        400, 0, 100,
                        new Bits(500).setRange(0, 50).setRange(100, 250),
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(450, 500),
                        400, 400, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(10, 250),
                        new Bits(500).setRange(450, 500),
                        300, 400, 0,
                        new Bits(500).setRange(10, 250),
                        "srcPos + length <= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(350, 500),
                        499, 0, 100,
                        new Bits(500).setRange(50, 250).setAll(0),
                        "srcPos + length >= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        new Bits(500).setRange(350, 500),
                        490, 0, 100,
                        new Bits(500).setRange(0, 10).setRange(50, 250).setRange(450, 500),
                        "srcPos + length >= src.size(), destPos + length <= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(300, 450),
                        400, 499, 100,
                        new Bits(500).setRange(50, 250).setAll(499),
                        "srcPos + length <= src.size(), destPos + length >= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(300, 450),
                        400, 450, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        "srcPos + length <= src.size(), destPos + length >= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(350, 500),
                        450, 499, 100,
                        new Bits(500).setRange(50, 250).setAll(499),
                        "srcPos + length >= src.size(), destPos + length >= dest.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(350, 500),
                        450, 450, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        "srcPos + length >= src.size(), destPos + length >= dest.size(), src range == dest range"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        new Bits(500).setRange(350, 500),
                        499, 450, 100,
                        new Bits(500).setRange(50, 250).setAll(450),
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
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        0, 400, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(450, 500),
                        400, 0, 100,
                        new Bits(500).setRange(50, 100).setRange(450, 500),
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(450, 500),
                        400, 400, 100,
                        new Bits(500).setRange(450, 500),
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        45, 450, 0,
                        new Bits(500).setRange(50, 250),
                        "srcPos + length <= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 400),
                        499, 50, 100,
                        new Bits(500).setRange(51, 400),
                        "srcPos + length >= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        440, 50, 100,
                        new Bits(500).setRange(60, 250).setRange(450, 500),
                        "srcPos + length >= origin.size(), destPos + length <= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        200, 499, 100,
                        new Bits(500).setRange(50, 250).setAll(499),
                        "srcPos + length <= origin.size(), destPos + length >= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        200, 450, 100,
                        new Bits(500).setRange(50, 250).setRange(450, 500),
                        "srcPos + length <= origin.size(), destPos + length >= origin.size()"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        200, 499, 400,
                        new Bits(500).setRange(50, 250).setAll(499),
                        "srcPos + length >= origin.size(), destPos + length >= origin.size(), dest range < src range"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 250),
                        200, 200, 400,
                        new Bits(500).setRange(50, 250),
                        "srcPos + length >= origin.size(), destPos + length >= origin.size(), dest range == src range"
                ),
                Arguments.of(
                        new Bits(500).setRange(50, 500),
                        499, 0, 1000,
                        new Bits(500).setRange(50, 500).setAll(0),
                        "srcPos + length >= origin.size(), destPos + length >= origin.size(), dest range > src range"
                )
        );
    }
}
