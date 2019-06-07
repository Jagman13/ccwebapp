package com.csye6225.lms.controller;

import com.csye6225.lms.dao.BookRepository;
import com.csye6225.lms.pojo.Book;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class BookController {

    @Autowired
    private BookRepository bookRepository;


    @PostMapping(value = "/book")
    public ResponseEntity<Book> addBooks(@RequestBody Book newBook) {

        Book book = bookRepository.save(newBook);

        if (book == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(book);


    }

    @GetMapping(value = "/book")
    public ResponseEntity<List<Book>> findAll() {
        // read from database
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);  // return 200, with json body
    }

    @GetMapping(value = "/book/{bookId}")
    public ResponseEntity<Book> findById(@PathVariable UUID bookId) {
        /*try {

         //   Book book = bookRepository.findById(bookId);
            return ResponseEntity.ok(book);  // return 200, with json body
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); //return 404, with null body
        }*/

        return null ;
    }

}
