package com.csye6225.lms.controller;



import com.csye6225.lms.exception.ResourceNotFoundException;
import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.pojo.Image;
import com.csye6225.lms.service.BookService;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("book")
public class ImageController {

    @Autowired
    private BookService bookService;

    @PostMapping(value = "/{id}/image",produces = "application/json", consumes = "application/json")
    public ResponseEntity<Image> saveImage(@PathVariable UUID id , @Valid @RequestBody Image newImg , UriComponentsBuilder ucBuilder) throws URISyntaxException, Exception {

        Optional<Book> book = bookService.findById(id);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException("Book Id not found");
        }

        Book b = book.get();
        b.setImageDetails(newImg);
        bookService.save(b);


        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}/image/{img_id}")
                .buildAndExpand(b.getId() ,b.getImageDetails().getId())
                .toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<Image>(b.getImageDetails(),headers, HttpStatus.CREATED);


    }

    }
