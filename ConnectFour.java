/**
 * ConnectFour Class - Holds all game logic for CLI and GUI.
 * 
 * @author Jordan Castro
 * @version 01/25/20
 */
 
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;
import java.util.*;

class ConnectFour
{
	static Scanner scanner = new Scanner(System.in);
	static String[][] board = new String[6][15]; //Contains the computer keys that compose the CLI game board.
	static String player1;
	static String player2;
	static String currentPlayer;
	static String token1 = "O";
	static String token2 = "X";
	static String color1 = "BLUE";
	static String color2 = "YELLOW";
	static String currentToken;
	static String currentColor;
	static int chosenColumn = 0;
	static int turnCount;
	static int connected; //Used to check the amount of the same tokens connected.
	static boolean gameOver;
	static JFrame frame = new JFrame("Connect Four"); //Create window object.
	static ArrayList<ConnectFourToken> tokens = new ArrayList<ConnectFourToken>(); //Stores a ConnectFourToken object. Each object has attributes necessary for placement on the GUI board.
	static int windowEdgeSpacing = 5; //Increment for the coordinates of tokens in the GUI to be off the window edge.
	static int tokenSpacing = 86; //Increment to create spacing between tokens in the GUI.
	static int tokenDiameter = 73; //Used for width and height arguments for drawing ovals as circles.
	
