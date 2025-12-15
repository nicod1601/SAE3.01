package data;

public class Disque  extends Rond implements ISurface
{
	private static int       autoIncre;
	private static final int MAXLENGTH = 10;

	private final String     NOM       = "NOM allo";

	private Point[]          tabPoints;
	private Point            reponsable;
	private Point            centre;
	
	
	public Disque(Point centre, double rayon)
	{
		super(rayon);
		this.centre = centre;
	}

	public Disque(Point cente)
	{
		this(cente, 10);
	}

	public double calculerAire()
	{
		return Math.PI * super.getRayon() * super.getRayon();
	}

	public double calculerPerimetre() 
	{
		return 2 * Math.PI * super.getRayon();
	}

	public Disque testGenerique(int a,int b,int c,int d)
	{
		return this;
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

	@Override
	public double calculerSurface()
	{
		return Math.PI * Math.pow(super.getRayon(), 2);
	}
}
