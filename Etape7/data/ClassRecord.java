package data;

public record ClassRecord (String attrString, double attrDouble, int attrInt) implements IClassInterface
{
	@Override
	public String methodeInterface()
	{
		return this.attrString + " " + attrDouble;
	}
}
