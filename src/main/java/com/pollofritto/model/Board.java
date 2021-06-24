package com.pollofritto.model;

import java.util.HashMap;

public class Board {

	private HashMap<String, Column> columns = new HashMap<String, Column>();

	public void setColumns(HashMap<String, Column> columns) {
		this.columns = columns;
	}

	public HashMap<String, Column> getColumns() {
		return columns;
	}

}