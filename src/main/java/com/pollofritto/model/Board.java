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
	//TODO to be removed and placed in DataManager
	private long lastModified;
	private List<Column> columns = new ArrayList<Column>();
	
	public Board(long boardID, String title) {
		this.boardID = boardID;
		this.title = title;
	}
	
	public Board(Board board) {
		this(board.getBoardID(), board.getTitle());
		for(Column column : board.getColumns()) {
			this.columns.add(column.copy());
		}
	}
	
	public Board copy() {
		return new Board(this);
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
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}

	//TODO to be removed and placed in DataManager
	public void updateLastModified() {
		Date date = new Date();
		long timestamp = date.getTime();

		this.lastModified = timestamp;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

}