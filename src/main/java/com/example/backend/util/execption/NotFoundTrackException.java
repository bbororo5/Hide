package com.example.backend.util.execption;

import java.util.NoSuchElementException;

public class NotFoundTrackException extends NoSuchElementException {
	public NotFoundTrackException(String s) {
		super(s);
	}
}
