package com.pollofritto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Class that represents a single kanboard
 *
 */
public class Board {

	long boardID;
	long lastModified;
	private List<Column> columns = new ArrayList<Column>();

	public List<Column> getColumns() {
		return columns;
	}

	public long getLastModified() {
		return lastModified;
	}

	public long getBoardID() {
		return boardID;
	}

	public void setBoardID(long boardID) {
		this.boardID = boardID;
	}

	public void updateLastModified() {
		Date date = new Date();
		long timestamp = date.getTime();

		this.lastModified = timestamp;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

}