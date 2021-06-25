package com.pollofritto.model;

public class FileTile extends Tile {

	private String fileURI;
	
	public FileTile(String title, String author, TileType tileType, String fileURI) {
		super(title, author, tileType);
		this.fileURI = fileURI;
	}
	
	public String getFileURI() {
		return fileURI;
	}
	
	public void setFileURI(String fileURI) {
		this.fileURI = fileURI;
	}

}
