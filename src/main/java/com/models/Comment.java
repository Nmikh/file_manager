package com.models;

import lombok.*;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String nodeId;
    private String name;
    private String text;
}
