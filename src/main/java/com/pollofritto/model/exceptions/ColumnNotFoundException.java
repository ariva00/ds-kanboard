package com.pollofritto.model.exceptions;

public class ColumnNotFoundException extends ObjectNotFoundException {

	public ColumnNotFoundException() {
		super();
	}

	public ColumnNotFoundException(String message) {
		super(message);
	}

}
