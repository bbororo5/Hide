package com.example.backend.util.execption;

public class NotFoundTrackException extends RuntimeException{
    public NotFoundTrackException(String s) {
        super(s);
    }
}
