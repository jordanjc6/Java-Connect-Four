/**
 * ConnectFourToken Class - Used to create objects with attributes
 * corresponding to player tokens and board coordinates.
 * 
 * @author Jordan Castro
 * @version 01/25/20
 */

public class ConnectFourToken
{
	private int x;
	private int y;
	private String color;
	
	 /**
	 * A custom constructor.
	 * 
	 * @param x - The x-coordinate of the token on the GUI board.
	 * @param y - The y-coordinate of the token on the GUI board.
	 * @param color - Color of token.
	 */
	public ConnectFourToken(int x, int y, String color)
	{
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	//Getter for x-coordinate.
	public int getX()
    {
        return this.x;
    }
    
	//Getter for y-coordinate.
	public int getY()
    {
        return this.y;
    }
    
    //Getter for color.
	public String getColor()
    {
        return this.color;
    }
}
