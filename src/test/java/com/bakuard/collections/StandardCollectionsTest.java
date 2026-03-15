package com.bakuard.collections;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

class StandardCollectionsTest {

	@DisplayName("""
			toBitSet(bits):
			 bits is empty
			 => bitSet.isEmpty() == true
			""")
	@Test
	void toBitSet1() {
		Bits bits = new Bits();

		BitSet bitSet = StandardCollections.toBitSet(bits);

		Assertions.assertThat(bitSet.isEmpty()).isTrue();
	}

	@DisplayName("""
			toBitSet(bits):
			 bits is not empty,
			 bits.toArray() return one word
			 => bitSet.get(i) must be always equal bits.get(i)
			""")
	@Test
	void toBitSet2() {
		Bits bits = Bits.of(45, 0,1,10,15,44);

		BitSet bitSet = StandardCollections.toBitSet(bits);

		SoftAssertions assertions = new SoftAssertions();
		for(int i = 0; i < bits.size(); i++) assertions.assertThat(bitSet.get(i)).isEqualTo(bits.get(i));
		assertions.assertAll();
	}

	@DisplayName("""
			toBitSet(bits):
			 bits is not empty,
			 bits.toArray() return several words,
			 first word is not empty,
			 last word is not empty
			 => bitSet.get(i) must be always equal bits.get(i)
			""")
	@Test
	void toBitSet3() {
		Bits bits = Bits.of(1000, 0,1,10,15,44,556,700,991,996,998);

		BitSet bitSet = StandardCollections.toBitSet(bits);

		SoftAssertions assertions = new SoftAssertions();
		for(int i = 0; i < bits.size(); i++) assertions.assertThat(bitSet.get(i)).isEqualTo(bits.get(i));
		assertions.assertAll();
	}

	@DisplayName("""
			toBitSet(bits):
			 bits is not empty,
			 bits.toArray() return several words,
			 first word is empty,
			 last word is not empty
			 => bitSet.get(i) must be always equal bits.get(i)
			""")
	@Test
	void toBitSet4() {
		Bits bits = Bits.of(1000, 223,224,311,333,556,700,991,996,998);

		BitSet bitSet = StandardCollections.toBitSet(bits);

		SoftAssertions assertions = new SoftAssertions();
		for(int i = 0; i < bits.size(); i++) assertions.assertThat(bitSet.get(i)).isEqualTo(bits.get(i));
		assertions.assertAll();
	}

	@DisplayName("""
			toBitSet(bits):
			 bits is not empty,
			 bits.toArray() return several words,
			 first word is not empty,
			 last word is empty
			 => bitSet.get(i) must be always equal bits.get(i)
			""")
	@Test
	void toBitSet5() {
		Bits bits = Bits.of(1000, 223,224,311,333,556,553,570,571,598,599);

		BitSet bitSet = StandardCollections.toBitSet(bits);

		SoftAssertions assertions = new SoftAssertions();
		for(int i = 0; i < bits.size(); i++) assertions.assertThat(bitSet.get(i)).isEqualTo(bits.get(i));
		assertions.assertAll();
	}
}