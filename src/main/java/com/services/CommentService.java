package com.services;

import com.DAO.CommentsRepository;
import com.exceptions.NodeException;
import com.models.Comment;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final static String WRONG_NODE_ID_MESSAGE = "no such node exists";

    @Autowired
    private NodeService nodeService;

    @Autowired
    private CommentsRepository commentsRepository;

    public void addComment(Comment comment, String nodeId) throws NodeException {
        GridFSFile mainVersionParentNodeId = nodeService.getMainVersionParentNodeId(nodeId);
        comment.setNodeId(mainVersionParentNodeId.getId().asObjectId().getValue());
        commentsRepository.save(comment);
    }

    public void deleteComment(String commentId) {
        commentsRepository.deleteById(commentId);
    }

    public List<Comment> getAllComments(String nodeId) throws NodeException {
        GridFSFile mainVersionParentNodeId = nodeService.getMainVersionParentNodeId(nodeId);
        return commentsRepository.findByNodeId(mainVersionParentNodeId.getId().asObjectId().getValue());
    }

    public List<Comment> getAllCommentsPage(String nodeId, int page, int size) throws NodeException {
        GridFSFile mainVersionParentNodeId = nodeService.getMainVersionParentNodeId(nodeId);
        Page<Comment> comments = commentsRepository.findByNodeId(mainVersionParentNodeId.getId().asObjectId().getValue(), PageRequest.of(page, size));
        return comments.getContent();
    }

    public void deleteAllComments(String nodeId) throws NodeException {
        GridFSFile mainVersionParentNodeId = nodeService.getMainVersionParentNodeId(nodeId);
        if (mainVersionParentNodeId == null) {
            throw new NodeException(WRONG_NODE_ID_MESSAGE);
        }
        commentsRepository.deleteAllByNodeId(mainVersionParentNodeId.getId().asObjectId().getValue());

    }
}
