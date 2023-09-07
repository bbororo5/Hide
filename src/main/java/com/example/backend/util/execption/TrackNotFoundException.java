package com.example.backend.util.execption;

import java.util.NoSuchElementException;

public class TrackNotFoundException extends NoSuchElementException {
	public TrackNotFoundException(String s) {
		super(s);
	}
}
