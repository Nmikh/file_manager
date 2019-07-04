package com.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;


@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @JsonIgnore
    private String id;
    @JsonIgnore
    private ObjectId nodeId;
    private String name;
    private String text;
}
