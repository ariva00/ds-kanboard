package com.pollofritto.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a single kanboard
 *
 */
public class Board {

	private  List<Column> columns = new ArrayList<Column>();

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Column> getColumns() {
		return columns;
	}

}