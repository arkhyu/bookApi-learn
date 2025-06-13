package org.ncp.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ncp.bookapi.Main;
import org.ncp.bookapi.entities.Book;
import org.ncp.bookapi.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
@ContextConfiguration(classes = Main.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        bookRepository.deleteAllInBatch();
    }

    @Test
    public void shouldSaveBookONDatabaseOnExecute() {

        var book = createBook("title","author");

        ResponseEntity<Book> bookCreatedResult = testRestTemplate.postForEntity("/api/books", book,
                Book.class);

        assertThat(bookCreatedResult.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(bookCreatedResult.getBody()).isNotNull();
        assertThat(bookCreatedResult.getBody().getTitle()).isEqualTo("title");
        assertThat(bookCreatedResult.getBody().getAuthor()).isEqualTo("author");

        var persisted = bookRepository.findAll();
        assertThat(persisted).hasSize(1);
        assertThat(persisted.get(0).getTitle()).isEqualTo("title");

    }

    @Test
    public void shouldReturnBooksByGivenAuthor() {

        var book1 = createBook("Title One", "Author A");
        var book2 = createBook("Title Two", "Author B");
        var book3 = createBook("Title Three", "Author A");

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        // Act: call GET /api/books/by-author?author=Author A
        ResponseEntity<Book[]> response = testRestTemplate.getForEntity(
                "/api/books/by-author?author={author}", Book[].class, "Author A");

        // Assert: verify HTTP status and response body
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Book[] booksByAuthor = response.getBody();
        assertThat(booksByAuthor).isNotNull();
        assertThat(booksByAuthor).hasSize(2);

        // Assert that all returned books have author = "Author A"
        for (Book book : booksByAuthor) {
            assertThat(book.getAuthor()).isEqualTo("Author A");
        }
    }

    @Test
    public void shouldReturnErrorIfAuthorIsBlank() {

        var book1 = createBook("Title One", "Author A");

        bookRepository.save(book1);

        // Act: call GET /api/books/by-author?author=Author A
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                "/api/books/by-author?author={author}", String.class, "");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Author is required");
    }


    private static Book createBook(String title, String author) {
        return new Book(title, author);
    }
}
