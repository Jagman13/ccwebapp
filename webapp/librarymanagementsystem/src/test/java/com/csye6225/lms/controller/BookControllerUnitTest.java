package com.csye6225.lms.controller;

import com.csye6225.lms.dao.BookRepository;
import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.pojo.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc //need this in Spring Boot test
public class BookControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @InjectMocks
    private BookController bookController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithMockUser(username="honraoakshata@gmail.com",password = "Abcd@123",roles={"USER"})
    public void testAddBooksSuccess() throws Exception {
        //User does not exist in database
        //Book book = new Book("Java","Herbert Schildt","1234Ubs",5);

        //when(userRepository.findByEmail(userDetails.getEmail())).thenReturn(null);
        mockMvc.perform((post("/book").contentType(MediaType.APPLICATION_JSON).param("title", "Java").param("author", "Herbert Schildt").param("isbn","1234Ubs"))
                .andExpect(status().isOk()));
    }

}
