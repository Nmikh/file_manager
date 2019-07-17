package com.services;

import com.DAO.solr.NodeSolrRepository;
import com.exceptions.NodeException;
import com.models.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ServicesTestConfiguration.class)
public class NodeServiceTest {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private NodeSolrRepository nodeSolrRepository;

    private NodeService nodeService;

    private MultipartFile multipartFile;

    @Before
    public void init() {
        nodeService = new NodeService();
        ReflectionTestUtils.setField(nodeService, "gridFsTemplate", gridFsTemplate);
        ReflectionTestUtils.setField(nodeService, "nodeSolrRepository", nodeSolrRepository);

        multipartFile = new MockMultipartFile("file",
                "file.txt", "text/plain", hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d"));
    }

    @Test
    public void uploadNodeTest() throws IOException {
        String nodeId = nodeService.uploadNode(multipartFile);
        Assert.assertNotNull(nodeId);
    }

    @Test
    public void downloadNodeTest() throws IOException, NodeException {
        GridFsResource gridFsResource = nodeService.downloadNode("");
        Assert.assertNotNull(gridFsResource);
    }

    @Test
    public void deleteNodeTest() throws NodeException {
        nodeService.deleteNode("e04fd020ea3a6910a2d808002b30309d");
    }

    @Test
    public void searchNodeTest() {
        List<Node> nodes = nodeService.searchNode("");
        Assert.assertNotNull(nodes);
    }
}
