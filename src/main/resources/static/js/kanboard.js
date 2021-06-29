class Kanboard {

	constructor(containerID, uri){

		this.uri = uri;

		this.boards = undefined;

		this.board = undefined;

		this.state = "active";

		this.rootNode = document.getElementById(containerID);

		this.rootNode.classList.add("kanboard", "container");

		this.loginModal = new LoginModal(this);
		this.rootNode.appendChild(this.loginModal.getNodeTree());

		this.loginModal.login();
	}

	init(username){
		this.serverConnector = new ServerConnector(this.uri, this);

		this.username = username;

		this.tileCreationModal = new TileCreationModal(this.serverConnector, this.username);
		this.rootNode.appendChild(this.tileCreationModal.getNodeTree());
		this.tileEditModal = new TileEditModal(this.serverConnector, this.username);
		this.rootNode.appendChild(this.tileEditModal.getNodeTree());
		this.columnCreationModal = new ColumnCreationModal(this.serverConnector, this.username);
		this.rootNode.appendChild(this.columnCreationModal.getNodeTree());
		this.columnEditModal = new ColumnEditModal(this.serverConnector, this.username);
		this.rootNode.appendChild(this.columnEditModal.getNodeTree());
		this.boardCreationModal = new BoardCreationModal(this.serverConnector, this.username);
		this.rootNode.appendChild(this.boardCreationModal.getNodeTree());

		this.updateBoards();

		
	}

	updateBoard(){
		if(this.boardElement != undefined){
			this.rootNode.removeChild(this.boardElement.getNodeTree());
		}
		this.boardElement = new BoardElement(this.board, this.tileEditModal, this.columnEditModal, this.tileCreationModal, this.columnCreationModal, this.serverConnector, this.state);
		this.rootNode.append(this.boardElement.getNodeTree());
	}

	updateBoards(){
		this.serverConnector.getBoardsHeaders();
	}

	updateNavbar(){
		if(this.navbarElement != undefined){
			this.rootNode.removeChild(this.navbarElement.getNodeTree())
		}
		this.navbarElement = new NavbarElement(this.boards, this.serverConnector, this, this.boardCreationModal, this.loginModal);
		this.rootNode.appendChild(this.navbarElement.getNodeTree());
	}

	setState(state){
		this.state = state;
		console.log(state);
		this.updateBoard();
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

		
		

		if(boards != undefined && boards.length > 0){
			var select = document.createElement("select");
			select.classList.add("kanboard", "input");
			for(var board of boards){
				console.log(board);
				var option = document.createElement("option");
				option.setAttribute("value", board.id);
				option.append(board.title); 
				select.appendChild(option);
			}
	
			select.onchange = function(){
				serverConnector.getBoard(select.value);
			}
	
			buttonsSpan.appendChild(select);
			serverConnector.getBoard(select.value);

			var archiveButton = document.createElement("button");
			archiveButton.classList.add("kanboard", "button", "text-button");
			archiveButton.append("SHOW ARCHIVED");
			archiveButton.onclick = function(){
				if(kanboard.getState() == "archived"){
					kanboard.setState("active");
					archiveButton.removeChild(archiveButton.firstChild);
					archiveButton.append("SHOW ARCHIVED");
				}
					
				else if(kanboard.getState() == "active"){
					kanboard.setState("archived");
					archiveButton.removeChild(archiveButton.firstChild);
					archiveButton.append("SHOW ACTIVE");
				}
					
			}
			buttonsSpan.appendChild(archiveButton);
		}
		

		

		var newBoardButton = document.createElement("button");
		newBoardButton.classList.add("kanboard", "button", "text-button");
		newBoardButton.append("ADD BOARD");
		newBoardButton.onclick = function() {
			boardCreationModal.createBoard();
		}
		buttonsSpan.appendChild(newBoardButton);

		this.rootNode.appendChild(buttonsSpan);

		var loginButton = document.createElement("span");
		loginButton.classList.add("kanboard", "button", "text-button");

		var span = document.createElement("span");
		span.append(kanboard.username);
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

	getNodeTree(){
		return this.rootNode;
	}
}

class BoardElement {

	constructor(board, tileEditModal, columnEditModal, tileCreationModal, columnCreationModal, serverConnector, state) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "board");
		for (var index in board.columns) {
			if(board.columns[index].state == state){

				var prevTitle = undefined;
				var succTitle = undefined;

				if(board.columns[+index - 1] != undefined)
					prevTitle = board.columns[+index - 1].title;
			
				if(board.columns[+index + 1] != undefined)
					succTitle = board.columns[+index + 1].title;

				var column = new ColumnElement(board.id, board.columns[index], tileEditModal, columnEditModal, tileCreationModal, serverConnector, prevTitle, succTitle);

				this.rootNode.appendChild(column.getNodeTree());
			}
		}

		if(state == "active"){
			var columnCreationElement = new ColumnCreationElement (board.id, columnCreationModal);
			this.rootNode.appendChild(columnCreationElement.getNodeTree());
		}
		
	}

	getNodeTree(){
		return this.rootNode;
	}

}

