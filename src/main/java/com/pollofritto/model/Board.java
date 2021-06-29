package com.pollofritto.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a single kanboard
 *
 */
public class Board implements Serializable{

	private static final long serialVersionUID = 2L;
	private long id;
	private String title;
	private static long instanceCounter = 0L;
	private List<Column> columns = new ArrayList<Column>();

	public Board(String title) {
		this(instanceCounter++, title);
	}

	public Board(long id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public Board(Board board) {
		this(board.getId(), board.getTitle());
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

	public long getId() {
		return id;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

	public void setBoardTitle(String title) {
		this.title = title;
	}

}
