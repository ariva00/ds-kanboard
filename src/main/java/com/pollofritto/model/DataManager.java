package com.pollofritto.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pollofritto.model.Column.ColumnState;
import com.pollofritto.model.Tile.TileType;
import com.pollofritto.model.exceptions.*;
import com.pollofritto.persistence.DataPersistenceManager;

/**
 * 
 * Class that keeps track of the data base, guarantees its consistency and contacts a {@link DataPersistenceManager} on modifications
 *
 */
public class DataManager {

	private DataPersistenceManager dataPersistenceManager;
	
	private List<Board> boards;
	private Map<Long, Date> boardsLastModified;
	private Date lastModified;
	
	private long lastTileID;
	private long lastBoardID;

	/**
	 * Creates a new instance of {@link DataManager}, loading the data base from a {@link DataPersistenceManager}.<br>
	 * If the {@link DataPersistenceManager} points to a location where there is no data or where data is not compatible.
	 * a new {@link List} of boards is created.
	 * @param persistence {@link DataPersistenceManager} the {@link DataManager} will interact with
	 */
	@SuppressWarnings("unchecked")
	public DataManager(DataPersistenceManager persistence) {
		this.dataPersistenceManager = persistence;
		try {
			Object o = dataPersistenceManager.getData();
			if(o instanceof List)
				boards = (List<Board>) o;
			else throw new IOException();
		} catch (ClassNotFoundException|IOException e) {
			boards = new ArrayList<>();
		}
		updateLastIDs();
		
		boardsLastModified = new HashMap<Long, Date>();
		Date creation = new Date();
		lastModified = creation;
		for(Board board : boards) {
			boardsLastModified.put(board.getId(), creation);
		}
	}
	
	/**
	 * Returns a {@link Date} representing the last modification to the data base.
	 * @return
	 */
	public Date getLastModified() {
		return lastModified;
	}
	
	/**
	 * Returns a {@link Date} representing the last modifications to a {@link Board} in the data base.
	 * @param boardID id of the {@link Board}
	 * @return
	 */
	public Date getLastModified(long boardID) {
		return boardsLastModified.get(boardID);
	}
	
	/**
	 * Updates the {@link Date} of last modification in a specific board and thus the global one.
	 * @param boardID
	 * @return
	 */
	private Date updateLastModified(long boardID) {
		Date now = new Date();
		boardsLastModified.put(boardID, now);
		lastModified = now;
		return now;
	}
	
	/**
	 * Extracts the highest values for the ID of {@link Board} and {@link Tile}.
	 */
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
	
