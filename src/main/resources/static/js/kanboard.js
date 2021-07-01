class Kanboard {

	constructor(containerID, uri) {

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

	init(username) {
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

		var kanboardContext = this;
		this.nabarInterval = setInterval(function() {
			kanboardContext.serverConnector.getBoardsHeaders();
		}, 3000);

		this.baordInterval = setInterval(function() {
			kanboardContext.serverConnector.getBoard(kanboardContext.board.id);
		}, 3000);
	}

	updateBoard() {
		if (this.boardElement != undefined) {
			this.rootNode.removeChild(this.boardElement.getNodeTree());
		}
		this.boardElement = new BoardElement(this.board, this.tileEditModal, this.columnEditModal, this.tileCreationModal, this.columnCreationModal, this.serverConnector, this.state);
		this.rootNode.append(this.boardElement.getNodeTree());
	}

	updateBoards() {
		this.serverConnector.getBoardsHeaders();
	}

	updateNavbar() {
		if (this.navbarElement != undefined) {
			this.rootNode.removeChild(this.navbarElement.getNodeTree())
		}
		this.navbarElement = new NavbarElement(this.boards, this.serverConnector, this, this.boardCreationModal, this.loginModal);
		this.rootNode.insertBefore(this.navbarElement.getNodeTree(), this.rootNode.firstChild);
	}

	setState(state) {
		this.state = state;
		this.updateBoard();
	}

	getState() {
		return this.state;
	}

}

class NavbarElement {

	constructor(boards, serverConnector, kanboard, boardCreationModal, loginModal) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "navbar");

		var buttonsSpan = document.createElement("span");
		buttonsSpan.classList.add("kanboard", "buttons-line");

		if (boards != undefined && boards.length > 0) {
			var select = document.createElement("select");
			select.classList.add("kanboard", "input");
			for (var board of boards) {
				var option = document.createElement("option");
				option.setAttribute("value", board.id);
				option.append(board.title);
				select.appendChild(option);
			}

			select.onchange = function () {
				kanboard.lastBoardModified = undefined;
				serverConnector.getBoard(select.value);
			}

			buttonsSpan.appendChild(select);
			serverConnector.getBoard(select.value);

			var archiveButton = document.createElement("button");
			archiveButton.classList.add("kanboard", "button", "text-button");
			archiveButton.append("SHOW ARCHIVED");
			archiveButton.onclick = function () {
				if (kanboard.getState() == "archived") {
					kanboard.setState("active");
					archiveButton.removeChild(archiveButton.firstChild);
					archiveButton.append("SHOW ARCHIVED");
				}

				else if (kanboard.getState() == "active") {
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
		newBoardButton.onclick = function () {
			boardCreationModal.createBoard();
		}
		buttonsSpan.appendChild(newBoardButton);

		var helpButton = document.createElement("a");
		helpButton.setAttribute("href", "help.html");
		helpButton.classList.add("kanboard", "unboxed-button", "material-icons-outlined");
		helpButton.style.textDecoration = "none";
		helpButton.append("help");

		buttonsSpan.appendChild(helpButton);

		this.rootNode.appendChild(buttonsSpan);

		/*
		var legend = document.createElement("span");
		legend.classList.add("kanboard", "buttons-line");

		var informativeLegend = document.createElement("div");
		informativeLegend.style.margin = "0.5rem";
		informativeLegend.classList.add("kanboard", "tile");
		var informativeText = document.createElement("span");
		informativeText.classList.add("kanboard", "tile-title", "tile-informative");
		informativeText.append("Informative");
		informativeLegend.appendChild(informativeText);
		var informativeColor = document.createElement("div");
		informativeColor.classList.add("kanboard", "tile-color", "tile-informative");
		informativeColor.style.backgroundColor = "#f3a704";
		informativeLegend.appendChild(informativeColor);
		legend.appendChild(informativeLegend);

		var organizationalLegend = document.createElement("div");
		organizationalLegend.style.margin = "0.5rem";
		organizationalLegend.classList.add("kanboard", "tile");
		var organizationalText = document.createElement("span");
		organizationalText.classList.add("kanboard", "tile-title", "tile-organizational");
		organizationalText.append("Organizational");
		organizationalLegend.appendChild(organizationalText);
		var organizationalColor = document.createElement("div");
		organizationalColor.classList.add("kanboard", "tile-color", "tile-organizational");
		organizationalColor.style.backgroundColor = "#f3a704";
		organizationalLegend.appendChild(organizationalColor);
		legend.appendChild(organizationalLegend);

		this.rootNode.append(legend);
		*/

		var logo = document.createElement("img");
		logo.setAttribute("src", "/img/logo.png");
		logo.style.position = "relative";
		logo.style.height = "60%";
		logo.style.margin = "0.2rem";
		this.rootNode.appendChild(logo);

		var loginButton = document.createElement("span");
		loginButton.classList.add("kanboard", "button", "text-button");

		var span = document.createElement("span");
		span.append(kanboard.username);
		loginButton.appendChild(span);
		var icon = document.createElement("span");
		icon.classList.add("material-icons-outlined");
		icon.append("account_circle");
		icon.style.paddingLeft = "0.3rem";
		loginButton.appendChild(icon);

		loginButton.onclick = function () {
			loginModal.login();
		}

		this.rootNode.append(loginButton);
	}

	getNodeTree() {
		return this.rootNode;
	}
}

class BoardElement {

	constructor(board, tileEditModal, columnEditModal, tileCreationModal, columnCreationModal, serverConnector, state) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "board");

		var columns = new Array();
		for (var col of board.columns) {
			if (col.state == state)
				columns.push(col);
		}

		for (var index in columns) {
			if (columns[index].state == state) {
				var prevTitle = undefined;
				var succTitle = undefined;


				if (state == "active") {
					if (columns[+index - 1] != undefined)
						prevTitle = board.columns[+index - 1].title;

					if (columns[+index + 1] != undefined)
						succTitle = columns[+index + 1].title;
				}


				var column = new ColumnElement(board.id, columns[index], tileEditModal, columnEditModal, tileCreationModal, serverConnector, prevTitle, succTitle);

				this.rootNode.appendChild(column.getNodeTree());
			}
		}

		if (state == "active") {
			var columnCreationElement = new ColumnCreationElement(board.id, columnCreationModal);
			this.rootNode.appendChild(columnCreationElement.getNodeTree());
		}

	}

	getNodeTree() {
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
		span.onclick = function () {
			columnCreationModal.createColumn(boardID);
		}
		span.append("ADD COLUMN");
		tile.appendChild(span);

		this.rootNode.appendChild(tile);
	}

	getNodeTree() {
		return this.rootNode;
	}

}

