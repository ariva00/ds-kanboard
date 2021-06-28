class Kanboard {

	constructor(){
		this.state = "active";
	}

	setState(state){
		this.state = state;
	}

	getState(){
		return this.state;
	}
	
}

class NavbarElement {
	constructor(boards, serverConnector, kanboard, boardCreationModal, loginModal){
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "navbar");
		
		var buttonsSpan = document.createElement("span");
		buttonsSpan.style.display = "flex";

		var select = document.createElement("select");
		select.classList.add("kanboard", "input");

		for(board of boards){
			var option = document.createElement("option");
			option.setAttribute("value", board.id);
			option.append(board.title); 
			select.appendChild(option);
		}

		select.onchange = function(){
			serverConnector.getBoard(select.value);
		}

		buttonsSpan.appendChild(select);

		var archiveButton = document.createElement("button");
		archiveButton.classList("kanboard", "button", "text-button");
		archiveButton.append("SHOW ARCHIVED");
		archiveButton.onclick = function(){
			if(kanboard.state = "archived")
				kanboard.setState("active");
			if(kanboard.state = "active")
				kanboard.setState("archived");
		}
		buttonsSpan.appendChild(archiveButton);

		var newBoardButton = document.createElement("button");
		newBoardButton.classList("kanboard", "button", "text-button");
		newBoardButton.append("ADD BOARD");
		newBoardButton.onclick = function() {
			boardCreationModal.createBoard();
		}
		buttonsSpan.appendChild(newBoardButton);

		this.rootNode.appendChild(buttonsSpan);

		var loginButton = document.createElement("span");
		loginButton.classList.add("kanboard", "button", "text-button");

		var span = document.createElement("span");
		span.append("USERNAME");
		loginButton.appendChild(span);
		var icon = document.createElement("span");
		icon.classList.add("material-icons-outlined");
		icon.append("account_circle");
		loginButton.appendChild(icon);

		loginButton.onclick = function(){
			loginModal.login();
		}

		this.rootNode.append(loginButton);
	}
}

class BoardElement {
	constructor(board, tileEditModal, columnEditModal, tileCreationModal, columnCreationModal, serverConnector, state) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "board");
		for (index in board.columns) {
			if(board.columns[index].state == state){

				var prevTitle = undefined;
				var succTitle = undefined;

				if(board.columns[index - 1] != undefined)
					prevTitle = board.columns[index - 1].title;
			
				if(board.columns[index + 1] != undefined)
					succTitle = board.columns[index + 1].title;

				var column = new ColumnElement(board.id, board.columns[index], tileEditModal, columnEditModal, tileCreationModal, serverConnector, prevTitle, succTitle);

				this.rootNode.appendChild(column.getNodeTree());
			}
		}

		var columnCreationElement = new ColumnCreationElement (board.id, columnCreationModal);

		this.rootNode.appendChild(columnCreationElement.getNodeTree());
	}
}

class ColumnCreationElement {
	constructor(boardID, columnCreationModal) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "column");
		
		var tile = document.createElement("div");
		tile.classList.add("kanboard", "tile");
		this.rootNode.appendChild(tile);

		var span = document.createElement("span");
		span.classList.add("kanboard", "button", "text-button");
		span.onclick = function(){
			columnCreationModal.createColumn(boardID);
		}
		tile.appendChild(span);
	}

	getNodeTree(){
		return this.rootNode;
	}
}

class ColumnElement {
	constructor(boardID, column, tileEditModal, columnEditModal, tileCreationModal, serverConnector, prevTitle, succTitle) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "column");
		
		this.tiles = new Array();

		var columnHeaderElement = new ColumnHeaderElement(boardID, column, columnEditModal, serverConnector, prevTitle, succTitle);

		this.rootNode.appendChild(columnHeaderElement);

		for(var index in column.tiles){
			var prevTileID = undefined;
			var succTileID = undefined;

			if(column.tiles[index - 1] != undefined)
				prevTileID = column.tiles[index - 1].id;
			
			if(column.tiles[index + 1] != undefined)
				succTileID = column.tiles[index + 1].id;

			var tile = new TileElement(boardID, column.title, column.tiles[index], tileEditModal, serverConnector, prevTileID, succTileID)
			this.tiles.add(tile);
			this.rootNode.appendChild(tile.getNodeTree());
		}

		var tileCreationElement = new TileCreationElement(boardID, column.title, tileCreationModal);
		this.rootNode.appendChild(tileCreationElement.getNodeTree());
	}

	getNodeTree(){
		return this.rootNode;
	}
}

