package com.wiratamap.backendlibrary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BookDto(
        Long id,
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Author is required")
        String author,
        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^(978|979)\\d{10}$", message = "ISBN must be 13 digits, start with 978 or 979, and contain no dashes")
        String isbn,
        @NotBlank(message = "Publication year is required")
        String publicationYear,
        @NotBlank(message = "Genre is required")
        String genre,
        String description
) {
}
