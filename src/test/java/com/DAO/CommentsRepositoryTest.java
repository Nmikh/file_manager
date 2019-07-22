package com.DAO;

import com.models.Comment;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentsRepositoryTest {

    @Autowired
    private CommentsRepository commentsRepository;

    @Test
    public void saveAndDeleteCommentTest() {
        Comment comment = commentsRepository.save(new Comment(null, null, "name", "text"));
        Assert.assertNotNull(comment);

        commentsRepository.deleteById(comment.getId());
        Optional<Comment> deleteComment = commentsRepository.findById(comment.getId());
        Assert.assertNull(deleteComment);
    }
}
