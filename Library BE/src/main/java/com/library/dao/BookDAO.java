package com.library.dao;

import com.library.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public BookDAO(String url, String user, String password) {
        this.dbUrl = url;
        this.dbUser = user;
        this.dbPassword = password;
        initDatabase();
    }

    private void initDatabase() {
        String createAuthors = "CREATE TABLE IF NOT EXISTS authors ("
                + "id SERIAL PRIMARY KEY, "
                + "name VARCHAR(255) UNIQUE NOT NULL)";

        String createBooks = "CREATE TABLE IF NOT EXISTS books ("
                + "id SERIAL PRIMARY KEY, "
                + "author_id INT NOT NULL, "
                + "title VARCHAR(255) NOT NULL, "
                + "year INT NOT NULL, "
                + "copies INT NOT NULL, "
                + "FOREIGN KEY (author_id) REFERENCES authors(id))";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createAuthors);
            stmt.execute(createBooks);
        } catch (SQLException e) {
            System.err.println("Database init error: " + e.getMessage());
        }
    }

    private int getOrCreateAuthor(Connection conn, String authorName) throws SQLException {
        String findAuthor = "SELECT id FROM authors WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(findAuthor)) {
            pstmt.setString(1, authorName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }

        String insertAuthor = "INSERT INTO authors (name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertAuthor, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, authorName);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create author");
    }

    public void addBook(String author, String title, int year, int copies) throws SQLException {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            int authorId = getOrCreateAuthor(conn, author);
            String insertBook = "INSERT INTO books (author_id, title, year, copies) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertBook)) {
                pstmt.setInt(1, authorId);
                pstmt.setString(2, title);
                pstmt.setInt(3, year);
                pstmt.setInt(4, copies);
                pstmt.executeUpdate();
            }
        }
    }

    public boolean updateBookCopies(String author, String title, int addedCopies) throws SQLException {
        String sql = "UPDATE books SET copies = copies + ? WHERE id IN (" +
                "SELECT b.id FROM books b JOIN authors a ON b.author_id = a.id " +
                "WHERE a.name = ? AND b.title = ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addedCopies);
            pstmt.setString(2, author);
            pstmt.setString(3, title);
            return pstmt.executeUpdate() > 0;
        }
    }

    public void writeOffBook(int id, int amount) throws SQLException {
        String updateSql = "UPDATE books SET copies = copies - ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setInt(1, amount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }

        String deleteSql = "DELETE FROM books WHERE copies <= 0";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteSql);
        }
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.id, a.name AS author, b.title, b.year, b.copies FROM books b INNER JOIN authors a ON b.author_id = a.id";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("author"),
                        rs.getString("title"),
                        rs.getInt("year"),
                        rs.getInt("copies")
                ));
            }
        }
        return books;
    }

    public List<String> getAuthorsByPrefixAndTitle(String prefix, String title) throws SQLException {
        List<String> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT a.name FROM authors a ");
        if (title != null && !title.isBlank()) {
            sql.append("INNER JOIN books b ON a.id = b.author_id WHERE b.title = ? AND a.name LIKE ?");
        } else {
            sql.append("WHERE a.name LIKE ?");
        }
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (title != null && !title.isBlank()) {
                pstmt.setString(1, title);
                pstmt.setString(2, prefix + "%");
            } else {
                pstmt.setString(1, prefix + "%");
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) result.add(rs.getString(1));
            }
        }
        return result;
    }

    public List<String> getTitlesByPrefixAndAuthor(String prefix, String author) throws SQLException {
        List<String> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT b.title FROM books b ");
        if (author != null && !author.isBlank()) {
            sql.append("INNER JOIN authors a ON b.author_id = a.id WHERE a.name = ? AND b.title LIKE ?");
        } else {
            sql.append("WHERE b.title LIKE ?");
        }
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            if (author != null && !author.isBlank()) {
                pstmt.setString(1, author);
                pstmt.setString(2, prefix + "%");
            } else {
                pstmt.setString(1, prefix + "%");
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) result.add(rs.getString(1));
            }
        }
        return result;
    }
}