package game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Popup { //a popup menu
	
	private int x;
	private int y;
	private String title;
	private LinkedList<String> options;
	
	public Popup(int x, int y, String title) {
		
		this.x = x;
		this.y = y;
		this.title = title;
		options = new LinkedList<String>();
		
	}
	
	public Popup(int x, int y, String title, Attack[] attacks) {
		
		this.x = x;
		this.y = y;
		this.title = title;
		options = new LinkedList<String>();
		
		for(Attack a : attacks) {
			
			options.add(a.name());
			
		}
		
	}
	
	
	public void setPosition(int x, int y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public void draw(Graphics g) {
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x, y, 300, 40 + (20 * options.size()));
		
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
	
	public void add(String addition) { //adds a string to the options list
		
		options.add(addition);
		
	}

}