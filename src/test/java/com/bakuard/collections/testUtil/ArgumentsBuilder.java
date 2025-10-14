package com.bakuard.collections.testUtil;

import com.bakuard.collections.ReadableLinearStructure;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgumentsBuilder<T> {

    public static <T> ArgumentsBuilder<T> of(Stream<Fabric<T, ReadableLinearStructure<T>>> fabrics) {
        return new ArgumentsBuilder<>(fabrics);
    }


    private final List<Fabric<T, ReadableLinearStructure<T>>> fabrics;
    private final List<TestSuit<T>> testSuits;

    private ArgumentsBuilder(Stream<Fabric<T, ReadableLinearStructure<T>>> fabrics) {
        this.fabrics = fabrics.collect(Collectors.toCollection(ArrayList::new));
        this.testSuits = new ArrayList<>();
    }

    public ArgumentsBuilder<T> newTest() {
        testSuits.add(new TestSuit<>(fabrics));
        return this;
    }

    public ArgumentsBuilder<T> originStruct(T... initValues) {
        testSuits.getLast().originStruct(initValues);
        return this;
    }

    public ArgumentsBuilder<T> addArgs(Object... args) {
        testSuits.getLast().addArgs(args);
        return this;
    }

    public <S extends ReadableLinearStructure<T>> ArgumentsBuilder<T> addArgsFor(
            Class<?> structType, Function<S, List<Object>> argsFabric) {
        testSuits.getLast().addArgsFor(structType, argsFabric);
        return this;
    }

    public ArgumentsBuilder<T> expectedStruct(T... expectedValues) {
        testSuits.getLast().expectedStruct(expectedValues);
        return this;
    }

    public ArgumentsBuilder<T> expectedArray(T... expectedValues) {
        testSuits.getLast().expectedValue(expectedValues);
        return this;
    }

    public ArgumentsBuilder<T> expectedList(Object... expectedValues) {
        testSuits.getLast().expectedValue(List.of(expectedValues));
        return this;
    }

    public ArgumentsBuilder<T> expectedValue(Object expectedValue) {
        testSuits.getLast().expectedValue(expectedValue);
        return this;
    }

    public ArgumentsBuilder<T> expectedException(Class<? extends Throwable> exceptionType) {
        testSuits.getLast().expectedValue(exceptionType);
        return this;
    }

    public Stream<Arguments> build() {
        return testSuits.stream().flatMap(TestSuit::toArgumentsStream);
    }


    private static class TestSuit<T> {

        private final List<TestArgs<T>> testArgs;

        public TestSuit(List<Fabric<T, ReadableLinearStructure<T>>> fabrics) {
            testArgs = fabrics.stream()
                    .map(TestArgs::new)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public void originStruct(T... initValues) {
            testArgs.forEach(test -> test.createOrigin(initValues));
        }

        public void addArgs(Object... args) {
            testArgs.forEach(test -> test.addArgs(args));
        }

        public <S extends ReadableLinearStructure<T>> void addArgsFor(
                Class<?> structType, Function<S, List<Object>> argsFabric) {
            testArgs.stream()
                    .filter(test -> test.matchType(structType))
                    .findFirst()
                    .ifPresent(test -> test.addArgsFabric(argsFabric));
        }

        public void expectedStruct(T... expectedValues) {
            testArgs.forEach(test -> test.createExpected(expectedValues));
        }

        public void expectedValue(Object expectedValue) {
            testArgs.forEach(test -> test.setExpectedValue(expectedValue));
        }

        public Stream<Arguments> toArgumentsStream() {
            return testArgs.stream().map(TestArgs::toArguments);
        }
    }

    private static class TestArgs<T> {

        private final Fabric<T, ReadableLinearStructure<T>> fabric;
        private ReadableLinearStructure<T> origin;
        private Function argsFabric;
        private Object expected;

        public TestArgs(Fabric<T, ReadableLinearStructure<T>> fabric) {
            this.fabric = fabric;
            this.argsFabric = struct -> new ArrayList<>();
        }

        public boolean matchType(Class<?> structType) {
            return fabric.getType() == structType;
        }

        public void createOrigin(T... initValues) {
            origin = fabric.create(initValues);
        }

        public void createExpected(T... expectedValues) {
            expected = fabric.create(expectedValues);
        }

        public void setExpectedValue(Object expectedValue) {
            expected = expectedValue;
        }

        public void addArgs(Object... args) {
            Function argsFabric = struct -> Arrays.stream(args)
                    .collect(Collectors.toCollection(ArrayList::new));
            this.argsFabric = mergeArgsFabrics(this.argsFabric, argsFabric);
        }

        public void addArgsFabric(Function<? extends ReadableLinearStructure<T>, List<Object>> argsFabric) {
            this.argsFabric = mergeArgsFabrics(this.argsFabric, argsFabric);
        }

        public Arguments toArguments() {
            ArrayList<Object> resultArgs = new ArrayList<>();
            resultArgs.addFirst(origin);
            resultArgs.addAll((List<Object>)argsFabric.apply(origin));
            resultArgs.addLast(expected);
            return Arguments.of(resultArgs.toArray());
        }


        private Function mergeArgsFabrics(Function a, Function b) {
            return struct -> {
                List<Object> first = (List<Object>)a.apply(struct);
                List<Object> second = (List<Object>)b.apply(struct);
                ArrayList<Object> result = new ArrayList<>();
                result.addAll(first);
                result.addAll(second);
                return result;
            };
        }
    }
}
