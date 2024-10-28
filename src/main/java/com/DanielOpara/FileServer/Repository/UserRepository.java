package com.DanielOpara.FileServer.Repository;

import com.DanielOpara.FileServer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
