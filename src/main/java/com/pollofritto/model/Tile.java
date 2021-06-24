package com.pollofritto.model;

public abstract class Tile {

    int id;
    private String title;
    private String author;
    public enum TileType {Organizational, Informative}
    private TileType tileType;

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