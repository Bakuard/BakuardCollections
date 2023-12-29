package com.bakuard.collections.testUtil;

import com.bakuard.collections.ReadableLinearStructure;

public record StructAndMutator<T, S extends ReadableLinearStructure<T>>(S struct, Mutator<T, S> mutator) {}
