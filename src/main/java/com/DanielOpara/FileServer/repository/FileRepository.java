package com.DanielOpara.FileServer.repository;

import com.DanielOpara.FileServer.model.FileModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileModel, Long> {
}
