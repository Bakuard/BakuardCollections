package com.bakuard.collections.testUtil;

@FunctionalInterface
public interface Fabric<T, S> {

    public default S create(T... data) {
        return createWithSize(data.length, data);
    }

    public S createWithSize(int size, T... data);
}
