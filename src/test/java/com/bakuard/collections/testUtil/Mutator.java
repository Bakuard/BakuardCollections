package com.bakuard.collections.testUtil;

import com.bakuard.collections.ReadableLinearStructure;

public interface Mutator<T, S extends ReadableLinearStructure<T>> {

    void mutate(S structure);
}
