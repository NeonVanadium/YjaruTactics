package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JFrame;

public class Main {
	
	private static Game g;
	private static Hashtable<Integer, Permanent> fighters = new Hashtable<Integer, Permanent>();
	
	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		f.setSize(1500, 1000);
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
		
		
		fighters.put(1, new Permanent("NATHANIEL", Color.GREEN));
    	fighters.put(2, new Permanent("SERAILA", Color.CYAN));
    	fighters.put(3, new Permanent("CLAIO III", Color.RED));
    	fighters.put(4, new Permanent("CATHERINE", Color.BLACK));
    	fighters.put(5, new Permanent("WILLIAM", Color.DARK_GRAY));
    	fighters.put(6, new Permanent("JAREN", Color.PINK));
    	
    	LinkedList<Permanent> t1 = new LinkedList<Permanent>(), t2 = new LinkedList<Permanent>();
    	
    	t1.add(fighters.get(1));
    	t1.add(fighters.get(2));
    	t1.add(fighters.get(3));
    	t2.add(fighters.get(4));
    	t2.add(fighters.get(5));
    	t2.add(fighters.get(6));
    	
    	
		
		Game g = new Game(grid, t1, t2);
		g.setBackground(Color.BLACK);
		f.add(g);
		f.setVisible(true);
		
		
	}

	public static Game getGame() {
		
		return g;
		
	}
	
}