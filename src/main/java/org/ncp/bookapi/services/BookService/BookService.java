package org.ncp.bookapi.services.BookService;

import org.ncp.bookapi.entities.Book;
import org.ncp.bookapi.exceptions.BookNotFoundException;
import org.ncp.bookapi.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAllBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    public List<Book> getAllBooksByKeywordInTitle(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    public void deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new BookNotFoundException(id);
        }
    }
}
