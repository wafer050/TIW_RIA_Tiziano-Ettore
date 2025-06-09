{ // avoid variables ending up in the global scope

	// page components
	//let missionDetails, missionsList, wizard,
	let corsiList, appelliList, iscritti, wizard,
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
			makeCall("GET", "GetCorsi", null,
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
			makeCall("GET", "GetAppelli?corsoid=" + corsoid, null,
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
					iscritti.show(e.target.getAttribute("appelloid"));

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
			var anchorToClick =  // the first the one with that id
				(appelloId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
			if (anchorToClick) anchorToClick.dispatchEvent(e);
		}

	}







	function Iscritti(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
			document.getElementById("id_pubblicaform").style.visibility = "hidden";
			document.getElementById("id_verbalizzaform").style.visibility = "hidden";
		}

		this.show = function(appelloid, next) {


			//console.log("Appelloid passato al server:", appelloid);


			//form modifica voto
			document.getElementById("id_modificavotoform").style.visibility = "hidden";
			
			//mostra bottone pubblica
			document.getElementById("id_pubblicaform").style.visibility = "visible";
			//sessionStorage.setItem("currentAppelloId", appelloid);
			//mostra bottone verbalizza
			document.getElementById("id_verbalizzaform").style.visibility = "visible";
			//mostra bottone inserimento multiplo
			document.getElementById("id_inserimentomultiplo").style.visibility = "visible";

			//il resto di iscritti
			var self = this;
			makeCall("GET", "GetIscritti?appelloid=" + sessionStorage.getItem("currentAppelloId"), null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var iscrittiToShow = JSON.parse(req.responseText);
							if (iscrittiToShow.length == 0) {
								self.alert.textContent = "No iscritti yet!";
								return;
							}
							self.update(iscrittiToShow); // self visible by closure
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


		this.update = function(listaIscritti) {
			var elem, i, row, bottonecell, bottone, idcell, nomecell, cognomecell, matricolacell,
				mailcell, corsoLaureacell, votocell, statoValutazionecell;
			this.listcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			listaIscritti.forEach(function(iscritto) { // self visible here, not this
				row = document.createElement("tr");

				bottonecell = document.createElement("td")
				if (iscritto.statoValutazione == 'non inserito' || iscritto.statoValutazione == 'inserito') {
					bottone = document.createElement("button")
					bottonecell.appendChild(bottone);
					bottone.appendChild(document.createTextNode("bottone modifica voto"));
					
					
					//fa comparire il form per inserire il voto
					bottone.addEventListener('click', (e) => {
						makeCall("GET", "GetDatiStudente?studenteid=" + iscritto.id, null,
											function(req) {
												if (req.readyState == 4) {
													var message = req.responseText;
													if (req.status == 200) {
														var datiStudenteToShow = JSON.parse(req.responseText);
						
						document.getElementById("id_modificavotoform").style.visibility = "visible";
						
						document.getElementById("campo_studenteid").innerText = datiStudenteToShow.id;
						
						document.getElementById("hidden_campo_studenteid").value = datiStudenteToShow.id;
						document.getElementById("modificavoto_appelloid").value = sessionStorage.getItem("currentAppelloId")
						
						document.getElementById("campo_studentenome").innerText = datiStudenteToShow.nome;
						document.getElementById("campo_studentecognome").innerText = datiStudenteToShow.cognome;
						document.getElementById("campo_studentematricola").innerText = datiStudenteToShow.matricola;
						document.getElementById("campo_studentemail").innerText = datiStudenteToShow.mail;
						document.getElementById("campo_studentecorsolaurea").innerText = datiStudenteToShow.corsoLaurea;
						
						
						
						}
						}
						});
						});	
				}
				else {
					bottonecell.appendChild(document.createTextNode("non modificabile"));
				}

				idcell = document.createElement("td");
				idcell.appendChild(document.createTextNode(iscritto.id));

				nomecell = document.createElement("td");
				nomecell.appendChild(document.createTextNode(iscritto.nome));

				cognomecell = document.createElement("td");
				cognomecell.appendChild(document.createTextNode(iscritto.cognome));

				matricolacell = document.createElement("td");
				matricolacell.appendChild(document.createTextNode(iscritto.matricola));

				mailcell = document.createElement("td");
				mailcell.appendChild(document.createTextNode(iscritto.mail));

				corsoLaureacell = document.createElement("td");
				corsoLaureacell.appendChild(document.createTextNode(iscritto.corsoLaurea));

				votocell = document.createElement("td");
				votocell.appendChild(document.createTextNode(iscritto.voto));

				statoValutazionecell = document.createElement("td");
				statoValutazionecell.appendChild(document.createTextNode(iscritto.statoValutazione));

				row.appendChild(bottonecell);
				row.appendChild(idcell);
				row.appendChild(nomecell);
				row.appendChild(cognomecell);
				row.appendChild(matricolacell);
				row.appendChild(mailcell);
				row.appendChild(corsoLaureacell);
				row.appendChild(votocell);
				row.appendChild(statoValutazionecell);

				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";

		}




		this.autoclick = function() {
			//da vedere
		}

	}








	//wizard






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

			iscritti = new Iscritti(
				alertContainer,
				document.getElementById("id_tabellaiscritti"),
				document.getElementById("id_tabellaiscrittibody"));

				
			//bottone inserimento multiplo
			document.getElementById("id_inserimentomultiplo").style.visibility = "hidden";
			document.getElementById("no_votononinserito").style.visibility = "hidden";
			document.getElementById("id_bottoneinserimentomultiplo").addEventListener('click', (e) =>{
				
				document.getElementById("overlay2").style.visibility = "visible";
				document.getElementById("overlay2").style.display = "flex";
				
				//clona
				let righe = Array.from(document.getElementById("id_tabellaiscritti").cloneNode(true)
					.querySelectorAll('tbody > tr'));
				
				let righe_votoNonInserito = righe.filter(riga =>
					Array.from(riga.querySelectorAll('td')).at(-1).textContent === "non inserito"
				)
				
				if(righe_votoNonInserito.length === 0){
					document.getElementById("tabella_votononinserito").style.visibility = "hidden";
					document.getElementById("no_votononinserito").style.visibility = "visible";
					
				}
				else{
					document.getElementById("tabella_votononinserito").style.visibility = "visible";
					document.getElementById("no_votononinserito").style.visibility = "hidden";
					
					let body = document.getElementById("id_tabella_votononinserito");
					body.innerHTML= "";
				
					righe_votoNonInserito.forEach(riga => {
						//cancella bottone singola modifica
						riga.children[0].remove();
						
						let formCell = document.createElement("td");
						let input = document.createElement('input');
							input.type = 'text';
							input.name = 'voto';
							
						formCell.appendChild(input);
						riga.appendChild(formCell);
						
						body.appendChild(riga);
					
					})
					
				
				}
				
				
			}
			);
			//chiudi se cliccki su x oppure fuori
			document.getElementById("chiudiPopup2").addEventListener("click", function() {
							document.getElementById("overlay2").style.display = "none";
						});

						// Chiudi se clicchi fuori dal popup
			document.getElementById("overlay2").addEventListener("click", function(e) {
				if (e.target === this) {
					this.style.display = "none";
					}
			});
				
				
				
				
				
				
			//form modifica voto singolo
			document.getElementById("id_modificavotoform").style.visibility = "hidden";
			document.getElementById("invio_formmodificavoto").addEventListener('click', (e) =>{
				form = e.target.closest("form")
				if (form.checkValidity()) {
					makeCall("POST", "ModificaVoto", form, 
					function(req){
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {
								//refresh
								pageOrchestrator.refresh(sessionStorage.getItem("currentCorsoId"),
								sessionStorage.getItem("currentAppelloId"));



							}
							else {
								alertContainer.textContent = message;
													}
													}
					}
				 )
				
				
				
				}
				else{
					form.reportValidity();
				}
				
				
			})
				
				
			//bottone pubblica
			document.getElementById("id_pubblicaform").style.visibility = "hidden";
			document.getElementById('id_pubblica').addEventListener('click', (e) => {
				makeCall("GET", "PubblicaVoti?appelloid=" + sessionStorage.getItem("currentAppelloId"), null,
					function(req) {
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {

								//refresh
								pageOrchestrator.refresh(sessionStorage.getItem("currentCorsoId"),
									sessionStorage.getItem("currentAppelloId"));



							}
							else {
								alertContainer.textContent = message;
							}
						}
					}
				);
			})



			//bottone verbalizza



			document.getElementById("chiudiPopup").addEventListener("click", function() {
				document.getElementById("overlay").style.display = "none";
			});

			// Chiudi se clicchi fuori dal popup
			document.getElementById("overlay").addEventListener("click", function(e) {
				if (e.target === this) {
					this.style.display = "none";
				}
			});




			document.getElementById("id_verbalizzaform").style.visibility = "hidden";
			document.getElementById('id_verbalizza').addEventListener('click', (e) => {
				makeCall("GET", "VerbalizzaVoti?appelloid=" + sessionStorage.getItem("currentAppelloId"), null,
					function(req) {
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {

								//refresh
								pageOrchestrator.refresh(sessionStorage.getItem("currentCorsoId"),
									sessionStorage.getItem("currentAppelloId"));





								//popup
								document.getElementById("overlay").style.display = "flex";

								//aggiorna dati popup verbale:
								if (req.responseText === "") {
									document.getElementById("noverbale").style.visibility = "visible";
									document.getElementById("id_verbale").style.visibility = "hidden";
									document.getElementById("id_studentiverbale").style.visibility = "hidden";
								}
								else {
									document.getElementById("id_studentiverbalebody").innerHTML = ""; // empty the table body
									
									document.getElementById("noverbale").style.visibility = "hidden";
									document.getElementById("id_verbale").style.visibility = "visible";
									document.getElementById("id_studentiverbale").style.visibility = "visible";

									let verbaleToShow = JSON.parse(req.responseText);

									document.getElementById("campo_verbaleid").innerText = verbaleToShow.id;
									document.getElementById("campo_verbaledatacreazione").innerText = verbaleToShow.dataCreazione;
									document.getElementById("campo_verbaleora").innerText = verbaleToShow.oraCreazione;
									document.getElementById("campo_verbaleappello").innerText = verbaleToShow.idAppello;
									document.getElementById("campo_verbaledataappello").innerText = verbaleToShow.dataAppello;
									document.getElementById("campo_verbalecorso").innerText = verbaleToShow.idCorso;
									document.getElementById("campo_verbalenomecorso").innerText = verbaleToShow.nomeCorso;
									
									
									
									
									//studenti:
									var row;
									var body;
									var id; var nome; var cognome; var matricola; var mail; var corsoLaurea; var voto;
									verbaleToShow.studenti.forEach(function(studente) {
									row = document.createElement("tr");	
									body = document.getElementById("id_studentiverbalebody");
									body.appendChild(row);
									
									id = document.createElement("td");
									id.appendChild(document.createTextNode(studente.id));
									
									nome = document.createElement("td");
									nome.appendChild(document.createTextNode(studente.nome));
									
									cognome = document.createElement("td");
									cognome.appendChild(document.createTextNode(studente.cognome));
									
									matricola = document.createElement("td");
									matricola.appendChild(document.createTextNode(studente.matricola));
									
									mail = document.createElement("td");
									mail.appendChild(document.createTextNode(studente.mail));
									
									corsoLaurea = document.createElement("td");
									corsoLaurea.appendChild(document.createTextNode(studente.corsoLaurea));
									
									voto = document.createElement("td");
									voto.appendChild(document.createTextNode(studente.voto));
									
									
									row.appendChild(id);
									row.appendChild(nome);
									row.appendChild(cognome);
									row.appendChild(matricola);
									row.appendChild(mail);
									row.appendChild(corsoLaurea);
									row.appendChild(voto);
									});
				
								
							}
							}
							else {
								alertContainer.textContent = message;
							}
						}
					}
				);
			})



			
			



			//details and wizard





			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				//window.sessionStorage.removeItem('user');
				window.sessionStorage.clear();
			})
		};




		this.refresh = function(currentCorso, currentAppello) { // currentCorso initially null at start
			alertContainer.textContent = "";        // not null after creation of status change
			corsiList.reset();
			appelliList.reset();
			iscritti.reset();

			corsiList.show(function() {
				if (currentCorso != undefined) {
					corsiList.autoclick(currentCorso);
					if (currentAppello != undefined) {
						appelliList.autoclick(currentAppello)
					}
				}
			}); // closure preserves visibility of this





			//wizard.reset();




		};




	}
};
