package com.controllers;

import com.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
public class DocumentConrtrollerTest {
    public static final String URL_NODE = "/node/%s";
    public static final String URL_COMMENT = "/node/comment/%s";

    private final String FILE_PARAM = "file";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void uploadNodeTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(FILE_PARAM,
                "filename.txt", "text/plain", "TEST TEXT".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(String.format(URL_NODE, ""))
                .file(file))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNodeTest() throws Exception {
        mockMvc.perform(delete(String.format(URL_NODE, "/5d2de2065a36fa2ac3195049")))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