	/**
	 * Contacts the {@link DataPersistenceManager} to store the list of boards.
	 */
	private void syncStorage() {
		try {
			dataPersistenceManager.storeData(boards);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to be called after every modification, stores the data and updates the last modification {@link Date}.
	 * @param boardID
	 * @return
	 */
	private Date update(long boardID) {
		syncStorage();
		return updateLastModified(boardID);
	}
	
	/**
	 * Generates an unused id for a new {@link Tile}.
	 * @return
	 */
	public synchronized long generateTileID() {
		return ++lastTileID;
	}
	
	/**
	 * Generates an unused id for a new {@link Board}.
	 * @return
	 */
	public synchronized long generateBoardID() {
		return ++lastBoardID;
	}
	
	/**
	 * Inserts deep copy of a new {@link Board} in the data base.
	 * @param board
	 * @throws InvalidRequestException when the board id is already present
	 */
	public synchronized void addBoard(Board board) throws InvalidRequestException {
		if(isBoardPresent(board.getId()))
			throw new InvalidRequestException("A board with id \"" + board.getId() + "\" is already present");
		boards.add(board.copy());
		update(board.getId());
	}

	/**
	 * Returns the {@link List} of boards.
	 * @return
	 */
	private List<Board> getBoards() {
		return boards;
	}

	/**
	 * Returns a deep copy of the {@link List} of the {@link List} of boards.
	 * @return
	 */
	public List<Board> getBoardsCopy() {
		List<Board> boardsCopy = new ArrayList<Board>();
		for (Board b: getBoards()) {
			boardsCopy.add(b.copy());
		}
		return boardsCopy;
	}

	/**
	 * Returns the {@link Board} with the given id.
	 * @param id
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 */
	private Board getBoard(long id) throws BoardNotFoundException {
		for (Board b: boards) {
			if (b.getId() == id)
				return b;
		}
		throw new BoardNotFoundException("No board found with id: " + id);
	}

	/**
	 * Returns a deep copy of the {@link Board} with the given id.
	 * @param id
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 */
	public Board getBoardCopy(long id) throws  BoardNotFoundException {
		return getBoard(id).copy();
	}

	/**
	 * Returns the {@link List} of columns of the {@link Board} with the given id.
	 * @param boardID
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 */
	private List<Column> getColumns(long boardID) throws BoardNotFoundException {
		return getBoard(boardID).getColumns();
	}

	/**
	 * Returns a deep copy of the {@link List} of columns of the {@link Board} with the given id.
	 * @param boardID
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 */
	public List<Column> getColumnsCopy(long boardID) throws BoardNotFoundException {
		return getBoard(boardID).copy().getColumns();
	}

	/**
	 * Returns the {@link Column} with the given title of the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 */
	private Column getColumn(long boardID, String columnTitle) throws  BoardNotFoundException, ColumnNotFoundException {
		List<Column> columns = getBoard(boardID).getColumns();
		for (Column c : columns) {
			if (c.getTitle().equals(columnTitle))
				return c;
		}
		throw new ColumnNotFoundException("No column found with title \"" + columnTitle + "\" in board " + boardID);
	}

	/**
	 * Returns a deep copy of the {@link Column} with the given title of the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 */
	public Column getColumnCopy(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException {
		return getColumn(boardID, columnTitle).copy();
	}

	/**
	 * Returns the {@link List} of tiles of the {@link Column} with the given title of the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 */
	private List<Tile> getColumnTiles(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException {
		return getColumn(boardID, columnTitle).getTiles();
	}

	/**
	 * Returns a deep copy of the {@link List} of tiles of the {@link Column} with the given title of the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 */
	public List<Tile> getColumnTilesCopy(long boardID, String columnTitle) throws BoardNotFoundException, ColumnNotFoundException {
		return getColumn(boardID, columnTitle).copy().getTiles();
	}

	/**
	 * Returns the {@link Tile} with the given id of the {@link Column} with the given title of the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @param tileID
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 * @throws TileNotFoundException when no {@link Tile} with such id exists
	 */
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
	
	/**
	 * Returns a deep copy of the {@link Tile} with the given id of the {@link Column} with the given title of the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @param tileID
	 * @return
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 * @throws TileNotFoundException when no {@link Tile} with such id exists
	 */
	public Tile getTileCopy(long boardID, String columnTitle, long tileID) throws BoardNotFoundException, ColumnNotFoundException, TileNotFoundException {
		return getTile(boardID, columnTitle, tileID).copy();
	}

	/**
	 * Edits a {@link Column} with the given title of the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @param editedColumn
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 * @throws InvalidRequestException when trying to set the {@link Column} title to a title already present in the {@link Board} 
	 * 			or when trying to edit an archived column
	 */
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
	}

	/**
	 * Checks if a {@link Tile} id is already in use in the {@link Board} with the given id
	 * @param boardID
	 * @param columnTitle
	 * @return
	 */
	private boolean isTilePresent(long boardID, long tileID) {
		try {
			for(Column c : getBoard(boardID).getColumns())
				getTile(boardID, c.getTitle(), tileID);
		} catch (ObjectNotFoundException e) { 
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if a {@link Column} title is already in use in the {@link Board} with the given id
	 * @param boardID
	 * @param columnTitle
	 * @return
	 */
	private boolean isColumnPresent(long boardID, String columnTitle) {
		try {
			getColumn(boardID, columnTitle);
		} catch (ObjectNotFoundException e) { 
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if a {@link Board} id is already in use
	 * @param boardID
	 * @return
	 */
	private boolean isBoardPresent(long boardID) {
		try {
			getBoard(boardID);
		} catch (ObjectNotFoundException e) { 
			return false;
		}
		return true;
	}
	
	/**
	 * Edits a {@link Tile} with the given id of the {@link Column} with the given title of the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @param tileID
	 * @param editedTile
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 * @throws TileNotFoundException when no {@link Tile} with such id exists
	 * @throws InvalidRequestException when trying to edit a tile in an archived {@link Column}, 
	 * 			trying to edit {@link TileType} of the {@link Tile}
	 * @throws ClassCastException when editedTile is not of the same concrete type as the {@link Tile} with the same id in the data base
	 */
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
		
		try {
			if (selectedTile instanceof TextTile && ((TextTile)editedTile).getText() != null)
				((TextTile)selectedTile).setText(((TextTile)editedTile).getText());
			else if (selectedTile instanceof ImageTile && ((ImageTile)editedTile).getImageURI() != null)
				((ImageTile)selectedTile).setImageURI(((ImageTile)editedTile).getImageURI());
			else if (selectedTile instanceof FileTile && ((FileTile)editedTile).getFileURI() != null)
				((FileTile)selectedTile).setFileURI(((FileTile)editedTile).getFileURI());
		} catch (ClassCastException e) {
			throw new InvalidRequestException("Wrong content type");
		}
		
		update(boardID);
	}

	/**
	 * Inserts deep copy of a new {@link Column} in the {@link Board} with the given id in the data base.
	 * @param boardID
	 * @param column
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws InvalidRequestException when a {@link Column} with the same title is already present in the {@link Board}
	 */
	public synchronized void addColumn(long boardID, Column column) throws BoardNotFoundException, InvalidRequestException {
		List<Column> columns = getColumns(boardID);

		if (isColumnPresent(boardID, column.getTitle()))
			throw new InvalidRequestException("A column with title \"" + column.getTitle() + "\" is already present in this board");
		
		columns.add(column.copy());
		update(boardID);
	}

	/**
	 * Inserts a deep copy of the new {@link Tile} in the {@link Column} with the given title in the {@link Board} with the given id in data base.
	 * @param boardID
	 * @param columnTitle
	 * @param tile
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 * @throws InvalidRequestException when a {@link Tile} with the same id is already present in the {@link Board} 
	 * 			or when trying to add a {@link Tile} to an archived column
	 */
	public synchronized void addTile(long boardID, String columnTitle, Tile tile) throws BoardNotFoundException, ColumnNotFoundException, InvalidRequestException {
		List<Tile> tiles = getColumnTiles(boardID, columnTitle);
		Column column = getColumn(boardID, columnTitle);

		if(isTilePresent(boardID, tile.getId()))
			throw new InvalidRequestException("A tile with id \"" + tile.getId() + "\" is already present in this board");

		if (column.getState().equals(ColumnState.active)) {
			tiles.add(tile.copy());
			update(boardID);
		} else {
			throw new InvalidRequestException("Cannot add a tile in an archived column");
		}
	}

	/**
	 * Swaps the position of two tiles in the same {@link Column} with the given title in the {@link Board} with the given id.
	 * @param boardID
	 * @param columnTitle
	 * @param tileID1
	 * @param tileID2
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 * @throws TileNotFoundException when one of the two id does not identify a {@link Tile}
	 * @throws InvalidRequestException when trying to swap tiles in an archived {@link Column}
	 */
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

	/**
	 * Swaps the position of two columns in the same {@link Board} with the given id.
	 * @param boardID
	 * @param column1Title
	 * @param column2Title
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when one of the two titles does not identify a {@link Column}
	 * @throws InvalidRequestException when trying to swap archived columns
	 */
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

	/**
	 * Deletes the {@link Tile} with the given id in the {@link Column} with the given title in the {@link Board} with the given id
	 * @param boardID
	 * @param columnTitle
	 * @param tileID
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 * @throws TileNotFoundException when no {@link Tile} with such id exists
	 * @throws InvalidRequestException when trying to delete a tile in an archived {@link Column}
	 */
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

	/**
	 * Deletes the {@link Column} with the given title in the {@link Board} with the given id
	 * @param boardID
	 * @param columnTitle
	 * @param tileID
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when no {@link Column} with such title exists
	 * @throws InvalidRequestException when trying to delete a tile in an archived {@link Column}
	 */
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

	/**
	 * Moves the {@link Tile} with the given id in the {@link Column} with the given title (sourceColumnTitle) in the {@link Board} with the given id 
	 * to another {@link Column} in the same {@link Board} 
	 * @param boardID
	 * @param sourceColumnTitle title of the source {@link Column}
	 * @param destinationColumnTitle title of the destination {@link Column}
	 * @param tileID
	 * @throws BoardNotFoundException when no {@link Board} with such id exists
	 * @throws ColumnNotFoundException when one of the two titles does not identify a {@link Column}
	 * @throws TileNotFoundException when no {@link Tile} with such id exists
	 * @throws InvalidRequestException when trying to swap an archived {@link Column}
	 */
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
