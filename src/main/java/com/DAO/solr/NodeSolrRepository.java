package com.DAO.solr;

import com.models.Node;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

public interface NodeSolrRepository extends SolrCrudRepository<Node, String> {
    @Query("name:?0 OR content:?0")
    List<Node> findAllNodes(String query);

    void deleteByNodeId(String nodeId);
}
