package projet.data;

import java.util.List;
import projet.data.ISurface;

public class Disque  extends Rond implements ISurface
{
	private Point       centre;
	private Rond        decentre;
	
	public Disque(Point centre, double rayon)
	{
		super(rayon);
		this.centre = centre;
	}

	public double calculerAire()
	{
		return Math.PI * super.getRayon() * super.getRayon();
	}

	public double calculerPerimetre() 
	{
		return 2 * Math.PI * super.getRayon();
	}

	public void setX(int x) 
	{
		centre.setX(x);
	}

	public void setY(int y) 
	{
		centre.setY(y);
	}

	public Point getCentre()
	{
		return centre;
	}

	public double calculerSurface()
	{
		return Math.PI * Math.pow(super.getRayon(), 2);
	}
}
