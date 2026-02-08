package com.wiratamap.backendlibrary.repository;

import com.wiratamap.backendlibrary.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    @Query("""
    SELECT b 
        FROM Book b 
    WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) 
        OR LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    List<Book> searchByTitleOrAuthor(String search);
}
