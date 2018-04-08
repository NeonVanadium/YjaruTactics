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
		f.setSize(1435, 1050);
		f.setTitle("GAME");
		
		Tile[][] grid = new Tile[15][6];
		
		for(int i = 0; i < grid.length; i++){
			
			for(int j = 0; j < grid[0].length; j++){
				
				grid[i][j] = new Tile(i, j, "Sprites/Grass.png", false);
				
			}
			
		}
		
		/*for(int i = 0; i < grid.length / 2; i++) {
			
			if(i != 4) grid[i][7] = new Tile(i, 7, true);
			
		}
		
		for(int i = 0; i < grid.length / 2; i++) {
			
			if(i != 4) grid[i][grid[0].length - 5] = new Tile(i, 7, true);
			
		}
		
		for(int i = 7; i < grid[0].length - 4; i++) {
			
			if(i != 10) grid[grid.length / 2][i] = new Tile(i, 7, true);
			
		}*/
	
		fighters.put(1, new Permanent("NATHANIEL", "Sprites/Nathaniel.png", 15, 34));
    	fighters.put(2, new Permanent("SERAILA", "Sprites/Seraila.png", 13, 34));
    	fighters.put(3, new Permanent("CLAIO III", "Sprites/Claio III.png", 13, 34));
    	fighters.put(4, new Permanent("CATHERINE", "Sprites/Catherine.png", 15, 34));
    	fighters.put(5, new Permanent("WILLIAM", "Sprites/William.png", 13, 34));
    	fighters.put(6, new Permanent("JAREN", "Sprites/Jaren.png", 15, 34));
    	fighters.put(7, new Permanent("KANOS", "Sprites/Kanos.png", 13, 34));
    	
    	LinkedList<Permanent> t1 = new LinkedList<Permanent>(), t2 = new LinkedList<Permanent>();
    	
    	t1.add(fighters.get(1));
    	t1.add(fighters.get(2));
    	t1.add(fighters.get(3));
    	t2.add(fighters.get(4));
    	t1.add(fighters.get(5));
    	t2.add(fighters.get(6));
    	t2.add(fighters.get(7));
		
    	//f.setUndecorated(true);
		Game g = new Game(grid, t1, t2);
		g.setBackground(Color.BLACK);
		f.setBackground(Color.BLACK);
		f.add(g);
		f.setVisible(true);
		
		while(g.isEnabled()){
			
			//g.draw();
			
		}
		
		f.setEnabled(false);
		System.exit(0);
		
	}

	public static Game getGame() {
		
		return g;
		
	}
	
}