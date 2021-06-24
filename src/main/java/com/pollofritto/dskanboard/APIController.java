package com.pollofritto.dskanboard;

import com.pollofritto.model.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIController {

	@GetMapping("/api/board")
	public Board getBoard() {
		Board demoBoard = new Board();
		Column demoColumn1 = new Column("colonna1", Column.ColumnState.active);
		Column demoColumn2 = new Column("colonna2", Column.ColumnState.archived);

		demoColumn1.getTiles().put(1, new TextTile("titolo1", "autore1", Tile.TileType.Informative, "demo1"));
		demoColumn2.getTiles().put(2, new ImageTile("titolo2", "autore2", Tile.TileType.Organizational, "demo2"));

		demoBoard.getColumns().put(demoColumn1.getTitle(), demoColumn1);
		demoBoard.getColumns().put(demoColumn2.getTitle(), demoColumn2);

		return demoBoard;
	}
}
