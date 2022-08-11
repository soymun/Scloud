package com.example.zipzip.Entity;


import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "useres")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "roooles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.ORDINAL)
    private List<Role> role;

    @OneToMany
    @JoinColumn(name = "file_id")
    private List<File> files;
}
