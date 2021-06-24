package com.pollofritto.model;

import java.util.HashMap;

/**
 * Class that represents a single column in a kanboard
 *
 */
public class Column {

    public enum ColumnState {active, archived}

    private String title;
    private ColumnState state;
    private HashMap<Integer, Tile> tiles = new HashMap<Integer, Tile>();

    public Column(String title, ColumnState state) {
        this.title = title;
        this.state = state;
    }

    public Column(String title, ColumnState state, HashMap<Integer, Tile> tiles) {
        this(title, state);
        this.tiles = tiles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ColumnState getState() {
        return state;
    }

    public void setState(ColumnState state) {
        this.state = state;
    }

    public HashMap<Integer, Tile> getTiles() {
        return tiles;
    }

    public void setTiles(HashMap<Integer, Tile> tiles) {
        this.tiles = tiles;
    }

}