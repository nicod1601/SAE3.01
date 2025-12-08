package projet.metier;

import java.util.List;

public class Methode
{
	private String visibilite;
	private String type;
	private String nom;

	private boolean estStatic;
	private List<String> lstParametre;

	public Methode(String visibilite, String type, String nom, boolean estStatic, List<String> lstParametre)
	{
		this.visibilite = visibilite;
		this.type = type;
		this.nom = nom;
		this.estStatic = estStatic;
		this.lstParametre = lstParametre;
	}

	public String getVisibilite()
	{
		return visibilite;
	}

	public String getType()
	{
		return type;
	}

	public String getNom()
	{
		return nom;
	}

	public boolean isEstStatic()
	{
		return estStatic;
	}

	public List<String> getLstParametre()
	{
		return lstParametre;
	}

}
