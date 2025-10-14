package com.bakuard.collections;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class StandardCollections {

	public static <T> ArrayList<T> toArrayList(Iterable<T> iterable) {
		ArrayList<T> result = new ArrayList<>();
		iterable.forEach(result::add);
		return result;
	}

	public static <T> LinkedList<T> toLinkedList(Iterable<T> iterable) {
		LinkedList<T> result = new LinkedList<>();
		iterable.forEach(result::add);
		return result;
	}

	public static <T> ArrayDeque<T> toArrayDeque(Iterable<T> iterable) {
		ArrayDeque<T> result = new ArrayDeque<>();
		iterable.forEach(result::add);
		return result;
	}

	public static <T> PriorityQueue<T> toPriorityQueue(Iterable<T> iterable, Comparator<T> comparator) {
		PriorityQueue<T> result = new PriorityQueue<>(comparator);
		iterable.forEach(result::add);
		return result;
	}

	public static <T> HashSet<T> toHashSet(Iterable<T> iterable) {
		HashSet<T> result = new HashSet<>();
		iterable.forEach(result::add);
		return result;
	}

	public static <T> TreeSet<T> toTreeSet(Iterable<T> iterable, Comparator<T> comparator) {
		TreeSet<T> result = new TreeSet<>(comparator);
		iterable.forEach(result::add);
		return result;
	}

	public static <T> LinkedHashSet<T> toLinkedHashSet(Iterable<T> iterable) {
		LinkedHashSet<T> result = new LinkedHashSet<>();
		iterable.forEach(result::add);
		return result;
	}

	public static <T> List<T> toImmutableList(Iterable<T> iterable) {
		return Collections.unmodifiableList(toArrayList(iterable));
	}

	public static <T> Set<T> toImmutableSet(Iterable<T> iterable) {
		return Collections.unmodifiableSet(toHashSet(iterable));
	}

	public static <T> SortedSet<T> toImmutableSortedSet(Iterable<T> iterable, Comparator<T> comparator) {
		return Collections.unmodifiableSortedSet(toTreeSet(iterable, comparator));
	}


	private StandardCollections() {}

}
