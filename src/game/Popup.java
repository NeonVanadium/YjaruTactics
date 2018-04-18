package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Popup { //a popup menu
	
	private int x;
	private int y;
	private static final int width = 300;
	private int height;
	private String title;
	private LinkedList<String> options;
	
	public Popup(int x, int y, String title) {
		
		this.x = x;
		this.y = y;
		this.title = title;
		options = new LinkedList<String>();
		
	}
	
	public Popup(int x, int y, String title, Ability[] abilities) {
		
		this.x = x;
		this.y = y;
		this.title = title;
		options = new LinkedList<String>();
		
		for(Ability a : abilities) {
			
			options.add(a.name());
			
		}
		
	}
	
	
	public void setPosition(int x, int y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public void draw(Graphics g) {
		
		g.setColor(Color.LIGHT_GRAY);
		height = 40 + (20 * options.size()); //not including the title/banner
		g.fillRect(x, y, width, 40 + (20 * options.size()));
		
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(30F));
		g.drawString(title, x, y + 30);
		g.setFont(g.getFont().deriveFont(20F));
		
		int i = 0;
		for(String s : options) {
			
			g.drawString(s, x, y + 55 + (20 * i));
			i++;
			
		}
		
	}
	
	public boolean inRange(int x, int y){ //is this point inside the area of the popup?
		
		if(x < this.x || x > this.x + width){
			
			System.out.println("Exited on x");
			return false;
			
		}
		if(y < this.y || y > this.y + height){
			
			System.out.println("Exited on y");
			return false;
			
		}
		
		return false;
		
	}
	
	public void add(String addition) { //adds a string to the options list
		
		options.add(addition);
		
	}

}