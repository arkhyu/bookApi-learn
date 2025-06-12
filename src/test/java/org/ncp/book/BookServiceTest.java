package org.ncp.book;

import org.ncp.bookapi.entities.Book;
import org.ncp.bookapi.repositories.BookRepository;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.ncp.bookapi.services.BookService.BookService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    private final BookRepository repository = mock(BookRepository.class);
    private final BookService service = new BookService(repository);

    @Test
    void createBookCreatedBookOnExecute()
    {
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);

        //Example creating manually the test object
        Book book = new Book("Title", "Author");

        service.createBook(book);

        verify(repository).save(bookArgumentCaptor.capture());
        Book savedBook = bookArgumentCaptor.getValue();
        assertEquals(book.getTitle(), savedBook.getTitle());
        assertEquals(book.getAuthor(), savedBook.getAuthor());
    }

    @Test
    void getBookByIdReturnsBookBookIfBookExist()
    {

        Long bookId = 1L;
        Book book = new Book("Test Title", "Test Author");
        book.setId(bookId);

        when(repository.findById(bookId)).thenReturn(Optional.of(book));

        Optional<Book> result = service.getBookById(bookId);
        // Assert
        assertTrue(result.isPresent());
        assertEquals(bookId, result.get().getId());
        assertEquals("Test Title", result.get().getTitle());
        assertEquals("Test Author", result.get().getAuthor());

        verify(repository, times(1)).findById(bookId);
    }

    @Test
    void getBookByIdReturnsEmptyOptionalWhenBookDoesNotExist() {
        // Arrange
        Long bookId = 99L;
        when(repository.findById(bookId)).thenReturn(Optional.empty());

        // Act
        Optional<Book> result = service.getBookById(bookId);

        // Assert
        assertTrue(result.isEmpty(), "Expected Optional to be empty when book is not found");

        verify(repository, times(1)).findById(bookId);
    }

    @Test
    void deleteBookDeletesBookWhenItExists() {
        // Arrange
        Long bookId = 1L;
        when(repository.existsById(bookId)).thenReturn(true);

        // Act
        service.deleteBook(bookId);

        // Assert
        verify(repository, times(1)).deleteById(bookId);
    }

    @Test
    void shouldThrowExceptionWhenBookDoesNotExist() {
        // Arrange
        Long bookId = 99L;
        when(repository.existsById(bookId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.deleteBook(bookId);
        });

        assertEquals("Book not found with id: 99", exception.getMessage());
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void getAllBooksReturnsAllBooksOnExecute() {
        List<Book> books = List.of(
                new Book("Title One", "Author A"),
                new Book("Title Two", "Author B")
        );
        when(repository.findAll()).thenReturn(books);

        var result = service.getAllBooks();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }
}