class ColumnCreationElement {

	constructor(boardID, columnCreationModal) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "column");
		
		var tile = document.createElement("div");
		tile.classList.add("kanboard", "tile");

		var span = document.createElement("span");
		span.classList.add("kanboard", "button", "text-button");
		span.onclick = function(){
			columnCreationModal.createColumn(boardID);
		}
		span.append("ADD COLUMN");
		tile.appendChild(span);

		this.rootNode.appendChild(tile);
	}

	getNodeTree(){
		return this.rootNode;
	}

}

class ColumnElement {

	constructor(boardID, column, tileEditModal, columnEditModal, tileCreationModal, serverConnector, prevTitle, succTitle) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "column");
		

		var columnHeaderElement = new ColumnHeaderElement(boardID, column, columnEditModal, serverConnector, prevTitle, succTitle);

		this.rootNode.appendChild(columnHeaderElement.getNodeTree());

		for(var index in column.tiles){
			var prevTileID = undefined;
			var succTileID = undefined;

			if(column.tiles[+index - 1] != undefined)
				prevTileID = column.tiles[+index - 1].id;
			
			if(column.tiles[+index + 1] != undefined)
				succTileID = column.tiles[+index + 1].id;

			var tile = new TileElement(boardID, column.title, column.tiles[index], tileEditModal, serverConnector, prevTileID, succTileID);
			this.rootNode.appendChild(tile.getNodeTree());
		}

		if(column.state == "active"){
			var tileCreationElement = new TileCreationElement(boardID, column.title, tileCreationModal);
			this.rootNode.appendChild(tileCreationElement.getNodeTree());
		}
		
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
		tileHeader.classList.add("kanboard", "buttons-line");

		var tileTitle = document.createElement("p");
		tileTitle.classList.add("kanboard", "column-title");
		tileTitle.append(column.title);
		tileHeader.appendChild(tileTitle);

		var buttonsSpan = document.createElement("span");
		var archiveButton = document.createElement("span");
		archiveButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button")

		if(column.state == "active"){
			archiveButton.append("archive");
			archiveButton.onclick = function() {
				serverConnector.archiveColumn(boardID, column.title);
			}
		}

		if(column.state == "archived"){
			archiveButton.append("unarchive");
			archiveButton.onclick = function() {
				serverConnector.archiveColumn(boardID, column.title, false);
			}
		}

		buttonsSpan.appendChild(archiveButton);

		if(column.state == "active"){
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
		}
		

		

		tileHeader.appendChild(buttonsSpan);

		this.rootNode.appendChild(tileHeader);

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

		
		var tileHeader = document.createElement("div");
		tileHeader.classList.add("kanboard", "buttons-line");
		
		var tileTitle = document.createElement("p");
		if(tile.tileType = "Informative")
			tileTitle.classList.add("kanboard", "tile-title", "tile-informative");
		else
			tileTitle.classList.add("kanboard", "tile-title", "tile-organizational");
		tileTitle.append(tile.title);
		tileHeader.appendChild(tileTitle);

		
		var tileEditDeleteDiv = document.createElement("div");

		
		var editButton = document.createElement("span");
		editButton.append("edit");
		editButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
		editButton.onclick = function(){
			tileEditModal.edit(boardID, columnTitle, tile);
		};
		tileEditDeleteDiv.appendChild(editButton);

		
		var deleteButton = document.createElement("span");
		deleteButton.append("delete")
		deleteButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
		deleteButton.onclick = function(){
			serverConnector.deleteTile(boardID, columnTitle, tile.id);
		}
		tileEditDeleteDiv.appendChild(deleteButton);

		tileHeader.appendChild(tileEditDeleteDiv);
		this.rootNode.appendChild(tileHeader);

		
		tileHeader = document.createElement("div");
		tileHeader.classList.add("kanboard", "buttons-line");
		
		var tileUpDownDiv = document.createElement("div");
		
		if(prevID != undefined){
			var upButton = document.createElement("span");
			upButton.append("arrow_upward");
			upButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			upButton.onclick = function(){
				serverConnector.swapTiles(boardID, columnTitle, tile.id, prevID);
			}
			tileUpDownDiv.appendChild(upButton);
		}
		
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
			img.setAttribute("src", "/files/" + tile.imageURI);
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
		return this.rootNode;
	}
}