class ColumnElement {

	constructor(boardID, column, tileEditModal, columnEditModal, tileCreationModal, serverConnector, prevTitle, succTitle) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "column");


		var columnHeaderElement = new ColumnHeaderElement(boardID, column, columnEditModal, serverConnector, prevTitle, succTitle);

		this.rootNode.appendChild(columnHeaderElement.getNodeTree());

		for (var index in column.tiles) {
			var prevTileID = undefined;
			var succTileID = undefined;

			if (column.state == "active") {
				if (column.tiles[+index - 1] != undefined)
					prevTileID = column.tiles[+index - 1].id;

				if (column.tiles[+index + 1] != undefined)
					succTileID = column.tiles[+index + 1].id;
			}


			var tile = new TileElement(boardID, column.title, column.tiles[index], tileEditModal, serverConnector, column.state, prevTileID, succTileID, prevTitle, succTitle);
			this.rootNode.appendChild(tile.getNodeTree());
		}

		if (column.state == "active") {
			var tileCreationElement = new TileCreationElement(boardID, column.title, tileCreationModal);
			this.rootNode.appendChild(tileCreationElement.getNodeTree());
		}

	}

	getNodeTree() {
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

		if (column.state == "active") {
			archiveButton.append("archive");
			archiveButton.title = "archive";
			archiveButton.onclick = function () {
				serverConnector.archiveColumn(boardID, column.title);
			}
		}

		if (column.state == "archived") {
			archiveButton.append("unarchive");
			archiveButton.title = "unarchive";
			archiveButton.onclick = function () {
				serverConnector.archiveColumn(boardID, column.title, false);
			}
		}

		buttonsSpan.appendChild(archiveButton);

		if (column.state == "active") {
			var editButton = document.createElement("span");
			editButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button")
			editButton.append("edit");
			editButton.title = "edit";
			editButton.onclick = function () {
				columnEditModal.edit(boardID, column);
			}
			buttonsSpan.appendChild(editButton);

			var deleteButton = document.createElement("span");
			deleteButton.classList.add("material-icons-outlined", "kanboard", "delete-button");
			deleteButton.append("delete");
			deleteButton.title = "delete";
			deleteButton.onclick = function () {
				serverConnector.deleteColumn(boardID, column.title);
			}
			buttonsSpan.appendChild(deleteButton);
		}


		tileHeader.appendChild(buttonsSpan);

		this.rootNode.appendChild(tileHeader);

		var columnRightLeftSpan = document.createElement("span");

		if (prevTitle != undefined) {
			var leftButton = document.createElement("span");
			leftButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			leftButton.append("arrow_back");
			leftButton.title = "move left";
			leftButton.onclick = function () {
				serverConnector.swapColumns(boardID, column.title, prevTitle);
			}
			columnRightLeftSpan.appendChild(leftButton);
		}

		if (succTitle != undefined) {
			var rightButton = document.createElement("span");
			rightButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			rightButton.append("arrow_forward");
			rightButton.title = "move right";
			rightButton.onclick = function () {
				serverConnector.swapColumns(boardID, column.title, succTitle);
			}
			columnRightLeftSpan.appendChild(rightButton);
		}

		this.rootNode.appendChild(columnRightLeftSpan);
	}

	getNodeTree() {
		return this.rootNode;
	}

}

