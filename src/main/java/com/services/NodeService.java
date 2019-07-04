package com.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
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

@Service
public class NodeService {
    private final static String NODE_ID_PARAMETER_NAME = "_id";
    private final static String VERSION_PARAMETER_NAME = "version";
    private final static String VERSION_PARENT_PARAMETER_NAME = "version_parent";
    private final static String VERSION_PARENT_METADATA_PARAMETER_NAME = "metadata.version_parent";
    private final static Integer VERSION_FIRST = 1;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String uploadNode(MultipartFile file) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put(VERSION_PARAMETER_NAME, VERSION_FIRST);
        return gridFsTemplate.store(file.getInputStream(), file.getName(), file.getContentType(), metaData).toString();
    }

    public GridFsResource downloadNode(String id) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(id)));
        GridFsResource resource = gridFsTemplate.getResource(gridFsFile);

        return resource;
    }

    //todo
    //delete comments
    public void deleteNode(String nodeId) {
        GridFSFile gridFsLastVersionChildFile = getLastVersionChildNodeId(nodeId);
        GridFSFile gridFsVersionParentFile = getVersionParentNode(((BsonObjectId) gridFsLastVersionChildFile.getId()).getValue().toString());

        while (gridFsVersionParentFile != null) {
            gridFsTemplate.delete(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(gridFsLastVersionChildFile.getId())));

            gridFsLastVersionChildFile = gridFsVersionParentFile;
            gridFsVersionParentFile = getVersionParentNode(((BsonObjectId) gridFsLastVersionChildFile.getId()).getValue().toString());
        }

        gridFsTemplate.delete(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(gridFsLastVersionChildFile.getId())));
    }

    public String updateNode(MultipartFile file, String id) throws IOException {
        GridFSFile gridFsFile = getLastVersionChildNodeId(id);

        DBObject metaData = new BasicDBObject();
        Integer version = (Integer) gridFsFile.getMetadata().get(VERSION_PARAMETER_NAME);
        metaData.put(VERSION_PARAMETER_NAME, ++version);
        metaData.put(VERSION_PARENT_PARAMETER_NAME, gridFsFile.getId());

        return gridFsTemplate.store(file.getInputStream(), file.getName(), file.getContentType(), metaData).toString();
    }

    private GridFSFile getVersionChildNode(String nodeId) {
        return gridFsTemplate.findOne(new Query(Criteria.where(VERSION_PARENT_METADATA_PARAMETER_NAME).is(new ObjectId(nodeId))));
    }

    private GridFSFile getLastVersionChildNodeId(String nodeId) {
        String lastVersionChildNodeId = nodeId;

        GridFSFile gridFsFile = getVersionChildNode(lastVersionChildNodeId);
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

    private String getMainVersionParentNodeId(String nodeId) {
        String mainVersionParentNodeId = nodeId;

        GridFSFile gridFsFile = getVersionParentNode(mainVersionParentNodeId);
        while (gridFsFile != null) {
            mainVersionParentNodeId = gridFsFile.getId().asString().getValue();
            gridFsFile = getVersionParentNode(mainVersionParentNodeId);
        }

        return mainVersionParentNodeId;
    }
}
