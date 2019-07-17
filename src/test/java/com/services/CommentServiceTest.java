package com.services;

import com.DAO.CommentsRepository;
import com.exceptions.NodeException;
import com.models.Comment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ServicesTestConfiguration.class)
public class CommentServiceTest {

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private NodeService nodeService;

    private CommentService commentService;

    @Before
    public void init() {
        commentService = new CommentService();
        ReflectionTestUtils.setField(commentService, "commentsRepository", commentsRepository);
        ReflectionTestUtils.setField(commentService, "nodeService", nodeService);
    }

    @Test
    public void addCommentTest() throws NodeException {
        String commentId = commentService.addComment(new Comment(null, null, "name", "text"), "");
        Assert.assertNotNull(commentId);
    }

    @Test
    public void getAllCommentsTest() throws NodeException {
        List<Comment> nodeId = commentService.getAllComments("nodeId");
        Assert.assertNotNull(nodeId);
    }

    @Test
    public void getAllCommentsPageTest() throws NodeException {
        List<Comment> nodeId = commentService.getAllCommentsPage("nodeId", 1, 1);
        Assert.assertNotNull(nodeId);
    }
}
