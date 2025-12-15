package data;

public record Musique (String nom,int durre) implements ISurface
{
    @Override
	public double calculerSurface()
	{
		return 10.1;
	}
}
