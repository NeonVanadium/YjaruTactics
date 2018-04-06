package game;

public class Tile {
	
	private Permanent occupier = null; //the permanent, if any, that is on this tile
	private boolean impassable = false;
	private final int x;
	private final int y;
	//private Picture sprite;
	
	public Tile(int row, int col, boolean impassable){
		
		this.x = col;
		this.y = row;
		this.impassable = impassable;
		
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
	
	public Permanent occupier(){ return occupier; }
	
	public Permanent remove(){Permanent temp = occupier; occupier = null; return temp; }
	
	public String toString() {
		
		return "Tile at row " + y + " col " + x +".\nPASSABLE: " + !impassable + "\nOCCUPIER: " + occupier +"\n";
		
	}

}
