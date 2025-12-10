package projet.data;

public class Rectangle
{
	private Point x;
	private Point y;

	public Rectangle(Point x, Point y)
	{
		this.x = x;
		this.y = y;
	}


	public Point getX()
	{
		return this.x;
	}

	public void setX(Point x)
	{
		this.x = x;
	}

	public Point getY()
	{
		return this.y;
	}

	public void setY(Point y)
	{
		this.y = y;
	}
	
}