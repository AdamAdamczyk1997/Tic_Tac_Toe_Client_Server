
import java.util.Random;

public class TicTacToe implements Cloneable
{

    private final int NOBODY_TURN = 0, PLAYER_TURN   = 1, PLAYER2_TURN = -1;
    private final char NOBODY_MARK   = ' ', PLAYER_MARK   = 'X', PLAYER2_MARK = 'O';

    private int whoseTurn = NOBODY_TURN, firstTurn = NOBODY_TURN;
    private char[] grid = new char[9];
    private int[] moves = new int[9];
    private int numMoves = 0;
    public String nickPlayer1, nickPlayer2;

    public TicTacToe(String name1, String name2)
    {
        nickPlayer1 = name1;
        nickPlayer2 = name2;

        for(int i = 0; i < 9; i++)
        {
            grid[i] = NOBODY_MARK;
        }

        generateMoves();
    }

    public TicTacToe clone() throws CloneNotSupportedException
    {
        TicTacToe clone = (TicTacToe)super.clone();
        clone.grid = this.grid.clone();
        clone.moves = this.moves.clone();

        return clone;
    }

    public int chooseFirstPlayer()
    {
        Random generator = new Random();

        if(generator.nextInt(2) == 0)
        {
            System.out.println(" Zaczyna " + nickPlayer1);
            setFirstTurn(PLAYER_TURN);
            return 1;
        }

        else
        {
            System.out.println(" Zaczyna " + nickPlayer2);
            setFirstTurn(PLAYER2_TURN);
            return 2;
        }
    }

    public String drawBoard()
    {
        StringBuilder toReturn = new StringBuilder();

        for(char space : grid)
        {
            if(space == PLAYER_MARK) toReturn.append("1");
            else if(space == PLAYER2_MARK) toReturn.append("2");
            else toReturn.append("-");
        }

        return toReturn + "\n";
    }

    public void generateMoves()
    {
        // Generate the list of possible moves and store them in moves.
        for(int i = 0; i < moves.length; i++)
        {
            // If it is not taken, mark the square as available.  Otherwise, it is unavailable.
            if (grid[i] == NOBODY_MARK) moves[i] = 1;
            else moves[i] = 0;
        }

    }

    /**
     * Returns a boolean value regarding whether or not a proposed move is legal.
     */
    public boolean legalMove(int move)
    {
        // Return false if the move is outside the bounds of the game.
        if(move < 0 || move > 8)
        {
            return true;
        }

        // Returns true if the move is available and false if it is not.
        return moves[move] != 1;
    }

    /**
     * Updates the game so player ({player}_TURN) makes a move at the specified
     * point in the grid.
     */
    public void placePiece(int player, int move)
    {
        if (player == PLAYER_TURN) {
            grid[move] = PLAYER_MARK;
            numMoves++;

            // Generate the new set of moves taken/not taken in the game.
            generateMoves();
        }

        else if (player == PLAYER2_TURN){
            grid[move] = PLAYER2_MARK;
            numMoves++;

            // Generate the new set of moves taken/not taken in the game.
            generateMoves();
        }

    }

    /**
     * Returns an integer value based on examining the state of the game:
     * 0 - game is on-going
     * 1 - player1 has won
     * 2 - player2 has won
     * 3 - game is a tie
     */
    public int result()
    {
        // First check the columns to see if there are any winners.
        for(int i = 0; i < 3; i++)
        {
            // Has the player1 won?
            if(grid[i] == PLAYER_MARK && grid[i + 3] == PLAYER_MARK && grid[i + 6] == PLAYER_MARK)
            {
                return 1;
            }

            // Has the player2 won?
            else if(grid[i] == PLAYER2_MARK && grid[i + 3] == PLAYER2_MARK && grid[i + 6] == PLAYER2_MARK)
            {
                return 2;
            }
        }

        // Then check rows for any winners.
        for(int i = 0; i <= 6; i += 3)
        {
            // Has the player won?
            if(grid[i] == PLAYER_MARK && grid[i + 1] == PLAYER_MARK && grid[i + 2] == PLAYER_MARK)
            {
                return 1;
            }

            // Has the player2 won?
            if(grid[i] == PLAYER2_MARK && grid[i + 1] == PLAYER2_MARK && grid[i + 2] == PLAYER2_MARK)
            {
                return 2;
            }
        }

        // Finally, check the diagonals.
        if(grid[0] == PLAYER_MARK && grid[4] == PLAYER_MARK && grid[8] == PLAYER_MARK)
        {
            return 1;
        }

        else if(grid[2] == PLAYER_MARK && grid[4] == PLAYER_MARK && grid[6] == PLAYER_MARK)
        {
            return 1;
        }


        if(grid[0] == PLAYER2_MARK && grid[4] == PLAYER2_MARK && grid[8] == PLAYER2_MARK)
        {
            return 2;
        }

        else if(grid[2] == PLAYER2_MARK && grid[4] == PLAYER2_MARK && grid[6] == PLAYER2_MARK)
        {
            return 2;
        }

        // If there are 9 moves at this point, the game is a draw.
        if(numMoves == 9)
        {
            return 3;
        }

        // Otherwise, the game continues!
        else
        {
            return 0;
        }
    }

     //Returns true if the game is over or false if it is not.
    public boolean isOver()
    {
        return (result() == 0);
    }

    //Return the int value of who controls the current turn.
    public int getWhoseTurn()
    {
        return whoseTurn;
    }

     // Sets the int value of who controls the current turn.
    public void setWhoseTurn(int whoseTurn)
    {
        this.whoseTurn = whoseTurn;
    }

     //Return the int value of who gets to go first in this game.
    public int getFirstTurn()
    {
        return firstTurn;
    }

    //Sets the value of who gets to go first in this game.
    public void setFirstTurn(int firstTurn)
    {
        this.firstTurn = firstTurn;
    }

     //Returns an array of legal moves.
    public int[] getMoves()
    {
        return moves;
    }

    //Returns the int value representing the player has the first move of the game.
    public int getPLAYER_TURN()
    {
        return PLAYER_TURN;
    }

    // Returns the int value representing the player2 has the first move of the game.
    public int getPLAYER2_TURN()
    {
        return PLAYER2_TURN;
    }
}
