package data;

public class ClassPrincipal extends ClassHeritage implements IClassInterface
{
/*╔════════════════════════╗*/
/*║       Attribut         ║*/
/*╚════════════════════════╝*/
	private        final String  CONSTANTE        = "constante en string";
	private static final int     CONSTANTE_STATIC = 10;

	private static       int     attributStatic;

	private              String  attribut;
 
	private              ClassAssociation[] tabAsso;
	private              ClassAssociation   asso1;
	private              ClassAssociation   asso2;
	
/*╔════════════════════════╗*/
/*║      Constructeur      ║*/
/*╚════════════════════════╝*/
	public ClassPrincipal(int attributFille, ClassAssociation asso1)
	{
		super(attributFille);
		this.asso1 = asso1;
	}

	public ClassPrincipal(ClassAssociation asso1)
	{
		this(10, asso1);
	}

	public int testInt(int un)
	{
		return 1;
	}

	public double testDouble(double unVirguleUn) 
	{
		return 1.1;
	}

	public String methodeInterface()
	{
		return attribut + " " + attributStatic;
	}
	
	/* 
	public void testLongeur(int a, int b, int c, int d, int e, int f, int g, int h) 
	{
		System.out.println("testLongeur");
	}*/
}

