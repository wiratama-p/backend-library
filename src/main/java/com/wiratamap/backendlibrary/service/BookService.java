package com.wiratamap.backendlibrary.service;

import com.wiratamap.backendlibrary.dto.BookDto;
import com.wiratamap.backendlibrary.entity.Book;
import com.wiratamap.backendlibrary.exception.RecordNotFoundException;
import com.wiratamap.backendlibrary.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public BookDto create(BookDto bookDto) {
        Book book = toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return toDto(savedBook);
    }

    public BookDto update(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book not found with id: " + id));

        book.setTitle(bookDto.title());
        book.setAuthor(bookDto.author());
        book.setIsbn(bookDto.isbn());
        book.setPublicationYear(bookDto.publicationYear());
        book.setGenre(bookDto.genre());
        book.setDescription(bookDto.description());

        Book updatedBook = bookRepository.save(book);
        return toDto(updatedBook);
    }

    private Book toEntity(BookDto bookDto) {
        return Book.builder()
                .title(bookDto.title())
                .author(bookDto.author())
                .isbn(bookDto.isbn())
                .publicationYear(bookDto.publicationYear())
                .genre(bookDto.genre())
                .description(bookDto.description())
                .build();
    }

    private BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getGenre(),
                book.getDescription()
        );
    }
}
