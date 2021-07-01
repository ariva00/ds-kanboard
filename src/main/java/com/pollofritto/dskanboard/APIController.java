package com.pollofritto.dskanboard;

import com.pollofritto.model.*;
import com.pollofritto.model.Column.ColumnState;
import com.pollofritto.model.Tile.TileType;

import com.pollofritto.model.exceptions.InvalidRequestException;
import com.pollofritto.model.exceptions.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * {@link RestController} for handling the API requests.
 *
 */
@RestController
public class APIController {

	/**
	 * Creates a new {@link Board}.
	 * @param boardTitle title of the new {@link Board}
	 * @return
	 */
	@PostMapping("/api/boards/add/")
	public ResponseEntity<String> addBoard(@RequestParam (value = "boardTitle") String boardTitle) {
		Board board = new Board(DsKanboardApplication.getDataManager().generateBoardID(), boardTitle);
		try {
			DsKanboardApplication.getDataManager().addBoard(board);
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		String boardURI = "/api/" + board.getId() + '/';
		return new ResponseEntity<>(boardURI, HttpStatus.CREATED);
	}

	/**
	 * Returns the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param lastModifed {@link String} representing the instant in format "EEE, dd MMM yyyy HH:mm:ss.SSS zzz" (e.g. "Sun, 20 Jul 1969 16:17:00.000 EDT") 
	 * 			indicating the last update received from the server
	 * @param response 
	 * @return
	 */
	@GetMapping("/api/{boardID}/")
	public Board getBoard(@PathVariable long boardID, @RequestHeader(value = "If-Modified-Since-Millis", required = false) String lastModifed, HttpServletResponse response) {
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSS zzz");
		Board selectedBoard;
		
		try {
			selectedBoard = DsKanboardApplication.getDataManager().getBoardCopy(boardID);
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		
		if (lastModifed != null) {
			try {
				Date clientLastModified = format.parse(lastModifed);
				Date serverLastModified = DsKanboardApplication.getDataManager().getLastModified(boardID);
				
				if(format.format(clientLastModified).equals(format.format(serverLastModified))) {
					throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
				}
			} catch (ParseException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		}
		
		response.setHeader("Last-Modified-Millis", format.format(DsKanboardApplication.getDataManager().getLastModified(boardID)));
		
		return selectedBoard;
	}

	/**
	 * Returns the list of all the boards.
	 * @return
	 */
	@GetMapping("/api/boards/")
	public List<Board> getBoards() {
		return DsKanboardApplication.getDataManager().getBoardsCopy();
	}

	/**
	 * Creates a new {@link Column} in the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the new {@link Column}
	 * @param color {@link String} in format "#rrggbbaa" representing the color of the new {@link Column}
	 * @return
	 */
	@PostMapping("/api/{boardID}/columns/add/")
	public ResponseEntity<String> addColumn(@PathVariable String boardID,
											@RequestParam (value = "columnTitle") String columnTitle,
											@RequestParam (value = "color", required = false) String color) {

		Column column = new Column(columnTitle, color);

		if (columnTitle.isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Title");

		try {
			DsKanboardApplication.getDataManager().addColumn(Long.parseLong(boardID), column);
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

		String columnURI = "/api/" + boardID + "/" + column.getTitle() + "/";
		return new ResponseEntity<>(columnURI, HttpStatus.CREATED);
	}

	/**
	 * Returns the list of all the columns in a {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @return
	 */
	@GetMapping("/api/{boardID}/columns/")
	public List<Column> getColumns(@PathVariable String boardID) {
		try {
			return DsKanboardApplication.getDataManager().getColumnsCopy(Long.parseLong(boardID));
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Return the {@link Column} with title {columnTitle} in the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the {@link Column}
	 * @return
	 */
	@GetMapping("/api/{boardID}/{columnTitle}/")
	public Column getColumn(@PathVariable String boardID,
							@PathVariable String columnTitle) {
		try {
			return DsKanboardApplication.getDataManager().getColumnCopy(Long.parseLong(boardID), columnTitle);
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Edits the {@link Column} with title {columnTitle} in the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle current title of the {@link Column}
	 * @param newTitle edited title of the {@link Column}
	 * @param columnState {@link String} representing the edited {@link ColumnState} of the {@link Column}
	 * @param color {@link String} in format "#rrggbbaa" representing the edited color of the {@link Column}
	 * @return
	 */
	@PutMapping("/api/{boardID}/{columnTitle}/edit/")
	public ResponseEntity<String> editColumn(@PathVariable String boardID,
											 @PathVariable String columnTitle,
											 @RequestParam (value = "columnTitle", required = false) String newTitle,
											 @RequestParam (value = "state", required = false) ColumnState columnState,
											 @RequestParam (value = "color", required = false) String color) {

		Column editedColumn = new Column(newTitle, columnState, color);

		try {
			DsKanboardApplication.getDataManager().editColumn(Long.parseLong(boardID), columnTitle, editedColumn);
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

		String columnURI = "/api/" + boardID + "/" + editedColumn.getTitle() + "/";
		return new ResponseEntity<>(columnURI, HttpStatus.OK);
	}

	/**
	 * Creates a new {@link Tile} in the {@link Column} with title {columnTitle} in the board with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the {@link Column}
	 * @param title title of the new {@link Tile}
	 * @param author username of the author or the new {@link Tile}
	 * @param tileType {@link String} representing the {@link TileType} of the new {@link Tile}
	 * @param color {@link String} in format "#rrggbbaa" representing the color of the new {@link Tile}
	 * @param text content of the new {@link Tile} when a new {@link TextTile} is being created
	 * @param imageURI content of the new {@link Tile} when a new {@link ImageTile} is being created
	 * @param fileURI content of the new {@link Tile} when a new {@link FileTile} is being created
	 * @param contentType {@link String} weather "text", "image" or "file" representing the concrete implementation of the new {@link Tile}
	 * @return
	 */
	@PostMapping("/api/{boardID}/{columnTitle}/tiles/add/")
	public ResponseEntity<String> addTile(@PathVariable String boardID,
										  @PathVariable String columnTitle,
										  @RequestParam (value = "tileTitle") String title,
										  @RequestParam (value = "author") String author,
										  @RequestParam (value = "tileType") String tileType,
										  @RequestParam (value = "color") String color,
										  @RequestParam (value = "text", required = false) String text,
										  @RequestParam (value = "imageURI", required = false) String imageURI,
										  @RequestParam (value = "fileURI", required = false) String fileURI,
										  @RequestParam (value = "contentType") String contentType) {
		Tile newTile;

		switch (contentType) {
			case "text":
				newTile = new TextTile(title, author, TileType.valueOf(tileType), color, text, DsKanboardApplication.getDataManager().generateTileID());
				break;
			case "image":
				if(imageURI != null && !imageURI.equals(""))
					newTile = new ImageTile(title, author, TileType.valueOf(tileType), color, imageURI, DsKanboardApplication.getDataManager().generateTileID());
				else
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing imageURI");
				break;
			case "file":
				if(fileURI != null && !fileURI.equals(""))
					newTile = new FileTile(title, author, TileType.valueOf(tileType), color, fileURI, DsKanboardApplication.getDataManager().generateTileID());
				else
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing fileURI");
				break;
			default:
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, contentType + " content type not supported");
		}

		try {
			DsKanboardApplication.getDataManager().addTile(Long.parseLong(boardID), columnTitle, newTile);
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

		String tileURI = "/api/" + boardID + "/" + columnTitle + "/" + newTile.getId() + '/';
		return new ResponseEntity<>(tileURI, HttpStatus.CREATED);
	}

	/**
	 * Returns the list of all the tiles in the {@link Column} with title {columnTitle} in the board with id {boardID}.
	 * @param boardID id of
	 * @param columnTitle
	 * @return
	 */
	@GetMapping("/api/{boardID}/{columnTitle}/tiles/")
	public List<Tile> getColumnTiles(@PathVariable String boardID,
									 @PathVariable String columnTitle) {
		try {
			return DsKanboardApplication.getDataManager().getColumnTilesCopy(Long.parseLong(boardID), columnTitle);
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Returns the {@link Tile} with id {tileID} in the {@link Column} with title {columnTitle} in the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the {@link Column}
	 * @param tileID id of the {@link Tile}
	 * @return
	 */
	@GetMapping("/api/{boardID}/{columnTitle}/{tileID}/")
	public Tile getTile(@PathVariable String boardID,
										@PathVariable String columnTitle,
										@PathVariable String tileID) {
		try {
			return DsKanboardApplication.getDataManager().getTileCopy(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID));
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Edits the {@link Tile} with id {tileID} in the {@link Column} with title {columnTitle} in the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the {@link Column}
	 * @param tileID id of the {@link Tile}
	 * @param tileTitle edited title of the {@link Tile}
	 * @param tileAuthor edited author of the {@link Tile}
	 * @param tileType {@link String} representing the {@link TileType} of the {@link Tile}
	 * @param color {@link String} in format "#rrggbbaa" representing the edited color of the {@link Tile}
	 * @param text edited content of the {@link Tile} when a {@link TextTile} is being edited
	 * @param imageURI edited content of the {@link Tile} when a {@link ImageTile} is being edited
	 * @param fileURI edited content of the {@link Tile} when a {@link FileTile} is being edited
	 * @param contentType {@link String} weather "text", "image" or "file" representing the concrete implementation of the edited {@link Tile}
	 * @return
	 */
	@PutMapping("/api/{boardID}/{columnTitle}/{tileID}/edit/")
	public ResponseEntity<String> editTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @PathVariable String tileID,
										   @RequestParam (value = "tileTitle") String tileTitle,
										   @RequestParam (value = "author") String tileAuthor,
										   @RequestParam (value = "tileType") String tileType,
										   @RequestParam (value = "color") String color,
										   @RequestParam (value = "text", required = false) String text,
										   @RequestParam (value = "imageURI", required = false) String imageURI,
										   @RequestParam (value = "fileURI", required = false) String fileURI,
										   @RequestParam (value = "contentType") String contentType) {

		try {
			Tile selectedTile = DsKanboardApplication.getDataManager().getTileCopy(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID));
			Tile editedTile;

			switch (contentType) {
				case "text":
					if (selectedTile instanceof  TextTile)
						editedTile = new TextTile(tileTitle, tileAuthor, TileType.valueOf(tileType), color, text, Long.parseLong(tileID));
					else
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change tile class");
					break;
				case "image":
					if (selectedTile instanceof ImageTile)
						editedTile = new ImageTile(tileTitle, tileAuthor, TileType.valueOf(tileType), color, imageURI, Long.parseLong(tileID));
					else
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change tile class");
					break;
				case "file":
					if (selectedTile instanceof FileTile)
						editedTile = new FileTile(tileTitle, tileAuthor, TileType.valueOf(tileType), color, fileURI, Long.parseLong(tileID));
					else
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change tile class");
					break;
				default:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, contentType + " content type not supported");
			}

			DsKanboardApplication.getDataManager().editTile(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID), editedTile);
			String tileURI = "/api/" + boardID + "/" + columnTitle + "/" + editedTile.getId() + '/';
			return new ResponseEntity<>(tileURI, HttpStatus.OK);

		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Moves a {@link Tile} from a {@link Column} with title {columnTitle} to another in the same {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the current {@link Column}
	 * @param tileID id of the {@link Tile}
	 * @param destinationColumnTitle title of the destination {@link Column}
	 * @return
	 */
	@PutMapping("/api/{boardID}/{columnTitle}/{tileID}/move/")
	public ResponseEntity<String> moveTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @PathVariable String tileID,
										   @RequestParam (value = "destinationColumnTitle") String destinationColumnTitle) {
		try {
			DsKanboardApplication.getDataManager().moveTile(Long.parseLong(boardID), columnTitle, destinationColumnTitle, Long.parseLong(tileID));
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

		String movedTileURI = "/api/" + boardID + "/" + columnTitle + "/" + tileID + "/";
		return new ResponseEntity<>(movedTileURI, HttpStatus.OK);
	}

	/**
	 * Swaps the position of two columns in the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param column1 title of the first {@link Column}
	 * @param column2 title of the second {@link Column}
	 * @return
	 */
	@PutMapping("/api/{boardID}/columns/swap/")
	public ResponseEntity<String> swapColumns(@PathVariable String boardID,
											  @RequestParam (value = "column1") String column1,
											  @RequestParam (value = "column2") String column2) {
		try {
			DsKanboardApplication.getDataManager().swapColumns(Long.parseLong(boardID), column1, column2);
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Swaps the position of two tiles in the {@link Column} with title {columnTitle} in the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the {@link Column}
	 * @param tileID1 id of the first {@link Tile}
	 * @param tileID2 id of the second {@link Tile}
	 * @return
	 */
	@PutMapping("/api/{boardID}/{columnTitle}/tiles/swap/")
	public ResponseEntity<String> swapTiles(@PathVariable String boardID,
											@PathVariable String columnTitle,
											@RequestParam (value = "tileID1") String tileID1,
											@RequestParam (value = "tileID2") String tileID2) {
		try {
			DsKanboardApplication.getDataManager().swapTiles(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID1), Long.parseLong(tileID2));
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Deletes the {@link Column} with title {columnTitle} in the board with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the {@link Column}
	 * @return
	 */
	@DeleteMapping("/api/{boardID}/{columnTitle}/delete/")
	public ResponseEntity<String> deleteColumn(@PathVariable String boardID,
											   @PathVariable String columnTitle) {
		try {
			DsKanboardApplication.getDataManager().deleteColumn(Long.parseLong(boardID), columnTitle);
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Deletes the {@link Tile} with id {tileID} in the {@link Column} with title {columnTitle} in the {@link Board} with id {boardID}.
	 * @param boardID id of the {@link Board}
	 * @param columnTitle title of the {@link Column}
	 * @param tileID id of the {@link Tile}
	 * @return
	 */
	@DeleteMapping("/api/{boardID}/{columnTitle}/{tileID}/delete/")
	public ResponseEntity<String> deleteTile(@PathVariable String boardID,
											   @PathVariable String columnTitle,
											   @PathVariable String tileID) {
		try {
			DsKanboardApplication.getDataManager().deleteTile(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID));
		} catch (InvalidRequestException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Returns the list of all the boards without the columns data.
	 * @param lastModifed {@link String} representing the instant in format "EEE, dd MMM yyyy HH:mm:ss.SSS zzz" (e.g. "Sun, 20 Jul 1969 16:17:00.000 EDT") 
	 * 			indicating the last update received from the server
	 * @param response
	 * @return
	 */
	@GetMapping("/api/boards/headers/")
	public List<Board> getBoardsHeaders(@RequestHeader(value = "If-Modified-Since-Millis", required = false) String lastModifed, HttpServletResponse response) {
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSS zzz");
		if(lastModifed != null) {
			try {
				Date clientLastModified = format.parse(lastModifed);
				Date serverLastModified = DsKanboardApplication.getDataManager().getLastModified();
				
				if(format.format(clientLastModified).equals(format.format(serverLastModified))) {
					throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
				}
			} catch (ParseException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		}
		
		response.setHeader("Last-Modified-Millis", format.format(DsKanboardApplication.getDataManager().getLastModified()));
		
		List<Board> boards = DsKanboardApplication.getDataManager().getBoardsCopy();

		for (Board b: boards) {
			b.setColumns(null);
		}

		return boards;
	}

}