class Modal {
	constructor(serverConnector, author){
		this.serverConnector = serverConnector;
		this.author = author;

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

	constructor(serverConnector, author){
		super(serverConnector, author);
		this.form = document.createElement("form");
		this.form.classList.add("kanboard", "form");

		this.titleInput = document.createElement("input");
		this.titleInput.classList.add("kanboard", "input");
		this.titleInput.setAttribute("type", "text");
		this.titleInput.setAttribute("placeholder", "title");
		this.titleInput.setAttribute("name", "tileTitle");
		this.form.appendChild(this.titleInput);

		this.selectType = document.createElement("select");
		this.selectType.classList.add("kanboard", "input");
		this.selectType.setAttribute("name", "tileType");

		var option = document.createElement("option");
		option.setAttribute("value", "Informative");
		option.append("Informative");
		this.selectType.appendChild(option);

		var option = document.createElement("option");
		option.setAttribute("value", "Organizational");
		option.append("Organizational"); 
		this.selectType.appendChild(option);

		this.form.appendChild(this.selectType);

		this.selectContent = document.createElement("select");
		this.selectContent.classList.add("kanboard", "input");
		this.selectContent.setAttribute("name", "contentType");

		var option = document.createElement("option");
		option.setAttribute("value", "text");
		option.append("Text");
		this.selectContent.appendChild(option);

		var option = document.createElement("option");
		option.setAttribute("value", "image");
		option.append("Image"); 
		this.selectContent.appendChild(option);

		var option = document.createElement("option");
		option.setAttribute("value", "file");
		option.append("File"); 
		this.selectContent.appendChild(option);

		this.form.appendChild(this.selectContent);

		this.textInput = document.createElement("textarea");
		this.textInput.classList.add("kanboard", "input");
		this.textInput.setAttribute("placeholder", "text content");
		this.textInput.setAttribute("name", "text");
		this.form.appendChild(this.textInput);

		this.colorInput = document.createElement("input");
		this.colorInput.classList.add("kanboard", "input");
		this.colorInput.setAttribute("type", "color");
		this.colorInput.setAttribute("name", "color");
		this.form.appendChild(this.colorInput);

		this.fileURIInput = document.createElement("input");
		this.fileURIInput.setAttribute("type", "hidden");
		this.fileURIInput.setAttribute("name", "fileURI");
		this.form.appendChild(this.fileURIInput);

		this.authorInput = document.createElement("input");
		this.authorInput.setAttribute("type", "hidden");
		this.authorInput.setAttribute("name", "author");
		this.authorInput.value = this.author;
		this.form.appendChild(this.authorInput);

		this.modalDiv.appendChild(this.form);

		this.fileForm = document.createElement("form");
		this.fileForm.classList.add("kanboard", "form");

		this.fileLabel = document.createElement("label");
		this.fileLabel.classList.add("kanboard", "input");

		this.filename = document.createElement("span");
		this.filename.append("select file");
		this.fileLabel.appendChild(this.filename);

		this.fileInput = document.createElement("input");
		this.fileInput.classList.add("kanboard", "input");
		this.fileInput.setAttribute("type", "file");
		this.fileInput.setAttribute("name", "file");
		this.fileInput.style.display = "none";
		this.fileLabel.appendChild(this.fileInput);

		this.fileForm.appendChild(this.fileLabel);

		this.modalDiv.appendChild(this.fileForm);

		var modalContext = this;

		var buttonsDiv = document.createElement("div");
		buttonsDiv.classList.add("kanboard", "buttons-line");
		
		this.createButton = document.createElement("button");
		this.createButton.classList.add("kanboard", "button", "text-button");
		this.createButton.append("CREATE");
		buttonsDiv.append(this.createButton);

		var cancelButton = document.createElement("button");
		cancelButton.classList.add("kanboard", "button", "cancel-button");
		cancelButton.append("CANCEL");
		cancelButton.onclick = function(){
			modalContext.hide();
		}
		buttonsDiv.append(cancelButton);

		this.modalDiv.append(buttonsDiv);
		
	}

	createTile(boardID, columnTitle){
		this.titleInput.value = "";
		this.textInput.value = "";
		this.fileInput.value = "";
		this.filename.firstChild.remove();
		this.filename.append("select file");

		var modalContext = this;
		this.createButton.onclick = function(){
			switch(modalContext.selectContent.value){
				case "text":
					modalContext.serverConnector.createTextTile(boardID, columnTitle, modalContext.form);
					break;
				case "image":
					modalContext.serverConnector.createImageTile(boardID, columnTitle, modalContext.fileForm, modalContext.fileURIInput, modalContext.form);
					break;
				case "file":
					modalContext.serverConnector.createFileTile(boardID, columnTitle, modalContext.fileForm, modalContext.fileURIInput, modalContext.form);
			}
		}

		this.show();

		console.log("/api/" + boardID + "/" + columnTitle + "/tiles/add/");
	}

	validate(){
		var modalContext = this;
		this.createButton.onclick = function(){
			switch(modalContext.selectContent.value){
				case "text":
					modalContext.serverConnector.createTextTile(boardID, columnTitle, modalContext.form);
					break;
				case "image":
					modalContext.serverConnector.createImageTile(boardID, columnTitle, modalContext.fileForm, modalContext.fileURIInput, modalContext.form);
					break;
				case "file":
					modalContext.serverConnector.createFileTile(boardID, columnTitle, modalContext.fileForm, modalContext.fileURIInput, modalContext.form);
			}
		}
	}

}

class ColumnCreationModal extends Modal{

