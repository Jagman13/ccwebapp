package com.csye6225.lms.controller;

import com.csye6225.lms.exception.ResourceNotFoundException;
import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.pojo.RestApiError;
import com.csye6225.lms.service.BookService;
import com.csye6225.lms.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ImageService imageService;

    @GetMapping(value = "/")
    public ResponseEntity<List<Book>> findAll() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBook(@PathVariable UUID id) {
        Optional<Book> book = bookService.findById(id);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException("Book Id not found");
        }
        return ResponseEntity.ok(book.get());
    }

    @PostMapping(value = "/", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book newBook , UriComponentsBuilder ucBuilder) throws URISyntaxException , Exception{
        newBook.setId(null);
        Book book = bookService.createBook(newBook);
        if (book == null) {
            throw new Exception("Internal Server error");

        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(book.getId())
                .toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<Book>(book,headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> updateBook(@Valid @RequestBody Book book) {
        if (book.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestApiError("Validation Failed", "Id must be passed"));
        }
        Optional<Book> b = bookService.findById(book.getId());
        if (!b.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        bookService.createBook(book);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable UUID id) throws Exception{
        Optional<Book> book = bookService.findById(id);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException("Book Id not found");
        }

        Book b = book.get();
        if(b.getImageDetails()!=null){
            imageService.DeleteImage(b);
        }

        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
