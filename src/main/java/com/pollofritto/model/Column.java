package com.pollofritto.model;

import java.util.HashMap;

public class Column {

    String title;
    enum State {active, archived}
    HashMap<Integer, Tile> tiles = new HashMap<Integer, Tile>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<Integer, Tile> getTiles() {
        return tiles;
    }

    public void setTiles(HashMap<Integer, Tile> tiles) {
        this.tiles = tiles;
    }

}