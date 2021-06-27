package com.pollofritto.model;

import java.util.ArrayList;
import java.util.List;

// TODO: Handling on board, column or tile not found
public class DataManager {

	private List<Board> boards = new ArrayList<Board>();

	public void addBoard(Board board) {
		boards.add(board);
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
			}
		}
	}

	public void editTile(long boardID, String columnTitle, long tileID, Tile editedTile) {
		Tile selectedTile = getTile(boardID, columnTitle, tileID);
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);

		for (Tile t: tiles) {
			if (t.getId() == tileID) {
				tiles.set(tiles.indexOf(selectedTile), editedTile);
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
	}

	// TODO: Exception on archived column
	public void addTile(long boardID, String columnTitle, Tile tile) {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Column column = getColumn(boardID, columnTitle);

		if (column.getState().equals("active"))
			tiles.add(tile);
	}

	public void swapTiles(long boardID, String columnTitle, long tileID1, long tileID2) {
		Tile tile1 = getTile(boardID, columnTitle, tileID1);
		Tile tile2 = getTile(boardID, columnTitle, tileID2);
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		int indexTile1 = tiles.indexOf(tile1);
		int indexTile2 = tiles.indexOf(tile2);

		Tile tmpTile = tile1;
		tiles.set(indexTile1, tile2);
		tiles.set(indexTile2, tmpTile);
	}

	public void swapColumns(long boardID, String column1Title, String column2Title) {
		Column column1 = getColumn(boardID, column1Title);
		Column column2 = getColumn(boardID, column2Title);
		List<Column> columns = getColumns(boardID);
		int indexColumn1 = columns.indexOf(column1);
		int indexColumn2 = columns.indexOf(column2);

		Column tmpColumn = column1;
		columns.set(indexColumn1, column2);
		columns.set(indexColumn2, tmpColumn);
	}

	public void deleteTile(long boardID, String columnTitle, long tileID) {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Tile selectedTile = getTile(boardID, columnTitle, tileID);
		tiles.remove(selectedTile);
	}

	public void deleteColumn(long boardID, String columnTitle) {
		List<Column> columns = getColumns(boardID);
		Column selectedColumn = getColumn(boardID, columnTitle);

		if (selectedColumn.getState().equals("active"))
			columns.remove(selectedColumn);
	}

	public void deleteBoard(long boardID) {
		List<Board> boards = getBoards();
		Board selectedBoard = getBoard(boardID);
		boards.remove(selectedBoard);
	}

	public void moveTile(long boardID, String sourceColumnTitle, String destinationColumnTitle, long tileID) {
		Column sourceColumn = getColumn(boardID, sourceColumnTitle);
		Column destinationColumn = getColumn(boardID, destinationColumnTitle);
		List<Tile> sourceTiles = getColumnTiles(boardID, sourceColumnTitle);
		List<Tile> destinationTiles = getColumnTiles(boardID, destinationColumnTitle);
		Tile selectedTile = getTile(boardID, sourceColumnTitle, tileID);

		int sourceColumnTileIndex = sourceTiles.indexOf(selectedTile);
		sourceTiles.remove(selectedTile);
		destinationTiles.add(sourceColumnTileIndex, selectedTile);
	}

	public long lastModified(long boardID) {
		Board board = getBoard(boardID);
		board.updateLastModified();
		return board.getLastModified();
	}

}