class TileCreationElement {
	constructor(boardID, columnTitle, tileCreationModal) {
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "tile");
		var createTileButton = document.createElement("span");
		createTileButton.classList.add("kanboard", "button", "text-button");
		createTileButton.onclick = function () {
			tileCreationModal.createTile(boardID, columnTitle);
		}
		createTileButton.append("ADD TILE");
		this.rootNode.appendChild(createTileButton);
	}

	getNodeTree() {
		return this.rootNode;
	}
}

class TileElement {
	constructor(boardID, columnTitle, tile, tileEditModal, serverConnector, state, prevID, succID, prevTitle, succTitle) {
		this.tile = tile;
		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "tile");

		var colorDiv = document.createElement("div");
		//colorDiv.classList.add("kanboard", "tile-color");
		colorDiv.style.backgroundColor = tile.color;
		this.rootNode.appendChild(colorDiv);


		var tileHeader = document.createElement("div");
		tileHeader.classList.add("kanboard", "buttons-line");

		var tileTitle = document.createElement("p");
		if (tile.tileType == "Informative") {
			tileTitle.classList.add("kanboard", "tile-title", "tile-informative");
			colorDiv.classList.add("kanboard", "tile-color", "tile-informative");
			this.rootNode.title = "Informative";
		} else {
			tileTitle.classList.add("kanboard", "tile-title", "tile-organizational");
			colorDiv.classList.add("kanboard", "tile-color", "tile-organizational");
			this.rootNode.title = "Organizational";
		}
		tileTitle.append(tile.title);
		tileHeader.appendChild(tileTitle);


		var tileEditDeleteDiv = document.createElement("div");

		if (state == "active") {
			var editButton = document.createElement("span");
			editButton.append("edit");
			editButton.title = "edit";
			editButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			editButton.onclick = function () {
				tileEditModal.edit(boardID, columnTitle, tile);
			};
			tileEditDeleteDiv.appendChild(editButton);

			var deleteButton = document.createElement("span");
			deleteButton.append("delete");
			deleteButton.title = "delete";
			deleteButton.classList.add("material-icons-outlined", "kanboard", "delete-button");
			deleteButton.onclick = function () {
				serverConnector.deleteTile(boardID, columnTitle, tile.id);
			}
			tileEditDeleteDiv.appendChild(deleteButton);
		}


		tileHeader.appendChild(tileEditDeleteDiv);
		this.rootNode.appendChild(tileHeader);


		tileHeader = document.createElement("div");
		tileHeader.classList.add("kanboard", "buttons-line");

		var tileUpDownDiv = document.createElement("div");

		if (prevTitle != undefined) {
			var leftButton = document.createElement("span");
			leftButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			leftButton.append("arrow_back");
			leftButton.title = "move left";
			leftButton.onclick = function () {
				serverConnector.moveTile(boardID, columnTitle, tile.id, prevTitle);
			}
			tileUpDownDiv.appendChild(leftButton);
		}

		if (prevID != undefined) {
			var upButton = document.createElement("span");
			upButton.append("arrow_upward");
			upButton.title = "move up";
			upButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			upButton.onclick = function () {
				serverConnector.swapTiles(boardID, columnTitle, tile.id, prevID);
			}
			tileUpDownDiv.appendChild(upButton);
		}

		if (succID != undefined) {
			var downButton = document.createElement("span");
			downButton.append("arrow_downward");
			downButton.title = "move down";
			downButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			downButton.onclick = function () {
				serverConnector.swapTiles(boardID, columnTitle, tile.id, succID);
			}
			tileUpDownDiv.appendChild(downButton);
		}

		if (succTitle != undefined) {
			var rightButton = document.createElement("span");
			rightButton.classList.add("material-icons-outlined", "kanboard", "unboxed-button");
			rightButton.append("arrow_forward");
			rightButton.title = "move right";
			rightButton.onclick = function () {
				serverConnector.moveTile(boardID, columnTitle, tile.id, succTitle);
			}
			tileUpDownDiv.appendChild(rightButton);
		}

		tileHeader.appendChild(tileUpDownDiv);

		var tileAuthor = document.createElement("p");
		tileAuthor.classList.add("kanboard", "tile-author");
		tileAuthor.append(tile.author);
		tileHeader.appendChild(tileAuthor);
		this.rootNode.appendChild(tileHeader);


		if (tile.text != undefined) {
			var p = document.createElement("p");
			p.append(tile.text);
			p.classList.add("kanboard", "tile-content-text");
			this.rootNode.appendChild(p);
		}

		if (tile.imageURI != undefined) {
			var img = document.createElement("img");
			img.setAttribute("src", "/images/" + tile.imageURI);
			img.classList.add("kanboard", "tile-content-image");
			this.rootNode.appendChild(img);
		}

		if (tile.fileURI != undefined) {
			var fileButton = document.createElement("a");
			fileButton.classList.add("kanboard", "right", "button", "text-button");
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

	getNodeTree() {
		return this.rootNode;
	}
}

class Modal {
	constructor(serverConnector, author) {
		this.serverConnector = serverConnector;
		this.author = author;

		this.rootNode = document.createElement("div");
		this.rootNode.classList.add("kanboard", "modal-container");
		this.hide();

		this.modalDiv = document.createElement("div");
		this.modalDiv.classList.add("kanboard", "modal");
		this.rootNode.appendChild(this.modalDiv);
	}

