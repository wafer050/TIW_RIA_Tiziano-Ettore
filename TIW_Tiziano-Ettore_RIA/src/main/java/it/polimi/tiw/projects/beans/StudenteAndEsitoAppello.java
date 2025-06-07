package it.polimi.tiw.projects.beans;

public class StudenteAndEsitoAppello {
	private int id;
	private String nome;
	private String cognome;
	private int matricola;
	private String mail;
	private String corsoLaurea;
	private String voto;
	private String statoValutazione;
	
	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}
	
	public String getCognome() {
		return cognome;
	}
	
	public int getMatricola() {
		return matricola;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getCorsoLaurea() {
		return corsoLaurea;
	}
	
	public String getVoto() {
		return voto;
	}

	public String getStatoValutazione() {
		return statoValutazione;
	}

	
	
	public void setId(int i) {
		id = i;
	}
	
	public void setNome(String un) {
		nome = un;
	}
	
	public void setCognome(String un) {
		cognome = un;
	}
	
	public void setMatricola(int un) {
		matricola = un;
	}
	
	public void setMail(String un) {
		mail = un;
	}
	
	public void setCorsoLaurea(String un) {
		corsoLaurea = un;
	}

	public void setVoto(String voto) {
		this.voto = voto;
	}

	public void setStatoValutazione(String statoValutazione) {
		this.statoValutazione = statoValutazione;
	}
	
}