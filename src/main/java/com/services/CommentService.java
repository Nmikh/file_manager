package com.services;

import com.DAO.CommentsRepository;
import com.models.Comment;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private NodeService nodeService;

    @Autowired
    private CommentsRepository commentsRepository;

    public void addComment(Comment comment, String nodeId) {
        GridFSFile mainVersionParentNodeId = nodeService.getMainVersionParentNodeId(nodeId);
        comment.setNodeId(mainVersionParentNodeId.getId().asObjectId().getValue());
        commentsRepository.save(comment);
    }

    public void deleteComment(String commentId) {
        commentsRepository.deleteById(commentId);
    }

    public List<Comment> getAllComments(String nodeId) {
        GridFSFile mainVersionParentNodeId = nodeService.getMainVersionParentNodeId(nodeId);
        return commentsRepository.findByNodeId(mainVersionParentNodeId.getId().asObjectId().getValue());
    }

    public List<Comment> getAllCommentsPage(String nodeId, int page, int size) {
        GridFSFile mainVersionParentNodeId = nodeService.getMainVersionParentNodeId(nodeId);
        Page<Comment> comments = commentsRepository.findByNodeId(mainVersionParentNodeId.getId().asObjectId().getValue(), PageRequest.of(page, size));
        return comments.getContent();
    }

    public void deleteAllComments(String nodeId) {
        GridFSFile mainVersionParentNodeId = nodeService.getMainVersionParentNodeId(nodeId);
        commentsRepository.deleteAllByNodeId(mainVersionParentNodeId.getId().asObjectId().getValue());

    }
}
