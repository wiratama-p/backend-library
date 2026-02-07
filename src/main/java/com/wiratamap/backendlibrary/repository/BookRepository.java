package com.wiratamap.backendlibrary.repository;

import com.wiratamap.backendlibrary.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