	constructor(serverConnector){
		super(serverConnector);
		this.form = document.createElement("form");
		this.form.classList.add("kanboard", "form");

		this.titleInput = document.createElement("input");
		this.titleInput.classList.add("kanboard", "input");
		this.titleInput.setAttribute("type", "text");
		this.titleInput.setAttribute("placeholder", "title");
		this.titleInput.setAttribute("name", "columnTitle");
		this.form.appendChild(this.titleInput);

		this.colorInput = document.createElement("input");
		this.colorInput.classList.add("kanboard", "input");
		this.colorInput.setAttribute("type", "color");
		this.colorInput.setAttribute("name", "color");
		this.form.appendChild(this.colorInput);

		this.modalDiv.appendChild(this.form);

		var modalContext = this;

		var buttonsDiv = document.createElement("div");
		buttonsDiv.classList.add("kanboard", "buttons-line");
		
		this.createButton = document.createElement("button");
		this.createButton.classList.add("kanboard", "button", "text-button");
		this.createButton.append("CREATE");
		buttonsDiv.append(this.createButton);

		var cancelButton = document.createElement("button");
		cancelButton.classList.add("kanboard", "button", "cancel-button");
		cancelButton.append("CANCEL");
		cancelButton.onclick = function(){
			modalContext.hide();
		}
		buttonsDiv.append(cancelButton);

		this.modalDiv.append(buttonsDiv);
		
	}
	
