package com.example.backend.util.execption;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {
	public UserNotFoundException(String s) {
		super(s);
	}
}
