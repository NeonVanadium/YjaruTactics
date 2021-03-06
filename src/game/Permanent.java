package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class Permanent {
	
	private int x; //x-coordinate of position
	private int y; //y-coordinate of position
	private int hp = 10; //health points
	private int dex = 5;
	private int team; //one or two
	private int prevX;
	private int prevY;
	private final int ap = 5; //action points
	private int curFrame = 0; //0-face, 1-rightfacing, 2-back, 3-leftfacing
	private String name; //name of the permanent
	private boolean isMovable = true; //can this object be moved
	private boolean immortal = false; //can this object be destroyed
	private boolean solid = true; //is this object solid (does it make the occupied tile impassable)
	private BufferedImage[] frames;
	private Ability[] abilities;
	private int curAbility;
	
	public Permanent(String name, String spritesheet, int w, int h, int[] abilities) {
		
		this.name = name;
		
		frames = new BufferedImage[4];
		
		for(int i = 0; i < 4; i++) {
			
			try {
				frames[i] = ImageIO.read(new File(spritesheet)).getSubimage(w * i, 0, w, h);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		this.abilities = new Ability[abilities.length];
		Hashtable<Integer, Ability> attackTable = Main.getAbilities();
		
		for(int i = 0; i < abilities.length; i++) {
			
			this.abilities[i] = attackTable.get(abilities[i]);
			
		}
		
	}
	
	public boolean canTarget(Permanent p){
		
		Ability a = abilities[curAbility];
		
		if(Main.getGame().getRemainingAP() < a.cost()) return false; //if the ability costs more action points than this permanent has availible
		
		//if(!Main.getGame().getBoard().inRange(a.range(), y, x).contains(p)) return false; //confims that the permanent is targetable
		
		if(a.type() == 2 && p != this) return false; //if is a self-spell and target permanent is not the caster
		
		if(a.type() == 1 && p.getTeam() != this.getTeam()) return false; //if is a same-team spell and target permanent is not on the caster's team
		
		if(a.type() == 0 && p.getTeam() == this.getTeam()) return false; //if is a self-spell and target permanent is not on the other team
		
		return Main.getGame().getBoard().inRange(a.range(), y, x).contains(p);
		
	}
	
	public BufferedImage getFrame(int i) { return frames[i]; }
	
	public BufferedImage getSprite() {
		
		return frames[curFrame];
		
	}
	
	public void enter(int x, int y){
		
		face(x, y);
		
		prevX = this.x;
		prevY = this.y;
		this.x = x;
		this.y = y;
		
	}
	
	public boolean face(int x, int y){
		
		int prev = curFrame;
		
		if(x < this.x) curFrame = 3;
		else if(x > this.x) curFrame = 1;
		else if(y < this.y) curFrame = 2;
		else if(y > this.y) curFrame = 0;
		
		if(curFrame == prev) return false;
		return true;
		
	}
	
	public boolean attack(Tile t){ //true of succeeded
		
		if(t == null || t.occupier() == null) return false;
		return use(abilities[curAbility], t.occupier());
	
	}
	
	private int use(Ability a){
		
		if((int) (Math.random() * dex) == 0) { //should be a 1/dex chance of missing. TODO confirm this
			
			return 0; 
			
		}
		
		if(a.didCrit()) {
			
			int damage = a.high() + a.damageRoll(); //max possible damage plus an extra roll
			
			if(damage > 0) Main.getGame().toConsole("<<<CRIT>>>", Color.RED);
			if(damage < 0) Main.getGame().toConsole("<<<CRIT HEAL>>>", Color.GREEN);
			
			return damage;
			
		}
		
		return a.damageRoll();
		
	}
	
	private boolean use(Ability a, Permanent p) {
		
		int damage = 0;
		
		if(a.times() == 1) {
			
			damage = use(a);
			
		}
		else {
			
			int total = 0;
			int numCrits = 0;
			int numHits = 0;
			int temp;
			
			for(int i = 0; i < a.times(); i++) {
				
				temp = use(a);
				
				if(temp != 0) numHits++; //if this attack hit
				if(temp > a.high()) numCrits++; //if this attack was a crit (since a crit must, by definition, be higher than the high damage bound)
				
				total += a.damageRoll();
				
			}
			
			if(numCrits > 0) Main.getGame().toConsole("<<<CRIT x" + numCrits + ">>>", Color.RED);
			if(numHits > 0) Main.toConsole("Hit " + numHits + " times.");

			damage = total;

		}
		
		if(damage == 0){
			
			Main.toConsole(name + "'s " + a.name() + " failed.");
			return false;
			
		}
		
		p.changeHealth(-damage);
		
		Main.toConsole(name + ": " + a.name().toUpperCase() + " for " + damage + " on " + p.getName() + ".");
		return true;
		
	}
	
	public boolean setCurAbility(int to) { 
		
		if(curAbility == to) return false;
		
		curAbility = to; 
		return true;
		
	} 
	
	public String abilitiesString(){
		
		String str = "";
		
		boolean first = true;
		for(Ability a : abilities){
			
			if(!first) str += ", ";
			else first = false;
			str += a.name();
			
		}
		
		return str;
		
	}
	
	public Ability[] getAbilities() { return abilities; }
	
	public Ability getCurAbility() { return abilities[curAbility]; }
	
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
	
	public void setFacing(int direction) { //TODO create a setFacing that takes a permanent and then faces in the direction of that permanent
		
		if(direction < 0 || direction > 3) return;
		
		curFrame = direction;
		
	}
	
	public boolean face(Permanent other){
		
		return face(other.x, other.y);
		
	}
	
	public int distanceTo(Permanent other){
    	
    	return Math.abs(other.x() - x) + Math.abs(other.y() - y);
    	
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
	
	public static Permanent[] sortByY(ArrayDeque<Permanent> d) { //returns an array of all elements of d sorted by y value
		
    	Permanent[] arr = d.toArray(new Permanent[0]);
    	int minY;
    	int minYindex;
    	Permanent temp;
    	
    	for(int i = 0; i < arr.length - 1; i++) {
    		
    		minY = arr[i].y;
    		minYindex = i;
    		
    		for(int j = i + 1; j < arr.length; j++) {
    			
    			if(arr[j].y < minY) {
    				
    				minY = arr[j].y;
    				minYindex = j;
    				
    			}
    			
    		}
    		
    		if(minYindex != i) {
    			
    			temp = arr[i];
    			arr[i] = arr[minYindex];
    			arr[minYindex] = temp;
    			
    		}
    		
    	}
    	
    	return arr;
    	
    }
	
}