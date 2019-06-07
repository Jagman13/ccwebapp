package com.csye6225.lms.pojo;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="book")
public class Book {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    @Column(nullable = false, length = 36, unique = true)
    private UUID id ;

    @Column(nullable = false)
    @NotBlank(message = "title cannot be blank")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "author cannot be blank")
    private String author;

    @Column(nullable = false)
    @NotBlank(message = "isbn cannot be blank")
    private String isbn;

    @Column(nullable = false)
    @Min(value = 1, message = "quantity must be greater than or equal to 1")
    private int quantity;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
