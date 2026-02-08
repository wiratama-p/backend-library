package com.wiratamap.backendlibrary.dto;

import jakarta.validation.constraints.NotBlank;

public record BookDto(
        Long id,
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Author is required")
        String author,
        @NotBlank(message = "ISBN is required")
        String isbn,
        @NotBlank(message = "Publication year is required")
        String publicationYear,
        @NotBlank(message = "Genre is required")
        String genre,
        String description
) {
}
