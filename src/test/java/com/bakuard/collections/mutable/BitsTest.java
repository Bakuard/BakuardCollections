package com.bakuard.collections.mutable;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class BitsTest {

    @Test
    @DisplayName("Bits(Bits other): create copy => new bits equal other")
    void Bits_copy1() {
        Bits expected = new Bits(100000);
        expected.setRange(7890, 95400);
        expected.clearAll(9000, 9500, 9506, 20000, 50121, 50700, 80000, 90000);
        Bits actual = new Bits(expected);

        Assertions.assertThat(expected).isEqualTo(actual);
    }

    @Test
    @DisplayName("Bits(Bits other): change original after creating copy => copy don't change")
    void Bits_copy2() {
        Bits original = new Bits(100000);
        original.setRange(7890, 95400);
        original.clearAll(9000, 9500, 9506, 20000, 50121, 50700, 80000, 90000);
        Bits copy = new Bits(original);
        Bits expected = new Bits(100000);
        expected.setRange(7890, 95400);
        expected.clearAll(9000, 9500, 9506, 20000, 50121, 50700, 80000, 90000);

        original.clearAll();

        Assertions.assertThat(expected).isEqualTo(copy);
    }

    @Test
    @DisplayName("Bits(numberBits): numberBits < 0 => exception")
    void Bits_numberBits1() {
        Assertions.assertThatExceptionOfType(IndexOutOfBoundsException.class).
                isThrownBy(() -> new Bits(-1));
    }

    @Test
    @DisplayName("Bits(numberBits): numberBits > 0 => size == numberBits")
    void Bits_numberBits2() {
        Bits actual = new Bits(10000);

        Assertions.assertThat(actual.getSize()).isEqualTo(10000);
    }

    @Test
    @DisplayName("Bits(numberBits): numberBits > 0, get index without range => doesn't throws")
    void Bits_numberBits3() {
        Bits actual = new Bits(10000);

        Assertions.assertThatNoException().isThrownBy(() -> actual.get(9999));
    }

    @Test
    @DisplayName("Bits(numberBits): numberBits > 0 => new bits is empty")
    void Bits_numberBits4() {
        Bits actual = new Bits(10000);

        Assertions.assertThat(actual.isClean()).isTrue();
    }

    @Test
    @DisplayName("get(index): index < 0 => exception")
    void get1() {
        Bits actual = new Bits(100000);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.get(-1));
    }

    @Test
    @DisplayName("get(index): index = size => exception")
    void get2() {
        Bits actual = new Bits(100000);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.get(100000));
    }

    @Test
    @DisplayName("get(index): index > size => exception")
    void get3() {
        Bits actual = new Bits(100000);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.get(100001));
    }

    @Test
    @DisplayName("set(index), get(index): set correct index => get() return true with this index")
    void set_get1() {
        Bits bits = new Bits(100000);

        bits.set(10);
        bits.set(512);
        bits.set(52170);

        Assertions.assertThat(new boolean[]{bits.get(10), bits.get(512), bits.get(52170)}).
                containsOnly(true);
    }

    @Test
    @DisplayName("set(index), get(index): set correct same index twice and more => get() return true with this index")
    void set_get2() {
        Bits bits = new Bits(100);

        bits.set(10);
        bits.set(10);
        bits.set(52);
        bits.set(52);
        bits.set(52);

        Assertions.assertThat(new boolean[]{bits.get(10), bits.get(52)}).
                containsOnly(true);
    }

    @Test
    @DisplayName("set(index), get(index): set correct index => get() return false with all different indexes")
    void set_get3() {
        Bits actual = new Bits(100000);

        actual.set(10);
        actual.set(512);
        actual.set(52170);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i != 10 && i != 512 && i != 52170) Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("set(index): index < 0 => exception")
    void set1() {
        Bits actual = new Bits(100000);

        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> actual.set(-1));
    }

    @Test
    @DisplayName("set(index): index = size => exception")
    void set2() {
        Bits actual = new Bits(100000);

        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> actual.set(100000));
    }

    @Test
    @DisplayName("set(index): index > size => exception")
    void set3() {
        Bits actual = new Bits(100000);

        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> actual.set(100001));
    }
    
    @Test
    @DisplayName("setAll(indexes): all indexes is correct => get() return true for each")
    void setAllWithArguments1() {
        Bits bits = new Bits(10000);

        bits.setAll(0, 500, 2001);

        Assertions.assertThat(new boolean[]{bits.get(0), bits.get(500), bits.get(2001)}).
                containsOnly(true);
    }

    @Test
    @DisplayName("setAll(indexes): all indexes is correct => get() return false with all different indexes")
    void setAllWithArguments2() {
        Bits actual = new Bits(10000);

        actual.setAll(0, 500, 2001);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i != 0 && i != 500 && i != 2001) Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("setAll(indexes): all indexes is correct, duplicate indexes => get() return true for each")
    void setAllWithArguments3() {
        Bits bits = new Bits(10000);

        bits.setAll(0, 0, 0, 500, 500, 2001);

        Assertions.assertThat(new boolean[]{bits.get(0), bits.get(500), bits.get(2001)}).
                containsOnly(true);
    }

    @Test
    @DisplayName("setAll(indexes): one index is less than the lower bound => exception, object not changed")
    void setAllWithArguments4() {
        Bits actual = new Bits(10000);
        actual.setAll(0, 51, 763, 2012, 7590);
        Bits expected = new Bits(actual);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.setAll(-1, 0, 12, 34));
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("setAll(indexes): one index is greater than the top bound => exception, object not changed")
    void setAllWithArguments5() {
        Bits actual = new Bits(10000);
        actual.setAll(0, 51, 763, 2012, 7590);
        Bits expected = new Bits(actual);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.setAll(0, 12, 34, 10000));
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("setAll(indexes): no arguments or empty index array => do nothing")
    void setAllWithArguments6() {
        Bits actual = new Bits(10000);
        actual.setAll(0, 51, 763, 2012, 7590);
        Bits expected = new Bits(actual);

        int[] emptyIndexArray = new int[0];
        actual.setAll(emptyIndexArray);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): correct fromIndex and toIndex => fill interval with ones")
    void setRange1() {
        Bits actual = new Bits(10000);

        actual.setRange(647, 6399);

        for(int i = 647; i < 6399; i++) Assertions.assertThat(actual.get(i)).isTrue();
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): correct fromIndex and toIndex => bits outside the interval are not changed")
    void setRange2() {
        Bits actual = new Bits(10000);

        actual.setRange(647, 6399);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i < 647 || i >= 6399) Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): fromIndex == toIndex => do nothing")
    void setRange3() {
        Bits actual = new Bits(10000);

        actual.setRange(1200, 1200);

        Assertions.assertThat(actual.isClean()).isTrue();
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): fromIndex < 0 => exception")
    void setRange4() {
        Bits actual = new Bits(100);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.setRange(-1, 100));
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): toIndex > bits.getSize() => exception")
    void setRange5() {
        Bits actual = new Bits(100);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.setRange(0, 101));
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): fromIndex > toIndex => exception")
    void setRange6() {
        Bits actual = new Bits(100);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.setRange(50, 10));
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): correct interval within one word => fill interval with ones")
    void setRange7() {
        Bits actual = new Bits(100);

        actual.setRange(12, 50);

        for(int i = 12; i < 50; i++) {
            Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): correct interval within one word => bits outside the interval are not changed")
    void setRange8() {
        Bits actual = new Bits(100);

        actual.setRange(12, 50);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i < 12 || i >= 50) Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): correct interval within two word => fill interval with ones")
    void setRange9() {
        Bits actual = new Bits(500);

        actual.setRange(12, 96);

        for(int i = 12; i < 96; i++) {
            Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): correct interval within two word => bits outside the interval are not changed")
    void setRange10() {
        Bits actual = new Bits(500);

        actual.setRange(12, 96);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i < 12 || i >= 96) Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): correct interval within three word => fill interval with ones")
    void setRange11() {
        Bits actual = new Bits(500);

        actual.setRange(12, 128);

        for(int i = 12; i < 128; i++) {
            Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("setRange(fromIndex, toIndex): correct interval within three word => bits outside the interval are not changed")
    void setRange12() {
        Bits actual = new Bits(500);

        actual.setRange(12, 128);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i < 12 || i >= 128) Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("setAll(): => set all bits to one")
    void setAllWithoutArguments1() {
        Bits actual = new Bits(140);

        actual.setAll();

        for(int i = 0; i < actual.getSize(); i++) {
            Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("setAll(): call expandTo() after setAll() => bits greater or equal old value of getSize() is zero")
    void setAllWithoutArguments2() {
        Bits actual = new Bits(140);

        actual.setAll();
        actual.expandTo(1000);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i >= 140) Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("clear(), get(): clear correct index with one value => get() return false with this index")
    void clear_get1() {
        Bits bits = new Bits(10000);
        bits.setAll();

        bits.clear(12);
        bits.clear(13);
        bits.clear(4014);

        Assertions.assertThat(new boolean[]{bits.get(12), bits.get(13), bits.get(4014)}).
                containsOnly(false);
    }

    @Test
    @DisplayName("clear(), get(): clear correct index twice and more => get() return false with this index")
    void clear_get2() {
        Bits bits = new Bits(10000);
        bits.setAll();

        bits.clear(12);
        bits.clear(12);
        bits.clear(12);
        bits.clear(13);
        bits.clear(4014);
        bits.clear(4014);

        Assertions.assertThat(new boolean[]{bits.get(12), bits.get(13), bits.get(4014)}).
                containsOnly(false);
    }

    @Test
    @DisplayName("clear(), get(): clear correct index => get() return true with all different indexes")
    void clear_get3() {
        Bits bits = new Bits(10000);
        bits.setAll();

        bits.clear(12);
        bits.clear(13);
        bits.clear(4014);

        for(int i = 0; i < bits.getSize(); i++) {
            if(i != 12 && i != 13 && i != 4014) Assertions.assertThat(bits.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("clear(): index < 0 => exception")
    void clear1() {
        Bits actual = new Bits(100000);
        
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.clear(-1));
    }

    @Test
    @DisplayName("clear(): index = size => exception")
    void clear2() {
        Bits actual = new Bits(100000);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.clear(100000));
    }

    @Test
    @DisplayName("clear(): index > size => exception")
    void clear3() {
        Bits actual = new Bits(100000);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.clear(100001));
    }
    
    @Test
    @DisplayName("clearAll(indexes): all indexes is correct => get() return false for each")
    void clearAllWithArguments1() {
        Bits bits = new Bits(10000);
        bits.setAll();

        bits.clearAll(0, 12, 9999);

        Assertions.assertThat(new boolean[]{bits.get(0), bits.get(12), bits.get(9999)}).
                containsOnly(false);
    }

    @Test
    @DisplayName("clearAll(indexes): all indexes is correct => get() return true with all different indexes")
    void clearAllWithArguments2() {
        Bits bits = new Bits(10000);
        bits.setAll();

        bits.clearAll(0, 12, 9999);

        Assertions.assertThat(new boolean[]{bits.get(0), bits.get(12), bits.get(9999)}).
                containsOnly(false);
    }

    @Test
    @DisplayName("clearAll(indexes): all indexes is correct, duplicate indexes => get() return false for each")
    void clearAllWithArguments3() {
        Bits bits = new Bits(10000);
        bits.setAll();

        bits.clearAll(0, 0, 0, 12, 12, 9999);

        Assertions.assertThat(new boolean[]{bits.get(0), bits.get(12), bits.get(9999)}).
                containsOnly(false);
    }

    @Test
    @DisplayName("clearAll(indexes): one index is less than the lower bound => exception, object not changed")
    void clearAllWithArguments4() {
        Bits actual = new Bits(10000);
        actual.setRange(2017, 7001);
        Bits expected = new Bits(actual);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.clearAll(-1, 2017, 4500));
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("clearAll(indexes): one index is greater than the top bound => exception, object not changed")
    void clearAllWithArguments5() {
        Bits actual = new Bits(10000);
        actual.setRange(2017, 7001);
        Bits expected = new Bits(actual);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.clearAll(2017, 4500, 10000));
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("clearAll(indexes): no arguments or empty index array => do nothing")
    void clearAllWithArguments6() {
        Bits actual = new Bits(10000);
        actual.setRange(2017, 7001);
        Bits expected = new Bits(actual);

        int[] emptyIndexArray = new int[0];
        actual.clearAll(emptyIndexArray);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): correct fromIndex and toIndex => fill interval with zeros")
    void clearRange1() {
        Bits actual = new Bits(10000);
        actual.setAll();

        actual.clearRange(637, 6405);

        for(int i = 637; i < 6405; i++) {
            Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): correct fromIndex and toIndex => bits outside the interval are not changed")
    void clearRange2() {
        Bits actual = new Bits(10000);
        actual.setAll();

        actual.clearRange(637, 6405);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i < 637 || i >= 6405) Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): fromIndex == toIndex => do nothing")
    void clearRange3() {
        Bits actual = new Bits(10000);
        actual.setAll();

        actual.clearRange(1000, 1000);

        for(int i = 0; i < actual.getSize(); i++) {
            Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): fromIndex < 0 => exception")
    void clearRange4() {
        Bits actual = new Bits();

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.clearRange(-1, 100));
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): toIndex > bits.getSize() => exception")
    void clearRange5() {
        Bits actual = new Bits(10000);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.clearRange(0, 10001));
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): fromIndex > toIndex => exception")
    void clearRange6() {
        Bits actual = new Bits(10000);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> actual.clearRange(1000, 999));
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): correct interval within one word => fill interval with zeros")
    void clearRange7() {
        Bits actual = new Bits(100);
        actual.setAll();

        actual.clearRange(12, 50);

        for(int i = 12; i < 50; i++) {
            Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): correct interval within one word => bits outside the interval are not changed")
    void clearRange8() {
        Bits actual = new Bits(100);
        actual.setAll();

        actual.clearRange(12, 50);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i < 12 || i >= 50) Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): correct interval within two word => fill interval with zeros")
    void clearRange9() {
        Bits actual = new Bits(500);
        actual.setAll();

        actual.clearRange(12, 96);

        for(int i = 12; i < 96; i++) {
            Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): correct interval within two word => bits outside the interval are not changed")
    void clearRange10() {
        Bits actual = new Bits(500);
        actual.setAll();

        actual.clearRange(12, 96);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i < 12 || i >= 96) Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): correct interval within three word => fill interval with zeros")
    void clearRange11() {
        Bits actual = new Bits(500);
        actual.setAll();

        actual.clearRange(12, 128);

        for(int i = 12; i < 128; i++) {
            Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("clearRange(fromIndex, toIndex): correct interval within three word => bits outside the interval are not changed")
    void clearRange12() {
        Bits actual = new Bits(500);
        actual.setAll();

        actual.clearRange(12, 128);

        for(int i = 0; i < actual.getSize(); i++) {
            if(i < 12 || i >= 128) Assertions.assertThat(actual.get(i)).isTrue();
        }
    }

    @Test
    @DisplayName("clearAll(): set all bits to zero")
    void clearAllWithoutArguments() {
        Bits actual = new Bits(10000);
        actual.setRange(127, 3407);
        actual.setRange(5009, 9077);

        actual.clearAll();

        Assertions.assertThat(actual.isClean()).isTrue();
    }

    @Test
    @DisplayName("and(other): idempotence property, same object")
    void and1() {
        Bits operand = new Bits(10000);
        operand.setRange(0, 3000);
        Bits expected = new Bits(operand);

        Bits actual = operand.and(operand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("and(other): idempotence property, not same object")
    void and2() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(0, 3000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(0, 3000);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.and(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("and(other): zero property, operands have the same size")
    void and3() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(2077, 6707);
        Bits empty = new Bits(10000);

        Bits actual = firstOperand.and(empty);

        Assertions.assertThat(actual).isEqualTo(empty);
    }

    @Test
    @DisplayName("and(other): zero property, first operand greater than zero")
    void and4() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(2077, 6707);
        Bits empty = new Bits(1000);
        Bits expected = new Bits(10000);

        Bits actual = firstOperand.and(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("and(other): zero property, first operand less than zero")
    void and5() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(2077, 6707);
        Bits empty = new Bits(20000);
        Bits expected = new Bits(10000);

        Bits actual = firstOperand.and(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("and(other): unit property, operands have the same size")
    void and6() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(2077, 6707);
        Bits full = new Bits(10000);
        full.setAll();
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.and(full);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("and(other): unit property, first operand less than unit")
    void and7() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(2077, 6707);
        Bits full = new Bits(20000);
        full.setAll();
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.and(full);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("and(other): commutative property, operands have the same size")
    void and8() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(1200, 7464);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(3000, 9900);

        Bits actualA = new Bits(firstOperand).and(secondOperand);
        Bits actualB = new Bits(secondOperand).and(firstOperand);

        Assertions.assertThat(actualA).isEqualTo(actualB);
    }

    @Test
    @DisplayName("and(other): commutative property, first operand greater than second")
    void and9() {
        Bits firstOperand = new Bits(20000);
        firstOperand.setRange(1200, 7764);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(3000, 9900);

        Bits actualA = new Bits(firstOperand).and(secondOperand);
        Bits actualB = new Bits(secondOperand).and(firstOperand);
        
        Assertions.assertThat(actualA).
                usingComparator(Bits::compareIgnoreSize).
                isEqualTo(actualB);
    }

    @Test
    @DisplayName("and(other): commutative property, first operand less than second")
    void and10() {
        Bits firstOperand = new Bits(5000);
        firstOperand.setRange(1200, 4764);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(3000, 9900);

        Bits actualA = new Bits(firstOperand).and(secondOperand);
        Bits actualB = new Bits(secondOperand).and(firstOperand);

        Assertions.assertThat(actualA).
                usingComparator(Bits::compareIgnoreSize).
                isEqualTo(actualB);
    }

    @Test
    @DisplayName("and(other): transitive property, operands have the same size")
    void and11() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(2350, 4700);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(3700, 6001);
        Bits thirdOperand = new Bits(10000);
        thirdOperand.setRange(5047, 9090);

        Bits actualA = new Bits(firstOperand).and(secondOperand).and(thirdOperand);
        Bits actualB = new Bits(firstOperand).and(thirdOperand).and(secondOperand);

        Assertions.assertThat(actualA).isEqualTo(actualB);
    }

    @Test
    @DisplayName("and(other): transitive property, operands have the different size")
    void and12() {
        Bits firstOperand = new Bits(5000);
        firstOperand.setRange(2350, 4700);
        Bits secondOperand = new Bits(7000);
        secondOperand.setRange(3700, 6001);
        Bits thirdOperand = new Bits(10000);
        thirdOperand.setRange(5047, 9090);

        Bits actualA = new Bits(firstOperand).and(secondOperand).and(thirdOperand);
        Bits actualB = new Bits(firstOperand).and(thirdOperand).and(secondOperand);

        Assertions.assertThat(actualA).
                usingComparator(Bits::compareIgnoreSize).
                isEqualTo(actualB);
    }

    @Test
    @DisplayName("and(other): operands have same size => correct result")
    void and13() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(64, 1024);
        firstOperand.setRange(4089, 8064);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(600, 5500);
        Bits expected = new Bits(10000);
        expected.setRange(600, 1024);
        expected.setRange(4089, 5500);

        Bits actual = firstOperand.and(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("and(other): first operand greater than second operand => correct result")
    void and14() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(64, 1024);
        firstOperand.setRange(4089, 8064);
        Bits secondOperand = new Bits(6000);
        secondOperand.setRange(600, 5500);
        Bits expected = new Bits(10000);
        expected.setRange(600, 1024);
        expected.setRange(4089, 5500);

        Bits actual = firstOperand.and(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("and(other): first operand less than second operand => correct result")
    void and15() {
        Bits firstOperand = new Bits(6000);
        firstOperand.setRange(600, 5500);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(64, 1024);
        secondOperand.setRange(4089, 8064);
        Bits expected = new Bits(6000);
        expected.setRange(600, 1024);
        expected.setRange(4089, 5500);

        Bits actual = firstOperand.and(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("or(other): idempotence property, same object")
    void or1() {
        Bits operand = new Bits(10000);
        operand.setRange(0, 3001);
        Bits expected = new Bits(operand);

        Bits actual = operand.or(operand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("or(other): idempotence property, not same object")
    void or2() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(0, 3001);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(0, 3001);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.or(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("or(other): first operand less than second => size of first operand equal second")
    void or3() {
        Bits firstOperand = new Bits(1000);
        Bits secondOperand = new Bits(10000);

        firstOperand.or(secondOperand);

        Assertions.assertThat(firstOperand.getSize()).isEqualTo(10000);
    }

    @Test
    @DisplayName("or(other): zero property, operands have the same size")
    void or4() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3050);
        firstOperand.setRange(5500, 9000);
        Bits empty = new Bits(10000);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.or(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("or(other): zero property, first operand greater than second operand")
    void or5() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3050);
        firstOperand.setRange(5500, 9000);
        Bits empty = new Bits(5000);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.or(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("or(other): unit property, operands have the same size")
    void or6() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3050);
        firstOperand.setRange(5500, 9000);
        Bits full = new Bits(10000);
        full.setAll();

        Bits actual = firstOperand.or(full);

        Assertions.assertThat(actual).isEqualTo(full);
    }

    @Test
    @DisplayName("or(other): commutative property, operands have the same size")
    void or7() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3050);
        firstOperand.setRange(5500, 9000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(2900, 6000);

        Bits actualA = new Bits(firstOperand).or(secondOperand);
        Bits actualB = new Bits(secondOperand).or(firstOperand);

        Assertions.assertThat(actualA).isEqualTo(actualB);
    }

    @Test
    @DisplayName("or(other): commutative property, first operand greater than second operand")
    void or8() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3050);
        firstOperand.setRange(5500, 9000);
        Bits secondOperand = new Bits(6000);
        secondOperand.setRange(2900, 6000);

        Bits actualA = new Bits(firstOperand).or(secondOperand);
        Bits actualB = new Bits(secondOperand).or(firstOperand);

        Assertions.assertThat(actualA).isEqualTo(actualB);
    }

    @Test
    @DisplayName("or(other): transitive property, operands have the same size")
    void or9() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3050);
        firstOperand.setRange(5500, 9000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(2900, 6000);
        Bits thirdOperand = new Bits(10000);
        thirdOperand.setRange(7700, 10000);

        Bits actualA = new Bits(firstOperand).or(secondOperand).or(thirdOperand);
        Bits actualB = new Bits(firstOperand).or(thirdOperand).or(secondOperand);

        Assertions.assertThat(actualA).isEqualTo(actualB);
    }

    @Test
    @DisplayName("or(other): operands have the same size => correct result")
    void or10() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3050);
        firstOperand.setRange(5500, 9000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(2900, 6000);
        Bits expected = new Bits(10000);
        expected.setRange(100, 9000);

        Bits actual = firstOperand.or(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("or(other): first operand greater than second operand")
    void or11() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3050);
        firstOperand.setRange(5500, 9000);
        Bits secondOperand = new Bits(6000);
        secondOperand.setRange(2900, 6000);
        Bits expected = new Bits(10000);
        expected.setRange(100, 9000);

        Bits actual = firstOperand.or(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("xor(other): same object")
    void xor1() {
        Bits operand = new Bits(10000);
        operand.setRange(0, 3600);
        Bits empty = new Bits(10000);

        Bits actual = operand.xor(operand);

        Assertions.assertThat(actual).isEqualTo(empty);
    }

    @Test
    @DisplayName("xor(other): first operand equal second operand, not same object")
    void xor2() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(0, 3600);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(0, 3600);
        Bits empty = new Bits(10000);

        Bits actual = firstOperand.xor(secondOperand);

        Assertions.assertThat(actual).isEqualTo(empty);
    }

    @Test
    @DisplayName("xor(other): first operand less than second => size of first operand equal second")
    void xor3() {
        Bits firstOperand = new Bits(1000);
        Bits secondOperand = new Bits(10000);

        firstOperand.xor(secondOperand);

        Assertions.assertThat(firstOperand.getSize()).isEqualTo(10000);
    }

    @Test
    @DisplayName("xor(other): second operand is empty, operands have the same size => result equal first operand")
    void xor4() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(0, 3600);
        Bits empty = new Bits(10000);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.xor(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("xor(other): second operand is empty, first operand greater than second => result equal first operand")
    void xor5() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(0, 3600);
        Bits empty = new Bits(5000);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.xor(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("xor(other): second operand is full, operands have the same size => result is inverted first operand")
    void xor6() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(0, 3600);
        firstOperand.setRange(7200, 10000);
        Bits full = new Bits(10000);
        full.setAll();
        Bits expected = new Bits(10000);
        expected.setRange(3600, 7200);

        Bits actual = firstOperand.xor(full);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("xor(other): commutative property, operands have the same size")
    void xor7() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 7000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(4500, 9900);

        Bits actualA = new Bits(firstOperand).xor(secondOperand);
        Bits actualB = new Bits(secondOperand).xor(firstOperand);

        Assertions.assertThat(actualA).isEqualTo(actualB);
    }

    @Test
    @DisplayName("xor(other): commutative property, first operand greater than second")
    void xor8() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(4500, 9900);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(100, 7000);

        Bits actualA = new Bits(firstOperand).xor(secondOperand);
        Bits actualB = new Bits(secondOperand).xor(firstOperand);

        Assertions.assertThat(actualA).isEqualTo(actualB);
    }

    @Test
    @DisplayName("xor(other): transitive property, operands have the same size")
    void xor9() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(0, 2000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(1500, 4500);
        Bits thirdOperand = new Bits(10000);
        thirdOperand.setRange(4000, 6500);

        Bits actualA = new Bits(firstOperand).xor(secondOperand).xor(thirdOperand);
        Bits actualB = new Bits(firstOperand).xor(thirdOperand).xor(secondOperand);

        Assertions.assertThat(actualA).isEqualTo(actualB);
    }

    @Test
    @DisplayName("xor(other): operands have the same size => correct result")
    void xor10() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3600);
        firstOperand.setRange(6600, 9900);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(2000, 8000);
        Bits expected = new Bits(10000);
        expected.setRange(100, 2000);
        expected.setRange(3600, 6600);
        expected.setRange(8000, 9900);

        Bits actual = firstOperand.xor(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("xor(other): first operand greater than second operand")
    void xor11() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(100, 3600);
        firstOperand.setRange(6600, 9900);
        Bits secondOperand = new Bits(7000);
        secondOperand.setRange(2000, 7000);
        Bits expected = new Bits(10000);
        expected.setRange(100, 2000);
        expected.setRange(3600, 6600);
        expected.setRange(7000, 9900);

        Bits actual = firstOperand.xor(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): first operand equal second => empty bits")
    void andNot1() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(120, 3405);
        firstOperand.setRange(5959, 9047);
        Bits secondOperand = new Bits(firstOperand);
        Bits empty = new Bits(10000);

        Bits actual = firstOperand.andNot(secondOperand);

        Assertions.assertThat(actual).isEqualTo(empty);
    }

    @Test
    @DisplayName("andNot(other): first operand is empty, first operand size less than second => empty bits")
    void andNot2() {
        Bits empty = new Bits(5000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(100, 7400);
        Bits expected = new Bits(5000);

        Bits actual = empty.andNot(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): first operand is empty, operands have the same size => empty bits")
    void andNot3() {
        Bits empty = new Bits(10000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(120, 3400);
        secondOperand.setRange(5600, 9780);
        Bits expected = new Bits(10000);

        Bits actual = empty.andNot(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): first operand is empty, first operand size greater than second => empty bits")
    void andNot4() {
        Bits empty = new Bits(10000);
        Bits secondOperand = new Bits(5000);
        secondOperand.setRange(25, 1200);
        secondOperand.setRange(2030, 4856);
        Bits expected = new Bits(10000);

        Bits actual = empty.andNot(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): second operand is empty, first operand size less than second => result equals first")
    void andNot5() {
        Bits firstOperand = new Bits(5000);
        firstOperand.setRange(1, 1001);
        firstOperand.setRange(1506, 4700);
        Bits empty = new Bits(10000);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.andNot(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): second operand is empty, operands have the same size => result equals first")
    void andNot6() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(0, 1200);
        firstOperand.setRange(3000, 10000);
        Bits empty = new Bits(10000);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.andNot(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): second operand is empty, first operand size greater than second => result equals first")
    void andNot7() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(300, 4500);
        firstOperand.setRange(5051, 9780);
        Bits empty = new Bits(5000);
        Bits expected = new Bits(firstOperand);

        Bits actual = firstOperand.andNot(empty);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): second operand is full, operands have the same size => empty bits")
    void andNot8() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(63, 6050);
        firstOperand.setRange(8700, 9909);
        Bits full = new Bits(10000);
        full.setAll();
        Bits expected = new Bits(10000);

        Bits actual = firstOperand.andNot(full);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): second operand is full, second operand size greater than first => empty bits")
    void andNot9() {
        Bits firstOperand = new Bits(5000);
        firstOperand.setRange(0, 3404);
        firstOperand.setRange(3789, 5000);
        Bits full = new Bits(10000);
        full.setAll();
        Bits expected = new Bits(5000);

        Bits actual = firstOperand.andNot(full);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): first operand size less than second => correct result")
    void andNot10() {
        Bits firstOperand = new Bits(5000);
        firstOperand.setRange(12, 3000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(2000, 7000);
        Bits expected = new Bits(5000);
        expected.setRange(12, 2000);

        Bits actual = firstOperand.andNot(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): operands have the same size => correct result")
    void andNot11() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(10, 6000);
        Bits secondOperand = new Bits(10000);
        secondOperand.setRange(4500, 8800);
        Bits expected = new Bits(10000);
        expected.setRange(10, 4500);

        Bits actual = firstOperand.andNot(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("andNot(other): first operand size greater than second => correct result")
    void andNot12() {
        Bits firstOperand = new Bits(10000);
        firstOperand.setRange(10, 9000);
        Bits secondOperand = new Bits(5000);
        secondOperand.setRange(3400, 5000);
        Bits expected = new Bits(10000);
        expected.setRange(10, 3400);
        expected.setRange(5000, 9000);

        Bits actual = firstOperand.andNot(secondOperand);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("not(): involution")
    void not1() {
        Bits operand = new Bits(10000);
        operand.setRange(1200, 3400);
        operand.setRange(4577, 9898);
        Bits expected = new Bits(operand);

        Bits actual = operand.not().not();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("not(): one word => correct result")
    void not2() {
        Bits operand = new Bits(63);
        operand.setAll(0,1,2,3,4,23,24,62);
        Bits expected = new Bits(63);
        expected.setRange(5, 23);
        expected.setRange(25, 62);

        Bits actual = operand.not();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("not(): one word => don't change bits out of range")
    void not3() {
        Bits operand = new Bits(45);
        operand.setAll(0,1,2,3,4,5,23,24,25);

        operand.not();
        operand.expandTo(63);

        for(int i = 45; i < 63; i++) {
            Assertions.assertThat(operand.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("not(): several word => correct result")
    void not4() {
        Bits operand = new Bits(255);
        operand.setRange(25, 240);
        Bits expected = new Bits(255);
        expected.setRange(0, 25);
        expected.setRange(240, 255);

        Bits actual = operand.not();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("not(other): several word => don't change bits out of range")
    void not5() {
        Bits operand = new Bits(245);
        operand.setRange(25, 215);

        operand.not();
        operand.expandTo(260);

        for(int i = 245; i < 260; i++) {
            Assertions.assertThat(operand.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("copyAll(src): target object size less than src size => correct result")
    void copyAll1() {
        Bits actual = new Bits(5000);
        actual.setRange(120, 2090);
        Bits src = new Bits(10000);
        src.setRange(209, 3400);
        src.setRange(5907, 9070);
        Bits expected = new Bits(src);

        actual.copyAll(src);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("copyAll(src): target object size equal src size => correct result")
    void copyAll2() {
        Bits actual = new Bits(10000);
        actual.setRange(100, 4000);
        actual.setRange(4500, 9001);
        Bits src = new Bits(10000);
        src.setRange(3400, 6000);
        Bits expected = new Bits(src);

        actual.copyAll(src);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("copyAll(src): target object size greater than src size => correct result")
    void copyAll3() {
        Bits actual = new Bits(10000);
        actual.setRange(100, 4000);
        actual.setRange(4500, 9001);
        Bits src = new Bits(5000);
        src.setRange(3400, 5000);
        Bits expected = new Bits(src);

        actual.copyAll(src);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("copyAll(src): target object size greater than src size => all bits out of range is zero")
    void copyAll4() {
        Bits actual = new Bits(10000);
        actual.setRange(100, 4000);
        actual.setRange(4500, 9001);
        Bits src = new Bits(5000);
        src.setRange(3400, 5000);

        actual.copyAll(src);

        actual.expandTo(10000);
        for(int i = 5000; i < 10000; i++) {
            Assertions.assertThat(actual.get(i)).isFalse();
        }
    }

    @Test
    @DisplayName("copyAll(src): the object being copied has changed after calling copyAll() => don't change target object")
    void copyAll5() {
        Bits actual = new Bits(10000);
        actual.setRange(100, 4000);
        actual.setRange(4500, 9001);
        Bits src = new Bits(5000);
        src.setRange(3400, 5000);
        Bits expected = new Bits(src);

        actual.copyAll(src);
        src.clearAll();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("copyRange(src, srcPos, length, destPos): src is null => exception")
    void copyRange1() {
        Bits actual = new Bits(500);
        
        Assertions.assertThatNullPointerException().isThrownBy(
                () -> actual.copyRange(null, 12, 100, 300));
    }

    @Test
    @DisplayName("copyRange(src, srcPos, length, destPos): srcPos is negative => exception")
    void copyRange2() {
        Bits actual = new Bits(500);
        Bits src = new Bits(500);
        
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(
                () -> actual.copyRange(src, -1, 100, 300));
    }

    @Test
    @DisplayName("copyRange(src, srcPos, length, destPos): srcPos == src.getSize() => exception")
    void copyRange3() {
        Bits actual = new Bits(500);
        Bits src = new Bits(500);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(
                () -> actual.copyRange(src, 500, 100, 0));
    }

    @Test
    @DisplayName("copyRange(src, srcPos, length, destPos): destPos is negative => exception")
    void copyRange4() {
        Bits actual = new Bits(500);
        Bits src = new Bits(500);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(
                () -> actual.copyRange(src, 0, 100, -1));
    }

    @Test
    @DisplayName("copyRange(src, srcPos, length, destPos): destPos == dest.getSize() => exception")
    void copyRange5() {
        Bits actual = new Bits(500);
        Bits src = new Bits(500);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(
                () -> actual.copyRange(src, 400, 100, 500));
    }

    @Test
    @DisplayName("copyRange(src, srcPos, length, destPos): length is negative => exception")
    void copyRange6() {
        Bits actual = new Bits(500);
        Bits src = new Bits(500);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(
                () -> actual.copyRange(src, 12, -1, 300));
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             srcPos is first bit,
             destPos is first bit,
             src range is within src,
             dest range is within dest
             => correct result
            """)
    void copyRange7() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(0, 200);
        Bits expected = new Bits(500);
        expected.setRange(0, 250);

        dest.copyRange(src, 0, 100, 0);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             srcPos is first bit,
             destPos is at the end,
             src range is within src,
             dest range is within dest
             => correct result
            """)
    void copyRange8() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(50, 200);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.setRange(450, 500);

        dest.copyRange(src, 0, 100, 400);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             srcPos is at the end,
             destPos is first bit,
             src range is within src,
             dest range is within dest
             => correct result
            """)
    void copyRange9() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(400, 450);
        Bits expected = new Bits(500);
        expected.setRange(0, 50);
        expected.setRange(100, 250);

        dest.copyRange(src, 400, 100, 0);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             srcPos is at the end,
             destPos is at the end,
             src range is within src,
             dest range is within dest
             => correct result
            """)
    void copyRange10() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(450, 500);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.setRange(450, 500);

        dest.copyRange(src, 400, 100, 400);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             srcPos is first bit,
             destPos is first bit,
             src range is within src,
             dest range is within dest
             => result equal dest
            """)
    void copyRange11() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits expected = new Bits(dest);

        dest.copyRange(dest, 0, 100, 0);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             srcPos is first bit,
             destPos is at the end,
             src range is within src,
             dest range is within dest
             => correct result
            """)
    void copyRange12() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.setRange(450, 500);

        dest.copyRange(dest, 0, 100, 400);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             srcPos is at the end,
             destPos is first bit,
             src range is within src,
             dest range is within dest
             => correct result
            """)
    void copyRange13() {
        Bits dest = new Bits(500);
        dest.setRange(450, 500);
        Bits expected = new Bits(500);
        expected.setRange(50, 100);
        expected.setRange(450, 500);

        dest.copyRange(dest, 400, 100, 0);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             srcPos is at the end,
             destPos is at the end,
             src range is within src,
             dest range is within dest
             => result equal dest
            """)
    void copyRange14() {
        Bits dest = new Bits(500);
        dest.setRange(450, 500);
        Bits expected = new Bits(dest);

        dest.copyRange(dest, 400, 100, 400);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             length == 0
             => result equal dest
            """)
    void copyRange15() {
        Bits dest = new Bits(500);
        dest.setRange(10, 250);
        Bits src = new Bits(500);
        src.setRange(450, 500);
        Bits expected = new Bits(dest);

        dest.copyRange(src, 300, 0, 400);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             length == 0
             => result equal dest
            """)
    void copyRange16() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits expected = new Bits(dest);

        dest.copyRange(dest, 45, 0, 450);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             srcPos is last bit,
             src range is out of src,
             dest range is within dest
             => correct result
            """)
    void copyRange17() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(350, 500);
        Bits expected = new Bits(500);
        expected.set(0);
        expected.setRange(50, 250);

        dest.copyRange(src, 499, 100, 0);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             srcPos is not last bit,
             src range is out of src,
             dest range is within dest
             => correct result
            """)
    void copyRange18() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        dest.setRange(450, 500);
        Bits src = new Bits(500);
        src.setRange(350, 500);
        Bits expected = new Bits(500);
        expected.setRange(0, 10);
        expected.setRange(50, 250);
        expected.setRange(450, 500);

        dest.copyRange(src, 490, 100, 0);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             srcPos is last bit,
             src range is out of src,
             dest range is within dest
             => correct result
            """)
    void copyRange19() {
        Bits dest = new Bits(500);
        dest.setRange(50, 400);
        Bits expected = new Bits(500);
        expected.setRange(51, 400);

        dest.copyRange(dest, 499, 100, 50);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             srcPos is not last bit,
             src range is out of src,
             dest range is within dest
             => correct result
            """)
    void copyRange20() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        dest.setRange(450, 500);
        Bits expected = new Bits(500);
        expected.setRange(60, 250);
        expected.setRange(450, 500);

        dest.copyRange(dest, 440, 100, 50);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             destPos is last bit,
             src range is within src,
             dest range is out of dest
             => correct result
            """)
    void copyRange21() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(300, 450);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.set(499);

        dest.copyRange(src, 400, 100, 499);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             destPos is not last bit,
             src range is within src,
             dest range is out of dest
             => correct result
            """)
    void copyRange22() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(300, 450);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.setRange(450, 500);

        dest.copyRange(src, 400, 100, 450);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             destPos is last bit,
             src range is within src,
             dest range is out of dest
             => correct result
            """)
    void copyRange23() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.set(499);

        dest.copyRange(dest, 200, 100, 499);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             destPos is not last bit,
             src range is within src,
             dest range is out of dest
             => correct result
            """)
    void copyRange24() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.setRange(450, 500);

        dest.copyRange(dest, 200, 100, 450);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             dest range < src range,
             src range is out of src,
             dest range is out of dest
             => correct result
            """)
    void copyRange25() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(350, 500);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.set(499);

        dest.copyRange(src, 450, 100, 499);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             dest range == src range,
             src range is out of src,
             dest range is out of dest
             => correct result
            """)
    void copyRange26() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(350, 500);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.setRange(450, 500);

        dest.copyRange(src, 450, 100, 450);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are different objects,
             dest range > src range,
             src range is out of src,
             dest range is out of dest
             => correct result
            """)
    void copyRange27() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits src = new Bits(500);
        src.setRange(350, 500);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.set(450);

        dest.copyRange(src, 499, 100, 450);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             dest range < src range,
             src range is out of src,
             dest range is out of dest
             => correct result
            """)
    void copyRange28() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits expected = new Bits(500);
        expected.setRange(50, 250);
        expected.set(499);

        dest.copyRange(dest, 200, 400, 499);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             dest range == src range,
             src range is out of src,
             dest range is out of dest
             => result equal dest
            """)
    void copyRange29() {
        Bits dest = new Bits(500);
        dest.setRange(50, 250);
        Bits expected = new Bits(dest);

        dest.copyRange(dest, 200, 400, 200);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            copyRange(src, srcPos, length, destPos):
             dest and src are same object,
             dest range > src range,
             src range is out of src,
             dest range is out of dest
             => correct result
            """)
    void copyRange30() {
        Bits dest = new Bits(500);
        dest.setRange(50, 500);
        Bits expected = new Bits(500);
        expected.setRange(50, 500);
        expected.set(0);

        dest.copyRange(dest, 499, 1000, 0);

        Assertions.assertThat(dest).isEqualTo(expected);
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

        int actual = dest.copyRange(src, 350, 0, 300);

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

        int actual = dest.copyRange(src, 350, 100, 300);

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

        int actual = dest.copyRange(src, 499, 100, 0);

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

        int actual = dest.copyRange(src, 450, 10, 499);

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

        int actual = dest.copyRange(src, 499, 10, 495);

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

        int actual = dest.copyRange(src, 499, 10, 499);

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

        int actual = dest.copyRange(src, 480, 50, 499);

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

        dest.copyRange(dest, 0, 100, 50);

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

        dest.copyRange(dest, 50, 100, 50);

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

        dest.copyRange(dest, 75, 100, 25);

        Assertions.assertThat(dest).isEqualTo(expected);
    }

    @Test
    @DisplayName("expandTo(newSize): newSize < bits size => return false")
    void expandTo1() {
        Bits actual = new Bits(500);
        actual.setRange(280, 450);

        Assertions.assertThat(actual.expandTo(499)).isFalse();
    }

    @Test
    @DisplayName("expandTo(newSize): newSize == bits size => return false")
    void expandTo2() {
        Bits actual = new Bits(500);
        actual.setRange(280, 450);

        Assertions.assertThat(actual.expandTo(500)).isFalse();
    }

    @Test
    @DisplayName("expandTo(newSize): newSize > bits size => return true")
    void expandTo3() {
        Bits actual = new Bits(500);
        actual.setRange(280, 450);

        Assertions.assertThat(actual.expandTo(501)).isTrue();
    }

    @Test
    @DisplayName("expandTo(newSize): newSize < bits size => don't change target object")
    void expandTo4() {
        Bits actual = new Bits(500);
        actual.setRange(280, 450);
        Bits expected = new Bits(actual);

        actual.expandTo(499);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("expandTo(newSize): newSize == bits size => don't change target object")
    void expandTo5() {
        Bits actual = new Bits(500);
        actual.setRange(280, 450);
        Bits expected = new Bits(actual);

        actual.expandTo(500);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("expandTo(newSize): newSize > bits size => expand target object")
    void expandTo6() {
        Bits actual = new Bits(500);
        actual.setRange(280, 450);
        Bits expected = new Bits(501);
        expected.setRange(280, 450);

        actual.expandTo(501);

        Assertions.assertThat(actual).isEqualTo(expected);
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
        actual.expandTo(500);

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
        actual.expandTo(500);

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

        Assertions.assertThat(empty.isClean()).isTrue();
    }

    @Test
    @DisplayName("isClean(): bits is empty, size > 0 => return true")
    void isClean2() {
        Bits empty = new Bits(500);

        Assertions.assertThat(empty.isClean()).isTrue();
    }

    @Test
    @DisplayName("isClean(): one bit is set, first bit is set => return false")
    void isClean3() {
        Bits actual = new Bits(500);
        actual.set(0);

        Assertions.assertThat(actual.isClean()).isFalse();
    }

    @Test
    @DisplayName("isClean(): one bit is set, unit bit in middle => return false")
    void isClean4() {
        Bits actual = new Bits(500);
        actual.set(300);

        Assertions.assertThat(actual.isClean()).isFalse();
    }

    @Test
    @DisplayName("isClean(): one bit is set, high bit is set => return false")
    void isClean5() {
        Bits actual = new Bits(500);
        actual.set(499);

        Assertions.assertThat(actual.isClean()).isFalse();
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

}