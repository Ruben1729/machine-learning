package tictactoe;

public class Board {

	private int[] board;
	
	public Board() {
		
		board = new int[9];
		
	}
	
	public boolean checkIfEmpty(int index)
	{
		
		if(board[index] != 0)
			return false;

		return true;
		
	}
	
	public float[] genInput()
	{
		
		float[] inputs = new float[27];
		
		for(int b = 0; b < board.length; b ++)
		{
			
			switch(board[b])
			{
			
				case 0:
					inputs[(b * 3)] = 1;
					break;
					
				case 1:
					inputs[(b * 3) + 1] = 1;
					break;
					
				case 2:
					inputs[(b * 3) + 2] = 1;
					break;

			}
			
		}
		
		return inputs;
		
	}
	
	public boolean insertPos(int index, int player){
		
		if(checkIfEmpty(index)) {
			
			board[index] = player;
			return true;
			
		}
			
		return false;
		
	}
	
	public int checkWinner(){

		for(int p = 1; p <= 2; p ++)
		{
			
			for(int i = 0; i < 3; i ++)
			{
				
				if(board[i*3] == p && board[i*3 + 1]== p && board[i*3 + 2]== p)
					return p;
				
				if(board[i]==p && board[i + 3] == p && board[i + 6] == p)
					return p;
				
			}
			
			if((board[0] == p && board[4] == p && board[8] == p) || 
					(board[2] == p && board[4] == p && board[6] == p))
				return p;
			
		}
		
		for(int b = 0; b < board.length; b++)
		{
			
			if(board[b] == 0)
				return -1;
			
		}
		
		return 0;//return -1 for draw
		
	}
	
	public int[] getBoard() {return board;}
	
}
