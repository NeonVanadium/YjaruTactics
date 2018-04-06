package game;

import java.awt.Color;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

public class Main {
	
	private static Game g;
	
	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		f.setSize(1250, 1500);
		f.setTitle("GAME");
		
		Tile[][] grid = new Tile[20][20];
		
		for(int i = 0; i < grid.length; i++){
			
			for(int j = 0; j < grid[0].length; j++){
				
				grid[i][j] = new Tile(i, j, false);
				
			}
			
		}
		
		for(int i = 0; i < grid.length / 2; i++) {
			
			if(i != 4) grid[i][7] = new Tile(i, 7, true);
			
		}
		
		Game g = new Game(grid);
		g.setBackground(Color.BLACK);
		f.add(g);
		f.setVisible(true);
		
		
	}

	public static Game getGame() {
		
		return g;
		
	}
	
}