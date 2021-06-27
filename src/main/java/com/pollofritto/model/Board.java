package com.pollofritto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Class that represents a single kanboard
 *
 */
public class Board {

	private long boardID;
	private String title;
	private static long instanceCounter = 0L;
	long lastModified;
	private List<Column> columns = new ArrayList<Column>();


	public Board(String title) {
		this(instanceCounter++, title);
	}

	public Board(long boardID, String title) {
		this.boardID = boardID;
		this.title = title;
		updateLastModified();
	}


	public List<Column> getColumns() {
		return columns;
	}

	public long getLastModified() {
		return lastModified;
	}

	public long getBoardID() {
		return boardID;
	}

	public String getBoardTitle() {
		return title;
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

	public void setBoardTitle(String title) {
		this.title = title;
	}

}