package com.services;

import com.DAO.solr.NodeSolrRepository;
import com.exceptions.NodeException;
import com.models.Node;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class NodeService {
    private final static String WRONG_NODE_ID_MESSAGE = "no such node exists";

    private final static String NODE_ID_PARAMETER_NAME = "_id";
    private final static String VERSION_PARAMETER_NAME = "version";
    private final static String VERSION_PARENT_PARAMETER_NAME = "version_parent";
    private final static String VERSION_PARENT_METADATA_PARAMETER_NAME = "metadata.version_parent";
    private final static Integer VERSION_FIRST = 1;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private NodeSolrRepository nodeSolrRepository;

    public String uploadNode(MultipartFile file) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put(VERSION_PARAMETER_NAME, VERSION_FIRST);

        String nodeId = gridFsTemplate.store(file.getInputStream(), file.getName(), file.getContentType(), metaData).toString();

        StringWriter writer = new StringWriter();
        IOUtils.copy(file.getInputStream(), writer, StandardCharsets.UTF_8);

        nodeSolrRepository.save(new Node(nodeId, file.getOriginalFilename(), writer.toString()));

        return nodeId;
    }

    public GridFsResource downloadNode(String id) throws NodeException, IOException {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(id)));
        if (gridFsFile == null) {
            throw new NodeException(WRONG_NODE_ID_MESSAGE);
        }

        return gridFsTemplate.getResource(gridFsFile);
    }

    public void deleteNode(String nodeId) throws NodeException {
        nodeSolrRepository.deleteByNodeId(nodeId);

        GridFSFile gridFsLastVersionChildFile = getLastVersionChildNode(nodeId);
        GridFSFile gridFsVersionParentFile = getVersionParentNode(((BsonObjectId) gridFsLastVersionChildFile.getId()).getValue().toString());

        while (gridFsVersionParentFile != null) {
            gridFsTemplate.delete(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(gridFsLastVersionChildFile.getId())));

            gridFsLastVersionChildFile = gridFsVersionParentFile;
            gridFsVersionParentFile = getVersionParentNode(((BsonObjectId) gridFsLastVersionChildFile.getId()).getValue().toString());
        }

        gridFsTemplate.delete(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(gridFsLastVersionChildFile.getId())));
    }

    public String updateNode(MultipartFile file, String id) throws IOException, NodeException {
        GridFSFile gridFsFile = getLastVersionChildNode(id);

        nodeSolrRepository.deleteByNodeId(((BsonObjectId) gridFsFile.getId()).getValue().toString());

        DBObject metaData = new BasicDBObject();
        Integer version = (Integer) gridFsFile.getMetadata().get(VERSION_PARAMETER_NAME);
        metaData.put(VERSION_PARAMETER_NAME, ++version);
        metaData.put(VERSION_PARENT_PARAMETER_NAME, gridFsFile.getId());

        String nodeId = gridFsTemplate.store(file.getInputStream(), file.getName(), file.getContentType(), metaData).toString();

        StringWriter writer = new StringWriter();
        IOUtils.copy(file.getInputStream(), writer, StandardCharsets.UTF_8);

        nodeSolrRepository.save(new Node(nodeId, file.getName(), writer.toString()));

        return nodeId;
    }

    public List<Node> searchNode(String query) {
        return nodeSolrRepository.findAllNodes(query);
    }

    private GridFSFile getVersionChildNode(String nodeId) {
        return gridFsTemplate.findOne(new Query(Criteria.where(VERSION_PARENT_METADATA_PARAMETER_NAME).is(new ObjectId(nodeId))));
    }

    public GridFSFile getLastVersionChildNode(String nodeId) throws NodeException {
        String lastVersionChildNodeId = nodeId;

        GridFSFile node = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(lastVersionChildNodeId)));
        if (node == null) {
            throw new NodeException(WRONG_NODE_ID_MESSAGE);
        }

        GridFSFile gridFsFile = getVersionChildNode(lastVersionChildNodeId);
        if (gridFsFile == null) {
            return node;
        }

        while (gridFsFile != null) {
            lastVersionChildNodeId = ((BsonObjectId) gridFsFile.getId()).getValue().toString();
            gridFsFile = getVersionChildNode(lastVersionChildNodeId);
        }

        return gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(lastVersionChildNodeId)));
    }

    private GridFSFile getVersionParentNode(String nodeId) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(nodeId)));
        if (gridFsFile.getMetadata().get(VERSION_PARENT_PARAMETER_NAME) != null) {
            return gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(gridFsFile.getMetadata().get(VERSION_PARENT_PARAMETER_NAME))));
        }

        return null;
    }

    public GridFSFile getMainVersionParentNodeId(String nodeId) throws NodeException {
        String mainVersionParentNodeId = nodeId;

        GridFSFile node = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(mainVersionParentNodeId)));
        if (node == null) {
            throw new NodeException(WRONG_NODE_ID_MESSAGE);
        }

        GridFSFile gridFsFile = getVersionParentNode(mainVersionParentNodeId);
        if (gridFsFile == null) {
            return node;
        }

        while (gridFsFile != null) {
            mainVersionParentNodeId = ((BsonObjectId) gridFsFile.getId()).getValue().toString();
            gridFsFile = getVersionParentNode(mainVersionParentNodeId);
        }

        return gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(mainVersionParentNodeId)));
    }
}
