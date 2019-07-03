package com.DAO;

import com.models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comment, String> {

    public List<Comment> findByNodeId(String nodeId);
}
