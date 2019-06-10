package com.csye6225.lms.pojo;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name="image")
public class Image {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Type(type = "uuid-char")
    @Column(nullable = false, length = 36, unique = true)
    private UUID id ;

    @Column(nullable = false)
    @NotBlank(message = "image url cannot be blank")
    private String url;


    public Image() {

    }

    public Image(UUID id,String url) {
        this.id = id;
        this.url = url;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
