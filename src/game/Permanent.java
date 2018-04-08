package game;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Permanent {
	
	private int x; //x-coordinate of position
	private int y; //y-coordinate of position
	private int hp = 20; //health points
	private int team; //one or two
	private String name; 
	private int prevX;
	private int prevY;
	private int ap = 5; //action points
	private boolean isMovable = true; //can this object be moved
	private boolean immortal = false; //can this object be destroyed
	private boolean solid = true; //is this object solid (does it make the occupied tile impassable)
	private BufferedImage[] frames;
	private int curFrame = 0; //0-face, 1-rightfacing, 2-back, 3-leftfacing
	
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
	
	public Permanent(String name, String spritesheet, int w, int h) {
		
		this.name = name;
		
		frames = new BufferedImage[4];
		
		for(int i = 0; i < 4; i++) {
			
			try {
				frames[i] = ImageIO.read(new File(spritesheet)).getSubimage(w * i, 0, w, h);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public BufferedImage getSprite() {
		
		return frames[curFrame];
		
	}
	
	public void enter(int x, int y){
		
		if(x < this.x) curFrame = 3;
		if(x > this.x) curFrame = 1;
		if(y < this.y) curFrame = 2;
		if(y > this.y) curFrame = 0;
		
		prevX = this.x;
		prevY = this.y;
		this.x = x;
		this.y = y;
		
	}
	
	public String attack(Permanent p){
		
		if(p.getTeam() == team) return "";
		
		int damage = (int) ((Math.random() * 10) + 1);
		
		p.changeHealth(-damage);
		
		return name + " swings for " + damage + " on " + p.getName() + ".";
		
	}
	
	public boolean changeHealth(int change){
		
		if(!immortal) hp += change;
		
		return isAlive();
		
	}
	
	public boolean isAlive() {
		
		if(hp <= 0) return false;
		return true;
		
	}
	
	public boolean isAdjacentTo(Permanent other){
		
		if(Math.abs(this.x - other.x) == 1 ||  Math.abs(this.y - other.y) == 1) return true;
		return false;
		
	}
	
	public void setFacing(int direction) {
		
		if(direction < 0 || direction > 3) return;
		
		curFrame = direction;
		
	}
	
	public Point getPosition() { return new Point(x, y); }
	
	public void setPosition(int x, int y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public void setTeam(int team) {
		
		this.team = team;
		
	}
	
	public int getTeam() {
		
		return team;
		
	}
	
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