	createColumn(boardID){
		console.log("/api/" + boardID + "/columns/add/");
		this.titleInput.value = "";
		this.colorInput.value = "#f00";

		var modalContext = this;
		this.createButton.onclick = function(){
			modalContext.serverConnector.createColumn(boardID, new FormData(modalContext.form));
			modalContext.hide();
		}
		this.show();

		
	}

}

class BoardCreationModal extends Modal{

	constructor(serverConnector){
		super(serverConnector);
		this.form = document.createElement("form");
		this.form.classList.add("kanboard", "form");

		this.titleInput = document.createElement("input");
		this.titleInput.classList.add("kanboard", "input");
		this.titleInput.setAttribute("type", "text");
		this.titleInput.setAttribute("placeholder", "title");
		this.titleInput.setAttribute("name", "boardTitle");
		this.form.appendChild(this.titleInput);

		this.modalDiv.appendChild(this.form);

		var modalContext = this;

		var buttonsDiv = document.createElement("div");
		buttonsDiv.classList.add("kanboard", "buttons-line");
		
		this.createButton = document.createElement("button");
		this.createButton.classList.add("kanboard", "button", "text-button");
		this.createButton.append("CREATE");
		this.createButton.onclick = function(){
			modalContext.serverConnector.createBoard(new FormData(modalContext.form));
			modalContext.hide();
		}
		buttonsDiv.append(this.createButton);

		var cancelButton = document.createElement("button");
		cancelButton.classList.add("kanboard", "button", "cancel-button");
		cancelButton.append("CANCEL");
		cancelButton.onclick = function(){
			modalContext.hide();
		}
		buttonsDiv.append(cancelButton);

		this.modalDiv.append(buttonsDiv);
		
	}
	
	createBoard(){
		console.log("/api/boards/add/");
		this.titleInput.value = "";
		this.show();
	}

}

class TileEditModal extends Modal{

	edit(boardID, columnTitle, tile){
		console.log("/api/" + boardID + "/" + columnTitle + "/" + tile.id + "/edit/");
	}

}

class ColumnEditModal extends Modal{

	edit(boardID, columnTitle){
		console.log("/api/" + boardID + "/" + columnTitle + "/edit/");
	}

}

class LoginModal extends Modal{

	constructor(kanboard){
		super();
		var form = document.createElement("form");
		form.classList.add("kanboard", "form");
		
		var usernameInput = document.createElement("input");
		usernameInput.classList.add("kanboard", "input");
		usernameInput.setAttribute("type", "text");
		usernameInput.setAttribute("placeholder", "username");
		usernameInput.setAttribute("name", "username");

		form.appendChild(usernameInput);
		this.modalDiv.appendChild(form);

		var button = document.createElement("button");
		button.disabled = true;
		button.classList.add("kanboard", "button", "text-button");
		button.append("LOGIN");
		this.modalDiv.appendChild(button);


		usernameInput.onchange = function(){
			if(usernameInput.value == "" || usernameInput.value == undefined){
				button.disabled = true;
			}
			else{
				button.disabled = false;
			}
		}

		var modalContext = this;
		button.onclick = function(){
			kanboard.init(usernameInput.value);
			modalContext.hide();
		}
	}

	login(){
		console.log("login");
		this.show();
	}

}

class ServerConnector {