	getNodeTree() {
		return this.rootNode;
	}

	show() {
		this.rootNode.style.display = "flex";
	}

	hide() {
		this.rootNode.style.display = "none";
	}

}

class TileCreationModal extends Modal {

	constructor(serverConnector, author) {
		super(serverConnector, author);

		var modalContext = this;

		this.form = document.createElement("form");
		this.form.classList.add("kanboard", "form");
		this.form.onchange = function () {
			modalContext.validate();
		}
		this.form.onkeyup = function () {
			modalContext.validate();
		}

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

		var colorInputSpan = document.createElement("span");
		colorInputSpan.classList.add("kanboard", "buttons-line", "input");
		colorInputSpan.append("Color: ");

		this.colorInput = document.createElement("input");
		this.colorInput.classList.add("kanboard", "color-input");
		this.colorInput.setAttribute("type", "color");
		this.colorInput.setAttribute("name", "color");

		colorInputSpan.appendChild(this.colorInput);
		this.form.appendChild(colorInputSpan);

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
		this.fileForm.onchange = function () {
			modalContext.validate();
		}

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

		this.form.setAttribute("action", "javascript:void(0);");
		this.fileForm.setAttribute("action", "javascript:void(0);");

		var buttonsDiv = document.createElement("div");
		buttonsDiv.classList.add("kanboard", "buttons-line");

		this.createButton = document.createElement("button");
		this.createButton.classList.add("kanboard", "button", "text-button");
		this.createButton.append("CREATE");
		buttonsDiv.append(this.createButton);

		var cancelButton = document.createElement("button");
		cancelButton.classList.add("kanboard", "button", "cancel-button");
		cancelButton.append("CANCEL");
		cancelButton.onclick = function () {
			modalContext.hide();
		}
		buttonsDiv.append(cancelButton);

		this.modalDiv.append(buttonsDiv);

	}

	createTile(boardID, columnTitle) {
		this.boardID = boardID;
		this.columnTitle = columnTitle;
		this.titleInput.value = "";
		this.textInput.value = "";
		this.fileInput.value = "";
		this.filename.firstChild.remove();
		this.filename.append("select file");

		this.show();
		this.validate();
	}

	validate() {
		var modalContext = this;
		this.createButton.onclick = function () {
			switch (modalContext.selectContent.value) {
				case "text":
					modalContext.serverConnector.createTextTile(modalContext.boardID, modalContext.columnTitle, modalContext.form);
					break;
				case "image":
					modalContext.serverConnector.createImageTile(modalContext.boardID, modalContext.columnTitle, modalContext.fileForm, modalContext.fileURIInput, modalContext.form);
					break;
				case "file":
					modalContext.serverConnector.createFileTile(modalContext.boardID, modalContext.columnTitle, modalContext.fileForm, modalContext.fileURIInput, modalContext.form);
			}
			modalContext.hide();
		}
		var valid = true;
		if (this.titleInput.value == "") {
			valid = false;
		}

		switch (this.selectContent.value) {
			case "text":
				this.fileLabel.disabled = true;
				this.fileLabel.classList.add("disabled");
				this.fileInput.disabled = true;
				this.fileInput.classList.add("disabled");
				this.textInput.disabled = false;
				this.textInput.classList.remove("disabled");
				if (this.textInput.value == "") {
					valid = false;
				}

				break;
			case "image":
				this.fileLabel.disabled = false;
				this.fileLabel.classList.remove("disabled");
				this.fileInput.disabled = false;
				this.fileInput.classList.remove("disabled");
				this.textInput.disabled = true;
				this.textInput.classList.add("disabled");
				if (this.fileInput.value == "") {
					valid = false;
					this.filename.firstChild.remove();
					this.filename.append("select file")
				} else {
					this.filename.firstChild.remove();
					this.filename.append(this.fileInput.files[0].name);
				}
				this.fileURIInput.setAttribute("name", "imageURI");

				this.fileInput.accept = "image/*";

				break;
			case "file":
				this.fileLabel.disabled = false;
				this.fileLabel.classList.remove("disabled");
				this.fileInput.disabled = false;
				this.fileInput.classList.remove("disabled");
				this.textInput.disabled = true;
				this.textInput.classList.add("disabled");
				if (this.fileInput.value == "") {
					valid = false;
					this.filename.firstChild.remove();
					this.filename.append("select file");
				} else {
					this.filename.firstChild.remove();
					this.filename.append(this.fileInput.files[0].name);
				}
				this.fileURIInput.setAttribute("name", "fileURI");

				this.fileInput.accept = "*/*";
		}

		if (valid) {
			this.createButton.disabled = false;
			this.createButton.classList.remove("disabled");
		} else {
			this.createButton.disabled = true;
			this.createButton.classList.add("disabled");
		}
	}

}

class ColumnCreationModal extends Modal {

