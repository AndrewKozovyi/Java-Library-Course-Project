package com.library.model;

public class Book {
    private int id;
    private String author;
    private String title;
    private int year;
    private int copies;

    public Book(int id, String author, String title, int year, int copies) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.year = year;
        this.copies = copies;
    }

    public int getId() { return id; }
    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public int getYear() { return year; }
    public int getCopies() { return copies; }
}