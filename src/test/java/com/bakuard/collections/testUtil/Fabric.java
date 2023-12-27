package com.bakuard.collections.testUtil;

public interface Fabric<T, S> {

    public S create(T... data);
}
