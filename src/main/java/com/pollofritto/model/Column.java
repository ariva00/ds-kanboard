package com.pollofritto.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a single column in a kanboard
 *
 */
public class Column {

    public enum ColumnState {active, archived}

    private String title;
    private ColumnState state;
    private List<Tile> tiles = new ArrayList<Tile>();

    public Column(String title) {
        this.title = title;
        this.state = ColumnState.valueOf("active");
    }

    public Column(String title, ColumnState state) {
        this(title);
        this.state = state;
    }

    public Column(String title, ColumnState state, List<Tile> tiles) {
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

    public List<Tile> getTiles() {
        return  tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

}