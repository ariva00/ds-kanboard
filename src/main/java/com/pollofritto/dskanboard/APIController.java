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

	private DataManager dataManager = new DataManager();

	@PostMapping("/api/boards/add/")
	public ResponseEntity<String> addBoard(@RequestParam (value = "boardTitle", required = true) String boardTitle) {
		Board board = new Board(boardTitle);
		dataManager.addBoard(board);
		String boardURI = "/api/" + board.getBoardID() + '/';
		return new ResponseEntity<String>(boardURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/")
	public Board getBoard(@PathVariable long boardID) {
		return dataManager.getBoard(boardID);
	}

	@PostMapping("/upload/image/")
	public ResponseEntity<String> uploadImage() {
		return null;
	}

	@GetMapping("/api/boards/")
	public List<Board> getBoards() {
		return dataManager.getBoards();
	}

	@PostMapping("/api/{boardID}/columns/add/")
	public ResponseEntity<String> addColumn(@PathVariable String boardID,
											@RequestParam (name = "columnTitle", required = true) String columnTitle) {

		Column column = new Column(columnTitle);
		dataManager.addColumn(Long.parseLong(boardID), column);
		String columnURI = "/api/" + boardID + "/" + column.getTitle() + "/";

		return new ResponseEntity<String>(columnURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/columns/")
	public List<Column> getColumns(@PathVariable String boardID) {
		return dataManager.getColumns(Long.parseLong(boardID));
	}

	@GetMapping("/api/{boardID}/{columnTitle}/")
	public Column getColumn(@PathVariable String boardID,
							@PathVariable String columnTitle) {
		return dataManager.getColumn(Long.parseLong(boardID), columnTitle);
	}


	@PutMapping("/api/{boardID}/{columnTitle}/edit/")
	public ResponseEntity<String> editColumn(@PathVariable String boardID,
											 @PathVariable String columnTitle,
											 @RequestParam (value = "title", required = false) String newTitle,
											 @RequestParam (value = "state", required = false) ColumnState columnState) {

		Column selectedColumn = dataManager.getColumn(Long.parseLong(boardID), columnTitle);

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
										   @RequestParam (value = "title", required = true) String tileTitle,
										   @RequestParam (value = "author", required = true) String tileAuthor,
										   @RequestParam (value = "tileType", required = true) String tileType,
										   @RequestParam (value = "text", required = false) String text,
										   @RequestParam (value = "image", required = false) String imageURI,
										   @RequestParam (value = "fileURI", required = false) String fileURI,
										   @RequestParam (value = "contentType", required = true) String contentType) {

		Column column = dataManager.getColumn(Long.parseLong(boardID), columnTitle);
		Tile newTile;

		switch (contentType) {
			case "text":
				newTile = new TextTile(tileTitle, tileAuthor, TileType.valueOf(tileType), text);
				break;
			case "image":
				newTile = new ImageTile(tileTitle, tileAuthor, TileType.valueOf(tileType), imageURI);
				break;
			case "file":
				newTile = new FileTile(tileTitle, tileAuthor, TileType.valueOf(tileType), fileURI);
				break;
			default:
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

		dataManager.addTile(Long.parseLong(boardID), columnTitle, newTile);
		String tileURI = "/api/" + boardID + "/" + columnTitle + "/" + newTile.getId() + '/';
		return new ResponseEntity<String>(tileURI, HttpStatus.OK);
	}

	@GetMapping("/api/{boardID}/{columnTitle}/tiles/")
	public List<Tile> getColumnTiles(@PathVariable String boardID,
									 @PathVariable String columnTitle) {
		List<Tile> tmp = dataManager.getColumnTiles(Long.parseLong(boardID), columnTitle);
		System.out.println(tmp);
		return tmp;
	}

	@GetMapping("/api/{boardID}/{columnTitle}/{tileID}/")
	public Tile getTile(@PathVariable String boardID,
						@PathVariable String columnTitle,
						@PathVariable String tileID) {
		return dataManager.getTile(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID));
	}

	@PutMapping("/api/{boardID}/{columnTitle}/{tileID}/edit/")
	public ResponseEntity<String> editTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @PathVariable String tileID,
										   @RequestParam (value = "title", required = true) String tileTitle,
										   @RequestParam (value = "author", required = true) String tileAuthor,
										   @RequestParam (value = "tileType", required = true) String tileType,
										   @RequestParam (value = "text", required = false) String text,
										   @RequestParam (value = "image", required = false) String imageURI,
										   @RequestParam (value = "fileURI", required = false) String fileURI,
										   @RequestParam (value = "contentType", required = true) String contentType) {

		Column column = dataManager.getColumn(Long.parseLong(boardID), columnTitle);
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

		dataManager.editTile(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID), editedTile);
		String tileURI = "/api/" + boardID + "/" + columnTitle + "/" + editedTile.getId() + '/';
		return new ResponseEntity<String>(tileURI, HttpStatus.OK);
	}

	@PutMapping("/api/{boardID}/{columnTitle}/{tileID}/move/")
	public ResponseEntity<String> moveTile(@PathVariable String boardID,
										   @PathVariable String columnTitle,
										   @PathVariable String tileID,
										   @RequestParam String destinationColumnTitle) {

		Column destinationColumn = dataManager.getColumn(Long.parseLong(boardID), destinationColumnTitle);
		dataManager.moveTile(Long.parseLong(boardID), columnTitle, destinationColumnTitle, Long.parseLong(tileID));
		// TODO: Add moved tile URI
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@PutMapping("/api/{boardID}/columns/swap/")
	public ResponseEntity<String> swapColumns(@PathVariable String boardID,
											  @RequestParam String column1,
											  @RequestParam String column2) {

		dataManager.swapColumns(Long.parseLong(boardID), column1, column2);
		// TODO: add URI or something
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@PutMapping("/api/{boardID}/{columnTitle}/swap/")
	public ResponseEntity<String> swapTiles(@PathVariable String boardID,
											@PathVariable String columnTitle,
											@RequestParam String tileID1,
											@RequestParam String tileID2) {
		dataManager.swapTiles(Long.parseLong(boardID), columnTitle, Long.parseLong(tileID1), Long.parseLong(tileID2));
		// TODO: add URI or something
		return new ResponseEntity<String>(HttpStatus.OK);
	}

}
