package com.DanielOpara.FileServer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class FileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(name="file_name")
    public String fileName;

    @Lob
    @Column(columnDefinition = "LONGBLOB", nullable = false)
    public byte[] fileData;

    @Column(nullable = false)
    public String email;
}
