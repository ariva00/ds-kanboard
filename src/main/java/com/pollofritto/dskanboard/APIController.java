package com.pollofritto.dskanboard;

import com.pollofritto.model.*;
import com.pollofritto.model.Column.ColumnState;
import com.pollofritto.model.Tile.TileType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIController {

	@GetMapping("/api/board/")
	public Board getBoard() {
		Board demoBoard = new Board();
		Column demoColumn1 = new Column("colonna1", ColumnState.active);
		Column demoColumn2 = new Column("colonna2", ColumnState.archived);

		demoColumn1.getTiles().put(1, new TextTile("titolo1", "autore1", TileType.Informative, "demo1"));
		demoColumn2.getTiles().put(2, new ImageTile("titolo2", "autore2", TileType.Organizational, "demo2"));

		demoBoard.getColumns().put(demoColumn1.getTitle(), demoColumn1);
		demoBoard.getColumns().put(demoColumn2.getTitle(), demoColumn2);

		return demoBoard;
	}
}
