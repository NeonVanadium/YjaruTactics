package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Game extends JPanel {

	private static final long serialVersionUID = -4063135471056442683L; //honestly no idea what this is but it gives me a squiggly boi if i don't have it
	private Board board; //the game board
	private Popup popup = null; //the current popup;
    private ArrayDeque<Permanent> units; //all the units, queued for their turn
    private ArrayDeque<Message> console; //most recent ten messages
    private int curTurnAP; //remaining action points for this turn
    private Permanent cur; //unit whose turn it is
    private final Permanent[] team1; //all units of team 1
    private final Permanent[] team2; //all units of team 2
    private int living1; //number of living units on team one
    private int living2; //number of living units on team two
    
    private class Message{
    	
    	private String text;
    	private Color color;
    	
    	public Message(String t) {
    		
    		text = t;
    		color = Color.WHITE;
    		
    	}
    	public Message(String t, Color c) {
    		
    		text = t;
    		color = c;
    		
    	}
    	
    }
    
    public Game(Board board, LinkedList<Permanent> team1, LinkedList<Permanent> team2, Dimension framesize) {
    	
    	this.board = board;//new Board(arr, 60);
    	console = new ArrayDeque<Message>();
    	units = new ArrayDeque<Permanent>();
    	this.team1 = new Permanent[team1.size()];
    	this.team2 = new Permanent[team2.size()];
    	living1 = this.team1.length;
    	living2 = this.team2.length;
    	
    	this.setBackground(Color.BLACK);
    	
    	List<Permanent> temp = new ArrayList<Permanent>();
    	
    	int i = 0;
    	for(Permanent p : team1) {
    		
    		p.setPosition(1, 1 + (board.height() / living1) * (i));
    		p.setFacing(1);
    		p.setTeam(1);
    		this.team1[i] = p;
    		temp.add(p);
    		i++;
    		
    	}
    	i = 0;
    	for(Permanent p : team2) {
    		
    		p.setPosition(board.width() - 2, 1 + (board.height() / living2) * (i));
    		p.setFacing(3);
    		p.setTeam(2);
    		this.team2[i] = p;
    		temp.add(p);
    		i++;
    		
    	}
    	
    	Collections.shuffle(temp); 	
    	
    	for(Permanent p : temp){
    		
    		units.add(p);
    		board.getTile(p.y(), p.x()).put(p);
    		
    	}
    	
    	addMouseListener(new MouseAdapter() {
    		
    		public void mousePressed(MouseEvent e) {
    			
    			if(e.getButton() == 3 && popup == null) {	
    					
    				popup = new Popup(e.getX(), e.getY(), cur.getName() + "'s moves:", cur.getAbilities());
    						
    						
    			}
    			else if(popup != null && popup.inRange(e.getX(), e.getY())){
    				
    				int selection = popup.getOption(e.getY());

    				if(selection >= 0 && cur.setCurAbility(popup.getOption(e.getY()))) {
    					
    					toConsole(cur.getName() + " readies " + cur.getCurAbility().name() + ".");
    					popup = null;
    					
    				}
    				
    			}
    			else {
    				
    				popup = null;
	    			int x = board.toGridScale(e.getX() - board.Xoffset());
	    			int y = board.toGridScale(e.getY() - board.Yoffset());
	    			Tile tile = board.getTile(y, x); //TODO fix whatever error this is throwing
	    			
	    			//mouseClickInfo(x, y);
	    			
	    			
					if(tile.occupier() != null && cur.canTarget(tile.occupier())){//&& tile.occupier().getTeam() != cur.getTeam() && cur.distanceTo(tile.occupier()) <= cur.getAttacks()[0].range()){ //if this is not cur's current location, this tile has an occupier, and that occupier is not the same team as the current unit
					
						String attack = cur.attack(tile.occupier());
						
						if(!attack.isEmpty()) {
							
							curTurnAP = 0;
							toConsole(attack);
							if(!tile.occupier().isAlive()) {
								
								toConsole(tile.occupier().getName() + " DIES", Color.RED);
								kill(tile.occupier());
	
							}
							
							checkTurn();
							info(getGraphics());
	    					drawGrid(getGraphics());
							
						}
						
					}
					else if(board.isMarked(y, x)) {
	    				
						curTurnAP -= getDistance(cur, x, y);
						//int distance = getDistance(cur, x, y);
						
	    				if(board.getTile(y, x).put(cur)){
	    					
	    					
	    					if(cur.prevY() == 0) {
	    						
	    						getGraphics().clearRect(cur.prevX() * board.length(), (cur.prevY() + 1) * board.length(), board.length(), board.length());
	    						
	    					}
	    					else {
	    						
	    						board.drawTile(getGraphics(), cur.prevY() + 1, cur.prevX(), Game.this);
	    						
	    					}
	    					board.drawTile(getGraphics(), cur.prevY(), cur.prevX(), Game.this);
	       					board.getTile(cur.prevY(), cur.prevX()).remove();
	    					
	    					board.clearMarked();
	    					drawGrid(getGraphics());
	    					
	    					//curTurnAP -= distance;
	    					
	    				}
	    				    				
	    				checkTurn();
	
	    			}
	    			
	    		}
    			
    		}//end of else (not right click)
    		
    	});  	
    	
    	cur = units.getFirst();
		curTurnAP = cur.getAP();
		toConsole(cur.getName() + " begins their turn.");
    		
    }
    
    
    public void toConsole(String s) {
    	
    	if(s.isEmpty()) return;
    	
    	toConsole(new Message(s));
    	
    }
    
    public void toConsole(String s, Color c) {
    	
    	if(s.isEmpty()) return;
    	
    	toConsole(new Message(s, c));
    	
    }
    
    public void toConsole(Message m) { //adds nonempty string s to virtual console for display
    	
    	console.addFirst(m);
    	
    	if(console.size() > 30) console.removeLast();
    	
    }
    
    public boolean kill(Permanent p) { //remove unit p from game
    	
    	if(units.contains(p)) {
    		
    		units.remove(p);
    		
    		board.getTile(p.y(), p.x()).remove();
    		
    		if(p.getTeam() == 1) living1 -= 1;
    		else living2 -= 1;
    		
    		checkWin();
    		return true;
    		
    	}
    	
    	checkWin();
    	
    	return false;

    	
    }
    
    public void draw(){ //currenly unused
    	
    	Graphics g = getGraphics();
    	drawGrid(g);
    	
    	
    }
    
	public void paintComponent(Graphics g) { //dunno but im supposed to have this
		super.paintComponent(g);
		drawGrid(g);
		if(popup != null) {
    	
    		popup.draw(g);
    		
    	}
	}
	
	private void checkWin(){ //has someone won?
		
		if(living1 <= 0){

			System.out.println("TEAM 2 WINS");
			Main.endGame(2);
			
			
		}
		if(living2 <= 0){

			System.out.println("TEAM 1 WINS");
			Main.endGame(1);
			
		}
		
	}
	
	public int getRemainingAP() { return curTurnAP; }
	
	private void mouseClickInfo(int x, int y) {//displays information from the tile at row y and col x
		
		System.out.println("MOUSE CLICK: row " +  y + " col " + x);
		System.out.println(board.getTile(y, x));
		
	}
    
    private void checkTurn() { //checks if cur unit's turn has ended, if so, turn passes to next unit
    	
    	if(curTurnAP <= 0) {
			
			units.addLast(units.removeFirst());
			cur = units.getFirst();
			curTurnAP = cur.getAP();
			toConsole("------------------------------------------------");
			toConsole(cur.getName() + " begins their turn.");
			drawGrid(getGraphics());
			
		}
		
    }
    
    private int getDistance(Permanent p, int x, int y){
    	
    	return Math.abs(p.x() - x) + Math.abs(p.y() - y);
    	
    }
    
    private void drawGrid(Graphics g) {
    	checkTurn();
    	board.draw(g, this);
    	info(g);
    	board.range(cur.x(), cur.y(), curTurnAP, g);
    	drawPermanents(g);
	
    }
    
    private void info(Graphics g) {//TODO
    	
    	int x = board.Xoffset() - 500;//+ board.width() * board.length() + 10;
    	int y = board.Yoffset() + 20;
    	
    	g.setFont(new Font("Fonts/arial.tff", 10, 20));
    	
    	g.setColor(Color.BLACK);
    	
    	g.fillRect(x - 1, y - 21, 400, 980);
    	
    	g.setColor(Color.WHITE);
    	
    	g.drawString("It is " + cur.getName() + "'s turn, " + curTurnAP + " Action Points left", x, y);
    	
    	y += 75;
    	int temp = y;
    	
    	for(Permanent p : team1){
    		if(p != null){
    			if(p.isAlive()) g.drawString(p.getName() + ": " + p.HP() + " HP", x, y + 1);
    			else g.drawString(p.getName() + " (DEAD)", x, y);
    			y+=25;
    		}
    		
    	}
    	
    	int maxy = y;
    	y = temp;
    	
    	
    	for(Permanent p : team2){
    		if(p != null){
    			if(p.isAlive()) g.drawString(p.getName() + ": " + p.HP() + " HP", x + 200, y + 1);
    			else g.drawString(p.getName() + " (DEAD)", x + 200, y);
    			y+=25;
    		}
    	}
    	
    	if(y < maxy) y = maxy;
    	
    	for(Message m : console) {
    		y += 25;
    		g.setColor(m.color);
    		g.drawString(m.text, x, y);
    		
    	}
    	
    }
    
    private void drawPermanents(Graphics g) {
    	
    	BufferedImage sprite;
    	
    	for(Permanent p : units) {
    		
    		sprite = p.getSprite();
    		int w = sprite.getWidth() * (board.length() / 20);
    		int h = sprite.getHeight() * (board.length() / 20);
    		
        	if(cur.x() == p.x() && cur.y() == p.y()) g.setColor(Color.WHITE);
        	else if(p.getTeam() != cur.getTeam()) {
        			
        		if(/*cur.isAdjacentTo(p)*/cur.distanceTo(p) <= cur.getCurAbility().range()) g.setColor(Color.RED);
        		else g.setColor(new Color(255, 100, 100));
        			
        			
        	}
        	else g.setColor(Color.GREEN);		
    		
    		g.drawRect((p.x() * board.length()) + board.Xoffset(), (p.y() * board.length()) + board.Yoffset(), board.length(), board.length());
    		p.compensatedDraw(3, (p.x() * board.length()) + board.length() / (board.length() / sprite.getWidth()) + board.Xoffset(), (p.y() * board.length()), g, this);
    		//g.drawImage(sprite, (p.x() * board.length()) + board.length() / (board.length() / sprite.getWidth()) + board.Xoffset(), (p.y() * board.length() - 51) + board.Yoffset(), w, h, this);
    		
    	}

    }
    
    private void endScreen(int winner) {
    	
    	Graphics g = getGraphics();
    	
    	g.setColor(Color.BLACK);
    	
    	g.fillRect(0, 0, Main.getFramewidth(), Main.getFrameheight());
    	
    	g.drawString("Team " + winner + " wins.", Main.getFramewidth() / 2, Main.getFrameheight() / 2);
    	
    }
			
}

