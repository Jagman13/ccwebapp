package com.csye6225.lms.controller;

import com.csye6225.lms.exception.ResourceNotFoundException;
import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.pojo.RestApiError;
import com.csye6225.lms.service.AmazonS3ImageService;
import com.csye6225.lms.service.BookService;
import com.csye6225.lms.service.ImageService;
import com.google.gson.Gson;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    static {

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }
    private final static Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private StatsDClient statsDClient;

    @Autowired
    private BookService bookService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private Environment environment;

    @Autowired
    private AmazonS3ImageService s3ImageService;

    @GetMapping(value = "/")
    public ResponseEntity<List<Book>> findAll() {
        statsDClient.incrementCounter("endpoint.allbook.http.get");
        logger.info("Get all books endpoint request");
        List<Book> books = bookService.findAll();
        if(environment.getActiveProfiles()[0].equalsIgnoreCase("prod")) {
            for(Book book: books){
                if(book.getImageDetails()!=null){
                    logger.info("Attaching presigned url for book with id "+ book.getId());
                    book.getImageDetails().setUrl(s3ImageService.getPreSignedUrl(book.getImageDetails().getUrl()));
                }
            }
        }
        String list= new Gson().toJson(books);
        logger.info("Books returned: " + list );
        logger.info("Get all books - Response code: " + HttpStatus.OK);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBook(@PathVariable UUID id) {
        statsDClient.incrementCounter("endpoint.bookById.http.get");
        Optional<Book> book = bookService.findById(id);
        Book existingBook= book.get();
        if (!book.isPresent()) {
            logger.error("Book ID " +id + " not found. " );
            logger.info("Get book by Id- Response code: " + HttpStatus.NOT_FOUND);
            throw new ResourceNotFoundException("Book Id not found");
        }

        if(environment.getActiveProfiles()[0].equalsIgnoreCase("prod") && existingBook.getImageDetails()!=null) {
            existingBook.getImageDetails().setUrl(s3ImageService.getPreSignedUrl(existingBook.getImageDetails().getUrl()));
        }
        return ResponseEntity.ok(existingBook);
    }

    @PostMapping(value = "/", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book newBook , UriComponentsBuilder ucBuilder) throws URISyntaxException , Exception{

        Gson gson= new Gson();
        statsDClient.incrementCounter("endpoint.addBook.http.post");
        logger.debug("Add book request " + gson.toJson(newBook));

        newBook.setId(null);
        Book book = bookService.createBook(newBook);
        if (book == null) {
            logger.info("Add Book- Response code: " + HttpStatus.INTERNAL_SERVER_ERROR);
            throw new Exception("Internal Server error");
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(book.getId())
                .toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        logger.info("Add Book- Response code: " + HttpStatus.CREATED);
        return new ResponseEntity<Book>(book,headers, HttpStatus.CREATED);
    }

    @PutMapping(value = "/", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> updateBook(@Valid @RequestBody Book book) {
        Gson gson = new Gson();
        statsDClient.incrementCounter("endpoint.updateBook.http.put");

        if (book.getId() == null) {
            logger.error("Validation failed for request: ID cannot be null "+ gson.toJson(book) );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestApiError("Validation Failed", "Id must be passed"));
        }
        Optional<Book> b = bookService.findById(book.getId());
        if (!b.isPresent()) {
            logger.error("Book ID " + book.getId() + " not found" );
            return ResponseEntity.badRequest().build();
        }
        if(null!=b.get().getImageDetails())
        {
            book.setImageDetails(b.get().getImageDetails());
        }
        bookService.createBook(book);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable UUID id) throws Exception{

        statsDClient.incrementCounter("endpoint.deleteBook.http.delete");

        Optional<Book> book = bookService.findById(id);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException("Book Id not found");
        }

        Book b = book.get();
        if(b.getImageDetails()!=null){
            if(environment.getActiveProfiles()[0].equalsIgnoreCase("prod")){
                s3ImageService.deleteImageFromS3(b.getImageDetails().getUrl());
            }else{
                imageService.deleteImageFromDisk(b.getImageDetails().getUrl());
            }
        }

        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
