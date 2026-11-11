package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Post entities.
 * Extends JpaRepository which provides standard CRUD operations
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
