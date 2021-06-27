package com.pollofritto.model;

public class ImageTile extends Tile {
    private String imageURI;

    public ImageTile(String title, String author, TileType tileType, String image) {
        super(title, author, tileType);
        this.imageURI = imageURI;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

}