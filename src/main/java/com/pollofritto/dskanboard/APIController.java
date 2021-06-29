package com.pollofritto.dskanboard;

import com.pollofritto.dskanboard.exceptions.NotFoundException;
import com.pollofritto.dskanboard.exceptions.BadRequestException;
import com.pollofritto.model.*;
import com.pollofritto.model.Column.ColumnState;
import com.pollofritto.model.Tile.TileType;

import com.pollofritto.model.exceptions.InvalidRequestException;
import com.pollofritto.model.exceptions.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class APIController {

	@PostMapping("/api/boards/add/")
	public ResponseEntity<String> addBoard(@RequestParam (value = "boardTitle") String boardTitle) {
		Board board = new Board(boardTitle);
		DsKanboardApplication.getDataManager().addBoard(board);
		String boardURI = "/api/" + board.getId() + '/';
		return new ResponseEntity<>(boardURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/")
	public Board getBoard(@PathVariable long boardID) throws NotFoundException {
		try {
			return DsKanboardApplication.getDataManager().getBoardCopy(boardID);
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	@GetMapping("/api/boards/")
	public List<Board> getBoards() {
		return DsKanboardApplication.getDataManager().getBoardsCopy();
	}

	@PostMapping("/api/{boardID}/columns/add/")
	public ResponseEntity<String> addColumn(@PathVariable String boardID,
											@RequestParam (value = "columnTitle") String columnTitle) throws BadRequestException, NotFoundException {

		Column column = new Column(columnTitle);
		try {
			DsKanboardApplication.getDataManager().addColumn(Long.parseLong(boardID), column);
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}

		String columnURI = "/api/" + boardID + "/" + column.getTitle() + "/";
		return new ResponseEntity<>(columnURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/columns/")
	public List<Column> getColumns(@PathVariable String boardID) throws NotFoundException {
		try {
			return DsKanboardApplication.getDataManager().getColumnsCopy(Long.parseLong(boardID));
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	@GetMapping("/api/{boardID}/{columnTitle}/")
	public Column getColumn(@PathVariable String boardID,
							@PathVariable String columnTitle) throws NotFoundException {
		try {
			return DsKanboardApplication.getDataManager().getColumnCopy(Long.parseLong(boardID), columnTitle);
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	@PutMapping("/api/{boardID}/{columnTitle}/edit/")
	public ResponseEntity<String> editColumn(@PathVariable String boardID,
											 @PathVariable String columnTitle,
											 @RequestParam (value = "columnTitle", required = false) String newTitle,
											 @RequestParam (value = "state", required = false) ColumnState columnState) throws BadRequestException, NotFoundException {

		Column editedColumn = new Column(newTitle, columnState);

		try {
			DsKanboardApplication.getDataManager().editColumn(Long.parseLong(boardID), columnTitle, editedColumn);
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
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
										   @RequestParam (value = "text", required = false) String text,
										   @RequestParam (value = "imageURI", required = false) String imageURI,
										   @RequestParam (value = "fileURI", required = false) String fileURI,
										   @RequestParam (value = "contentType") String contentType) throws BadRequestException, NotFoundException {
		Tile newTile;

		switch (contentType) {
			case "text":
				newTile = new TextTile(title, author, TileType.valueOf(tileType), text);
				break;
			case "image":
				newTile = new ImageTile(title, author, TileType.valueOf(tileType), imageURI);
				break;
			case "file":
				newTile = new FileTile(title, author, TileType.valueOf(tileType), fileURI);
				break;
			default:
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		try {
			DsKanboardApplication.getDataManager().addTile(Long.parseLong(boardID), columnTitle, newTile);
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}

		String tileURI = "/api/" + boardID + "/" + columnTitle + "/" + newTile.getId() + '/';
		return new ResponseEntity<>(tileURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/{columnTitle}/tiles/")
	public List<Tile> getColumnTiles(@PathVariable String boardID,
									 @PathVariable String columnTitle) throws NotFoundException {
		try {
			return DsKanboardApplication.getDataManager().getColumnTilesCopy(Long.parseLong(boardID), columnTitle);
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	@GetMapping("/api/{boardID}/{columnTitle}/{tileID}/")
	public Tile getTile(@PathVariable String boardID,
										@PathVariable String columnTitle,
										@PathVariable String tileID) throws NotFoundException {
		try {
			return DsKanboardApplication.getDataManager().getTileCopy(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID));
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	@PutMapping("/api/{boardID}/{columnTitle}/{tileID}/edit/")
	public ResponseEntity<String> editTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @PathVariable String tileID,
										   @RequestParam (value = "tileTitle") String tileTitle,
										   @RequestParam (value = "author") String tileAuthor,
										   @RequestParam (value = "tileType") String tileType,
										   @RequestParam (value = "text", required = false) String text,
										   @RequestParam (value = "imageURI", required = false) String imageURI,
										   @RequestParam (value = "fileURI", required = false) String fileURI,
										   @RequestParam (value = "contentType") String contentType) throws BadRequestException, NotFoundException {
		Tile editedTile;

		switch (contentType) {
			case "text":
				editedTile = new TextTile(tileTitle, tileAuthor, TileType.valueOf(tileType), text);
				break;
			case "image":
				editedTile = new ImageTile(tileTitle, tileAuthor, TileType.valueOf(tileType), imageURI);
				break;
			case "file":
				editedTile = new FileTile(tileTitle, tileAuthor, TileType.valueOf(tileType), fileURI);
				break;
			default:
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		try {
			DsKanboardApplication.getDataManager().editTile(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID), editedTile);
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}

		String tileURI = "/api/" + boardID + "/" + columnTitle + "/" + editedTile.getId() + '/';
		return new ResponseEntity<>(tileURI, HttpStatus.OK);
	}

	@PutMapping("/api/{boardID}/{columnTitle}/{tileID}/move/")
	public ResponseEntity<String> moveTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @PathVariable String tileID,
										   @RequestParam (value = "destinationColumnTitle") String destinationColumnTitle) throws BadRequestException, NotFoundException {
		try {
			DsKanboardApplication.getDataManager().moveTile(Long.parseLong(boardID), columnTitle, destinationColumnTitle, Long.parseLong(tileID));
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}

		String movedTileURI = "/api/" + boardID + "/" + columnTitle + "/" + tileID + "/";
		return new ResponseEntity<>(movedTileURI, HttpStatus.OK);
	}

	// TODO: check "Cannot invoke "com.pollofritto.model.Column.getTiles()" because the return value of "com.pollofritto.model.DataManager.getColumn(long, String)" is null"
	@PutMapping("/api/{boardID}/columns/swap/")
	public ResponseEntity<String> swapColumns(@PathVariable String boardID,
											  @RequestParam (value = "column1") String column1,
											  @RequestParam (value = "column2") String column2) throws BadRequestException, NotFoundException {
		try {
			DsKanboardApplication.getDataManager().swapColumns(Long.parseLong(boardID), column1, column2);
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/api/{boardID}/{columnTitle}/tiles/swap/")
	public ResponseEntity<String> swapTiles(@PathVariable String boardID,
											@PathVariable String columnTitle,
											@RequestParam (value = "tileID1") String tileID1,
											@RequestParam (value = "tileID2") String tileID2) throws BadRequestException, NotFoundException{
		try {
			DsKanboardApplication.getDataManager().swapTiles(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID1), Long.parseLong(tileID2));
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/api/{boardID}/{columnTitle}/delete/")
	public ResponseEntity<String> deleteColumn(@PathVariable String boardID,
											   @PathVariable String columnTitle)  throws BadRequestException, NotFoundException {
		try {
			DsKanboardApplication.getDataManager().deleteColumn(Long.parseLong(boardID), columnTitle);
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/api/{boardID}/{columnTitle}/{tileID}/delete/")
	public ResponseEntity<String> deleteColumn(@PathVariable String boardID,
											   @PathVariable String columnTitle,
											   @PathVariable String tileID) throws BadRequestException, NotFoundException {
		try {
			DsKanboardApplication.getDataManager().deleteTile(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID));
		} catch (InvalidRequestException e) {
			throw new BadRequestException(e.getMessage());
		} catch (ObjectNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}


	@GetMapping("/api/boards/headers/")
	public List<Board> getBoardsHeaders() {
		List<Board> boards = DsKanboardApplication.getDataManager().getBoardsCopy();

		for (Board b: boards) {
			b.setColumns(null);
		}

		return boards;
	}

}
