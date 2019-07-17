package com.controllers;

import com.Application;
import com.exceptions.NodeException;
import com.google.gson.Gson;
import com.models.Comment;
import com.services.CommentService;
import com.services.NodeService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
public class DocumentControllerTest {
    public static final String URL_NODE = "/node/%s";
    public static final String URL_NODE_SEARCH = "/node/search/%s";
    public static final String URL_COMMENT = "/node/comment/%s";

    private final String FILE_PARAM = "file";

    private String testNodeId;
    private String commentId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private CommentService commentService;

    @BeforeEach
    public void init() throws IOException, NodeException {
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM,
                "filename.txt", "text/plain", "TEST TEXT".getBytes());

        testNodeId = nodeService.uploadNode(file);
        commentId = commentService.addComment(new Comment(null, null, "name", "text"), testNodeId);
    }

    @AfterEach
    public void clear() throws NodeException {
        nodeService.deleteNode(testNodeId);
        commentService.deleteComment(commentId);
    }


    //create
    @Test
    public void uploadNodeTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM,
                "filename.txt", "text/plain", "TEST TEXT".getBytes());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(String.format(URL_NODE, ""))
                .file(file))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String nodeId = mvcResult.getResponse().getContentAsString();
        nodeService.deleteNode(nodeId);
    }

    @Test
    public void addCommentTest() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(new Comment(null, null, "name", "text"));

        MvcResult mvcResult = mockMvc.perform(post((String.format(URL_COMMENT, "/".concat(testNodeId))))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andReturn();


        String commentNewId = mvcResult.getResponse().getContentAsString();
        commentService.deleteComment(commentNewId);
        commentService.deleteComment(commentId);
    }

    //view
    @Test
    public void getNodeTest() throws Exception {
        mockMvc.perform(get((String.format(URL_NODE, "/".concat(testNodeId)))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getAllCommentsTest() throws Exception {
        mockMvc.perform(get((String.format(URL_COMMENT, "/".concat(testNodeId)))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getCommentsPageTest() throws Exception {
        mockMvc.perform(get((String.format(URL_COMMENT, "/".concat(testNodeId).concat("/1/1")))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    //change
    @Test
    public void upgradeNodeTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM,
                "filename.txt", "text/plain", "TEST TEXT".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload(String.format(URL_NODE, "/".concat(testNodeId)));
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(builder
                .file(file))
                .andExpect(status().isOk());
    }

    //search
    @Test
    public void searchNodeTest() throws Exception {
        mockMvc.perform(get((String.format(URL_NODE_SEARCH, "/".concat(testNodeId)))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    //delete
    @Test
    public void deleteNodeTest() throws Exception {
        mockMvc.perform(delete(String.format(URL_NODE, "/".concat(testNodeId))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCommentTest() throws Exception {
        mockMvc.perform(delete(String.format(URL_COMMENT, "/".concat(commentId))))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