	constructor(serverConnector) {
		super(serverConnector);
		this.form = document.createElement("form");
		this.form.classList.add("kanboard", "form");

		this.titleInput = document.createElement("input");
		this.titleInput.classList.add("kanboard", "input");
		this.titleInput.setAttribute("type", "text");
		this.titleInput.setAttribute("placeholder", "title");
		this.titleInput.setAttribute("name", "columnTitle");
		this.form.appendChild(this.titleInput);

		var colorInputSpan = document.createElement("span");
		colorInputSpan.classList.add("kanboard", "buttons-line", "input");
		colorInputSpan.append("Color: ");

		this.colorInput = document.createElement("input");
		this.colorInput.classList.add("kanboard", "color-input");
		this.colorInput.setAttribute("type", "color");
		this.colorInput.setAttribute("name", "color");

		colorInputSpan.appendChild(this.colorInput);
		this.form.appendChild(colorInputSpan);


		this.modalDiv.appendChild(this.form);

		this.form.setAttribute("action", "javascript:void(0);");

		var modalContext = this;

		this.form.onchange = function () {
			modalContext.validate();
		}
		this.form.onkeyup = function () {
			modalContext.validate();
		}

		var buttonsDiv = document.createElement("div");
		buttonsDiv.classList.add("kanboard", "buttons-line");

		this.createButton = document.createElement("button");
		this.createButton.classList.add("kanboard", "button", "text-button");
		this.createButton.append("CREATE");
		buttonsDiv.append(this.createButton);

		var cancelButton = document.createElement("button");
		cancelButton.classList.add("kanboard", "button", "cancel-button");
		cancelButton.append("CANCEL");
		cancelButton.onclick = function () {
			modalContext.hide();
		}
		buttonsDiv.append(cancelButton);

		this.modalDiv.append(buttonsDiv);

	}

	createColumn(boardID) {
		this.titleInput.value = "";
		this.colorInput.value = "#f000";

		var modalContext = this;
		this.createButton.onclick = function () {
			modalContext.serverConnector.createColumn(boardID, new FormData(modalContext.form));
			modalContext.hide();
		}
		this.show();
		this.validate();
	}

	validate() {
		var valid = true;
		if (this.titleInput.value == "" || this.titleInput.value == undefined) {
			valid = false
		}

		if (valid) {
			this.createButton.disabled = false;
			this.createButton.classList.remove("disabled");
		} else {
			this.createButton.disabled = true;
			this.createButton.classList.add("disabled");
		}
	}

}

class BoardCreationModal extends Modal {

	constructor(serverConnector) {
		super(serverConnector);
		this.form = document.createElement("form");
		this.form.classList.add("kanboard", "form");

		this.titleInput = document.createElement("input");
		this.titleInput.classList.add("kanboard", "input");
		this.titleInput.setAttribute("type", "text");
		this.titleInput.setAttribute("placeholder", "title");
		this.titleInput.setAttribute("name", "boardTitle");
		this.form.appendChild(this.titleInput);

		this.form.setAttribute("action", "javascript:void(0);");

		this.modalDiv.appendChild(this.form);

		var modalContext = this;

		this.titleInput.onkeyup = function (e) {
			if (e.code == "Enter") {
				modalContext.serverConnector.createBoard(new FormData(modalContext.form));
				modalContext.hide();
			}
			modalContext.validate();
		}

		var buttonsDiv = document.createElement("div");
		buttonsDiv.classList.add("kanboard", "buttons-line");

		this.createButton = document.createElement("button");
		this.createButton.classList.add("kanboard", "button", "text-button");
		this.createButton.append("CREATE");
		this.createButton.onclick = function () {
			modalContext.serverConnector.createBoard(new FormData(modalContext.form));
			modalContext.hide();
		}
		buttonsDiv.append(this.createButton);

		var cancelButton = document.createElement("button");
		cancelButton.classList.add("kanboard", "button", "cancel-button");
		cancelButton.append("CANCEL");
		cancelButton.onclick = function () {
			modalContext.hide();
		}
		buttonsDiv.append(cancelButton);

		this.modalDiv.append(buttonsDiv);

	}

	createBoard() {
		this.titleInput.value = "";
		this.show();
		this.validate();
		this.titleInput.focus();
	}

	validate() {
		var valid = true;
		if (this.titleInput.value == "" || this.titleInput.value == undefined) {
			valid = false
		}

		if (valid) {
			this.createButton.disabled = false;
			this.createButton.classList.remove("disabled");
		} else {
			this.createButton.disabled = true;
			this.createButton.classList.add("disabled");
		}
	}

}

class TileEditModal extends Modal {

