package com.wiratamap.backendlibrary.dto;

public record BookDto(
        Long id,
        String title,
        String author,
        String isbn,
        String publicationYear,
        String genre,
        String description
) {
}
