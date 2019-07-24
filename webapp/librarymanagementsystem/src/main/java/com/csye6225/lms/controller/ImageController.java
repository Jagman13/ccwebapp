package com.csye6225.lms.controller;
import com.csye6225.lms.service.AmazonS3ImageService;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;



import com.csye6225.lms.exception.ResourceNotFoundException;
import com.csye6225.lms.pojo.Book;
import com.csye6225.lms.pojo.Image;
import com.csye6225.lms.service.BookService;
import com.csye6225.lms.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("book")
public class ImageController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private AmazonS3ImageService s3ImageService;

    @Autowired
    private Environment environment;

    @PostMapping(value = "/{id}/image")
    public ResponseEntity<Image> saveImage(@PathVariable UUID id , @RequestPart("url") MultipartFile file, UriComponentsBuilder ucBuilder) throws URISyntaxException, Exception {
        Optional<Book> book = bookService.findById(id);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException("Book Id not found");
        }

        if(book.get().getImageDetails()!=null){
            throw new Exception("Only one image can be added per book");
        }

        Book b = book.get();
        String originalFileName = file.getOriginalFilename();
        Image image = null;
        if(environment.getActiveProfiles()[0].equalsIgnoreCase("prod")) {
            //save to s3 and database
            String preSignedUrl= s3ImageService.uploadImageToS3(b , file);
            image = imageService.saveImageToDatabase( b.getId()+ "_" + originalFileName, b);
            image.setUrl(preSignedUrl);
        }else {
            //save to local
            String imageUrl = imageService.saveImageToDisk(b , originalFileName, file);
            image = imageService.saveImageToDatabase( imageUrl, b);
        }
        return  ResponseEntity.ok(image);
    }

    @GetMapping(value = "/{idBook}/image/{idImage}")
    public ResponseEntity<Image> getImage(@PathVariable UUID idBook ,@PathVariable UUID idImage) {
        imageService.checkBookImageMapping(idBook,idImage);
        Optional<Image> image = imageService.getImage(idImage);
        Image existingImage= image.get();
        if(environment.getActiveProfiles()[0].equalsIgnoreCase("prod")) {
            // get preSignedUrl
            existingImage.setUrl(s3ImageService.getPreSignedUrl(existingImage.getUrl()));
        }
        return ResponseEntity.ok(existingImage);
    }

    @DeleteMapping(value = "/{idBook}/image/{idImage}")
    public ResponseEntity<Object> deleteImage(@PathVariable UUID idBook ,@PathVariable UUID idImage) throws Exception {
        imageService.checkBookImageMapping(idBook,idImage);
        Optional<Book> book = bookService.findById(idBook);
        Image image = book.get().getImageDetails();
        if(environment.getActiveProfiles()[0].equalsIgnoreCase("prod")) {
            //save to s3
            s3ImageService.deleteImageFromS3(image.getUrl());
        }else {
            //delete from local
            imageService.deleteImageFromDisk(image.getUrl());

        }
        imageService.deleteImageFromDatabase(image.getId(),book.get());
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{idBook}/image/{idImage}")
    public ResponseEntity<Object> putImage(@PathVariable UUID idBook ,@PathVariable UUID idImage,@RequestPart("url") MultipartFile file) throws Exception {
        imageService.checkBookImageMapping(idBook,idImage);
        Optional<Book> book = bookService.findById(idBook);
        String fileNameNew = file.getOriginalFilename();
        String imageUrl= null;
        if(environment.getActiveProfiles()[0].equalsIgnoreCase("prod")) {
            //delete existing image from s3
            s3ImageService.deleteImageFromS3(book.get().getImageDetails().getUrl());
            //save new image to s3
            s3ImageService.uploadImageToS3(book.get() , file);
            imageUrl = book.get().getId()+ "_" + fileNameNew;
        }else {
            //delete from local
            imageService.deleteImageFromDisk(book.get().getImageDetails().getUrl());
            // save new image to disk and database
            imageUrl = imageService.saveImageToDisk(book.get(), fileNameNew, file);
        }

        Image image =imageService.updateImage(book.get(), imageUrl, idImage);

        return ResponseEntity.noContent().build();
    }

}
