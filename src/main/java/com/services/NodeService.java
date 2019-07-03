package com.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
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
    private final static String COMMENTS_PARAMETER_NAME = "comments";
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

    public void deleteNode(String id) {
        String parentId;
        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(id)));
        if (gridFsFile.getMetadata().get(VERSION_PARENT_PARAMETER_NAME) != null) {
            parentId = (String) gridFsFile.getMetadata().get(VERSION_PARENT_PARAMETER_NAME);
        } else {
            parentId = id;
        }

        gridFsTemplate.delete(new Query(Criteria.where(VERSION_PARENT_METADATA_PARAMETER_NAME).is(parentId)));
        gridFsTemplate.delete(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(parentId)));
    }

    public String updateNode(MultipartFile file, String id) throws IOException {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(id)));

        DBObject metaData = new BasicDBObject();
        Integer version = (Integer) gridFsFile.getMetadata().get(VERSION_PARAMETER_NAME);
        metaData.put(VERSION_PARAMETER_NAME, ++version);
        if (gridFsFile.getMetadata().get(VERSION_PARENT_PARAMETER_NAME) != null) {
            metaData.put(VERSION_PARENT_PARAMETER_NAME, gridFsFile.getMetadata().get(VERSION_PARENT_PARAMETER_NAME));
        } else {
            metaData.put(VERSION_PARENT_PARAMETER_NAME, id);
        }

        return gridFsTemplate.store(file.getInputStream(), file.getName(), file.getContentType(), metaData).toString();
    }

    @SuppressWarnings("unchecked")
    private String getMainParent(String nodeId) {
        String mainParentId = nodeId;

        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(nodeId)));
        while (gridFsFile.getMetadata().get(VERSION_PARENT_PARAMETER_NAME) != null) {
            mainParentId = (String) gridFsFile.getMetadata().get(VERSION_PARENT_PARAMETER_NAME);
            gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(NODE_ID_PARAMETER_NAME).is(mainParentId)));
        }

        return mainParentId;
    }


    @SuppressWarnings("unchecked")
    private String getLastChild(String nodeId) {
        String lastChildId = nodeId;

        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(VERSION_PARENT_METADATA_PARAMETER_NAME).is(nodeId)));
        while (gridFsFile != null) {
            lastChildId = String.valueOf(gridFsFile.getId());
            gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where(VERSION_PARENT_METADATA_PARAMETER_NAME).is(lastChildId)));
        }

        return lastChildId;
    }
}
