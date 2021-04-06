package com.terraformersmc.vistas.resource;

@SuppressWarnings("serial")
public class InvalidPanoramaException extends RuntimeException {

	public InvalidPanoramaException(String string) {
		super(string);
	}

	public InvalidPanoramaException(String string, Throwable throwable) {
		super(string, throwable);
	}
}
