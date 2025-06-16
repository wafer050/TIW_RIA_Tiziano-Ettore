package it.polimi.tiw.projects.beans;

public class Esito {
	private int idStudente;
	private String nome;
	private String cognome;
	private int matricola;
	private String mail;
	private String corsoLaurea;
	private int idCorso;
	private String nomeCorso;
	private int idAppello;
	private String dataAppello;
	private String voto;
	private String statoDiValutazione;

	public int getIdStudente() {
		return idStudente;
	}

	public void setIdStudente(int idStudente) {
		this.idStudente = idStudente;
	}

	public int getIdAppello() {
		return idAppello;
	}

	public void setIdAppello(int idAppello) {
		this.idAppello = idAppello;
	}

	public String getVoto() {
		return voto;
	}

	public void setVoto(String voto) {
		this.voto = voto;
	}

	public String getStatoDiValutazione() {
		return statoDiValutazione;
	}

	public void setStatoDiValutazione(String statoDiValutazione) {
		this.statoDiValutazione = statoDiValutazione;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public int getMatricola() {
		return matricola;
	}

	public void setMatricola(int matricola) {
		this.matricola = matricola;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getCorsoLaurea() {
		return corsoLaurea;
	}

	public void setCorsoLaurea(String corsoLaurea) {
		this.corsoLaurea = corsoLaurea;
	}

	public int getIdCorso() {
		return idCorso;
	}

	public void setIdCorso(int idCorso) {
		this.idCorso = idCorso;
	}

	public String getNomeCorso() {
		return nomeCorso;
	}

	public void setNomeCorso(String nomeCorso) {
		this.nomeCorso = nomeCorso;
	}

	public String getDataAppello() {
		return dataAppello;
	}

	public void setDataAppello(String dataAppello) {
		this.dataAppello = dataAppello;
	}

}
