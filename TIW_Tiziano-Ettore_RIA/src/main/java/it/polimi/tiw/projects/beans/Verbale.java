package it.polimi.tiw.projects.beans;
import java.util.List;

public class Verbale {
	private int id;
	private String dataCreazione;
	private String oraCreazione;
	private int idAppello;
	private String dataAppello;
	private int idCorso;
	private String nomeCorso;
	private List<StudenteAndEsitoAppello> studenti;

	public int getId() {
		return id;
	}
	
	public String getDataCreazione() {
		return dataCreazione;
	}

	public String getOraCreazione() {
		return oraCreazione;
	}
	
	public int getIdAppello() {
		return idAppello;
	}

	public String getDataAppello() {
		return dataAppello;
	}
	
	public int getIdCorso() {
		return idCorso;
	}
	
	public String getNomeCorso() {
		return nomeCorso;
	}
	
	public List<StudenteAndEsitoAppello> geStudenti() {
		return studenti;
	}
	
	
	public void setId(int i) {
		id = i;
	}
	
	public void setDataCreazione(String dc) {
		dataCreazione = dc;
	}

	public void setOraCreazione(String oc) {
		oraCreazione = oc;
	}
	
	public void setIdAppello(int ida) {
		idAppello = ida;
	}

	

	public void setDataAppello(String dataAppello) {
		this.dataAppello = dataAppello;
	}

	

	public void setIdCorso(int idCorso) {
		this.idCorso = idCorso;
	}
	
	public void setNomeCorso(String nc) {
		nomeCorso = nc;
	}
	
	public void setStudenti(List<StudenteAndEsitoAppello> studenti) {
		this.studenti = studenti;
	}
	
}