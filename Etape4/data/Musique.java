package data;

public record Musique (String nom,int durre,int plusLong) implements ISurface
{
    @Override
	public double calculerSurface()
	{
		return 10.1;
	}
}
