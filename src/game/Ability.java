package game;

import java.awt.Rectangle;

public class Ability{//TODO change to ability
	
	private String name;
	private int cost; //cost (in AP) of the attack
	private int high; //maximum raw damage dealt to all non-immortal permanents in area of effect
	private int low; //minimum raw damage dealt to all non-immortal permanents in area of effect
	private Rectangle area;
	private int crit; // 1/crit is the chance to deal double damage (higher chance with lower value). If negative, crit impossible
	private int times; //how many times this attack hits
	private int range; //the effective range of the attack
	private int type; //0 = other, 1 = team, 2 = self;
	
	//TODO rework type into multiple variables that add more robustness
	
	public Ability(String name, int high, int low, int cost, int critDenominator, int times, int range, int type){ //single-target  attack
		
		this.name = name;
		this.high = high;
		this.low = low;
		this.cost = cost;
		this.times = times;
		this.crit = critDenominator;
		this.range = range;
		this.type = type;
		
	}
	
	public Ability(String name, int high, int low, int cost, int critDenominator, int times, int range, int type, int rectX, int rectY, int width, int height){ //non-single-target
		
		this.name = name;
		this.high = high;
		this.low = low;
		this.cost = cost;
		this.times = times;
		this.crit = critDenominator;
		this.type = type;
		this.range = range;
		this.area = new Rectangle(rectX, rectY, width, height);
		
	}
	
	public String name() { return name; }
	
	public int type() { return type; }
	
	public int times() { return times; }
	
	public boolean didCrit() { //rolls to see if crit
		
		if(crit < 0) return false;
		
		return (int) (Math.random() * crit) == 0;

	}
	
	public int damageRoll() { //performs a damage roll (generates a number inclusive-between high and low) and returns it
		
		return (int) (Math.random() * (high - low)) + low;
		
	}
	
	public int range() { return range; }
	
	public int cost() { return cost; }
	
	public boolean isAOE() { return area != null; }
	
	public Rectangle area(Permanent caster) { //the area of effect of the attack
		
		if(caster.getFacing() == 0 || caster.getFacing() == 2) return area;
		return new Rectangle(area.y, area.x, area.height, area.width); //rotate

		
	}
	
}