	constructor(uri, kanboard) {
		this.uri = uri;
		this.kanboard = kanboard;
	}

	createBoard(data){
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				connectorContext.getBoards()
			}
		});

		xhr.open("POST", this.uri + "/api/boards/add/");

		xhr.send(data);
	}

	createColumn(boardID, data){
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				connectorContext.getBoard(boardID);
			}
		});

		xhr.open("POST", this.uri + "/api/" + boardID + "/columns/add/");

		xhr.send(data);
	}

	createFileTile(boardID, columnTitle, fileForm, fileURIInput, dataForm) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				fileURIInput.value = this.responseText;
				fileURIInput.setAttribute("name", "fileURI");
				connectorContext.createTile(boardID, columnTitle, new FormData(dataForm));
			}
		});

		xhr.open("POST", this.uri + "/files/");

		xhr.send(new FormData(fileForm));
	}

	createImageTile(boardID, columnTitle, fileForm, fileURIInput, dataForm) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				fileURIInput.value = this.responseText;
				fileURIInput.setAttribute("name", "imageURI");
				connectorContext.createTile(boardID, columnTitle, new FormData(dataForm));
			}
		});

		xhr.open("POST", this.uri + "/files/");

		xhr.send(new FormData(fileForm));
	}

	createTextTile(boardID, columnTitle, dataForm) {
		this.createTile(boardID, columnTitle, new FormData(dataForm));
	}

	createTile(boardID, columnTitle, data) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				connectorContext.getBoard(boardID);
			}
		});

		xhr.open("POST", this.uri + "/api/" + boardID + "/" + columnTitle + "/tiles/add/");

		xhr.send(data);
	}

	getBoard(boardID) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				connectorContext.kanboard.board = JSON.parse(this.response);
				connectorContext.kanboard.updateBoard();
			}
		});

		xhr.open("GET", this.uri + "/api/" + boardID + "/");

		xhr.send();
	}

	getBoards() {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				connectorContext.kanboard.boards = JSON.parse(this.response);
				connectorContext.kanboard.updateNavbar();
			}
		});

		xhr.open("GET", this.uri + "/api/boards/");

		xhr.send();
	}

	getBoardsHeaders() {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				connectorContext.kanboard.boards = JSON.parse(this.response);
				connectorContext.kanboard.updateNavbar();
			}
		});

		xhr.open("GET", this.uri + "/api/boards/headers/");

		xhr.send();
	}

	deleteTile(boardID, columnTitle, tileID){
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				connectorContext.getBoard(connectorContext.kanboard.board.id);
			}
		});

		xhr.open("DELETE", this.uri + "/api/" + boardID + "/" + columnTitle + "/" + tileID + "/delete/");

		xhr.send();
	}

	swapTiles(boardID, columnTitle, tileID1, tileID2) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function() {
		if(this.readyState === 4) {
			connectorContext.getBoard(connectorContext.kanboard.board.id);
		}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/tiles/swap/");
		
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.send("tileID1=" + tileID1 + "&tileID2=" + tileID2);
	}

	swapColumns(boardID, columnTitle1, columnTitle2) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function() {
		if(this.readyState === 4) {
			connectorContext.getBoard(connectorContext.kanboard.board.id)
		}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/columns/swap/");

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.send("column1=" + columnTitle1 + "&column2=" + columnTitle2);
	}

	archiveColumn(boardID, columnTitle, archive) {

		var state = "archived";

		if(archive == false){
			state = "active";
		}

		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function() {
		if(this.readyState === 4) {
			connectorContext.getBoard(connectorContext.kanboard.board.id)
		}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/edit/");

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.send("state=" + state);
	}

	deleteColumn(boardID, columnTitle){
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				connectorContext.getBoard(connectorContext.kanboard.board.id)
			}
		});

		xhr.open("DELETE", this.uri + "/api/" + boardID + "/" + columnTitle + "/delete/");

		xhr.send();
	}
}
