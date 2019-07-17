package com.services;

import com.DAO.CommentsRepository;
import com.DAO.solr.NodeSolrRepository;
import com.DAO.solr.NodeSolrRepositoryTest;
import com.exceptions.NodeException;
import com.models.Comment;
import com.models.Node;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.util.Arrays;
import java.util.Calendar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Configuration
public class ServicesTestConfiguration {

    @Bean
    public NodeService getNodeService() throws NodeException {
        BsonValue bsonValue = getBsonValue();

        NodeService mock = Mockito.mock(NodeService.class);
        when(mock.getMainVersionParentNodeId(anyString())).thenReturn(
                new GridFSFile(bsonValue, "filename", 12, 12, Calendar.getInstance().getTime(), null, new Document()));

        return mock;
    }

    @Bean
    public CommentsRepository getCommentsRepository() {
        Page page = getPage();

        CommentsRepository mock = Mockito.mock(CommentsRepository.class);
        when(mock.findByNodeId(any())).thenReturn(Arrays.asList(
                new Comment("id", new ObjectId("5399aba6e4b0ae375bfdca88"), "name", "text")));
        when(mock.findByNodeId(any(), any())).thenReturn(page);
        when(mock.save(any())).thenReturn(
                new Comment("id", null, "name", "text"));

        doNothing().when(mock).deleteAllByNodeId(any());

        return mock;
    }

    private Page getPage() {
        Page mock = Mockito.mock(Page.class);
        when(mock.getContent()).thenReturn(Arrays.asList(
                new Comment("id", new ObjectId("5399aba6e4b0ae375bfdca88"), "name", "text")));

        return mock;
    }

    @Bean
    public NodeSolrRepository getNodeSolrRepository() {
        NodeSolrRepository mock = Mockito.mock(NodeSolrRepository.class);
        doNothing().when(mock).deleteByNodeId(anyString());
        when(mock.findAllNodes(any())).thenReturn(Arrays.asList(new Node("_id", "node.id", "name", "content")));

        return mock;
    }

    @Bean
    public GridFsTemplate getGridFsTemplate() {
        GridFsResource gridFsResource = getGridFsResource();
        BsonValue bsonValue = getBsonValue();

        GridFsTemplate mock = Mockito.mock(GridFsTemplate.class);
        when(mock.store(any(), anyString(), anyString(), (DBObject) any())).thenReturn(new ObjectId("5399aba6e4b0ae375bfdca88"));
        when(mock.findOne(any())).thenReturn(
                new GridFSFile(bsonValue, "filename", 12, 12, Calendar.getInstance().getTime(), null, new Document()));
        ;
        when(mock.getResource((GridFSFile) any())).thenReturn(gridFsResource);
        doNothing().when(mock).delete(any());

        return mock;
    }

    public GridFsResource getGridFsResource() {
        GridFsResource mock = Mockito.mock(GridFsResource.class);

        return mock;
    }

    public BsonValue getBsonValue() {
        BsonObjectId bsonObjectId = getBsonObjectId();

        BsonValue mock = Mockito.mock(BsonValue.class);
        when(mock.asObjectId()).thenReturn(bsonObjectId);

        return mock;
    }

    public BsonObjectId getBsonObjectId() {
        BsonObjectId mock = Mockito.mock(BsonObjectId.class);
        when(mock.getValue()).thenReturn(new ObjectId("5399aba6e4b0ae375bfdca88"));

        return mock;
    }
}