	//Main method initializes the components of the GUI window and runs the game logic.
	public static void main(String[] args)
	{					
		//The following code passes the instructions in the "run" method to the  AWT Event Dispatch Thread and updates actions in the GUI.
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{			
				frame.setVisible(true);
				frame.setLocation(0, 0);
				frame.setSize(617, 556);	
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Ends program when window is closed.
				frame.add(new CustomPaintComponent()); //Allows painting to create the GUI.
			}
		});
		
		gameOver = false;
		turnCount = 0;
		welcome();
		createBoard();
		drawBoard();
		currentPlayer = player1;
		currentToken = token1;
		currentColor = color1;
		while(gameOver == false)
		{
			playerTurn();
		}
	}
	
	//Greets the users, explains the game, and receives and stores the players' names.
	public static void welcome()
	{
		System.out.println("Connect Four! (2 players)");
		System.out.println("Rules: Players take turns placing their respective tokens in one of seven columns.");
		System.out.println("The player that connects four of their tokens in a row, column, or diagonal, wins the game!");
		System.out.println("Enter player 1 name: ");
		player1 = scanner.nextLine();
		System.out.println(player1 + ", your token is 'O' and your colour is blue.");	
		System.out.println("Enter player 2 name: ");
		player2 = scanner.nextLine();
		System.out.println(player2 + ", your token is 'X' and your colour is yellow.");
		System.out.println("Player 1 (" + player1 + ") starts the game.");
	}
	
	//Stores the characters in the "board" 2D array in order.
	public static void createBoard()
	{		
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[i].length - 1; j += 2)
			{
				board[i][j] = "|";
				board[i][j + 1] = "_";				
			}		
			
			board[i][board[i].length - 1] = "|"; //Stores the right edge of the current row.
		}
	}
	
	//Prints out the characters of the "board" 2D array.
	public static void drawBoard()
	{
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[i].length; j++)
			{
				System.out.print(board[i][j]);
			}
			
			System.out.println(""); //Moves cursor down to print the next row.		
		}
		
		System.out.println(" 1 2 3 4 5 6 7");
	}
	
	//Allows the current player to choose a column to drop their token.
	public static void playerTurn()
	{
		//This "do" loop makes sure the user inputs a valid column number.
		do
		{
			System.out.println(currentPlayer + ", what column do you want to place your token? (1-7)");
			
			while(!scanner.hasNextInt()) //Continue prompting the user for an integer if they haven't already done so.
			{
				scanner.nextLine();		
				System.out.println(currentPlayer + " choose from column 1-7!");	
			}
			
			int input = scanner.nextInt(); //Stores the user's inputted integer.
			
			if(input < 8 && input > 0) //1-7 are valid columns.
			{
				chosenColumn = input;
			}
			else
			{
				System.out.println(currentPlayer + " choose from column 1-7!");
			}
		}while(chosenColumn == 0);
		
		//Loop through rows starting from the bottom.
		for(int i = board.length - 1; i > -1; i--)
		{
			//Places token in the chosen column at the current row if no other token is already there.
			if(board[i][chosenColumn * 2 - 1] == "_") 
			{
				board[i][chosenColumn * 2 - 1] = currentToken;
	
				makeMoveGUI(i, chosenColumn); //Enables the GUI to be updated with the players' move.
				
				turnCount++;
				checkWin();
				checkDraw();
				switchTurn();
				break; //Exits loop immediately.
			}
		}
		
		drawBoard(); //Updates the CLI game board.
	}
	
	/**
	 * Creates, and stores in the "tokens" array, a ConnectFourToken object named token with its coordinates 
	 * and color corresponding to the current player and their chosen column.
	 * 
	 * @param row - The row number of the chosen column that is open to have a token placed in it.
	 * @param chosenColumn - The player's chosen column.
	 */
	public static void makeMoveGUI(int row , int chosenColumn)
	{
		int rowMultiple = row;
		ConnectFourToken token = new ConnectFourToken(windowEdgeSpacing + tokenSpacing * (chosenColumn - 1),windowEdgeSpacing + tokenSpacing * rowMultiple, currentColor); //(x, y, color)
		tokens.add(token);
	}
	
	//Switches players after each turn.
	public static void switchTurn()
	{
		chosenColumn = 0; //Causes the next player's turn to be error-checked in the "playerTurn" method.
		
		if(currentPlayer == player1)
		{
			currentPlayer = player2;
			currentToken = token2;
			currentColor = color2;
		}
		else
		{
			currentPlayer = player1;
			currentToken = token1;
			currentColor = color1;
		}		
	}

	//Loops through every row in the CLI to check for a win.
	public static void checkRows()
	{
		connected = 0;
		
		for(int i = 0; i < board.length; i++)
		{
			connected = 0; //Reset count after switching to the next row.
			
			for(int j = 1; j < board[i].length - 1; j += 2)
			{
				if(board[i][j] == currentToken)
				{
					connected++;
					
					if(connected == 4) //If four identical tokens are connected, end the game.
					{
						gameOver = true;
						System.out.println(currentPlayer + " won by connecting a row!");
						break; 
					}
				}
				else //If streak of identical tokens is broken, reset the count.
				{
					connected = 0;
				}
			}
		}
	}
	
	//Loops through every column in the CLI to check for a win.
	public static void checkColumns()
	{
		connected = 0;
		
		for(int j = 1; j < 14; j += 2) //The array indexes for the columns are 1, 3, 5, 7, 9, 11, 13 due to the other indexes being filled with the board characters.
		{
			connected = 0;
			
			for(int i = 0; i < board.length; i++)
			{
				if(board[i][j] == currentToken)
				{
					connected++;
					
					if(connected == 4)
					{
						gameOver = true;
						System.out.println(currentPlayer + " won by connecting a column!");
						break;
					}
				}
				else
				{
					connected = 0;
				}
			}
		}
	}
	
	//Checks diagonals starting from column one travelling up to the right.
	public static void colOneUp()
	{
		connected = 0;
		int counter = 0;
		int hiRow = 5; //Highest potential row index.
		int hiCol = 11; //Highest potential column index.
		
		for(int i = 0; i < 3; i++) //There are 3 of these diagonals that are possible.
		{
			if(i == 1 || i == 2) //Increments the upper limit of the array indexes in order to start from the bottom of column 1.
			{
				connected = 0;
				counter = 0;
				hiRow--;
				hiCol -= 2;
			}
			
			for(int j = 1; j <= hiCol; j += 2)
			{
				if(board[hiRow - counter][j] == currentToken)
				{
					connected++;
					
					if(connected == 4)
					{
						gameOver = true; 
						System.out.println(currentPlayer + " won by connecting a column 1 front slash !");
						break;
					}
				}
				else
				{
					connected = 0;
				}
				
				counter++;
			}
		}
	}
	
	//Checks diagonals starting from row six travelling up to the right.
	public static void rowSixUp()
	{
		connected = 0;
		int counter = 0;
		int loCol = 3; //Lowest potential column index.
		
		for(int i = 0; i < 3; i++) //There are 3 of these diagonals that are possible.
		{
			if(i == 1 || i == 2) //Increments the lower limit of the column array index in order to start from the left of row 6.
			{
				connected = 0;
				counter = 0;
				loCol += 2;
			}
			
			for(int j = loCol; j <= 13; j += 2)
			{
				if(board[5 - counter][j] == currentToken)
				{
					connected++;
					
					if(connected == 4)
					{
						gameOver = true;
						System.out.println(currentPlayer + " won by connecting a row 6 front slash!");
						break;
					}
				}
				else
				{
					connected = 0;
				}
				
				counter++;
			}
		}
	}
	
	//Checks diagonals starting from column one travelling down to the right.
	public static void colOneDown()
	{
		connected = 0;
		int counter = 0;
		int loRow = 0; //Lowest potential row index.
		int hiCol = 11; //Highest potential column index.
		
		for(int i = 0; i < 3; i++) //There are 3 of these diagonals that are possible.
		{
			if(i == 1 || i == 2) //Increments the array indexes in order to start from the top of column 1.
			{
				connected = 0;
				counter = 0;
				loRow++;
				hiCol -= 2;
			}
			
			for(int j = 1; j <= hiCol; j += 2)
			{
				if(board[loRow + counter][j] == currentToken)
				{
					connected++;
					
					if(connected == 4)
					{
						gameOver = true;
						System.out.println(currentPlayer + " won by connecting a column 1 backslash!");
						break;
					}
				}
				else
				{
					connected = 0;
				}
				
				counter++;
			}
		}
	}
	
	//Checks diagonals starting from row one travelling down to the right.
	public static void rowOneDown()
	{
		connected = 0;
		int counter = 0;
		int loCol = 3; //Lowest potential column index.
		int loRow = 0; //Lowest potential row index.
		
		for(int i = 0; i < 3; i++) //There are 3 of these diagonals that are possible.
		{
			if(i == 1 || i == 2) //Increments the array indexes in order to start from the left of row 1.
			{
				connected = 0;
				counter = 0;
				loCol += 2;
				loRow = 0;
			}
			
			for(int j = loCol; j <= 13; j += 2)
			{
				if(board[loRow + counter][j] == currentToken)
				{
					connected++;
					
					if(connected == 4)
					{
						gameOver = true;
						System.out.println(currentPlayer + " won by connecting a row 1 backslash!");
						break;
					}
				}
				else
				{
					connected = 0;
				}
				
				counter++;
			}
		}
	}
	
	//Calls all the methods that together check every potential diagonally connected four.
	public static void checkDiagonals()
	{
		connected = 0;
		
		colOneUp();
		rowSixUp();
		colOneDown();
		rowOneDown();
	}
	
	//Calls all the methods that will check if the current player has connected four of their tokens.
	public static void checkWin()
	{
		connected = 0;
		
		checkRows();
		checkColumns();
		checkDiagonals();
	}
	
	//Checks if the board is full and ends the game as a tie if so.
	public static void checkDraw()
	{		
		if(turnCount == 42) //There are 42 slots on the board.
		{
			gameOver = true;
		}
	}
}


