package com.example.backend.util.execption;

import java.util.NoSuchElementException;

public class DataNotFoundException extends NoSuchElementException {
    public DataNotFoundException(String s) {
        super(s);
    }
}
