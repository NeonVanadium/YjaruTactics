package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Game extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private static final int length = 60; //length of each tile
    private ArrayDeque<Permanent> units; //all the units, queued for their turn
    private ArrayDeque<Message> console; //most recent ten messages
    private int curTurnAP; //remaining action points for this turn
    private Permanent cur; //unit whose turn it is
    private Tile[][] grid; //the game board
    private Hashtable<Integer, Boolean> marked; //dfs marked array, but a hashtable so I can clear it in one line
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
    
    public Game(Tile[][] arr, LinkedList<Permanent> team1, LinkedList<Permanent> team2) {

    	//TODO either randomize units or make one unit per team move per turn
    	
    	grid = Arrays.copyOf(arr, arr.length);
    	marked = new Hashtable<Integer, Boolean>();
    	console = new ArrayDeque<Message>();
    	units = new ArrayDeque<Permanent>();
    	this.team1 = new Permanent[team1.size()];
    	this.team2 = new Permanent[team2.size()];
    	living1 = this.team1.length;
    	living2 = this.team2.length;
    	
    	this.setBackground(Color.BLACK);
    	
    	int i = 0;
    	for(Permanent p : team1) {
    		
    		p.setPosition(1, 1 + (height() / living1) * (i));
    		p.setFacing(1);
    		p.setTeam(1);
    		this.team1[i] = p;
    		units.add(p);
    		i++;
    		
    	}
    	i = 0;
    	for(Permanent p : team2) {
    		
    		p.setPosition(width() - 2, 1 + (height() / living2) * (i));
    		p.setFacing(3);
    		p.setTeam(2);
    		this.team2[i] = p;
    		units.add(p);
    		i++;
    		
    	}
    	
    	for(Permanent p : units){
    		
    		grid[p.y()][p.x()].put(p);
    		
    	}
    	
    	initListeners();
    	
    	cur = units.getFirst();
		curTurnAP = cur.getAP();
		toConsole(cur.getName() + " begins their turn.");
    		
    }
    
    private void initListeners() {
    	
    	addMouseListener(new MouseAdapter() {
    		
    		public void mousePressed(MouseEvent e) {
    			
    			int x = toGridScale(e.getX());
    			int y = toGridScale(e.getY());
    			Tile tile = grid[y][x];
    			
    			mouseClickInfo(x, y);
    			
				if(tile.occupier() != null && tile.occupier().getTeam() != cur.getTeam() && tile.occupier().isAdjacentTo(cur)){ //if this is not cur's current location, this tile has an occupier, and that occupier is not the same team as the current unit
				
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
				else if(marked.get(to1D(x, y)) != null) {
    				
					curTurnAP -= getDistance(cur, x, y);//TODO fix this positioning and find out why it doesnt work anywhere else
    				
    				if(grid[y][x].put(cur)){
    					getGraphics().clearRect(cur.prevX() * length, (cur.prevY() - 1) * length, length, length * 2);
    					grid[cur.prevY()][cur.prevX()].remove();
    					
    					marked.clear(); 
    					drawGrid(getGraphics());
    				}
    				    				
    				checkTurn();

    			}
    			
    		}
    		
    	});  	
    	
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
    		
    		grid[p.y()][p.x()].remove();
    		
    		if(p.getTeam() == 1) living1 -= 1;
    		else living2 -= 1;
    		
    		checkWin();
    		return true;
    		
    	}
    	
    	checkWin();
    	
    	return false;

    	
    }
    
    public int to1D(int x, int y) { //convert row y and column x to a 1-dimensional index
    	
    	return x + (y * length);
    	
    }
    
    public Tile getTile(int row, int col) { //get grid tile at row and col
    	
    	return grid[row][col];
    	
    }
	
    public void draw(){ //currenly unused
    	
    	drawGrid(getGraphics());
    	
    }
    
	public void paintComponent(Graphics g) { //dunno but im supposed to have this
		super.paintComponent(g);
		drawGrid(g);		
	}
	
	private void checkWin(){ //has someone won?
		
		if(living1 <= 0){
			setEnabled(false);
			setVisible(false);
			System.out.println("TEAM 2 WINS");
		}
		if(living2 <= 0){
			setEnabled(false);
			setVisible(false);
			
			System.out.println("TEAM 1 WINS");
		}
		
	}
	
	private void mouseClickInfo(int x, int y) {//displays information from the tile at row y and col x
		
		System.out.println("MOUSE CLICK: row " +  y + " col " + x);
		System.out.println(grid[y][x]);
		
	}
    
    private int toGridScale(int i) { //converts integer i, likely from a mouse event, to the corresponding number within the range of the grid
    	
    	return (round(i)) / length;
    	
    }
    
    private int round(int i) { //rounds i down to nearest multiple of length
    	
    	return i - (i % length);
    	
    }
    
    private void checkTurn() { //checks if cur unit's turn has ended, if so, turn passes to next unit
    	
    	if(curTurnAP <= 0) {
			
			units.addLast(units.removeFirst());
			cur = units.getFirst();
			curTurnAP = cur.getAP();
			toConsole(cur.getName() + " begins their turn.");
			drawGrid(getGraphics());
			
		}
		
    }
    
    private void range(int x, int y, int distance, Graphics g){
    	
    	marked.clear();
    	rangeHelper(x - 1, y, distance - 1, g);
    	rangeHelper(x + 1, y, distance - 1, g);
    	rangeHelper(x, y - 1, distance - 1, g);
    	rangeHelper(x, y + 1, distance - 1, g);
    	
    }
    
    private void rangeHelper(int x, int y, int distance, Graphics g) {
    	
    	if(distance < 0) return;
    	
    	if(x < 0 || x >= width() || y < 0 || y >= height()) return;
    	
    	if(marked.contains(to1D(x, y)));
    	
    	if(!grid[y][x].passable() || grid[y][x].occupier() != null) return;
    	
    	marked.put(to1D(x, y), true);
    	
    	g.setColor(Color.CYAN);
    	
    	g.drawRect(x * length, y * length, length, length);
    	
    	rangeHelper(x - 1, y, distance - 1, g);
    	rangeHelper(x + 1, y, distance - 1, g);
    	rangeHelper(x, y - 1, distance - 1, g);
    	rangeHelper(x, y + 1, distance - 1, g);
    	
    }
    
    /*private void drawTile(int x, int y, Graphics g){

    	g.fillRect(x * length, y * length, length, length);
    	g.setColor(Color.BLACK);
    	g.drawRect(x * length, y * length, length, length);
    	
    	
    }*/
    
    private int getDistance(Permanent p, int x, int y){
    	
    	return Math.abs(p.x() - x) + Math.abs(p.y() - y);
    	
    }
    
    private void drawGrid(Graphics g) {
    	
    	//turnCheck();
    	
    	for(int i = 0; i < grid.length; i++) {
			
			for(int j = 0; j < grid[0].length; j++) {
				
				g.drawImage(grid[i][j].getSprite(), j * length, i * length, length, length, this);
				
			}
			
		}
    	
    	
    	//g.drawImage(test, 0, 0, 100, 100, this);
    	info(g);
    	range(cur.x(), cur.y(), curTurnAP, g);
    	drawPermanents(g);
    	
    	
    	
    }
    
    private void info(Graphics g) {//TODO
    	
    	int x = width() * length + 10;
    	int y = 40;
    	//int w = 240; 
    	
    	g.setFont(new Font("Fonts/arial.tff", 10, 20));
    	
    	g.setColor(Color.BLACK);
    	
    	g.fillRect(x - 1, 10, 400, 980);
    	
    	g.setColor(Color.WHITE);
    	
    	g.drawString("It is " + cur.getName() + "'s turn, " + curTurnAP + " Action Points left", x, y - 5);
    	
    	y = 100;
    	int temp = 100;
    	
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
    		int w = sprite.getWidth() * (length / 20);
    		int h = sprite.getHeight() * (length / 20);
    		
        		
        	if(cur.x() == p.x() && cur.y() == p.y()) g.setColor(Color.WHITE);
        	else if(p.getTeam() != cur.getTeam()) {
        			
        		if(cur.isAdjacentTo(p)) g.setColor(Color.RED);
        		else g.setColor(new Color(255, 100, 100));
        			
        			
        	}
        	else g.setColor(Color.GREEN);		
    		
    		g.drawRect((p.x() * length), p.y() * length, length, length);
    		g.drawImage(sprite, (p.x() * length) + length / (length / sprite.getWidth()), p.y() * length - 51, w, h, this);
    		
    	}

    }
    
    
    private int width() {
    	
    	return grid[0].length;
    	
    }
    
    private int height() {
    	
    	return grid.length;
    	
    }
			
}

