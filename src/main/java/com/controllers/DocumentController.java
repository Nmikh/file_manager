package com.controllers;

import com.exceptions.NodeException;
import com.models.Comment;
import com.services.CommentService;
import com.services.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class DocumentController {
    @Autowired
    private NodeService nodeService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/node")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        try {
            return new ResponseEntity(nodeService.uploadNode(file), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/node/{node_id}")
    public ResponseEntity<Resource> download(@PathVariable("node_id") String nodeId) throws IOException {
        try {
            GridFsResource resource = nodeService.downloadNode(nodeId);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(resource.getContentType()))
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (NodeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/node/{node_id}")
    public ResponseEntity deleteNode(@PathVariable("node_id") String nodeId) {
        try {
            commentService.deleteAllComments(nodeId);
            nodeService.deleteNode(nodeId);
            return new ResponseEntity(HttpStatus.OK);
        } catch (NodeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/node/{node_id}")
    public ResponseEntity updateNode(@RequestParam("file") MultipartFile file, @PathVariable("node_id") String nodeId) throws IOException {
        try {
            return new ResponseEntity(nodeService.updateNode(file, nodeId), HttpStatus.OK);
        } catch (NodeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/node/comment/{node_id}")
    public ResponseEntity addComment(@RequestBody Comment comment, @PathVariable("node_id") String nodeId) {
        try {
            commentService.addComment(comment, nodeId);
            return new ResponseEntity(HttpStatus.OK);
        } catch (NodeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/node/comment/{comment_id}")
    public ResponseEntity deleteComment(@PathVariable("comment_id") String commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/node/comment/{node_id}")
    public ResponseEntity getAllComments(@PathVariable("node_id") String nodeId) {
        try {
            return new ResponseEntity(commentService.getAllComments(nodeId), HttpStatus.OK);
        } catch (NodeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/node/comment/{node_id}/{page}/{size}")
    public ResponseEntity getCommentsPage(@PathVariable("node_id") String nodeId,
                                          @PathVariable("page") int page,
                                          @PathVariable("size") int size) {
        try {
            return new ResponseEntity(commentService.getAllCommentsPage(nodeId, page, size), HttpStatus.OK);
        } catch (NodeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/node/search/{query}")
    public ResponseEntity searchNode(@PathVariable("query") String query) {
        return new ResponseEntity(nodeService.searchNode(query), HttpStatus.OK);
    }
}
