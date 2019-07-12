package com.csye6225.lms.controller;


import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.service.BookService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class BookControllerUnitTest {

//    private MockMvc mockMvc;
//
//    @MockBean
//    private BookService bookService;
//
//    @InjectMocks
//    private BookController bookController;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(bookController)
//                .build();
//    }

    @Test
    public void getAllBooks_ReturnsAllBooks() throws Exception {
//        UUID bookId1= UUID.randomUUID();
//        UUID bookId2= UUID.randomUUID();
//        List<Book> books = Arrays.asList(
//                new Book(bookId1,"title1","author1","isbn-1",1),
//                new Book(bookId2,"title2","author2","isbn-2",2));
//        when(bookService.findAll()).thenReturn(books);
//        mockMvc.perform(get("/book/"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(bookId1.toString())))
//                .andExpect(jsonPath("$[0].title", is("title1")))
//                .andExpect(jsonPath("$[0].author", is("author1")))
//                .andExpect(jsonPath("$[0].isbn", is("isbn-1")))
//                .andExpect(jsonPath("$[0].quantity", is(1)))
//                .andExpect(jsonPath("$[1].id", is(bookId2.toString())))
//                .andExpect(jsonPath("$[1].title", is("title2")))
//                .andExpect(jsonPath("$[1].author", is("author2")))
//                .andExpect(jsonPath("$[1].isbn", is("isbn-2")))
//                .andExpect(jsonPath("$[1].quantity", is(2)));
//        verify(bookService,times(1)).findAll();
//        verifyNoMoreInteractions(bookService);
    }

}
