package com.pollofritto.dskanboard;

import com.pollofritto.model.*;
import com.pollofritto.model.Column.ColumnState;
import com.pollofritto.model.Tile.TileType;

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
		return new ResponseEntity<String>(boardURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/")
	public Board getBoard(@PathVariable long boardID) {
		return DsKanboardApplication.getDataManager().getBoardClone(boardID);
	}

	@PostMapping("/upload/image/")
	public ResponseEntity<String> uploadImage() {
		return null;
	}

	@GetMapping("/api/boards/")
	public List<Board> getBoards() {
		return DsKanboardApplication.getDataManager().getBoardsClone();
	}

	@PostMapping("/api/{boardID}/columns/add/")
	public ResponseEntity<String> addColumn(@PathVariable String boardID,
											@RequestParam (name = "columnTitle") String columnTitle) {

		Column column = new Column(columnTitle);
		DsKanboardApplication.getDataManager().addColumn(Long.parseLong(boardID), column);
		String columnURI = "/api/" + boardID + "/" + column.getTitle() + "/";

		return new ResponseEntity<String>(columnURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/columns/")
	public List<Column> getColumns(@PathVariable String boardID) {
		return DsKanboardApplication.getDataManager().getColumnsClone(Long.parseLong(boardID));
	}

	@GetMapping("/api/{boardID}/{columnTitle}/")
	public Column getColumn(@PathVariable String boardID,
							@PathVariable String columnTitle) {
		return DsKanboardApplication.getDataManager().getColumnClone(Long.parseLong(boardID), columnTitle);
	}


	@PutMapping("/api/{boardID}/{columnTitle}/edit/")
	public ResponseEntity<String> editColumn(@PathVariable String boardID,
											 @PathVariable String columnTitle,
											 @RequestParam (value = "title", required = false) String newTitle,
											 @RequestParam (value = "state", required = false) ColumnState columnState) {

		Column selectedColumn = DsKanboardApplication.getDataManager().getColumnClone(Long.parseLong(boardID), columnTitle);

		if (newTitle != null)
			selectedColumn.setTitle(newTitle);

		if (columnState != null)
			selectedColumn.setState(columnState);

		String columnURI = "/api/" + boardID + "/" + selectedColumn.getTitle() + "/";

		return new ResponseEntity<String>(columnURI, HttpStatus.OK);
	}

	@PostMapping("/api/{boardID}/{columnTitle}/tiles/add/")
	public ResponseEntity<String> addTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @RequestParam (value = "title") String title,
										   @RequestParam (value = "author") String author,
										   @RequestParam (value = "tileType") String tileType,
										   @RequestParam (value = "text", required = false) String text,
										   @RequestParam (value = "image", required = false) String imageURI,
										   @RequestParam (value = "fileURI", required = false) String fileURI,
										   @RequestParam (value = "contentType") String contentType) {
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
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

		DsKanboardApplication.getDataManager().addTile(Long.parseLong(boardID), columnTitle, newTile);
		String tileURI = "/api/" + boardID + "/" + columnTitle + "/" + newTile.getId() + '/';
		return new ResponseEntity<String>(tileURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/{columnTitle}/tiles/")
	public List<Tile> getColumnTiles(@PathVariable String boardID,
									 @PathVariable String columnTitle) {
		return DsKanboardApplication.getDataManager().getColumnTilesClone(Long.parseLong(boardID), columnTitle);
	}

	@GetMapping("/api/{boardID}/{columnTitle}/{tileID}/")
	public Tile getTile(@PathVariable String boardID,
						@PathVariable String columnTitle,
						@PathVariable String tileID) {
		return DsKanboardApplication.getDataManager().getTileClone(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID));
	}

	@PutMapping("/api/{boardID}/{columnTitle}/{tileID}/edit/")
	public ResponseEntity<String> editTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @PathVariable String tileID,
										   @RequestParam (value = "title") String tileTitle,
										   @RequestParam (value = "author") String tileAuthor,
										   @RequestParam (value = "tileType") String tileType,
										   @RequestParam (value = "text", required = false) String text,
										   @RequestParam (value = "image", required = false) String imageURI,
										   @RequestParam (value = "fileURI", required = false) String fileURI,
										   @RequestParam (value = "contentType") String contentType) {
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
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

		DsKanboardApplication.getDataManager().editTile(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID), editedTile);
		String tileURI = "/api/" + boardID + "/" + columnTitle + "/" + editedTile.getId() + '/';
		return new ResponseEntity<String>(tileURI, HttpStatus.OK);
	}

	@PutMapping("/api/{boardID}/{columnTitle}/{tileID}/move/")
	public ResponseEntity<String> moveTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @PathVariable String tileID,
										   @RequestParam (name = "destinationColumnTitle") String destinationColumnTitle) {
		DsKanboardApplication.getDataManager().moveTile(Long.parseLong(boardID), columnTitle, destinationColumnTitle, Long.parseLong(tileID));
		String movedTileURI = "/api/" + boardID + "/" + columnTitle + "/" + tileID + "/";
		return new ResponseEntity<String>(movedTileURI, HttpStatus.OK);
	}

	// TODO: check "Cannot invoke "com.pollofritto.model.Column.getTiles()" because the return value of "com.pollofritto.model.DataManager.getColumn(long, String)" is null"
	@PutMapping("/api/{boardID}/columns/swap/")
	public ResponseEntity<String> swapColumns(@PathVariable String boardID,
											  @RequestParam (name = "column1") String column1,
											  @RequestParam (name = "column2") String column2) {

		DsKanboardApplication.getDataManager().swapColumns(Long.parseLong(boardID), column1, column2);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@PutMapping("/api/{boardID}/{columnTitle}/tiles/swap/")
	public ResponseEntity<String> swapTiles(@PathVariable String boardID,
											@PathVariable String columnTitle,
											@RequestParam (name = "tileID1") String tileID1,
											@RequestParam (name = "tileID2") String tileID2) {
		DsKanboardApplication.getDataManager().swapTiles(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID1), Long.parseLong(tileID2));
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@DeleteMapping("/api/{boardID}/delete/")
	public ResponseEntity<String> deleteBoard(@PathVariable String boardID) {
		DsKanboardApplication.getDataManager().deleteBoard(Long.parseLong(boardID));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/api/{boardID}/{columnTitle}/delete/")
	public ResponseEntity<String> deleteColumn(@PathVariable String boardID,
											   @PathVariable String columnTitle) {
		DsKanboardApplication.getDataManager().deleteColumn(Long.parseLong(boardID), columnTitle);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/api/{boardID}/{columnTitle}/{tileID}/delete/")
	public ResponseEntity<String> deleteColumn(@PathVariable String boardID,
											   @PathVariable String columnTitle,
											   @PathVariable String tileID) {
		DsKanboardApplication.getDataManager().deleteTile(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID));
		return new ResponseEntity<>(HttpStatus.OK);
	}


	@GetMapping("/api/boards/headers/")
	public List<Board> getBoardsHeaders() {
		List<Board> boards = DsKanboardApplication.getDataManager().getBoardsClone();

		for (Board b: boards) {
			b.setColumns(null);
		}

		return boards;
	}

}
