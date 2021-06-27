package com.pollofritto.model;

public class TextTile extends Tile {

	private String text;

	public TextTile(String title, String author, TileType tileType, String text) {
		super(title, author, tileType);
		this.text = text;
	}

	public TextTile(TextTile tile) {
		super(tile);
		this.text = tile.getText();
	}

	@Override
	public Tile copy() {
		return new TextTile(this);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
