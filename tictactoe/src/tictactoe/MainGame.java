package tictactoe;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;

public class MainGame extends Canvas implements Runnable{
	
	private static final long serialVersionUID = 1L;
	boolean player;
	Board board;
	float firstPlayerPos = -1, secondPlayerPos = -1;
	
	int[] stats = new int[3];
	
	private static JFrame frame;
	boolean aiTurn = true;
	
	public static void main(String[] args){
		frame = new JFrame("Tic Tac Toe");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setPreferredSize(new Dimension(610, 610));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		
		
		MainGame mg = new MainGame();
		
		
		frame.add(mg);
		
		
		frame.pack();
		frame.revalidate();
		frame.repaint();
		
		mg.run();
	}
	
	private MainGame(){
		
		player = true;
		
		board = new Board();
		
		this.addMouseListener(new Mouse());
		
	}
	
	public void run() {
		
		Network netAlpha = new Network(new Layer[] {new Layer(27, 16), new Layer(16, 16), new Layer(16, 9)});
		Network netBeta = new Network(new Layer[] {new Layer(27, 16), new Layer(16, 16), new Layer(16, 9)});
		
		Network[] netArray = new Network[] {netAlpha, netBeta};
		
		Random rand = new Random();
		
		int games = 1000000;
		
		for(int g = 0; g < games; g ++)
		{
			
			int winner = 0;
			
			Network currentNet = null;
			float[] inputs = null;
			float[] inputsTwoTurnsAgo = null;
			float[] inputsThreeTurnsAgo = null;
			float[] inputsOneTurnAgo = null;
			//float[] outputs = null;
			
			int pos = -1;
			
			while((winner = board.checkWinner()) == -1)
			{
				
				currentNet = player ? netAlpha : netBeta;

				inputs = board.genInput();

				
				do
					pos = rand.nextInt(9);
				while(!board.insertPos(pos, player ? 1 : 2));
				//render(board);
				//printBoard(player ? "Alpha" : "Beta");
				
				player = !player;
				
				inputsThreeTurnsAgo = inputsTwoTurnsAgo;
				inputsTwoTurnsAgo = inputsOneTurnAgo;
				inputsOneTurnAgo = inputs;
				
			}

			board = new Board();		
			
			if(winner == 1)
			{
				
				// Loser
				netArray[1].backprop(inputsTwoTurnsAgo, genDesired(pos));
				
				// Winner
				netArray[0].backprop(inputsOneTurnAgo, genDesired(pos));
				
			}
			else if(winner == 2)
			{
				
				// Loser
				netArray[0].backprop(inputsTwoTurnsAgo, genDesired(pos));
				
				// Winner
				netArray[1].backprop(inputsOneTurnAgo, genDesired(pos));
				
			}
			else
				// Draw
				currentNet.backprop(inputsThreeTurnsAgo, otherPossibilties(inputsTwoTurnsAgo));
				
			
			stats[winner] ++;
			
			System.out.println("draw - " + stats[0]);
			System.out.println("netAlpha - " + stats[1]);
			System.out.println("netBeta - " + stats[2]);
			
		}
		
		System.out.println("draw - " + stats[0]);
		System.out.println("netAlpha - " + stats[1]);
		System.out.println("netBeta - " + stats[2]);
		
		boolean machineStarts = true;
		while(true) {
			float[] inputs = null;
			int winner = -1;
			
			if(machineStarts)
				aiTurn = true;
			
			while((winner = board.checkWinner()) == -1) {
				render(board);
				
				if(aiTurn) {
					
					inputs = board.genInput();
					int pos = genPosition(netAlpha.generateOutputs(inputs));
					board.insertPos(pos, 2);
					aiTurn = false;
					
				}
			}
			machineStarts = !machineStarts;
			System.out.println(winner == 0 ? "Draw" : 
								winner == 1 ? "Player wins!":
									"AI wins!");
			
			board = new Board();
			
		}
		
	}
	
	public float[] otherPossibilties(float[] inputs) {
		
		float[] result = new float[board.getBoard().length];
		
		for(int i = 0; i < board.getBoard().length; i ++)
			if(inputs[(i * 3)] == 1)
				result[i] = 1;
		
		return result;
		
	}
	
	public void printBoard(String p) {
		
		System.out.println();
		System.out.println("Player " + p);
		System.out.println();
		System.out.println(" " + board.getBoard()[0] + " | " + board.getBoard()[1] + " | " + board.getBoard()[2]);
		System.out.println("   |   |   ");
		System.out.println("-----------");
		System.out.println("   |   |   ");
		System.out.println(" " + board.getBoard()[3] + " | " + board.getBoard()[4] + " | " + board.getBoard()[5]);
		System.out.println("   |   |   ");
		System.out.println("-----------");
		System.out.println("   |   |   ");
		System.out.println(" " + board.getBoard()[6] + " | " + board.getBoard()[7] + " | " + board.getBoard()[8]);
		System.out.println();
		
	}
	
	public int genPosition(float[] outputs) {
		
		float currentStrongestPosChances = -1;
		int strongestPosIndex = 0;
		
		for(int o = 0; o < outputs.length; o ++)
		{
			
				if(currentStrongestPosChances < outputs[o] && board.checkIfEmpty(o))
				{
					
					currentStrongestPosChances = outputs[o];
					strongestPosIndex = o;
					
				}
			
		}
		
		return strongestPosIndex;
		
	}
	
	public float[] genDesired(int lastMove) {
		
		float[] desired = new float[board.getBoard().length];
		desired[lastMove] = 1;
		
		return desired;
		
	}
	
	public float[] genDesiredIfOccupied() {
		
		int boardSize = board.getBoard().length;
		
		float[] desired = new float[boardSize];
		Arrays.fill(desired, 1);
		
		for(int b = 0; b < boardSize; b ++)
			if(!board.checkIfEmpty(b))
				desired[b] = 0;
		
		return desired;
		
	}
	
	
	public void render(Board b) {
		
		if(this.getBufferStrategy() == null)
			this.createBufferStrategy(3);
		
		BufferStrategy bs = this.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
	             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
	             RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.black);
		
		g2d.fillRect(0, 0, 610, 610);
		
		g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(Color.white);
		
		g2d.drawLine(203, 20, 203, 590);
		g2d.drawLine(406, 20, 406, 590);
		
		g2d.drawLine(20, 203, 590, 203);
		g2d.drawLine(20, 406, 590, 406);
		
		g2d.setStroke(new BasicStroke(21, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				if(b.getBoard()[(i * 3) + j] == 1) {
					g2d.setColor(Color.white);
					g2d.drawArc(203 * i + 40, 203 * j + 40, 120, 120, 0, 360);
				}else if(b.getBoard()[(i * 3) + j] == 2){
					g2d.setColor(Color.white);
					g2d.drawLine(203 * i + 40, 203 * j + 40, 203 * i + 160, 203 * j + 160);
					g2d.drawLine(203 * i + 40, 203 * j + 160, 203 * i + 160, 203 * j + 40);
				}
		
		
		bs.show();
		g.dispose();
		
	}
	
	class Mouse implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int mx = e.getX();
			int my = e.getY();
			
			for(int i = 0; i < 3; i++)
				for(int j = 0; j < 3; j++)
					if(mx > 203 * j && mx < 203 * (j + 1) &&
							my > 203 * i && my < 203 * (i + 1) && board.insertPos(j * 3 + i, 1) && !aiTurn)
						aiTurn = true;
		}

	}
}
