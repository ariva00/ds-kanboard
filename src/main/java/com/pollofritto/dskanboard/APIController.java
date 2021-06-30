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

@RestController
public class APIController {

	@PostMapping("/api/boards/add/")
	public ResponseEntity<String> addBoard(@RequestParam (value = "boardTitle") String boardTitle) {
		Board board = new Board(DsKanboardApplication.getDataManager().generateBoardID(), boardTitle);
		DsKanboardApplication.getDataManager().addBoard(board);
		String boardURI = "/api/" + board.getId() + '/';
		return new ResponseEntity<>(boardURI, HttpStatus.CREATED);
	}

	@GetMapping("/api/{boardID}/")
	public Board getBoard(@PathVariable long boardID, @RequestHeader(value = "If-Modified-Since-Millis", required = false) String lastModifed, HttpServletResponse response) {
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSS zzz");
		
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
		try {
			return DsKanboardApplication.getDataManager().getBoardCopy(boardID);
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/boards/")
	public List<Board> getBoards() {
		return DsKanboardApplication.getDataManager().getBoardsCopy();
	}

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

	@GetMapping("/api/{boardID}/columns/")
	public List<Column> getColumns(@PathVariable String boardID) {
		try {
			return DsKanboardApplication.getDataManager().getColumnsCopy(Long.parseLong(boardID));
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/{boardID}/{columnTitle}/")
	public Column getColumn(@PathVariable String boardID,
							@PathVariable String columnTitle) {
		try {
			return DsKanboardApplication.getDataManager().getColumnCopy(Long.parseLong(boardID), columnTitle);
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

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
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				break;
			case "file":
				if(fileURI != null && !fileURI.equals(""))
					newTile = new FileTile(title, author, TileType.valueOf(tileType), color, fileURI, DsKanboardApplication.getDataManager().generateTileID());
				else
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				break;
			default:
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

	@GetMapping("/api/{boardID}/{columnTitle}/tiles/")
	public List<Tile> getColumnTiles(@PathVariable String boardID,
									 @PathVariable String columnTitle) {
		try {
			return DsKanboardApplication.getDataManager().getColumnTilesCopy(Long.parseLong(boardID), columnTitle);
		} catch (ObjectNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

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
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
