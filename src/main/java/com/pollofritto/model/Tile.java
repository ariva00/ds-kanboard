package com.pollofritto.model;

import java.io.Serializable;

/**
 * Class that represents a single tile in a kanboard
 *
 */
public abstract class Tile implements Serializable {

	private static final long serialVersionUID = 1;

	public enum TileType {Organizational, Informative}

	private long id;
	private static long instanceCounter = 0L;
	private String title;
	private String author;
	private TileType tileType;

	private Tile(String title, String author, TileType tileType, long id) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.tileType = tileType;
	}

	public Tile(String title, String author, TileType tileType) {
		this(title, author, tileType, instanceCounter++);
	}

	public Tile(Tile tile) {
		this(tile.getTitle(), tile.getAuthor(), tile.getTileType(), tile.getId());
	}

	public abstract Tile copy();

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public TileType getTileType() {
		return tileType;
	}

	public void setTileType(TileType tileType) {
		this.tileType = tileType;
	}

}
