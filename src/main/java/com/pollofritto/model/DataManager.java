package com.pollofritto.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pollofritto.model.Column.ColumnState;
import com.pollofritto.persistence.DataPersistenceManager;

// TODO: Handling on board, column or tile not found
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
			boards = new ArrayList<Board>();
			e.printStackTrace();
		}
	}
	
	private void syncStorage() {
		try {
			dataPersistenceManager.storeData(boards);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addBoard(Board board) {
		boards.add(board);
		syncStorage();
	}

	public List<Board> getBoards() {
		return boards;
	}

	public Board getBoard(long id) {
		for (Board b: boards) {
			if (b.getBoardID() == id)
				return b;
		}

		return null;
	}

	public List<Column> getColumns(long boardID) {
		return getBoard(boardID).getColumns();
	}

	public Column getColumn(long boardID, String columnTitle) {
		List<Column> columns = getBoard(boardID).getColumns();
		for (Column c: columns) {
			if (c.getTitle().equals(columnTitle))
				return c;
		}

		return null;
	}

	public List<Tile> getColumnTiles(long boardID, String columnTitle) {
		return getColumn(boardID, columnTitle).getTiles();
	}

	public Tile getTile(long boardID, String columnTitle, long tileID) {
		Column selectedColumn = getColumn(boardID, columnTitle);
		List<Tile> tiles = selectedColumn.getTiles();

		for (Tile t: tiles) {
			if (t.getId() == tileID) {
				return t;
			}
		}
		return null;
	}

	public void editColumn(long boardID, String columnTitle, Column editedColumn) {
		List<Column> columns = getColumns(boardID);

		for (Column c: columns) {
			if (c.getTitle() == columnTitle) {
				c.setTitle(editedColumn.getTitle());
				c.setState(editedColumn.getState());
				syncStorage();
			}
		}
		
	}

	public void editTile(long boardID, String columnTitle, long tileID, Tile editedTile) {
		Tile selectedTile = getTile(boardID, columnTitle, tileID);
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);

		for (Tile t: tiles) {
			if (t.getId() == tileID) {
				tiles.set(tiles.indexOf(selectedTile), editedTile);
				syncStorage();
			}
		}
	}

	// TODO: Add exception on column with non unique title
	public void addColumn(long boardID, Column column) {
		List<Column> columns = getColumns(boardID);

		for (Column c: columns) {
			if (column.getTitle().equals(c.getTitle()))
				// Needs to throws an exception
				return;
		}
		columns.add(column);
		syncStorage();
	}

	// TODO: Exception on archived column
	public void addTile(long boardID, String columnTitle, Tile tile) {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Column column = getColumn(boardID, columnTitle);

		if (column.getState().equals(ColumnState.active)) {
			tiles.add(tile);
			syncStorage();
		}
	}

	public void swapTiles(long boardID, String columnTitle, long tileID1, long tileID2) {
		Tile tile1 = getTile(boardID, columnTitle, tileID1);
		Tile tile2 = getTile(boardID, columnTitle, tileID2);
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		int indexTile1 = tiles.indexOf(tile1);
		int indexTile2 = tiles.indexOf(tile2);
		
		Collections.swap(tiles, indexTile1, indexTile2);
		syncStorage();
	}

	public void swapColumns(long boardID, String column1Title, String column2Title) {
		Column column1 = getColumn(boardID, column1Title);
		Column column2 = getColumn(boardID, column2Title);
		List<Column> columns = getColumns(boardID);
		int indexColumn1 = columns.indexOf(column1);
		int indexColumn2 = columns.indexOf(column2);

		Collections.swap(columns, indexColumn1, indexColumn2);
		syncStorage();
	}

	public void deleteTile(long boardID, String columnTitle, long tileID) {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Tile selectedTile = getTile(boardID, columnTitle, tileID);
		tiles.remove(selectedTile);
		syncStorage();
	}

	public void deleteColumn(long boardID, String columnTitle) {
		List<Column> columns = getColumns(boardID);
		Column selectedColumn = getColumn(boardID, columnTitle);

		if (selectedColumn.getState().equals(ColumnState.active)) {
			columns.remove(selectedColumn);
			syncStorage();
		}
	}

	public void deleteBoard(long boardID) {
		List<Board> boards = getBoards();
		Board selectedBoard = getBoard(boardID);
		boards.remove(selectedBoard);
		syncStorage();
	}

	public void moveTile(long boardID, String sourceColumnTitle, String destinationColumnTitle, long tileID) {
		List<Tile> sourceTiles = getColumnTiles(boardID, sourceColumnTitle);
		List<Tile> destinationTiles = getColumnTiles(boardID, destinationColumnTitle);
		Tile selectedTile = getTile(boardID, sourceColumnTitle, tileID);

		int sourceColumnTileIndex = sourceTiles.indexOf(selectedTile);
		sourceTiles.remove(selectedTile);
		destinationTiles.add(sourceColumnTileIndex, selectedTile);
		syncStorage();
	}

}
