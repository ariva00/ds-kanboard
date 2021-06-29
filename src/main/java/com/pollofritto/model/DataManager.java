package com.pollofritto.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pollofritto.model.Column.ColumnState;
import com.pollofritto.model.exceptions.*;
import com.pollofritto.persistence.DataPersistenceManager;

public class DataManager {

	private DataPersistenceManager dataPersistenceManager;
	
	private List<Board> boards;

	public DataManager(DataPersistenceManager persistence) {
		this.dataPersistenceManager = persistence;
		try {
			Object o = dataPersistenceManager.getData();
			if(o instanceof ArrayList)
				boards = (ArrayList<Board>) o;
			else throw new IOException();
		} catch (ClassNotFoundException|IOException e) {
			boards = new ArrayList<>();
			e.printStackTrace();
		}
	}
	
	private void syncStorage() {
		try {
			dataPersistenceManager.storeData(boards);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addBoard(Board board) {
		boards.add(board);
		syncStorage();
	}

	private List<Board> getBoards() {
		return boards;
	}

	public List<Board> getBoardsCopy() {
		List<Board> boardsCopy = new ArrayList<Board>();
		for (Board b: getBoards()) {
			boardsCopy.add(b.copy());
		}
		return boardsCopy;
	}

	private Board getBoard(long id) throws BoardNotFoundException {
		for (Board b: boards) {
			if (b.getId() == id)
				return b;
		}
		throw new BoardNotFoundException("No board found with id: " + id);
	}

	public Board getBoardCopy(long id) throws  BoardNotFoundException {
		for (Board b: boards) {
			if (b.getId() == id)
				return b.copy();
		}
		throw new BoardNotFoundException("No board found with id: " + id);
	}

	private List<Column> getColumns(long boardID) throws BoardNotFoundException {
		return getBoard(boardID).getColumns();
	}

	public List<Column> getColumnsCopy(long boardID) throws BoardNotFoundException {
		return getBoard(boardID).copy().getColumns();
	}

	private Column getColumn(long boardID, String columnTitle) throws  BoardNotFoundException, ColumnNotFoundException {
		List<Column> columns = getBoard(boardID).getColumns();
		for (Column c: columns) {
			if (c.getTitle().equals(columnTitle))
				return c;
		}
		throw new ColumnNotFoundException("No column found with title \"" + columnTitle + "\" in board " + boardID);
	}

	public Column getColumnCopy(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException {
		List<Column> columns = getBoard(boardID).getColumns();
		for (Column c: columns) {
			if (c.getTitle().equals(columnTitle))
				return c.copy();
		}
		throw new ColumnNotFoundException("No column found with title \"" + columnTitle + "\" in board " + boardID);
	}

	private List<Tile> getColumnTiles(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException {
		return getColumn(boardID, columnTitle).getTiles();
	}

	public List<Tile> getColumnTilesCopy(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException {
		return getColumn(boardID, columnTitle).copy().getTiles();
	}

	private Tile getTile(long boardID, String columnTitle, long tileID) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException {
		Column selectedColumn = getColumn(boardID, columnTitle);
		List<Tile> tiles = selectedColumn.getTiles();

		for (Tile t: tiles) {
			if (t.getId() == tileID) {
				return t;
			}
		}
		throw new TileNotFoundException("No tile found with id \"" + tileID + "\" in column \"" + columnTitle + "\" of \"" + boardID + "\" board");
	}

	public Tile getTileCopy(long boardID, String columnTitle, long tileID) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException {
		Column selectedColumn = getColumn(boardID, columnTitle);
		List<Tile> tiles = selectedColumn.getTiles();

		for (Tile t: tiles) {
			if (t.getId() == tileID) {
				return t.copy();
			}
		}
		throw new TileNotFoundException("No tile found with id \"" + tileID + "\" in column \"" + columnTitle + "\" of \"" + boardID + "\" board");
	}

	public void editColumn(long boardID, String columnTitle, Column editedColumn) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		List<Column> columns = getColumns(boardID);

		for (Column c: columns) {
			if (c.getTitle().equals(columnTitle)) {
				if (c.getState().equals(ColumnState.archived) && (editedColumn.getState() == null || editedColumn.getState().equals(ColumnState.archived)))
					throw new InvalidRequestException("Cannot edit archived column");
				if (editedColumn.getTitle() != null)
					c.setTitle(editedColumn.getTitle());
				if (editedColumn.getState() != null)
					c.setState(editedColumn.getState());
				if (editedColumn.getColor() != null)
					c.setColor(editedColumn.getColor());
				syncStorage();
				return;
			}
		}
		throw new ColumnNotFoundException("No column found with title \"" + columnTitle + "\" in board " + boardID);
	}

	public void editTile(long boardID, String columnTitle, long tileID, Tile editedTile) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException, InvalidRequestException {
		Tile selectedTile = getTile(boardID, columnTitle, tileID);
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Column column = getColumn(boardID, columnTitle);

		if (column.getState().equals(ColumnState.archived)) {
			throw new InvalidRequestException("Cannot edit tile from archived board");
		}

		if (editedTile.getTitle() != null)
			selectedTile.setTitle(editedTile.getTitle());
		if (editedTile.getAuthor() != null)
			selectedTile.setAuthor(editedTile.getAuthor());
		if (editedTile.getColor() != null)
			selectedTile.setColor(editedTile.getColor());

		throw new TileNotFoundException("No tile found with id \"" + tileID + "\" in column \"" + columnTitle + "\" of \"" + boardID + "\" board");
	}

	public void addColumn(long boardID, Column column) throws BoardNotFoundException, InvalidRequestException {
		List<Column> columns = getColumns(boardID);

		for (Column c: columns) {
			if (column.getTitle().equals(c.getTitle()))
				throw new InvalidRequestException("A column with title \"" + column.getTitle() + "\" is already present in this board");
		}
		columns.add(column);
		syncStorage();
	}

	public void addTile(long boardID, String columnTitle, Tile tile) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Column column = getColumn(boardID, columnTitle);

		for (Tile t: tiles) {
			if (t.getId() == tile.getId())
				throw new InvalidRequestException("System error: a tile with id " + tile.getId() + "is already present in this board");
		}

		if (column.getState().equals(ColumnState.active)) {
			tiles.add(tile);
			syncStorage();
		} else {
			throw new InvalidRequestException("Cannot add a tile in an archived column");
		}
	}

	public void swapTiles(long boardID, String columnTitle, long tileID1, long tileID2) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException, InvalidRequestException {
		Tile tile1 = getTile(boardID, columnTitle, tileID1);
		Tile tile2 = getTile(boardID, columnTitle, tileID2);
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Column column = getColumn(boardID, columnTitle);
		int indexTile1 = tiles.indexOf(tile1);
		int indexTile2 = tiles.indexOf(tile2);

		if (column.getState().equals(ColumnState.archived))
			throw new InvalidRequestException("Cannot swap tiles of an archived column");

		Collections.swap(tiles, indexTile1, indexTile2);
		syncStorage();
	}

	public void swapColumns(long boardID, String column1Title, String column2Title) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		Column column1 = getColumn(boardID, column1Title);
		Column column2 = getColumn(boardID, column2Title);
		List<Column> columns = getColumns(boardID);
		int indexColumn1 = columns.indexOf(column1);
		int indexColumn2 = columns.indexOf(column2);

		if (column1.getState().equals(ColumnState.archived) || column2.getState().equals(ColumnState.archived))
			throw new InvalidRequestException("Cannot swap archived columns");

		Collections.swap(columns, indexColumn1, indexColumn2);
		syncStorage();
	}

	public void deleteTile(long boardID, String columnTitle, long tileID) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException, InvalidRequestException {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Tile selectedTile = getTile(boardID, columnTitle, tileID);
		Column column = getColumn(boardID, columnTitle);

		if (column.getState().equals(ColumnState.active)) {
			tiles.remove(selectedTile);
			syncStorage();
		} else {
			throw new InvalidRequestException("Cannot delete tile of an archived column");
		}
	}

	public void deleteColumn(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		List<Column> columns = getColumns(boardID);
		Column selectedColumn = getColumn(boardID, columnTitle);

		if (selectedColumn.getState().equals(ColumnState.active)) {
			columns.remove(selectedColumn);
			syncStorage();
		} else {
			throw new InvalidRequestException("Cannot delete an archived column");
		}
	}

	public void moveTile(long boardID, String sourceColumnTitle, String destinationColumnTitle, long tileID) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException, InvalidRequestException {
		List<Tile> sourceTiles = getColumnTiles(boardID, sourceColumnTitle);
		List<Tile> destinationTiles = getColumnTiles(boardID, destinationColumnTitle);
		Tile selectedTile = getTile(boardID, sourceColumnTitle, tileID);
		Column sourceColumn = getColumn(boardID, sourceColumnTitle);
		Column destinationColumn = getColumn(boardID, destinationColumnTitle);

		if (sourceColumn.getState().equals(ColumnState.archived) || destinationColumn.getState().equals(ColumnState.archived))
			throw new InvalidRequestException("Cannot alter the state of an archived column");

		int sourceColumnTileIndex = sourceTiles.indexOf(selectedTile);
		sourceTiles.remove(selectedTile);
		destinationTiles.add(sourceColumnTileIndex, selectedTile);
		syncStorage();
	}

}
