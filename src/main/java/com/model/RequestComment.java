package com.model;

import java.time.LocalDateTime;

/** A comment left on a relief request. */
public class RequestComment {
    private User author;
    private String text;
    private LocalDateTime timeStamp;

    /** @param author who wrote it @param text comment text */
    public RequestComment(User author, String text) {
        this.author = author;
        this.text = text;
        this.timeStamp = LocalDateTime.now();
    }

    /** @param author who wrote it @param text comment text @param timeStamp when it was posted */
    public RequestComment(User author, String text, LocalDateTime timeStamp) {
        this.author = author;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    /** @return author */
    public User getAuthor() {
        return author;
    }

    /** @return text */
    public String getText() {
        return text;
    }

    /** @return when it was posted */
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    /** @return "username: text" */
    @Override
    public String toString() {
        return (author == null ? "Unknown" : author.getUserName()) + ": " + text;
    }
}