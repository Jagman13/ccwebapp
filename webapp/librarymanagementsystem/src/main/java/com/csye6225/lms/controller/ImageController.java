package com.csye6225.lms.controller;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
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

    @PostMapping(value = "/{id}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<Image> saveImage(@PathVariable UUID id , @RequestPart("url") MultipartFile file, UriComponentsBuilder ucBuilder) throws URISyntaxException, Exception {
        Optional<Book> book = bookService.findById(id);
        if (!book.isPresent()) {
            throw new ResourceNotFoundException("Book Id not found");
        }

        if(book.get().getImageDetails()!=null){
            throw new Exception("Only one image can be added per book");
        }

        Book b = book.get();
        String fileName = file.getOriginalFilename();
        Image image = imageService.saveImage(b,fileName,file);
        return  ResponseEntity.ok(image);
    }

    @GetMapping(value = "/{idBook}/image/{idImage}")
    public ResponseEntity<Image> getImage(@PathVariable UUID idBook ,@PathVariable UUID idImage) {
        imageService.checkBookImageMapping(idBook,idImage);
        Optional<Image> image = imageService.getImage(idImage);
        return ResponseEntity.ok(image.get());
    }

    @DeleteMapping(value = "/{idBook}/image/{idImage}")
    public ResponseEntity<Object> deleteImage(@PathVariable UUID idBook ,@PathVariable UUID idImage) throws Exception {
        imageService.checkBookImageMapping(idBook,idImage);
        Optional<Book> book = bookService.findById(idBook);
        imageService.DeleteImage(book.get());
        imageService.deleteImage(book.get().getImageDetails().getId(),book.get());
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{idBook}/image/{idImage}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<Object> putImage(@PathVariable UUID idBook ,@PathVariable UUID idImage,@RequestPart("url") MultipartFile file) throws Exception {
        imageService.checkBookImageMapping(idBook,idImage);
        Optional<Book> book = bookService.findById(idBook);
        //Deleting the image from location
        imageService.DeleteImage(book.get());
        String fileNameNew = file.getOriginalFilename();
        //Adding new image to the location
        Image image =imageService.updateImage(fileNameNew,book.get(),file,idImage);
        return ResponseEntity.noContent().build();
    }

}
