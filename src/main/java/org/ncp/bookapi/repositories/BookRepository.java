package org.ncp.bookapi.repositories;

import org.ncp.bookapi.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>
{
    List<Book> findByAuthor(String author);
    List<Book> findByTitleContaining(String keyword);
}
