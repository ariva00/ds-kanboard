package com.pollofritto.model;

/**
 * Class that represents a single tile in a kanboard
 *
 */
public abstract class Tile {

    public enum TileType {Organizational, Informative}

    private long id;
    private static long instanceCounter = 0L;
    private String title;
    private String author;
    private TileType tileType;


    public Tile(String title, String author, TileType tileType) {
        this.id = instanceCounter++;
        this.title = title;
        this.author = author;
        this.tileType = tileType;
    }

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