	constructor(serverConnector, author) {
		super(serverConnector, author);

		var modalContext = this;

		this.form = document.createElement("form");
		this.form.classList.add("kanboard", "form");
		this.form.onchange = function () {
			modalContext.validate();
		}
		this.form.onkeyup = function () {
			modalContext.validate();
		}

		this.titleInput = document.createElement("input");
		this.titleInput.classList.add("kanboard", "input");
		this.titleInput.setAttribute("type", "text");
		this.titleInput.setAttribute("placeholder", "title");
		this.titleInput.setAttribute("name", "tileTitle");
		this.form.appendChild(this.titleInput);

		this.selectType = document.createElement("input");
		this.selectType.setAttribute("type", "hidden");
		this.selectType.setAttribute("name", "tileType");

		this.form.appendChild(this.selectType);

		this.selectContent = document.createElement("input");
		this.selectContent.setAttribute("type", "hidden");
		this.selectContent.setAttribute("name", "contentType");

		this.form.appendChild(this.selectContent);

		this.textInput = document.createElement("textarea");
		this.textInput.classList.add("kanboard", "input");
		this.textInput.setAttribute("placeholder", "text content");
		this.textInput.setAttribute("name", "text");
		this.form.appendChild(this.textInput);

		var colorInputSpan = document.createElement("span");
		colorInputSpan.classList.add("kanboard", "buttons-line", "input");
		colorInputSpan.append("Color: ");

		this.colorInput = document.createElement("input");
		this.colorInput.classList.add("kanboard", "color-input");
		this.colorInput.setAttribute("type", "color");
		this.colorInput.setAttribute("name", "color");

		colorInputSpan.appendChild(this.colorInput);
		this.form.appendChild(colorInputSpan);


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
		this.fileForm.onchange = function () {
			modalContext.validate();
		}

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

		this.form.setAttribute("action", "javascript:void(0);");
		this.fileForm.setAttribute("action", "javascript:void(0);");

		var buttonsDiv = document.createElement("div");
		buttonsDiv.classList.add("kanboard", "buttons-line");

		this.createButton = document.createElement("button");
		this.createButton.classList.add("kanboard", "button", "text-button");
		this.createButton.append("SAVE");
		buttonsDiv.append(this.createButton);

		var cancelButton = document.createElement("button");
		cancelButton.classList.add("kanboard", "button", "cancel-button");
		cancelButton.append("CANCEL");
		cancelButton.onclick = function () {
			modalContext.hide();
		}
		buttonsDiv.append(cancelButton);

		this.modalDiv.append(buttonsDiv);
	}

	edit(boardID, columnTitle, tile) {
		this.boardID = boardID;
		this.columnTitle = columnTitle;
		this.tileID = tile.id;
		this.titleInput.value = tile.title;

		this.textInput.value = "";

		if (tile.text != undefined) {
			this.selectContent.value = "text";
			this.textInput.value = tile.text;
		} else if (tile.imageURI != undefined) {
			this.selectContent.value = "image";
			this.fileURIInput.value = tile.imageURI;
			this.fileInput.value = "";
			this.filename.firstChild.remove();
			this.filename.append("Select File");
		} else if (tile.fileURI != undefined) {
			this.selectContent.value = "file";
			this.fileURIInput.value = tile.fileURI;
			this.fileInput.value = "";
			this.filename.firstChild.remove();
			this.filename.append("Select File");
		}


		switch (this.selectContent.value) {
			case "text":
				this.fileLabel.disabled = true;
				this.fileInput.disabled = true;
				this.textInput.removeAttribute("style");
				this.fileForm.style.display = "none";
				this.textInput.disabled = false;
				break;
			case "image":
				this.fileLabel.disabled = false;
				this.fileInput.disabled = false;
				this.textInput.disabled = true;
				this.textInput.style.display = "none";
				this.fileForm.removeAttribute("style");
				this.fileURIInput.setAttribute("name", "imageURI");
				break;
			case "file":
				this.fileLabel.disabled = false;
				this.fileInput.disabled = false;
				this.textInput.disabled = true;
				this.textInput.style.display = "none";
				this.fileForm.removeAttribute("style");
				this.fileURIInput.setAttribute("name", "fileURI");
		}

		this.selectType.value = tile.tileType;

		this.colorInput.value = tile.color;

		var modalContext = this;
		this.createButton.onclick = function () {
			switch (modalContext.selectContent.value) {
				case "text":
					modalContext.serverConnector.editTextTile(modalContext.boardID, modalContext.columnTitle, modalContext.tileID, modalContext.form);
					break;
				case "image":
					modalContext.serverConnector.editImageTile(modalContext.boardID, modalContext.columnTitle, modalContext.tileID, modalContext.fileForm, modalContext.fileInput, modalContext.fileURIInput, modalContext.form);
					break;
				case "file":
					modalContext.serverConnector.editFileTile(modalContext.boardID, modalContext.columnTitle, modalContext.tileID, modalContext.fileForm, modalContext.fileInput, modalContext.fileURIInput, modalContext.form);
			}
			modalContext.hide();
		}

		this.show();
		this.validate();
		this.titleInput.focus();
	}

	validate() {

		var valid = true;
		if (this.titleInput.value == "") {
			valid = false;
		}

		switch (this.selectContent.value) {
			case "text":
				if (this.textInput.value == "") {
					valid = false;
				}
				break;
			case "image":
				if (this.fileInput.value == "") {
					this.filename.firstChild.remove();
					this.filename.append("select file")
				} else {
					this.filename.firstChild.remove();
					this.filename.append(this.fileInput.files[0].name);
				}
				break;
			case "file":
				if (this.fileInput.value == "") {
					this.filename.firstChild.remove();
					this.filename.append("select file");
				} else {
					this.filename.firstChild.remove();
					this.filename.append(this.fileInput.files[0].name);
				}
		}

		if (this.fileInput.value == "") {
			this.filename.firstChild.remove();
			this.filename.append("select file");
		} else {
			this.filename.firstChild.remove();
			this.filename.append(this.fileInput.files[0].name);
		}

		if (valid) {
			this.createButton.disabled = false;
			this.createButton.classList.remove("disabled");
		} else {
			this.createButton.disabled = true;
			this.createButton.classList.add("disabled");
		}
	}

}

class ColumnEditModal extends Modal {

