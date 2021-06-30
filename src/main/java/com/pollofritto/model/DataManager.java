package com.pollofritto.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pollofritto.model.Column.ColumnState;
import com.pollofritto.model.exceptions.*;
import com.pollofritto.persistence.DataPersistenceManager;

public class DataManager {

	private DataPersistenceManager dataPersistenceManager;
	
	private List<Board> boards;
	private Map<Long, Date> boardsLastModified;
	private Date lastModified;
	
	private long lastTileID;
	private long lastBoardID;

	@SuppressWarnings("unchecked")
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
		updateLastIDs();
		
		boardsLastModified = new HashMap<Long, Date>();
		Date creation = new Date();
		lastModified = creation;
		for(Board board : boards) {
			boardsLastModified.put(board.getId(), creation);
		}
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
	public Date getLastModified(long boardID) {
		return boardsLastModified.get(boardID);
	}
	
	private Date updateLastModified(long boardID) {
		Date now = new Date();
		boardsLastModified.put(boardID, now);
		return now;
	}
	
	private void updateLastIDs() {
		lastBoardID = 0;
		lastTileID = 0;
		for(Board board : boards) {
			if(board.getId() > lastBoardID)
				lastBoardID = board.getId();
			for(Column column : board.getColumns())
				for(Tile tile : column.getTiles())
					if(tile.getId() > lastTileID)
						lastTileID = tile.getId();
		}
	}
	
