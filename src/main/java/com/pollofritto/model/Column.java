package com.pollofritto.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Class that represents a single column in a kanban board.
 *
 */
public class Column implements Serializable {

	private static final long serialVersionUID = 3L;

	public enum ColumnState {active, archived}

	private String title;
	private ColumnState state;
	private List<Tile> tiles = new ArrayList<Tile>();
	private String color;

	/**
	 * Creates an instance of {@link Column} in state {@link ColumnState} "active" and with null color attribute.
	 * @param title title of the {@link Column}
	 */
	public Column(String title) {
		this.title = title;
		this.state = ColumnState.active;
	}

	/**
	 * Creates an instance of {@link Column} in state {@link ColumnState} "active".
	 * @param title title of the {@link Column}
	 * @param color color {@link String} in format "#rrggbbaa" representing the color of the {@link Column}
	 */
	public Column(String title, String color) {
		this(title);
		this.color = color;
	}

	/**
	 * Creates an instance of {@link Column}.
	 * @param title title of the {@link Column}
	 * @param state state of the {@link Column}
	 * @param color color {@link String} in format "#rrggbbaa" representing the color of the {@link Column}
	 */
	public Column(String title, ColumnState state, String color) {
		this(title, color);
		this.state = state;
	}

	/**
	 * Creates an instance of {@link Column} and sets the list of tiles.
	 * @param title title of the {@link Column}
	 * @param state state of the {@link Column}
	 * @param color color {@link String} in format "#rrggbbaa" representing the color of the {@link Column}
	 * @param tiles {@link List} of tiles of the {@link Column}
	 */
	public Column(String title, ColumnState state, String color, List<Tile> tiles) {
		this(title, state, color);
		this.tiles = tiles;
	}

	/**
	 * Copy constructor of {@link Column}.<br>
	 * Creates a deep copy of the {@link Column}
	 * @param column
	 */
	public Column(Column column) {
		this(column.getTitle(), column.getState(), column.getColor());
		for(Tile tile : column.getTiles())
			this.tiles.add(tile.copy());
	}

	/**
	 * Returns the {@link String} in format "#rrggbbaa" representing the color of the {@link Column}.
	 * @return
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Sets the string representing the color of {@link Column} (should be in format "#rrggbbaa").
	 * @param color
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Creates a deep copy of the {@link Column}.
	 * @return
	 */
	public Column copy() {
		return new Column(this);
	}

	/**
	 * Returns the title of the {@link Column}.
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the tile of the {@link Column}.
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the {@link ColumnState} of the {@link Column}.
	 * @return
	 */
	public ColumnState getState() {
		return state;
	}

	/**
	 * Sets the {@link ColumnState} of the {@link Column}.
	 * @param state
	 */
	public void setState(ColumnState state) {
		this.state = state;
	}

	/**
	 * Returns the {@link List} of tiles of the {@link Board}
	 * @return
	 */
	public List<Tile> getTiles() {
		return  tiles;
	}

	/**
	 * Sets the {@link List} of tiles of the {@link Board}
	 * @param tiles
	 */
	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

}
