package org.onstage.common.mappers;

public interface GenericMapper<T, S> {
    T toEntity(S source);

    S toDTO(T source);
}
