import java.util.*;
public class CreeClass
{
	private String nom;
	private List<String> lstAttribut;
	private List<String> lstMethode;

	public CreeClass factoryCreeClass(String data)
	{
		if (this.verifdata(data))
			return new CreeClass(data);
		else
			return null;
	}

	private CreeClass(String Data )
	{
		try
		{


		}catch (Exception e)
		{

		}
	}

	private void ajouterAttribut()
	{

	}

	private void ajouterMethode()
	{

	}

	private boolean verifdata(String data)
	{
		// on vérifie que le ficher est en .java
		
	}
}