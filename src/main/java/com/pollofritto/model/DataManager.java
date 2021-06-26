package com.pollofritto.model;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

	List<Board> boards = new ArrayList<Board>();

	public Board getBoard(long id) throws CloneNotSupportedException {
		for (Board b: boards) {
			if (b.getBoardID() == id) {
				return b;
			}
		}
		return null;
	}

	public List<Column> getColumns(Board board) {
		return board.getColumns();
	}

	// TODO: Handling on column not found
	public Column getColumn(Board board, String columnTitle) {
		List<Column> columns = board.getColumns();
		for (Column c: columns) {
			if (c.getTitle().equals(columnTitle)) {
				return c;
			}
		}
		return null;
	}

	// TODO: Handling on tile not found
	public Tile getTile(Board board, String columnTitle, long tileID) {
		Column selectedColumn = getColumn(board, columnTitle);
		List<Tile> tiles = selectedColumn.getTiles();

		for (Tile t: tiles) {
			if (t.getId() == tileID) {
				return t;
			}
		}
		return null;
	}

	public void editColumn(long boardID, String columnTitle, Column column) {}

	public void editTile(long boardID, String columnTitle, long tileID, Tile tile) {}

	public void addColumn(long boardID, Column column) {}

	public void addTile(long boardID, String columnTitle, Tile tile) {}

	public void swapTiles(long boardID, String columnTitle, Tile tile1, Tile tile2) {}

	public void moveTile(long boardID, String sourceColumnTitle, String destColumnTitle, long tileID) {}

	public void swapColumns(long boardID, String column1Title, String column2Title) {}

	public long lastModified(long boardID) {
		return -1;
	}

}
