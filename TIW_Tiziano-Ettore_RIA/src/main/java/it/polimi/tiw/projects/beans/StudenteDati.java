package it.polimi.tiw.projects.beans;

public class StudenteDati {
	private int id;
	//private String role;
	//private String username;
	private String nome;
	private String cognome;
	private int matricola;
	private String mail;
	private String corsoLaurea;

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
	
	
}