	constructor(serverConnector) {
		super(serverConnector);

		var modalContext = this;

		this.form = document.createElement("form");
		this.form.classList.add("kanboard", "form");

		this.titleInput = document.createElement("input");
		this.titleInput.classList.add("kanboard", "input");
		this.titleInput.setAttribute("type", "text");
		this.titleInput.setAttribute("placeholder", "title");
		this.titleInput.setAttribute("name", "columnTitle");
		this.form.appendChild(this.titleInput);

		var colorInputSpan = document.createElement("span");
		colorInputSpan.classList.add("kanboard", "buttons-line", "input");
		colorInputSpan.append("Color: ");

		this.colorInput = document.createElement("input");
		this.colorInput.classList.add("kanboard", "color-input");
		this.colorInput.setAttribute("type", "color");
		this.colorInput.setAttribute("name", "color");

		colorInputSpan.appendChild(this.colorInput);
		this.form.appendChild(colorInputSpan);

		this.form.onchange = function () {
			modalContext.validate();
		}

		this.form.onkeyup = function () {
			modalContext.validate();
		}

		this.form.setAttribute("action", "javascript:void(0);");
		this.modalDiv.appendChild(this.form);

		var buttonsDiv = document.createElement("div");
		buttonsDiv.classList.add("kanboard", "buttons-line");

		this.createButton = document.createElement("button");
		this.createButton.classList.add("kanboard", "button", "text-button");
		this.createButton.append("SAVE");
		buttonsDiv.append(this.createButton);

		var cancelButton = document.createElement("button");
		cancelButton.classList.add("kanboard", "button", "cancel-button");
		cancelButton.append("CANCEL");
		cancelButton.onclick = function () {
			modalContext.hide();
		}
		buttonsDiv.append(cancelButton);

		this.modalDiv.append(buttonsDiv);

	}

	edit(boardID, column) {
		this.titleInput.value = column.title;
		this.colorInput.value = column.color;

		var modalContext = this;
		this.createButton.onclick = function () {
			modalContext.serverConnector.editColumn(boardID, column.title, new FormData(modalContext.form));
			modalContext.hide();
		}
		this.show();
		this.validate();
	}

	validate() {
		var valid = true;
		if (this.titleInput.value == "" || this.titleInput.value == undefined) {
			valid = false
		}

		if (valid) {
			this.createButton.disabled = false;
			this.createButton.classList.remove("disabled");
		} else {
			this.createButton.disabled = true;
			this.createButton.classList.add("disabled");
		}
	}

}

class LoginModal extends Modal {

	constructor(kanboard) {
		super();
		this.kanboard = kanboard;
		var form = document.createElement("form");
		form.classList.add("kanboard", "form");

		this.usernameInput = document.createElement("input");
		this.usernameInput.classList.add("kanboard", "input");
		this.usernameInput.setAttribute("type", "text");
		this.usernameInput.setAttribute("placeholder", "username");
		this.usernameInput.setAttribute("name", "username");

		form.appendChild(this.usernameInput);

		this.button = document.createElement("submit");
		this.button.disabled = true;
		this.button.classList.add("kanboard", "button", "text-button");
		this.button.append("LOGIN");

		form.appendChild(this.button);

		var modalContext = this;

		form.setAttribute("action", "javascript:void(0);");

		this.modalDiv.appendChild(form);

		this.usernameInput.onkeyup = function (e) {
			if (e.code == "Enter") {
				modalContext.authenticate();
			}
			modalContext.validate();
		}

		this.button.onclick = function () {
			modalContext.authenticate();

		}
	}

	authenticate() {
		if (this.usernameInput.value != "" && this.usernameInput.value != undefined) {
			this.kanboard.init(this.usernameInput.value);
			this.hide();
		} else {
			this.usernameInput.focus();
		}
	}

	login() {
		this.usernameInput.value = "";
		this.show();
		this.validate();
	}

	validate() {
		if (this.usernameInput.value == "" || this.usernameInput.value == undefined) {
			this.button.disabled = true;
		}
		else {
			this.button.disabled = false;
		}
	}

}

class ServerConnector {

	constructor(uri, kanboard) {
		this.uri = uri;
		this.kanboard = kanboard;
	}

	createBoard(data) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 201){
					connectorContext.getBoards();
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("POST", this.uri + "/api/boards/add/");

		xhr.send(data);
	}

