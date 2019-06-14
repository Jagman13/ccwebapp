package com.csye6225.lms.service;

import com.csye6225.lms.dao.ImageRepository;
import com.csye6225.lms.exception.ResourceNotFoundException;
import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.pojo.Image;
import com.csye6225.lms.service.BookService;
import com.csye6225.lms.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;


@Service
public class ImageService {

    @Autowired
    private ImageRepository imgRepository;

    @Autowired
    private BookService bookService;


    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "C://Users//honra//Desktop//Picture Folder//";

    public Image saveImage(String fileName , Book b){
        Image  newImg = new Image();
        newImg.setUrl(fileName);
        b.setImageDetails(newImg);
        b = bookService.createBook(b);
        return b.getImageDetails();
    }

    public Image updateImage(Image image){
        Image newImg =imgRepository.save(image);
        return newImg;
    }

    public Optional<Image> getImage(UUID idImage){
        return imgRepository.findById(idImage);
    }

    public void deleteImage(UUID idImage,Book b){
        b.setImageDetails(null);
        imgRepository.deleteById(idImage);
    }

    public void checkBookImageMapping(UUID idBook,UUID idImage){
        Optional<Book> book = bookService.findById(idBook);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException("Book Id not found");
        }
        Image image= book.get().getImageDetails();
        if(image == null){
            throw new ResourceNotFoundException("Image not found for the given bookID");
        }
        if(!image.getId().equals(idImage)){
            throw new ResourceNotFoundException("Book Id and Image Id do not match");
        }
    }
    public void DeleteImage(Book book) throws Exception {
        //Deleting the image from location
        String fileName = book.getImageDetails().getUrl();

        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch(NoSuchFileException e)
        {
            throw new Exception("No such file/directory exists  ");
        }
        catch(IOException e)
        {
            throw new Exception("Invalid permissions.  ");
        }
        deleteImage(book.getImageDetails().getId(),book);
    }

    public Image saveImage(Book book, String fileNameNew, MultipartFile file) throws Exception {
        String fileExtension="";
        if(fileNameNew.contains(".") && fileNameNew.lastIndexOf(".")!= 0)
        {
            fileExtension=fileNameNew.substring(fileNameNew.lastIndexOf(".")+1);
        }
        if (!fileExtension.equalsIgnoreCase("jpg") && !fileExtension.equalsIgnoreCase("png") && !fileExtension.equalsIgnoreCase("jpeg") ) {
            throw new Exception("File extension is not supported");
        }
        String destinationFilePath =UPLOADED_FOLDER + book.getId()+"_"+fileNameNew;
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(destinationFilePath);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("File cannot be saved");
        }
        Image img = saveImage(destinationFilePath , book) ;
        return img;
    }

}
