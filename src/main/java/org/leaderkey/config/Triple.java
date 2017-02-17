package org.leaderkey.config;

public class Triple<T, U, V> {
    public final T first;
    public final U second;
    public final V third;

    public Triple(T t, U u, V v) {
        first = t;
        second = u;
        third = v;
    }
}
