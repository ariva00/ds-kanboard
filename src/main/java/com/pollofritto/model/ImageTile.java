package com.pollofritto.model;

public class ImageTile extends Tile {


	private static final long serialVersionUID = 2L;
	private String imageURI;

	public ImageTile(String title, String author, TileType tileType, String color, String imageURI) {
		super(title, author, tileType, color);
		this.imageURI = imageURI;
	}

	public ImageTile(ImageTile tile) {
		super(tile);
		this.imageURI = tile.getImageURI();
	}

	@Override
	public Tile copy() {
		return new ImageTile(this);
	}

	public String getImageURI() {
		return imageURI;
	}

	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}

}
