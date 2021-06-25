package com.pollofritto.persistence;

import com.pollofritto.model.Board;
import com.pollofritto.model.Column;
import com.pollofritto.model.Tile;

import java.util.List;

public interface DataPersistenceFacade {

	public Board getBoard();
	public List<Column> getColumns();
	public Column getColumn(String columnTitle);
	public List<Tile> getTiles(String columnTitle);
	public Tile getTile(String columnTitle, int tileID);

	public void editColumn(Column column);
	public void editTile(Tile tile);

	public void addColumn(Column column);
	public void addTile(Column column, Tile tile);

	public void swapTiles(Tile tile1, Tile tile2);
	public void moveTile(Column column, Tile tile);
	public void swapColumns(Column column1, Column column2);

	public long lastModified();

}