package cph.databases.assignment.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Table(name = "address")
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;

    private int zipcode;

    // Constructors
    public Address() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public int getZipcode() {
        return zipcode;
    }


}