class ColumnHeaderElement {
	constructor(boardID, column, columnEditModal, serverConnector, prevTitle, succTitle) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "tile");

		var colorDiv = document.createElement("div");
		colorDiv.classList.add("kanboard", "column-color");
		colorDiv.style.backgroundColor = column.color;
		this.rootNode.appendChild(colorDiv);

		var tileHeader = document.createElement("div");
		tileHeader.classList.add("kanboard", "tile-header");

		var tileTitle = document.createElement("p");
		tileTitle.classList.add("kanboard", "column-title");
		tileTitle.append(column.title);

		var buttonsSpan = document.createElement("span");
		var archiveButton = document.createElement("span");
		archiveButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button")
		archiveButton.append("archive");
		archiveButton.onclick = function() {
			serverConnector.archiveColumn(boardID, column.title);
		}
		buttonsSpan.appendChild(archiveButton);

		var editButton = document.createElement("span");
		editButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button")
		editButton.append("edit");
		editButton.onclick = function() {
			columnEditModal.edit(boardID, column.title);
		}
		buttonsSpan.appendChild(editButton);

		var deleteButton = document.createElement("span");
		deleteButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button")
		deleteButton.append("delete");
		deleteButton.onclick = function() {
			serverConnector.deleteColumn(boardID, column.title);
		}
		buttonsSpan.appendChild(deleteButton);

		tileHeader.appendChild(buttonsSpan);

		var columnRightLeftSpan = document.createElement("span");

		if(prevTitle != undefined) {
			var rightButton = document.createElement("span");
			rightButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			rightButton.append("arrow_back");
			rightButton.onclick = function() {
				serverConnector.swapColumns(boardID, column.title, prevTitle);
			}
			columnRightLeftSpan.appendChild(rightButton);
		}
		
		if(succTitle != undefined) {
			var leftButton = document.createElement("span");
			leftButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			leftButton.append("arrow_forward");
			leftButton.onclick = function() {
				serverConnector.swapColumns(boardID, column.title, succTitle);
			}
			columnRightLeftSpan.appendChild(leftButton);
		}

		this.rootNode.appendChild(columnRightLeftSpan);
	}

	getNodeTree(){
		return this.rootNode;
	}
}

class TileCreationElement {
	constructor(boardID, columnTitle, tileCreationModal){
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "tile");
		var createTileButton = document.createElement("span");
		createTileButton.classList.add("kanboard", "button", "text-button");
		createTileButton.onclick = function(){
			tileCreationModal.createTile(boardID, columnTitle);
		}
		createTileButton.append("ADD TILE");
		this.rootNode.appendChild(createTileButton);
	}
	
	getNodeTree(){
		return this.rootNode;
	}
}

class TileElement {
	constructor(boardID, columnTitle, tile, tileEditModal, serverConnector, prevID, succID){
		this.tile = tile;
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "tile");
		
		var colorDiv = document.createElement("div");
		colorDiv.classList.add("kanboard", "tile-color");
		colorDiv.style.backgroundColor = tile.color;
		this.rootNode.appendChild(colorDiv);

		//header
		var tileHeader = document.createElement("div");
		tileHeader.classList.add("kanboard", "tile-header");
			//title
		var tileTitle = document.createElement("p");
		if(tile.tileType = "Informative")
			tileTitle.classList.add("kanboard", "tile-title", "tile-informative");
		else
			tileTitle.classList.add("kanboard", "tile-title", "tile-organizational");
		tileTitle.append(tile.title);
		tileHeader.appendChild(tileTitle);

			//edit-delete-div
		var tileEditDeleteDiv = document.createElement("div");

				//edit
		var editButton = document.createElement("span");
		editButton.append("edit");
		editButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
		editButton.onclick = function(){
			tileEditModal.edit(boardID, columnTitle, tile);
		};
		tileEditDeleteDiv.appendChild(editButton);

				//delete
		var deleteButton = document.createElement("span");
		deleteButton.append("delete")
		deleteButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
		deleteButton.onclick = function(){
			serverConnector.deleteTile(boardID, columnTitle, tile.id);
		}
		tileEditDeleteDiv.appendChild(deleteButton);

		tileHeader.appendChild(tileEditDeleteDiv);
		this.rootNode.appendChild(tileHeader);

		//header
		tileHeader = document.createElement("div");
		tileHeader.classList.add("kanboard", "tile-header");
			//up-down-div
		var tileUpDownDiv = document.createElement("div");
				//up
		if(prevID != undefined){
			var upButton = document.createElement("span");
			upButton.append("arrow_upward");
			upButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			upButton.onclick = function(){
				serverConnector.swapTiles(boardID, columnTitle, tile.id, prevID);
			}
			tileUpDownDiv.appendChild(upButton);
		}
				//down
		if(succID != undefined){
			var downButton = document.createElement("span");
			downButton.append("arrow_downward");
			downButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			downButton.onclick = function(){
				serverConnector.swapTiles(boardID, columnTitle, tile.id, succID);
			}
			tileUpDownDiv.appendChild(downButton);
		}

		tileHeader.appendChild(tileUpDownDiv);

