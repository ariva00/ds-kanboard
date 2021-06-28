class Kanboard {

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
	
	createColumn(){
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
}