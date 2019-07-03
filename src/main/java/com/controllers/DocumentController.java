package com.controllers;

import com.models.Comment;
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

    @PostMapping("/node")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity(nodeService.uploadNode(file), HttpStatus.OK);
    }

    @GetMapping("/node/{node_id}")
    public ResponseEntity<Resource> download(@PathVariable("node_id") String nodeId) throws IOException {

        GridFsResource resource = nodeService.downloadNode(nodeId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @PutMapping("/node/comment/{node_id}")
    public void addComment(@RequestBody Comment comment, @PathVariable("node_id") String nodeId) {

    }

    @DeleteMapping("/node/{node_id}")
    public ResponseEntity deleteNode(@PathVariable("node_id") String nodeId) {
        nodeService.deleteNode(nodeId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/node/{node_id}")
    public ResponseEntity updateNode(@RequestParam("file") MultipartFile file, @PathVariable("node_id") String nodeId) throws IOException {
        return new ResponseEntity(nodeService.updateNode(file, nodeId), HttpStatus.OK);
    }
}
