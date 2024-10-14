package org.onstage.common.mappers;

public interface GenericMapper<T, S> {
    T toDb(S source);

    S toApi(T source);
}
