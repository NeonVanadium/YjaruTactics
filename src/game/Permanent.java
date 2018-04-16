package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class Permanent implements Comparable<String>{
	
	private int x; //x-coordinate of position
	private int y; //y-coordinate of position
	private int hp = 10; //health points
	private int dex = 5;
	private int team; //one or two
	private int prevX;
	private int prevY;
	private int ap = 5; //action points
	private int curFrame = 0; //0-face, 1-rightfacing, 2-back, 3-leftfacing
	private String name; //name of the permanent
	private boolean isMovable = true; //can this object be moved
	private boolean immortal = false; //can this object be destroyed
	private boolean solid = true; //is this object solid (does it make the occupied tile impassable)
	private BufferedImage[] frames;
	private Attack[] attacks;
	
	public Permanent(String name, String spritesheet, int w, int h) { //TODO eventually remove
		
		this.name = name;
		
		frames = new BufferedImage[4];
		
		for(int i = 0; i < 4; i++) {
			
			try {
				frames[i] = ImageIO.read(new File(spritesheet)).getSubimage(w * i, 0, w, h);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		attacks = new Attack[] { Main.getAttacks().get(0) };
		
	}
	
	public Permanent(String name, String spritesheet, int w, int h, int[] attacks) {
		
		this.name = name;
		
		frames = new BufferedImage[4];
		
		for(int i = 0; i < 4; i++) {
			
			try {
				frames[i] = ImageIO.read(new File(spritesheet)).getSubimage(w * i, 0, w, h);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		this.attacks = new Attack[attacks.length];
		Hashtable<Integer, Attack> attackTable = Main.getAttacks();
		
		for(int i = 0; i < attacks.length; i++) {
			
			this.attacks[i] = attackTable.get(attacks[i]);
			
		}
		
	}
	
	public BufferedImage getFrame(int i) { return frames[i]; }
	
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
	
	public String attack(Permanent p){ //basic attacks
		
		return attack(attacks[0], p);
	
	}
	
	public String attack(Attack a, Permanent p) {
		
		if(p.getTeam() == team) return "";
		
		
		
		int damage = 0;
		
		if(a.times() == 1) {
			
			if((int) (Math.random() * 5) == 0) { //should be a 1/dex chance of missing. TODO confirm this
				
				return name + ": Miss.";
				
			}
			
			if(a.didCrit()) {
				
				Main.getGame().toConsole("<<<CRIT>>>", Color.RED);
				damage = 2 * a.damageRoll();
				
			}
			
			damage = a.damageRoll();
			
		}
		else {
			
			int total = 0;
			int numCrits = 0;
			
			for(int i = 0; i < a.times(); i++) {
				
				if(a.didCrit()) {
					
					total += 2 * a.damageRoll();
					numCrits += 1;
					
				}
				
				total += a.damageRoll();
				
			}
			
			if(numCrits > 0) Main.getGame().toConsole("<<<CRIT x" + numCrits + ">>>", Color.RED);

			damage = total;

		}
		
		p.changeHealth(-damage);
		
		return name + ": " + a.name().toUpperCase() + " for " + damage + " on " + p.getName() + ".";
		
	}
	
	public Attack[] getAttacks() { return attacks; }
	
	public int getFacing() { return curFrame; }
	
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
	
	public int scaleWidth(int scale) {
		
		return scaleWidth(curFrame, scale);
		
	}
	
	public int scaleHeight(int scale) {
		
		return scaleHeight(curFrame, scale);
		
	}
	
	public int scaleWidth(int dir, int scale) {
		
		if(dir == 0 || dir == 2) {
			
			return frames[dir].getWidth() * scale;
			
		}
		else if(dir == 1 || dir == 3) {
			
			return (frames[dir].getWidth()) * scale;
			
		}
		
		return 0;
		
	}
	
	public int scaleHeight(int dir, int scale) {
		
		return frames[dir].getHeight() * scale;
		
	}
	
	public void compensatedDraw(int dir, int scale, int x, int y, Graphics g, ImageObserver o) {
		
		y -= scale * (frames[dir].getHeight() - 34);
		//x -= scale * (frames[dir].getWidth() - 11);
		
		
		g.drawImage(frames[dir], x, y, scaleWidth(dir, scale), scaleHeight(dir, scale), o);
		
	}
	
	public void compensatedDraw(int scale, int x, int y, Graphics g, ImageObserver o) {
		
		compensatedDraw(curFrame, scale, x, y, g, o);
		
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
	
	public boolean isSolid() { return solid; }

	public int compareTo(String arg0) {
		
		return name.compareTo(arg0);
		
	};
	

}