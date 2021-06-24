
function kanboard(containerid, uri) {

	var data;

	var updateTable = function () {

		for(child of container.childNodes){
			container.removeChild(child);
		}

		var containerRow = document.createElement("div");

		containerRow.classList.add("kanboard", "container", "row");

		for (col of data.columns) {

			var column = document.createElement("table");

			column.classList.add("kanboard", "column");

			var header = document.createElement("th");
			//TODO generate column element
			header.append(col.title); 
			column.appendChild(header); 
			for (tile of col.tiles) {
				var row = document.createElement("tr");
				var content = document.createElement("td");
				content.appendChild(createTileElement(tile));
				console.log(tile);

				row.appendChild(content);
				column.appendChild(row);

			}
			containerRow.appendChild(column);
		}
		container.appendChild(containerRow);

	}

	function createTileElement(tile) {
		var container = document.createElement("div");

		container.classList.add("kanboard", "tile", "container");

		var button = document.createElement("button");
		button.classList.add("kanboard", "button");
		button.append("EDIT");
		var par;

		par = document.createElement("p");
		par.append(tile.author);
		par.classList.add("kanboard", "tile", "author");
		container.appendChild(par);

		par = document.createElement("p");
		par.classList.add("kanboard", "tile", "title");
		par.append(tile.title);
		container.appendChild(par);

		par = document.createElement("p");
		par.append(tile.tileType);
		par.classList.add("kanboard", "tile", "type");
		container.appendChild(par);

		container.append(button);

		if (tile.image != undefined) {
			var img = document.createElement("img");
			img.setAttribute("src", tile.image);
			img.classList.add("kanboard", "tile", "image")
			container.appendChild(img);
		}

		if (tile.text != undefined) {
			var text = document.createElement("p");
			text.append(tile.text);
			container.appendChild(text);
		}

		return container;
	}

	function retriveData() {
		var xhr = new XMLHttpRequest();
		xhr.withCredentials = true;

		xhr.addEventListener("readystatechange", function () {
			if (this.readyState === 4) {
				console.log(JSON.parse(this.response));
				data = JSON.parse(this.response);
				updateTable();
			}
		});

		xhr.open("GET", "/api/board/");

		xhr.send();
	}

	var container = document.getElementById(containerid);

	container.classList.add("kanboard", "container");

	retriveData();

	setInterval(retriveData, 3000);

}



