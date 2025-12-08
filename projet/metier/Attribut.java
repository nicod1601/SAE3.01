package projet.metier;

public class Attribut
{
	private String visibilite;
	private String type;
	private String nom;

	private boolean estStatic;
	private boolean estFinal;

	public Attribut(String visibilite, String type, String nom, boolean estStatic, boolean estFinal)
	{
		this.visibilite = visibilite;
		this.type = type;
		this.nom = nom;
		this.estStatic = estStatic;
		this.estFinal = estFinal;
	}

	public String getVisibilite()
	{
		return visibilite;
	}

	public String getType() {return type;}

	public String getNom() {
		return nom;
	}

	public boolean isEstStatic() {
		return estStatic;
	}

	public boolean isEstFinal() {
		return estFinal;
	}

}
