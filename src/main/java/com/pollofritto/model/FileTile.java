package com.pollofritto.model;

public class FileTile extends Tile {

	private static final long serialVersionUID = 2L;
	private String fileURI;
	
	public FileTile(String title, String author, TileType tileType, String color, String fileURI) {
		super(title, author, tileType, color);
		this.fileURI = fileURI;
	}

	public FileTile(String title, String author, TileType tileType, String color, String fileURI, long id) {
		super(title, author, tileType, color, id);
		this.fileURI = fileURI;
	}

	public FileTile(FileTile tile) {
		super(tile);
		this.fileURI = tile.getFileURI();
	}
	
	@Override
	public Tile copy() {
		return new FileTile(this);
	}
	
	public String getFileURI() {
		return fileURI;
	}
	
	public void setFileURI(String fileURI) {
		this.fileURI = fileURI;
	}

}
