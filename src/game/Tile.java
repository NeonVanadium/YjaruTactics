package game;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile {
	
	private Permanent occupier = null; //the permanent, if any, that is on this tile
	private boolean impassable = false;
	private final int x;
	private final int y;
	private Image sprite;
	
	public Tile(int row, int col, String spritepath, boolean impassable){
		
		this.x = col;
		this.y = row;
		this.impassable = impassable;
		
		try {
			sprite = ImageIO.read(new File(spritepath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean passable() { 
		
		if(impassable) return false;
		
		if(occupier != null && occupier.isSolid()) return false;
		
		return true; 
		
	}
	
	public boolean put(Permanent p){
		
		if(occupier != null) return false;
		
		occupier = p;
		p.enter(x, y);
		
		return true;
		
	}
	
	public Image getSprite() { return sprite; } 
	
	public Permanent occupier(){ return occupier; }
	
	public Permanent remove(){Permanent temp = occupier; occupier = null; return temp; }
	
	public String toString() {
		
		return "Tile at row " + y + " col " + x +".\nPASSABLE: " + !impassable + "\nOCCUPIER: " + occupier +"\n";
		
	}

}
