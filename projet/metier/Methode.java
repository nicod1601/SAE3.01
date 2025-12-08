package projet.metier;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Methode
{
	private String visibilite;
	private String type;
	private String nom;

	private boolean estStatic;
	private List<String[]> lstParametres;

	public Methode(String visibilite, String type, String nom, boolean estStatic, List<String[]> lstParametres)
	{
		this.visibilite   = visibilite;
		this.type         = type;
		this.nom          = nom;
		this.estStatic    = estStatic;
		
		this.lstParametres = lstParametres;
	}

	public String getVisibilite()
	{
		return this.visibilite;
	}

	public String getType()
	{
		return this.type;
	}

	public String getNom()
	{
		return this.nom;
	}

	public boolean isEstStatic()
	{
		return this.estStatic;
	}

	public List<String[]> getLstParametres()
	{
		return this.lstParametres;
	}

}
