package com.wiratamap.backendlibrary.controller;

import com.wiratamap.backendlibrary.entity.Book;
import com.wiratamap.backendlibrary.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void createBook_shouldReturn201_whenRequestIsValid() throws Exception {
        String requestBody = """
                {
                    "title": "Mommyclopedia: 78 Resep MPASI",
                    "author": "dr. Meta Hanindita, Sp.A",
                    "isbn": "978-602-8519-93-9",
                    "publicationYear": "2016",
                    "genre": "Parenting",
                    "description": "Kumpulan resep MPASI untuk bayi"
                }
                """;

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Mommyclopedia: 78 Resep MPASI")))
                .andExpect(jsonPath("$.author", is("dr. Meta Hanindita, Sp.A")))
                .andExpect(jsonPath("$.isbn", is("978-602-8519-93-9")))
                .andExpect(jsonPath("$.publicationYear", is("2016")))
                .andExpect(jsonPath("$.genre", is("Parenting")))
                .andExpect(jsonPath("$.description", is("Kumpulan resep MPASI untuk bayi")));
    }

    @Test
    void createBook_shouldReturn400_whenAllRequiredFieldsAreMissing() throws Exception {
        String requestBody = """
                {
                    "description": "Some description"
                }
                """;

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errors", hasSize(5)))
                .andExpect(jsonPath("$.errors", hasItem("title: Title is required")))
                .andExpect(jsonPath("$.errors", hasItem("author: Author is required")))
                .andExpect(jsonPath("$.errors", hasItem("isbn: ISBN is required")))
                .andExpect(jsonPath("$.errors", hasItem("publicationYear: Publication year is required")))
                .andExpect(jsonPath("$.errors", hasItem("genre: Genre is required")));
    }

    @Test
    void updateBook_shouldReturn200_whenRequestIsValid() throws Exception {
        Book existingBook = bookRepository.save(Book.builder()
                .title("Mommyclopedia: 78 Resep MPASI")
                .author("dr. Meta Hanindita, Sp.A")
                .isbn("978-602-8519-93-9")
                .publicationYear("2016")
                .genre("Parenting")
                .description("Kumpulan resep MPASI untuk bayi")
                .build());

        String requestBody = """
                {
                    "title": "Mommyclopedia: 78 Resep MPASI Edisi Revisi",
                    "author": "dr. Meta Hanindita, Sp.A",
                    "isbn": "978-602-8519-93-9",
                    "publicationYear": "2018",
                    "genre": "Parenting",
                    "description": "Kumpulan resep MPASI untuk bayi edisi revisi"
                }
                """;

        mockMvc.perform(put("/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(existingBook.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Mommyclopedia: 78 Resep MPASI Edisi Revisi")))
                .andExpect(jsonPath("$.author", is("dr. Meta Hanindita, Sp.A")))
                .andExpect(jsonPath("$.isbn", is("978-602-8519-93-9")))
                .andExpect(jsonPath("$.publicationYear", is("2018")))
                .andExpect(jsonPath("$.genre", is("Parenting")))
                .andExpect(jsonPath("$.description", is("Kumpulan resep MPASI untuk bayi edisi revisi")));
    }

    @Test
    void updateBook_shouldReturn404_whenBookNotFound() throws Exception {
        String requestBody = """
                {
                    "title": "Mommyclopedia: 78 Resep MPASI",
                    "author": "dr. Meta Hanindita, Sp.A",
                    "isbn": "978-602-8519-93-9",
                    "publicationYear": "2016",
                    "genre": "Parenting"
                }
                """;

        mockMvc.perform(put("/books/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Book not found with id: 999")));
    }
}
