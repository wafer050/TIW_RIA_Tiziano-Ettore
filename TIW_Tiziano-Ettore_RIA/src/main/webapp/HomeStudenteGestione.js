{ // avoid variables ending up in the global scope

	// page components
	//let missionDetails, missionsList, wizard,
	let corsiList, appelliList, wizard, //iscritti
		pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("user") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);


	// Constructors of view components

	function PersonalMessage(_user, messagecontainer) {
		this.nome = JSON.parse(_user).nome;
		this.cognome = JSON.parse(_user).cognome;
		this.show = function() {
			messagecontainer.textContent = this.nome + " " + this.cognome;
		}
	}



	function CorsiList(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", "GetCorsiStudente", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var corsiToShow = JSON.parse(req.responseText);
							if (corsiToShow.length == 0) {
								self.alert.textContent = "No corsi yet!";
								return;
							}
							self.update(corsiToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							//Location definito nel filtro loginChecker
							//window.sessionStorage.removeItem('user');
							window.sessionStorage.clear();
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};


		this.update = function(listaCorsi) {
			var elem, i, row, idcell, namecell, nameanchor, linkText;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			listaCorsi.forEach(function(corso) { // self visible here, not this
				row = document.createElement("tr");
				idcell = document.createElement("td");
				idcell.textContent = corso.id;
				row.appendChild(idcell);



				namecell = document.createElement("td");
				nameanchor = document.createElement("a");

				namecell.appendChild(nameanchor);
				linkText = document.createTextNode(corso.name);
				nameanchor.appendChild(linkText);
				// make list item clickable
				nameanchor.setAttribute('corsoid', corso.id); // set a custom HTML attribute
				nameanchor.addEventListener("click", (e) => {
					// dependency via module parameter
					sessionStorage.setItem("currentCorsoId", e.target.getAttribute("corsoid"));
					appelliList.show(e.target.getAttribute("corsoid")); // the list must know the details container
				}, false);
				nameanchor.href = "#";
				row.appendChild(namecell);
				self.listcontainerbody.appendChild(row);
			});
			//document.getElementById("corsiEaltro").classList.remove("superhidden");
			this.listcontainer.style.visibility = "visible";

		}

		this.autoclick = function(corsoId) {
			var e = new Event("click");
			var selector = "a[corsoid='" + corsoId + "']";
			var anchorToClick =  // the first the one with that id
				(corsoId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
			if (anchorToClick) anchorToClick.dispatchEvent(e);
		}

	}






	function AppelliList(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(corsoid, next) {
			var self = this;
			makeCall("GET", "GetAppelliStudente?corsoid=" + corsoid, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var appelliToShow = JSON.parse(req.responseText);
							if (appelliToShow.length == 0) {
								self.alert.textContent = "No appelli yet!";
								return;
							}
							self.update(appelliToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							//Location definito nel filtro loginChecker
							//window.sessionStorage.removeItem('user');
							window.sessionStorage.clear();
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};


		this.update = function(listaAppelli) {
			var elem, i, row, datecell, dateanchor, linkText;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			listaAppelli.forEach(function(appello) { // self visible here, not this
				row = document.createElement("tr");

				datecell = document.createElement("td");
				dateanchor = document.createElement("a");

				datecell.appendChild(dateanchor);
				linkText = document.createTextNode(appello.date);
				dateanchor.appendChild(linkText);
				// make list item clickable
				dateanchor.setAttribute('appelloid', appello.id); // set a custom HTML attribute
				dateanchor.addEventListener("click", (e) => {
					// dependency via module parameter
					sessionStorage.setItem("currentAppelloId", e.target.getAttribute("appelloid"));
					//iscritti.show(e.target.getAttribute("appelloid"));

				}, false);
				dateanchor.href = "#";
				row.appendChild(datecell);
				self.listcontainerbody.appendChild(row);
			});
			//document.getElementById("corsiEaltro").classList.remove("superhidden");
			this.listcontainer.style.visibility = "visible";

		}

		this.autoclick = function(appelloId) {
			var e = new Event("click");
			var selector = "a[appelloid='" + appelloId + "']";
			var anchorToClick =  // the first the one with that id
				(appelloId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
			if (anchorToClick) anchorToClick.dispatchEvent(e);
		}

	}

	//Iscritti


	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");

		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('user'),
				document.getElementById("id_nominativo"));
			personalMessage.show();

			corsiList = new CorsiList(
				alertContainer,
				document.getElementById("id_tabellacorsi"),
				document.getElementById("id_tabellacorsibody"));

			appelliList = new AppelliList(
				alertContainer,
				document.getElementById("id_tabellaappelli"),
				document.getElementById("id_tabellaappellibody"));

			//Iscritti

			//Bottoni

			//MOSTRA CORSI
			document.getElementById("corsiEaltro").classList.remove("superhidden");

			//logout
			/*
			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				//window.sessionStorage.removeItem('user');
				window.sessionStorage.clear();
			})
			*/

			//per evitare flckering
			//document.getElementById("corsiEaltro").style.visibility = "visible";
			//document.getElementById("corsiEaltro").classList.remove("superhidden");
		}



		this.refresh = function(currentCorso, currentAppello, showVerbali) { // currentCorso initially null at start
			alertContainer.textContent = "";        // not null after creation of status change
			corsiList.reset();
			appelliList.reset();
			//iscritti.reset();

			if (showVerbali === undefined) {
				corsiList.show(function() {
					if (currentCorso != undefined) {
						corsiList.autoclick(currentCorso);
						if (currentAppello != undefined) {
							appelliList.autoclick(currentAppello)
						}
					}
				}); // closure preserves visibility of this
			}
			else {
				//show verbali
			}

		};

	}
};

