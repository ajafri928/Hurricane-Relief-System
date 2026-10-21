package com.model;

import java.time.LocalDateTime;

public class RequestComment {
    private User author;
    private String text;
    private LocalDateTime timeStamp;

    public RequestComment(User author, String text){
        this.author = author;
        this.text = text;
        this.timeStamp = LocalDateTime.now();

    }
    public RequestComment(User author, String text, LocalDateTime timeStamp){
        this.author = author;
        this.text = text;
        this.timeStamp = timeStamp;
    }
    public User getAuthor() { return author; }
    public String getText() { return text; }
    public LocalDateTime getTimeStamp() { return timeStamp; }

    @Override
    public String toString() {
        return author.getUserName() + ": " + text;
    }
}
