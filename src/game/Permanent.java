package game;

import java.awt.Color;
import java.awt.Point;

public class Permanent {
	
	private int x; //x-coordinate of position
	private int y; //y-coordinate of position
	private int hp = 10; //health points
	private String name; 
	private int prevX;
	private int prevY;
	private int ap = 5; //action points
	private boolean isMovable = true; //can this object be moved
	private boolean immortal = false; //can this object be destroyed
	private boolean solid = true; //is this object solid (does it make the occupied tile impassable)
	//private Picture sprite;
	
	public class Attack{
		
		private String name;
		private int cost;
		private int damage; //damage dealt to all non-immortal permanents in area of effect
		private int width; //the width of the area of effect
		private int height; //the height of the area of effect
		
		public Attack(String name, int damage, int width, int height){
			
			this.name = name;
			this.damage = damage;
			this.width = width;
			this.height = width;
			
		}
		
	}
	
	protected Color c; //temporary for testing
	
	public Permanent(String name, int x, int y, Color c) {
		
		this.name = name;
		this.x = x;
		this.y = y;
		this.c = c;
		
	}
	
	public void enter(int row, int col){
		
		prevX = x;
		prevY = y;
		x = row;
		y = col;
		
	}
	
	public String attack(Permanent p){
		
		return "";
		
	}
	
	public boolean changeHealth(int change){
		
		if(immortal) return false;
		
		hp += change;
		
		return true;
		
	}
	
	public boolean isAdjacentTo(Permanent other){
		
		if(Math.abs(this.x - other.x) == 1 ||  Math.abs(this.y - other.y) == 1) return true;
		return false;
		
	}
	
	public Point getPosition() { return new Point(x, y); }
	
	public int x() { return x; }
	
	public int y() { return y;}
	
	public int prevX(){return prevX;}
	
	public int prevY(){return prevY;}
	
	public int HP() { return hp; }
	
	public int getAP() { return ap; }
	
	public String getName() { return name; }
	
	public boolean isMovable() { return isMovable; }
	
	public String toString() { return "PERMANENT \"" + name + "\" @ LOCATION: (" + x + ", " + y + ")"; }
	
	public boolean isImmortal() { return immortal; }
	
	public boolean isSolid() { return solid; };
	

}