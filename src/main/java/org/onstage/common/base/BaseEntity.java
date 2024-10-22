package org.onstage.common.base;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document
@FieldNameConstants
public abstract class BaseEntity {
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @Field
    @Builder.Default
    private Boolean deleted = null;
}