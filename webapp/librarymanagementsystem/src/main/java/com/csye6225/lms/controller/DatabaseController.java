package com.csye6225.lms.controller;

import com.csye6225.lms.service.AmazonS3ImageService;
import com.csye6225.lms.service.BookService;
import com.csye6225.lms.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cleanupDB")
public class DatabaseController {
    @Autowired
    private BookService bookService;

    @Autowired
    private CustomUserDetailsService userService ;

    @Autowired
    private AmazonS3ImageService s3ImageService;

    @PostMapping(value = "/")
    public ResponseEntity<Object> resetPassword(){
        System.out.println("Deleteing all data");
        bookService.deleteAll();
        userService.deleteAll();
        s3ImageService.deleteAll();
        System.out.println("Deleted all data");
        return ResponseEntity.noContent().build();
    }
}