	createColumn(boardID, data) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 201){
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
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
				if(this.status === 201) {
					fileURIInput.value = this.responseText;
					connectorContext.createTile(boardID, columnTitle, new FormData(dataForm));
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("POST", this.uri + "/files/add/");

		xhr.send(new FormData(fileForm));
	}

	createImageTile(boardID, columnTitle, fileForm, fileURIInput, dataForm) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 201) {
					fileURIInput.value = this.responseText;
					connectorContext.createTile(boardID, columnTitle, new FormData(dataForm));
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("POST", this.uri + "/images/add/");

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
				if(this.status === 201){
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
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
				if(this.status === 200){
					connectorContext.kanboard.board = JSON.parse(this.response);
					connectorContext.kanboard.updateBoard();
					connectorContext.kanboard.lastBoardModified = this.getResponseHeader("Last-Modified-Millis");
				} else if(this.status !== 304) {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
 			}
		});
		
		xhr.open("GET", this.uri + "/api/" + boardID + "/");

		if(this.kanboard.lastBoardModified != undefined)
			xhr.setRequestHeader("If-Modified-Since-Millis", this.kanboard.lastBoardModified);

		xhr.send();
	}

	getBoards() {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200){
					connectorContext.kanboard.boards = JSON.parse(this.response);
					connectorContext.kanboard.updateNavbar();
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
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
				if(this.status === 200) {
					connectorContext.kanboard.boards = JSON.parse(this.response);
					connectorContext.kanboard.updateNavbar();
					connectorContext.kanboard.lastModified = this.getResponseHeader("Last-Modified-Millis");
				} else if(this.status !== 304) {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("GET", this.uri + "/api/boards/headers/");

		if(this.kanboard.lastModified != undefined)
			xhr.setRequestHeader("If-Modified-Since-Millis", this.kanboard.lastModified);

		xhr.send();
	}

	deleteTile(boardID, columnTitle, tileID) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200) {
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("DELETE", this.uri + "/api/" + boardID + "/" + columnTitle + "/" + tileID + "/delete/");

		xhr.send();
	}

	swapTiles(boardID, columnTitle, tileID1, tileID2) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200) {
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/tiles/swap/");

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.send("tileID1=" + tileID1 + "&tileID2=" + tileID2);
	}

	swapColumns(boardID, columnTitle1, columnTitle2) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200) {
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/columns/swap/");

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.send("column1=" + columnTitle1 + "&column2=" + columnTitle2);
	}

	archiveColumn(boardID, columnTitle, archive) {

		var state = "archived";

		if (archive == false) {
			state = "active";
		}

		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200) {
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/edit/");

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.send("state=" + state);
	}

	deleteColumn(boardID, columnTitle) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200) {
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("DELETE", this.uri + "/api/" + boardID + "/" + columnTitle + "/delete/");

		xhr.send();
	}

	editFileTile(boardID, columnTitle, tileID, fileForm, fileInput, fileURIInput, dataForm) {
		if (fileInput.value == "" || fileInput.value == undefined) {
			this.editTile(boardID, columnTitle, tileID, new FormData(dataForm));
		} else {
			var xhr = new XMLHttpRequest();

			var connectorContext = this;
			xhr.addEventListener("readystatechange", function () {
				if (this.readyState === 4) {
					if(this.status === 201){
						fileURIInput.value = this.responseText;
						connectorContext.editTile(boardID, columnTitle, tileID, new FormData(dataForm));
					} else {
						alert("Error " + this.status + ": " + JSON.parse(this.response).message);
					}
				}
			});

			xhr.open("POST", this.uri + "/files/add/");

			xhr.send(new FormData(fileForm));
		}
	}

	editImageTile(boardID, columnTitle, tileID, fileForm, fileInput, fileURIInput, dataForm) {
		if (fileInput.value == "" || fileInput.value == undefined) {
			this.editTile(boardID, columnTitle, tileID, new FormData(dataForm));
		} else {
			var xhr = new XMLHttpRequest();

			var connectorContext = this;
			xhr.addEventListener("readystatechange", function () {
				if (this.readyState === 4) {
					if(this.status === 201){
						fileURIInput.value = this.responseText;
						connectorContext.editTile(boardID, columnTitle, tileID, new FormData(dataForm));
					} else {
						alert("Error " + this.status + ": " + JSON.parse(this.response).message);
					}
				}
			});

			xhr.open("POST", this.uri + "/images/add/");

			xhr.send(new FormData(fileForm));
		}

	}

	editTextTile(boardID, columnTitle, tileID, dataForm) {
		this.editTile(boardID, columnTitle, tileID, new FormData(dataForm));
	}

	editTile(boardID, columnTitle, tileID, data) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200){
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/" + tileID + "/edit/");

		xhr.send(data);
	}

	editColumn(boardID, columnTitle, data) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200){
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/edit/");

		xhr.send(data);
	}

	moveTile(boardID, columnTitle, tileID, destinationColumnTitle) {
		var xhr = new XMLHttpRequest();

		var connectorContext = this;
		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				if(this.status === 200){
					connectorContext.getBoard(boardID);
				} else {
					alert("Error " + this.status + ": " + JSON.parse(this.response).message);
				}
			}
		});

		xhr.open("PUT", this.uri + "/api/" + boardID + "/" + columnTitle + "/" + tileID + "/move/");

		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		xhr.send("destinationColumnTitle=" + destinationColumnTitle);
	}
}
