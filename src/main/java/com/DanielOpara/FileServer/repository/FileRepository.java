package com.DanielOpara.FileServer.repository;

import com.DanielOpara.FileServer.model.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileModel, Long> {
    Optional<FileModel> findByEmail(String email);
}
