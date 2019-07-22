package com.DAO.solr;

import com.models.Node;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NodeSolrRepositoryTest {
    @Autowired
    private NodeSolrRepository nodeSolrRepository;

    @Test
    public void saveNodeTest() {
        Node node = nodeSolrRepository.save(new Node("1", "nodeId", "name", "content"));
        Assert.assertNotNull(node);

        nodeSolrRepository.deleteById(node.getId());
    }

    @Test
    public void deleteNodeByIdTest() {
        Node node = nodeSolrRepository.save(new Node("1","nodeId", "name", "content"));
        Assert.assertNotNull(node);

        nodeSolrRepository.deleteByNodeId("nodeId");
        Assert.assertFalse(nodeSolrRepository.existsById(node.getId()));
    }
}
