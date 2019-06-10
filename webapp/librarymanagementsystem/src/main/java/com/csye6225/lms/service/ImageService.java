package com.csye6225.lms.service;

import com.csye6225.lms.dao.BookRepository;
import com.csye6225.lms.dao.ImageRepository;
import com.csye6225.lms.exception.ResourceNotFoundException;
import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.pojo.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ImageService {

    @Autowired
    private ImageRepository imgRepository;

    @Autowired
    private BookService bookService;


    public Image saveImage(Image newImg , Book b){

        b.setImageDetails(newImg);
        bookService.createBook(b);

        return newImg;

    }


}
