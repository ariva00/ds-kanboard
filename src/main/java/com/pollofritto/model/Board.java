package com.pollofritto.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Class that represents a single kanban board.
 *
 */
public class Board implements Serializable{

	private static final long serialVersionUID = 2L;
	private long id;
	private String title;
	private List<Column> columns = new ArrayList<Column>();

	/**
	 * Creates a new instance of {@link Board}.
	 * @param id id of the new {@link Board}
	 * @param title title of the new board
	 */
	public Board(long id, String title) {
		this.id = id;
		this.title = title;
	}
	
	/**
	 * Copy constructor of {@link Board}.<br>
	 * Creates a deep copy of the {@link Board}.
	 * @param board
	 */
	public Board(Board board) {
		this(board.getId(), board.getTitle());
		for(Column column : board.getColumns()) {
			this.columns.add(column.copy());
		}
	}

	/**
	 * Returns a deep copy of the {@link Board}.
	 * @return
	 */
	public Board copy() {
		return new Board(this);
	}
	
	/**
	 * Returns the {@link List} of columns of the {@link Board}
	 * @return
	 */
	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * Returns the id of the {@link Board}
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the title of the {@link Board}
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Returns the title of the {@link Board}
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the {@link List} of columns of the {@link Board}
	 * @param columns
	 */
	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

}
