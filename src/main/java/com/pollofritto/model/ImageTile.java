package com.pollofritto.model;

public class ImageTile extends Tile {
    private String image;

    public ImageTile(String title, String author, TileType tileType, String image) {
        super(title, author, tileType);
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}