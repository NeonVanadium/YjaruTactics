package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.Arrays;

import javax.swing.JPanel;

public class CharacterSelect extends JPanel {

	private static final long serialVersionUID = -3729437235743046623L;
	private Permanent[] fighters;
	private String[] fullnames; //the full names of each fighter
	private boolean[] taken;

	
	private static final int scale = 7;
	private static final int h = 35 * scale; //height scale of each fighter
	private static final int w = 13 * scale; //width scale of each fighter
	private static final int spacing = 80; //blank space (x) between the fighters when drawn.
	private static final int Yspacing = 35;
	private final int startX; //x value at which the first fighter in fighters[] is drawn
	private final int startY; //y value at which the first fighter in fighters[] is drawn
	private int dir = 0; //current direction the fighters are facing
	private int prevDir = -1; //previous direction the fighters were facing
	private int prevSelection = 0; //previously selected (moused-over) fighter
	private int selection = 0; //currently selected (moused-over) fighter
	private int t1pp = 3; //team one pickpoints
	private int t2pp = 3; //team two pickpoints;
	private Graphics g; //the graphics context of the menu
	
	//TODO clean up this whole class

	public CharacterSelect() {
		
		fullnames = Main.getFighters().keySet().toArray(new String[0]);

		Arrays.sort(fullnames);
		
		fighters = new Permanent[fullnames.length];
		
		for(int i = 0; i < fighters.length; i++) {
			
			fighters[i] = Main.getFighters().get(fullnames[i]);
			
		} 
		
		//fighters = Main.getFighters().values().toArray(new Permanent[0]);
		//Arrays.sort(fighters);
		taken = new boolean[fighters.length];
		
		startX = Main.getFramewidth() / 2 - ((8 * (w + spacing)) / 2) + 10;
		startY = 100;//(Main.getFramewidth() - ((fighters.length / 9) * (h + Yspacing))) / 9; //make work for /8
		
		addMouseMotionListener( new MouseMotionAdapter(){
			
			public void mouseMoved(MouseEvent e) {
				
				selection = toGridScale(e);
	
			}
			
		});
		
		addMouseListener( new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				
				if(!taken[selection]) {
				
					if(e.getButton() == 1) {
					
						if(t1pp > 0) {
							t1pp--;
							taken[selection] = true;
							Main.addToTeam1(fighters[selection]);
							drawMembers(1);
						}
					
					}
					if(e.getButton() == 3) {
					
						if(t2pp > 0) {
							t2pp--;
							Main.addToTeam2(fighters[selection]);
							taken[selection] = true;
							drawMembers(2);
						}
					}
					
					drawName(g, selection);
					
				}
			}
			
		});
		
	}
	
	public void incrementDir() {
		
		if(dir < 3) dir += 1;
		else dir -= 3;
		
	}
	
	public void changeDir(int change) { dir += change; }
	
	public int getDir() { return dir; }
	
	public void checkDone() {
		
		if(t1pp == 0 && t2pp == 0) {
			
			Main.getTimer().cancel();
			Main.getTimer().purge();
			Main.removeFromFrame(CharacterSelect.this);
			Main.endSelection();
			
		}
		
	}
	
	public void paintComponent(Graphics g) { //dunno but im supposed to have this
		super.paintComponent(g);
		showFighters();	
		drawMembers(0);
	}
	
	public void showFighters() {
		
		g = getGraphics();
		
		if(g != null) {
		
			g.setColor(Color.WHITE);
			g.setFont(g.getFont().deriveFont(20F));
			g.drawString("CHARACTER SELECT", 20, 20);
			
			g.drawString("Team one: " + t1pp + " pick points to spend.", startX, startY / 3);
			g.drawString("Team two: " + t2pp + " pick points to spend.", Main.getFramewidth()-(2 * startX), startY / 3);
			
			g.setColor(Color.DARK_GRAY);
			
			if(dir != prevDir) {
				
				prevDir = dir;
				
				g.fillRect(startX, startY - 10, Main.getFramewidth(), 815); //TODO actually calculate the height of the rect here and replace the dummy value
				int i = 0;
				
				for(Permanent p : fighters) {
					
					//TODO make the scale here a variable on the class so future changes can be made with ease
					p.compensatedDraw(dir, scale, (startX + ((w + spacing) * (i % 8))), startY + ((h + Yspacing) * (i / 8)), g, this);
									
					if(selection < fighters.length && selection > -1) {
						g.setColor(Color.RED);
						g.drawRect(startX + ((w + spacing) * (selection % 8)), -5 + startY + ((h + Yspacing) * (selection / 8)), w, h);
						prevSelection = selection;	
					}
					
					drawName(g, i);
				
					i++;
					
				}
			}
			if(isValidSelection()) {
				
				g.fillRect(startX, Main.getFrameheight() - startY - 20, 400, 30); //the "selected" text
				g.fillRect((startX + ((w + spacing) * (prevSelection % 8))), -10 + startY + ((h + Yspacing) * (prevSelection / 8)), w + 1, h + 15);
				
				fighters[prevSelection].compensatedDraw(dir, scale, (startX + ((w + spacing) * (prevSelection % 8))), startY + ((h + Yspacing) * (prevSelection / 8)), g, this);
				g.fillRect((startX + (w + spacing) * selection), startY, w, h);
				fighters[selection].compensatedDraw(dir, scale, (startX + ((w + spacing) * (selection % 8))), startY + ((h + Yspacing) * (selection / 8)), g, this);

				g.setColor(Color.RED);
				g.drawRect(startX + ((w + spacing) * (selection % 8)), -5 + startY + ((h + Yspacing) * (selection / 8)), w, h);
				
				g.setColor(Color.WHITE);
				g.drawString("Selected: " + fighters[selection].getName(), startX, Main.getFrameheight() - startY);//(startY + (2 * (h + Yspacing + Yspacing))));
				prevSelection = selection;
				
			}
			
		}
	}
	
	private boolean isValidSelection() {
		
		return selection != prevSelection && selection < fighters.length && selection > -1;
		
	}
	
	private void drawName(Graphics g, int fighter) {
		
		if(taken[fighter]) g.setColor(Color.RED);
		else g.setColor(Color.WHITE);
		
		g.setFont(g.getFont().deriveFont(14F));

		g.drawString(fullnames[fighter], (startX + ((w + spacing) * (fighter % 8))), startY + ((h + Yspacing) * (fighter / 8)) + h + 20);

	}
	
	private void drawMembers(int team) { //prints the names of the fighters on each team under their respective team header
		
		g.setColor(Color.DARK_GRAY);
		g.setFont(g.getFont().deriveFont(20F));
		
		if(team == 1) {
			
			g.fillRect(startX, (startY / 3) - 20, 400, 20 + (30 * Main.getTeam(2).size()));
			g.setColor(Color.WHITE);
			
			int i = 0;
			for(Permanent p : Main.getTeam(1)) {
				
				g.drawString(p.getName(), startX, (startY / 3) + (20 * (i + 1)));
				i++;
				
			}
			
		}
		if(team == 2) {
			
			g.fillRect(Main.getFramewidth()-(2 * startX), (startY / 3) - 20, 400, 20 + (30 * Main.getTeam(2).size()));
			g.setColor(Color.WHITE);
			
			int i = 0;
			for(Permanent p : Main.getTeam(2)) {
				
				g.drawString(p.getName(), Main.getFramewidth() - (2 * startX), (startY / 3) + (20 * (i + 1)));
				i++;
				
			}
			
		}
		
		g.drawString("Team one: " + t1pp + " pick points to spend.", startX, startY / 3);
		g.drawString("Team two: " + t2pp + " pick points to spend.", Main.getFramewidth()-(2 * startX), startY / 3);
		
	}
	
	private int toGridScale(MouseEvent e) {
		
		int x = e.getX();
		int y = e.getY();
		
		x -= startX;		
		x = (x - (x % (w + spacing))) / (w + spacing);
		if(x >= 8) x = 7;
		if(x < 0) x = 0;
		
		y -= startY;
		y = (y - (y % (h + Yspacing))) / (h + Yspacing);
		
		x += (8 * y);
		
		if(x >= fighters.length) x = fighters.length - 1;
		if(x < 0) x = 0;
		
		return x;
		
	}
	
	/*private Point toGridScale(int x, int y) {
		
		x -= startX;
		y = startY;
		
		x = (x - (x % (w + spacing))) / (w + spacing);
		
		
		return new Point(x, y);
		
	}*/
	
}
