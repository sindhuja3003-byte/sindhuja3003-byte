package com.example.demo.book;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookRepository repo;
    public BookController(BookRepository repo) { this.repo = repo; }

    @GetMapping("/health")
    public String health() { return "OK"; }

    @GetMapping("/books")
    @PreAuthorize("hasAuthority('SCOPE_books.read')")
    public List<Book> list() {
        return repo.findAll();
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_books.write')")
    public Book create(@RequestBody Book b) {
        return repo.save(b);
    }
}
