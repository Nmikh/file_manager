package com.DAO;

import com.models.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comment, String> {

    List<Comment> findByNodeId(ObjectId nodeId);

    void deleteAllByNodeId(ObjectId nodeId);

    Page<Comment> findByNodeId(ObjectId nodeId, Pageable pageable);
}
