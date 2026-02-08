package com.wiratamap.backendlibrary.service;

import com.wiratamap.backendlibrary.dto.BookDto;
import com.wiratamap.backendlibrary.entity.Book;
import com.wiratamap.backendlibrary.exception.DuplicateRecordException;
import com.wiratamap.backendlibrary.exception.RecordNotFoundException;
import com.wiratamap.backendlibrary.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    public BookDto create(BookDto bookDto) {
        validateIsbnUnique(bookDto.isbn(), null);

        Book book = toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return toDto(savedBook);
    }

    public BookDto findById(Long id) {
        return toDto(findBookById(id));
    }

    public void delete(Long id) {
        findBookById(id);
        bookRepository.deleteById(id);
    }

    public BookDto update(Long id, BookDto bookDto) {
        Book book = findBookById(id);
        validateIsbnUnique(bookDto.isbn(), id);

        book.setTitle(bookDto.title());
        book.setAuthor(bookDto.author());
        book.setIsbn(bookDto.isbn());
        book.setPublicationYear(bookDto.publicationYear());
        book.setGenre(bookDto.genre());
        book.setDescription(bookDto.description());

        Book updatedBook = bookRepository.save(book);
        return toDto(updatedBook);
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book not found with id: " + id));
    }

    private void validateIsbnUnique(String isbn, Long excludeId) {
        boolean exists = excludeId == null
                ? bookRepository.existsByIsbn(isbn)
                : bookRepository.existsByIsbnAndIdNot(isbn, excludeId);

        if (exists) {
            throw new DuplicateRecordException("Book with ISBN " + isbn + " already exists");
        }
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
