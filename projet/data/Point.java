package projet.data;

public class Point 
{
/*╔════════════════════════╗*/
/*║       Attribut         ║*/
/*╚════════════════════════╝*/
	private int nom;
	private int x;
	private int y;

/*╔════════════════════════╗*/
/*║      Constructeur      ║*/
/*╚════════════════════════╝*/

	public Point(int nom, int x, int y) 
	{
		this.nom = nom;
		this.x   = x;
		this.y   = y;
	}


/*╔════════════════════════╗*/
/*║   Getters & Setters    ║*/
/*╚════════════════════════╝*/
	public int  getNom()        
	{
		return this.nom;
	}

	public void setNom(int nom)
	{ 
		this.nom = nom;
	}

	public int  getX()          
	{ 
		return this.x;
	}

	public void setX(int x)     
	{ 
		this.x = x;    
	}

	public int  getY()          
	{ 
		return this.y;
	}

	public void setY(int y)     
	{ 
		this.y = y;    
	}

}