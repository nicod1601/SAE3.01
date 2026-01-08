package data;

import java.util.ArrayList;

public class ClassAssociation
{
/*╔════════════════════════╗*/
/*║       Attribut         ║*/
/*╚════════════════════════╝*/
	
	private String attrVerifComm; //Verification de si ça s'affiche

	private ArrayList<ClassPrincipal> lstClassPrincipals;

	private ClassAssociation          autoAssociation;


/*╔════════════════════════╗*/
/*║      Constructeur      ║*/
/*╚════════════════════════╝*/
/*
	public ClassAssociation(String attrVerifComm) 
	{
		this.attrVerifComm   = attrVerifComm;
	}
*/
	public ClassAssociation(String attrVerifComm) 
	{
		this.attrVerifComm   = attrVerifComm;/* public String getattrVerifComm()*/
	}


/*╔════════════════════════╗*/
/*║   Getters & Setters    ║*/
/*╚════════════════════════╝*/
	public String getAttrVerifComm()
	{
		return this.attrVerifComm;
	}

	public void /*boolean*/ setAttrVerifComm(String attrVerifComm)
	{ 
		this.attrVerifComm = attrVerifComm;
	}
}