	private void syncStorage() {
		try {
			dataPersistenceManager.storeData(boards);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Date update(long boardID) {
		syncStorage();
		return updateLastModified(boardID);
	}
	
	public synchronized long generateTileID() {
		return ++lastTileID;
	}
	
	public synchronized long generateBoardID() {
		return ++lastBoardID;
	}
	
	public synchronized void addBoard(Board board) {
		boards.add(board);
		update(board.getId());
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
		for (Column c : columns) {
			if (c.getTitle().equals(columnTitle))
				return c;
		}
		throw new ColumnNotFoundException("No column found with title \"" + columnTitle + "\" in board " + boardID);
	}

	public Column getColumnCopy(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException {
		List<Column> columns = getBoard(boardID).getColumns();
		for (Column c : columns) {
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

		for (Tile t : tiles) {
			if (t.getId() == tileID) {
				return t;
			}
		}
		throw new TileNotFoundException("No tile found with id \"" + tileID + "\" in column \"" + columnTitle + "\" of \"" + boardID + "\" board");
	}

	public Tile getTileCopy(long boardID, String columnTitle, long tileID) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException {
		Column selectedColumn = getColumn(boardID, columnTitle);
		List<Tile> tiles = selectedColumn.getTiles();

		for (Tile t : tiles) {
			if (t.getId() == tileID) {
				return t.copy();
			}
		}
		throw new TileNotFoundException("No tile found with id \"" + tileID + "\" in column \"" + columnTitle + "\" of \"" + boardID + "\" board");
	}

	public synchronized void editColumn(long boardID, String columnTitle, Column editedColumn) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		Column selectedColumn = getColumn(boardID, columnTitle);
		
		if (!selectedColumn.getTitle().equals(editedColumn.getTitle())) {
			if(isColumnPresent(boardID, editedColumn.getTitle())) {
				throw new InvalidRequestException("A column with title \"" + editedColumn.getTitle() + "\" is already present in this board");
			}
		}
		
		if (selectedColumn.getState().equals(ColumnState.archived) && (editedColumn.getState() == null || editedColumn.getState().equals(ColumnState.archived)))
			throw new InvalidRequestException("Cannot edit archived column");
		if (editedColumn.getTitle() != null)
			selectedColumn.setTitle(editedColumn.getTitle());
		if (editedColumn.getState() != null)
			selectedColumn.setState(editedColumn.getState());
		if (editedColumn.getColor() != null)
			selectedColumn.setColor(editedColumn.getColor());
		update(boardID);
		return;
	}

	private boolean isColumnPresent(long boardID, String columnTitle) {
		try {
			getColumn(boardID, columnTitle);
		} catch (ObjectNotFoundException e) { 
			return false;
		}
		return true;
	}
	
	public synchronized void editTile(long boardID, String columnTitle, long tileID, Tile editedTile) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException, InvalidRequestException {
		Tile selectedTile = getTile(boardID, columnTitle, tileID);
		Column column = getColumn(boardID, columnTitle);

		if (column.getState().equals(ColumnState.archived)) {
			throw new InvalidRequestException("Cannot edit tile from archived board");
		}

		if (editedTile.getTileType() != null && editedTile.getTileType() != selectedTile.getTileType())
			throw new InvalidRequestException("Cannot edit tileType attribute");
		
		if (editedTile.getTitle() != null)
			selectedTile.setTitle(editedTile.getTitle());
		if (editedTile.getAuthor() != null)
			selectedTile.setAuthor(editedTile.getAuthor());
		if (editedTile.getColor() != null)
			selectedTile.setColor(editedTile.getColor());
		
		if (selectedTile instanceof TextTile && ((TextTile)editedTile).getText() != null)
			((TextTile)selectedTile).setText(((TextTile)editedTile).getText());
		else if (selectedTile instanceof ImageTile && ((ImageTile)editedTile).getImageURI() != null)
			((ImageTile)selectedTile).setImageURI(((ImageTile)editedTile).getImageURI());
		else if (selectedTile instanceof FileTile && ((FileTile)editedTile).getFileURI() != null)
			((FileTile)selectedTile).setFileURI(((FileTile)editedTile).getFileURI());
		
		update(boardID);
	}

	public synchronized void addColumn(long boardID, Column column) throws BoardNotFoundException, InvalidRequestException {
		List<Column> columns = getColumns(boardID);

		for (Column c : columns) {
			if (column.getTitle().equals(c.getTitle()))
				throw new InvalidRequestException("A column with title \"" + column.getTitle() + "\" is already present in this board");
		}
		columns.add(column);
		update(boardID);
	}

	public synchronized void addTile(long boardID, String columnTitle, Tile tile) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Column column = getColumn(boardID, columnTitle);

		for (Tile t : tiles) {
			if (t.getId() == tile.getId())
				throw new InvalidRequestException("System error: a tile with id " + tile.getId() + "is already present in this board");
		}

		if (column.getState().equals(ColumnState.active)) {
			tiles.add(tile);
			update(boardID);
		} else {
			throw new InvalidRequestException("Cannot add a tile in an archived column");
		}
	}

	public synchronized void swapTiles(long boardID, String columnTitle, long tileID1, long tileID2) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException, InvalidRequestException {
		Tile tile1 = getTile(boardID, columnTitle, tileID1);
		Tile tile2 = getTile(boardID, columnTitle, tileID2);
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Column column = getColumn(boardID, columnTitle);
		int indexTile1 = tiles.indexOf(tile1);
		int indexTile2 = tiles.indexOf(tile2);

		if (column.getState().equals(ColumnState.archived))
			throw new InvalidRequestException("Cannot swap tiles of an archived column");

		Collections.swap(tiles, indexTile1, indexTile2);
		update(boardID);
	}

	public synchronized void swapColumns(long boardID, String column1Title, String column2Title) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		Column column1 = getColumn(boardID, column1Title);
		Column column2 = getColumn(boardID, column2Title);
		List<Column> columns = getColumns(boardID);
		int indexColumn1 = columns.indexOf(column1);
		int indexColumn2 = columns.indexOf(column2);

		if (column1.getState().equals(ColumnState.archived) || column2.getState().equals(ColumnState.archived))
			throw new InvalidRequestException("Cannot swap archived columns");

		Collections.swap(columns, indexColumn1, indexColumn2);
		update(boardID);
	}

	public synchronized void deleteTile(long boardID, String columnTitle, long tileID) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException, InvalidRequestException {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Tile selectedTile = getTile(boardID, columnTitle, tileID);
		Column column = getColumn(boardID, columnTitle);

		if (column.getState().equals(ColumnState.active)) {
			tiles.remove(selectedTile);
			update(boardID);
		} else {
			throw new InvalidRequestException("Cannot delete tile of an archived column");
		}
	}

	public synchronized void deleteColumn(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		List<Column> columns = getColumns(boardID);
		Column selectedColumn = getColumn(boardID, columnTitle);

		if (selectedColumn.getState().equals(ColumnState.active)) {
			columns.remove(selectedColumn);
			update(boardID);
		} else {
			throw new InvalidRequestException("Cannot delete an archived column");
		}
	}

	public synchronized void moveTile(long boardID, String sourceColumnTitle, String destinationColumnTitle, long tileID) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException, InvalidRequestException {
		List<Tile> sourceTiles = getColumnTiles(boardID, sourceColumnTitle);
		List<Tile> destinationTiles = getColumnTiles(boardID, destinationColumnTitle);
		Tile selectedTile = getTile(boardID, sourceColumnTitle, tileID);
		Column sourceColumn = getColumn(boardID, sourceColumnTitle);
		Column destinationColumn = getColumn(boardID, destinationColumnTitle);

		if (sourceColumn.getState().equals(ColumnState.archived) || destinationColumn.getState().equals(ColumnState.archived))
			throw new InvalidRequestException("Cannot alter the state of an archived column");

		int sourceColumnTileIndex = sourceTiles.indexOf(selectedTile);
		sourceTiles.remove(selectedTile);
		try {
			destinationTiles.add(sourceColumnTileIndex, selectedTile);
		} catch (IndexOutOfBoundsException e){
			destinationTiles.add(selectedTile);
		}
		
		update(boardID);
	}

}
