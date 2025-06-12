package org.ncp.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.ncp.bookapi.BookController;
import org.ncp.bookapi.entities.Book;
import org.ncp.bookapi.services.BookService.BookService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.ncp.bookapi.services.BookService.BookService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerTest {

    BookService mockService = mock(BookService.class);
    BookController controller = new BookController(mockService);

    @Test
    void getAllBooksShouldReturnAListOfBooksOnExecute() throws Exception{
        when(mockService.getAllBooks()).thenReturn(List.of(
                new Book("Title One", "Author A"),
                new Book("Title Two", "Author B")
        ));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        final MvcResult mvcResult = mockMvc.perform(
                        get("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title One"))
                .andExpect(jsonPath("$[0].author").value("Author A"))
                .andReturn();
    }

    @Test
    void getBookByIdReturnTheBookIfItMatchesTeId() throws Exception{
        when(mockService.getBookById(1L)).thenReturn(
                Optional.of(new Book("Title One", "Author A"))
        );

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        final MvcResult mvcResult = mockMvc.perform(
                        get("/api/books/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title One"))
                .andExpect(jsonPath("$.author").value("Author A"))
                .andReturn();
    }

    @Test
    void createBookCreatesTheBook() throws Exception{
        Book inputBook = new Book("Title One", "Author A");
        inputBook.setId(1L);
        Book returnedBook = new Book("Title One", "Author A");
        returnedBook.setId(1L);

        when(mockService.createBook(Mockito.any(Book.class))).thenReturn(returnedBook);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        // Act & Assert
        mockMvc.perform(post("/api/books")  // Replace with your actual path (e.g., /api/books)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title One"))
                .andExpect(jsonPath("$.author").value("Author A"));
    }

    @Test
    void deleteBookReturnsNoContentWhenBookIsDeleted() throws Exception {
        Long bookId = 1L;

        // simulate successful delete by doing exactly nothing
        doNothing().when(mockService).deleteBook(bookId);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBookReturnsNotFoundWhenBookDoesNotExist() throws Exception {
        Long bookId = 999L;

        doThrow(new RuntimeException("Book not found with id: " + bookId))
                .when(mockService).deleteBook(bookId);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        mockMvc.perform(delete("/api/books/999", bookId))
                .andExpect(status().isNotFound());
    }
}
