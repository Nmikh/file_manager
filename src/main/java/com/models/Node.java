package com.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName="nodes")
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    @Indexed("_id")
    @Id
    private String id;

    @Indexed("node.id")
    private String nodeId;

    @Indexed("name")
    private String name;

    @Indexed("content")
    private String content;

    public Node(String nodeId, String name, String content) {
        this.nodeId = nodeId;
        this.name = name;
        this.content = content;
    }
}
