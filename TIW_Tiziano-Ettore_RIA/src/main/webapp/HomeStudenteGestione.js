{ /// avoid variables ending up in the global scope

	/// page components
	///let missionDetails, missionsList, wizard,
	let corsiList, appelliList, esito,
		pageOrchestrator = new PageOrchestrator(); ///main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("user") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); /// initialize the components
			pageOrchestrator.refresh();
		} /// display initial content
	}, false);


	///Constructors of view components

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
							self.update(corsiToShow); /// self visible by closure
							if (next) next(); /// show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							///Location definito nel filtro loginChecker
							///window.sessionStorage.removeItem('user');
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
			this.listcontainerbody.innerHTML = ""; /// empty the table body
			/// build updated list
			var self = this;
			listaCorsi.forEach(function(corso) { /// self visible here, not this
				row = document.createElement("tr");
				idcell = document.createElement("td");
				idcell.textContent = corso.id;
				row.appendChild(idcell);



				namecell = document.createElement("td");
				nameanchor = document.createElement("a");

				namecell.appendChild(nameanchor);
				linkText = document.createTextNode(corso.name);
				nameanchor.appendChild(linkText);
				/// make list item clickable
				nameanchor.setAttribute('corsoid', corso.id); /// set a custom HTML attribute
				nameanchor.addEventListener("click", (e) => {
					/// dependency via module parameter
					sessionStorage.setItem("currentCorsoId", e.target.getAttribute("corsoid"));
					appelliList.show(e.target.getAttribute("corsoid")); /// the list must know the details container
				}, false);
				nameanchor.href = "#";
				row.appendChild(namecell);
				self.listcontainerbody.appendChild(row);
			});
			///document.getElementById("corsiEaltro").classList.remove("superhidden");
			this.listcontainer.style.visibility = "visible";

		}

		this.autoclick = function(corsoId) {
			var e = new Event("click");
			var selector = "a[corsoid='" + corsoId + "']";
			var anchorToClick =  /// the first the one with that id
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
							self.update(appelliToShow); /// self visible by closure
							if (next) next(); /// show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							///Location definito nel filtro loginChecker
							///window.sessionStorage.removeItem('user');
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
			this.listcontainerbody.innerHTML = ""; /// empty the table body
			/// build updated list
			var self = this;
			listaAppelli.forEach(function(appello) { /// self visible here, not this
				row = document.createElement("tr");

				datecell = document.createElement("td");
				dateanchor = document.createElement("a");

				datecell.appendChild(dateanchor);
				linkText = document.createTextNode(appello.date);
				dateanchor.appendChild(linkText);
				/// make list item clickable
				dateanchor.setAttribute('appelloid', appello.id); /// set a custom HTML attribute
				dateanchor.addEventListener("click", (e) => {
					/// dependency via module parameter
					sessionStorage.setItem("currentAppelloId", e.target.getAttribute("appelloid"));
					esito.show(e.target.getAttribute("appelloid"));
				}, false);
				dateanchor.href = "#";
				row.appendChild(datecell);
				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";

		}

		this.autoclick = function(appelloId) {
			var e = new Event("click");
			var selector = "a[appelloid='" + appelloId + "']";
			var anchorToClick =  /// the first the one with that id
				(appelloId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
			if (anchorToClick) anchorToClick.dispatchEvent(e);
		}

	}


	//da rivedere la forma per renderlo omogeneo col resto del codice
	function Esito(_alert) {
		this.alert = _alert;

		this.reset = function() {
			document.getElementById("esitoSection").classList.add("superhidden");
			document.getElementById("datiEsito").classList.add("superhidden");
			document.getElementById("cestino").classList.add("superhidden");
			document.getElementById("votoRifiutatoMsg").classList.add("superhidden");
			document.getElementById("esitoNonPubblicatoMessage").classList.add("superhidden");
		};

		this.show = function(appelloid, next) {
			var self = this;
			makeCall("GET", "GetEsitoStudente?appelloid=" + sessionStorage.getItem("currentAppelloId"), null, function(req) {
				if (req.readyState == 4) {
					var message = req.responseText;
					if (req.status == 200) {
						var esito = JSON.parse(req.responseText);
						if (esito.statoDiValutazione === "non inserito" || esito.statoDiValutazione === "inserito") {
							self.reset();
							document.getElementById("esitoSection").classList.remove("superhidden");
							document.getElementById("esitoNonPubblicatoMessage").classList.remove("superhidden");
						} else {
							self.update(esito);
							if (next) next();
						}
					} else if (req.status == 403) {
						window.location.href = req.getResponseHeader("Location");
						window.sessionStorage.clear();
					} else {
						self.alert.textContent = message;
					}
				}
			});
		};

		this.update = function(esito) {
			var self = this;
			// Mostra sezione
			document.getElementById("esitoSection").classList.remove("superhidden");
			document.getElementById("esitoNonPubblicatoMessage").classList.add("superhidden");
			document.getElementById("datiEsito").classList.remove("superhidden");
			document.getElementById("datiEsito").setAttribute("draggable", "false");
			document.getElementById("datiEsito").style.cursor = "default";

			// Popola i dati
			document.getElementById("esitoStudente").textContent = esito.nome + " " + esito.cognome;
			document.getElementById("esitoMatricola").textContent = esito.matricola;
			document.getElementById("esitoEmail").textContent = esito.mail;
			document.getElementById("esitoCorsoDiLaurea").textContent = esito.corsoLaurea;
			document.getElementById("esitoCorso").textContent = esito.nomeCorso;
			document.getElementById("esitoData").textContent = esito.dataAppello;
			document.getElementById("esitoVoto").textContent = esito.voto;
			document.getElementById("esitoStatoDiValutazione").textContent = esito.statoDiValutazione;

			//testo voto rifiutato
			const votoRifiutatoMsg = document.getElementById("votoRifiutatoMsg");
			if (esito.statoDiValutazione === "rifiutato") {
				votoRifiutatoMsg.classList.remove("superhidden");
			} else {
				votoRifiutatoMsg.classList.add("superhidden");
			}

			// Gestione bottone RIFIUTA
			const cestino = document.getElementById("cestino");
			if (((Number(esito.voto) >= 18 && Number(esito.voto) <= 30) || esito.voto === "30 e lode") && esito.statoDiValutazione === "pubblicato") {
				cestino.classList.remove("superhidden");
				document.getElementById("datiEsito").setAttribute("draggable", "true");		
				document.getElementById("datiEsito").style.cursor = "grab";
			} else {
				cestino.classList.add("superhidden");
			}
		};
	}



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

			esito = new Esito(
				alertContainer
			);

			//cestino
			document.getElementById("cestino").addEventListener('dragover', (e) => {
				e.preventDefault();
			})

			document.getElementById("cestino").addEventListener('drop', (e) => {
				e.preventDefault();
				document.getElementById("overlay").style.display = "flex";
			})

			//rifiuta se clicchi conferma
			document.getElementById("confermaRifiuto").addEventListener('click', (e) => {
				//document.getElementById("overlay").style.display = "none";
				makeCall("POST", "RifiutaVoto?appelloid=" + sessionStorage.getItem("currentAppelloId"), null,
					function(req) {
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {
								//refresh
								pageOrchestrator.refresh(sessionStorage.getItem("currentCorsoId"),
									sessionStorage.getItem("currentAppelloId"));
							} else {
								alertContainer.textContent = message;
							}
						}
					}
				);

			})

			///chiudi se clicchi cancella
			document.getElementById("cancellaRifiuto").addEventListener("click", function() {
				document.getElementById("overlay").style.display = "none";
			});

			// Chiudi se clicchi fuori dal popup
			document.getElementById("overlay").addEventListener("click", function(e) {
				if (e.target === this) {
					this.style.display = "none";
				}
			});



			//MOSTRA CORSI
			document.getElementById("corsiEaltro").classList.remove("superhidden");

			//logout
			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.clear();
			})


			//per evitare flickering
			//document.getElementById("corsiEaltro").classList.remove("superhidden");
		}



		this.refresh = function(currentCorso, currentAppello) { /// currentCorso initially null at start
			alertContainer.textContent = "";        /// not null after creation of status change
			corsiList.reset();
			appelliList.reset();
			esito.reset();
			document.getElementById("overlay").style.display = "none";

			corsiList.show(function() {
				if (currentCorso != undefined) {
					corsiList.autoclick(currentCorso);
					if (currentAppello != undefined) {
						appelliList.autoclick(currentAppello)
					}
				}
			});
		};

	}
};