/**
 * {@inheritDoc}
 */
class CustomPaintComponent extends Component
{
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g; //This object allows me to use its methods to draw.
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, 600, 516); //Draws black rectangle as the board (the "cookie"). (x, y, width, height)
				
		int counter = 0;
		int multiple = -1; 
		for(int i = 0; i < 42; i++)
		{
			if(i % 7 == 0) //When the loop paints to the end of the row start from the left and paint a new row underneath.
			{
				counter = 0;
				multiple++;
			}
			
			g2d.setColor(Color.WHITE);
			g2d.fillOval(ConnectFour.windowEdgeSpacing + ConnectFour.tokenSpacing * counter, ConnectFour.windowEdgeSpacing + ConnectFour.tokenSpacing * multiple, ConnectFour.tokenDiameter, ConnectFour.tokenDiameter); //Draws white circular spaces (the "cookie cutters"). (x, y, width, height)
			
			counter++;
		}
		
		//Draws all the player-placed tokens based on the objects that were stored in the "tokens" array after each turn.
		for(int i = 0; i <= ConnectFour.tokens.size() - 1; i++)
		{
			//Changes the circle color depending on the corresponding object's "color" attribute.
			g2d.setColor(Color.YELLOW);
			if(ConnectFour.tokens.get(i).getColor().equals("BLUE"))
			{
				g2d.setColor(Color.BLUE);
			}
			
			g2d.fillOval(ConnectFour.tokens.get(i).getX(), ConnectFour.tokens.get(i).getY(), ConnectFour.tokenDiameter, ConnectFour.tokenDiameter); //Draws token.
		}
		
		//Updates the GUI after every turn.
		ConnectFour.frame.repaint();		
	}
}
