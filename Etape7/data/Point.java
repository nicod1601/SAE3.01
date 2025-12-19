package data;

public class Point 
{
/*╔════════════════════════╗*/
/*║       Attribut         ║*/
/*╚════════════════════════╝*/
	
	private String nom;//Nom du Point
	private int x;
	private int y;
	private Disque[] lstDisqueAssocies;
	private Point point;


/*╔════════════════════════╗*/
/*║      Constructeur      ║*/
/*╚════════════════════════╝*/
/*
	public Point(String nom, int x, int y) 
	{
		this.nom = nom;
		this.x   = x;
		this.y   = y;
	}
*/
	public Point(String nom, int x, int y) 
	{
		this.nom = nom;
		this.x   = x;/* public String getNom()*/
		this.y   = y;
	}


/*╔════════════════════════╗*/
/*║   Getters & Setters    ║*/
/*╚════════════════════════╝*/
	public String getNom()
	{
		return this.nom;
	}

	public void setNom(String nom)
	{ 
		this.nom = nom;
	}

	public int getX()
	{ 
		return this.x;
	}

	public void /*boolean*/ setX(int x)
	{ 
		this.x = x;
	}

	public int getY()
	{
		return this.y;
	}

	public void setY(int y)
	{ 
		this.y = y;
	}

}