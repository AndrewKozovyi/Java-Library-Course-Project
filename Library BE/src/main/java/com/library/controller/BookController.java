package com.library.controller;

import com.library.dao.BookDAO;
import com.library.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:4200")
public class BookController {

    private final BookDAO bookDAO;

    public BookController(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            return ResponseEntity.ok(bookDAO.getAllBooks());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody Map<String, String> payload) {
        try {
            bookDAO.addBook(
                    payload.get("author"),
                    payload.get("title"),
                    Integer.parseInt(payload.get("year")),
                    Integer.parseInt(payload.get("copies"))
            );
            return ResponseEntity.ok("Success");
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/return")
    public ResponseEntity<String> returnBook(@RequestBody Map<String, String> payload) {
        try {
            boolean success = bookDAO.updateBookCopies(
                    payload.get("author"),
                    payload.get("title"),
                    Integer.parseInt(payload.get("copies"))
            );
            if (success) {
                return ResponseEntity.ok("Success");
            } else {
                return ResponseEntity.badRequest().body("Not found");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/writeoff")
    public ResponseEntity<String> writeOffBook(@RequestBody Map<String, Integer> payload) {
        try {
            bookDAO.writeOffBook(
                    payload.get("id"),
                    payload.get("amount")
            );
            return ResponseEntity.ok("Success");
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/authors")
    public ResponseEntity<List<String>> getAuthors(@RequestParam String prefix, @RequestParam(required = false) String title) {
        try {
            return ResponseEntity.ok(bookDAO.getAuthorsByPrefixAndTitle(prefix, title));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/titles")
    public ResponseEntity<List<String>> getTitles(@RequestParam String prefix, @RequestParam(required = false) String author) {
        try {
            return ResponseEntity.ok(bookDAO.getTitlesByPrefixAndAuthor(prefix, author));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}