			//author
		var tileAuthor = document.createElement("p");
		if(tile.tileType = "Informative")
			tileAuthor.classList.add("kanboard", "tile-author");
		tileAuthor.append(tile.author);
		tileHeader.appendChild(tileAuthor);
		this.rootNode.appendChild(tileHeader);

		
		if(tile.text != undefined){
			var p = document.createElement("p");
			p.append(tile.text);
			p.classList.add("kanboard", "tile-content-text");
			this.rootNode.appendChild(p);
		}

		if(tile.imageURI != undefined){
			var img = document.createElement("img");
			img.setAttribute("src", "tile.imageURL");
			img.classList.add("kanboard", "tile-content-image");
			this.rootNode.appendChild(img);
		}
		
		if(tile.fileURI != undefined){
			var fileButton = document.createElement("a");
			fileButton.classList.add("kanboard", "right", "button", "text-button" );
			fileButton.setAttribute("href", "/files/" + tile.fileURI);
			var span = document.createElement("span");
			span.append(tile.fileURI);
			var icon = document.createElement("span");
			icon.classList.add("material-icons-outlined");
			icon.append("file_download");
			fileButton.appendChild(span);
			fileButton.appendChild(icon);
			this.rootNode.appendChild(fileButton);
		}
	}

	getNodeTree(){
		return this.rootNode();
	}
}

class Modal {
	constructor(serverConnector){
		this.serverConnector = serverConnector;
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "modal-container");
		this.hide();

		this.modalDiv = document.createElement("div");
		this.modalDiv.classList.add("kanboard", "modal");
		this.rootNode.appendChild(this.modalDiv);
	}

	getNodeTree(){
		return this.rootNode;
	}

	show(){
		this.rootNode.style.display = "flex";
	}

	hide(){
		this.rootNode.style.display = "none";
	}

}

class TileCreationModal extends Modal{

	createTile(boardID, columnTitle){
		console.log("/api/" + boardID + "/" + columnTitle + "/tiles/add/");
	}

}

class ColumnCreationModal extends Modal{
	
	createColumn(boardID){
		console.log("/api/" + boardID + "/columns/add/")
	}

}

class BoardCreationModal extends Modal{
	
	createBoard(){
		console.log("/api/" + boardID + "/columns/add/")
	}

}

class TileEditModal extends Modal{

	edit(boardID, columnTitle, tile){
		console.log("/api/" + boardID + "/" + columnTitle + "/" + tile.id + "/edit/");
	}

}

class ColumnEditModal extends Modal{

	edit(boardID, column){
		console.log("/api/" + boardID + "/" + column.title + "/edit/");
	}

}

class LoginModal extends Modal{

	login(){
		console.log("login");
	}

}

class ServerConnector {

	constructor(uri, callbackFunction) {
		this.uri = uri;
		this.callback = callbackFunction;
	}

	getBoard(boardID) {
		var xhr = new XMLHttpRequest();

		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				this.callback(this);
			}
		});

		xhr.open("GET", this.uri + "/api/" + boardID + "/");

		xhr.send();
	}

	getBoards() {
		var xhr = new XMLHttpRequest();

		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				this.callback(this);
			}
		});

		xhr.open("GET", this.uri + "/api/baords/");

		xhr.send();
	}

	deleteTile(boardID, columnTitle, tileID){
		var xhr = new XMLHttpRequest();

		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				this.callback(this);
			}
		});

		xhr.open("DELETE", this.uri + "/api/" + boardID + "/" + columnTitle + "/" + tileID + "/delete/");

		xhr.send();
	}

	swapTiles(boardID, columnTitle, tileID1, tileID2) {
		var xhr = new XMLHttpRequest();

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.addEventListener("readystatechange", function() {
		if(this.readyState === 4) {
			this.callback(this);
		}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/swap/");

		xhr.send("tileID1=" + tileID1 + "&tileID2=" + tileID2);
	}

	swapColumns(boardID, columnTitle1, columnTitle2) {
		var xhr = new XMLHttpRequest();

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.addEventListener("readystatechange", function() {
		if(this.readyState === 4) {
			this.callback(this);
		}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/columns/swap/");

		xhr.send("column1=" + columnTitle1 + "&column2=" + columnTitle2);
	}

	archiveColumn(boardID, columnTitle, archive) {

		var state = "archived";

		if(archive == false){
			state = "active";
		}

		var xhr = new XMLHttpRequest();

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.addEventListener("readystatechange", function() {
		if(this.readyState === 4) {
			this.callback(this);
		}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/edit/");

		xhr.send("state=" + state);
	}

	deleteColumn(boardID, columnTitle){
		var xhr = new XMLHttpRequest();

		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				this.callback(this);
			}
		});

		xhr.open("DELETE", this.uri + "/api/" + boardID + "/" + columnTitle + "/delete/");

		xhr.send();
	}
}
