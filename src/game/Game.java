package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.awt.event.*;

public class Game extends JPanel {

	
	/*
	 * 
	 * TODO
	 * 
	 * organize methods in Game
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	private static final int length = 50; //length of each tile
    private ArrayDeque<Permanent> units;
    private ArrayDeque<String> console; //most recent ten messages
    private int curTurnAP;
    private Permanent cur; //unit whose turn it is
    private Tile[][] grid;
    private Hashtable<Integer, Boolean> marked;
    private final Permanent[] allFighters;
    
    public Game(Tile[][] arr, LinkedList<Permanent> team1, LinkedList<Permanent> team2) {

    	grid = Arrays.copyOf(arr, arr.length);
    	marked = new Hashtable<Integer, Boolean>();
    	console = new ArrayDeque<String>();
    	units = new ArrayDeque<Permanent>();
    	allFighters = new Permanent[team1.size() * team2.size()];
    	
    	int i = 1;
    	for(Permanent p : team1) {
    		
    		p.setPosition(1, 2 + (4 * i));
    		p.setTeam(1);
    		allFighters[i] = p;
    		units.add(p);
    		i++;
    		
    	}
    	i = 1;
    	for(Permanent p : team2) {
    		
    		p.setPosition(width() - 2, 2 + (4 * i));
    		p.setTeam(2);
    		allFighters[i + (team1.size() - 1)] = p;
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
    			
    			//System.out.println(e.getButton());
    			
    			int x = toGridScale(e.getX());
    			int y = toGridScale(e.getY());
    			
    			//mouseClickInfo(x, y);
    			
    			if(marked.get(to1D(x, y)) != null) {
    				
    				Tile tile = grid[y][x];
    				
    				if((x != cur.x() || y != cur.y()) && tile.occupier() != null && tile.occupier().getTeam() != cur.getTeam()){ //if this is not cur's current location, this tile has an occupier, and that occupier is not the same team as the current unit
    					
    					//toConsole(cur.getName() + " attacks " + grid[y][x].occupier().getName() + " (1 Damage)");
    					//changeHP(grid[y][x].occupier(), -1);
    					String attack = cur.attack(tile.occupier());
    					
    					if(!attack.isEmpty()) {
    						
    						toConsole(attack);
    						if(!tile.occupier().isAlive()) {
    							
    							toConsole(tile.occupier().getName() + " DIES");
    							kill(tile.occupier());

    						}
    						info(getGraphics());
        					drawGrid(getGraphics());
    						
    					}
    					
    				}
    				
    				curTurnAP -= getDistance(cur, x, y); //TODO fix this positioning and find out why it doesnt work anywhere else
    				
    				if(grid[y][x].put(cur)){
    					grid[cur.prevY()][cur.prevX()].remove();
    					//curTurnAP -= getDistance(cur, x, y);
    					marked.clear(); 
    					drawGrid(getGraphics());
    				}
    				    				
    				turnCheck();

    			}
    			else{
    				
    				drawTile(round(e.getX()), round(e.getY()), getGraphics(), Color.RED);
    				
    			}
    			
    		}
    		
    	});  	
    	
    }
    
    public void toConsole(String s) { //adds nonempty string s to virtual console for display
    	
    	//if(s.isEmpty()) return;
    	
    	console.addFirst(s);
    	
    	if(console.size() > 10) console.removeLast();
    	
    }
    
    public boolean kill(Permanent p) {
    	
    	if(units.contains(p)) {
    		
    		units.remove(p);
    		
    		grid[p.y()][p.x()].remove();
    		
    		return true;
    		
    	}
    	
    	return false;

    	
    }
    
    public int to1D(int x, int y) {
    	
    	return x + (y * length);
    	
    }
    
    public Dimension getPrefferedSize() {
		
		return new Dimension(500, 500);
			
	}
    
    public Tile getTile(int row, int col) {
    	
    	return grid[row][col];
    	
    }
		
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawGrid(g);
			
	}
	
	private void mouseClickInfo(int x, int y) {
		
		System.out.println("MOUSE CLICK: row " +  y + " col " + x);
		System.out.println(grid[y][x]);
		
	}
    
    private int toGridScale(int i) { //converts integer i, likely from a mouse event, to the corresponding number within the range of the grid
    	
    	return (round(i)) / length;
    	
    }
    
    private int round(int i) {
    	
    	return i - (i % length);
    	
    }
    
    private void turnCheck() {
    	
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
    	
    	if(x < 0 || x >= width() || y < 0 || y >= height()) return;
    	
    	if(!grid[y][x].passable() && !(grid[y][x].occupier() != null && !grid[y][x].occupier().isImmortal())) return;
    		
    	if(distance < 0) return;
    	
    	marked.put(to1D(x, y), true);
    	g.setColor(Color.GREEN);
    	g.drawRect(x * length, y * length, length, length);
    	
    	rangeHelper(x - 1, y, distance - 1, g);
    	rangeHelper(x + 1, y, distance - 1, g);
    	rangeHelper(x, y - 1, distance - 1, g);
    	rangeHelper(x, y + 1, distance - 1, g);
    	
    }
    
    private void drawTile(int x, int y, Graphics g, Color c) {
		
		g.setColor(c);
		drawTile(x, y, g);
	
    }
    
    private void drawTile(int x, int y, Graphics g){

    	g.fillRect(x * length, y * length, length, length);
    	g.setColor(Color.BLACK);
    	g.drawRect(x * length, y * length, length, length);
    	
    	
    }
    
    private int getDistance(Permanent p, int x, int y){
    	
    	return Math.abs(p.x() - x) + Math.abs(p.y() - y);
    	
    }
    
    private void drawGrid(Graphics g) {
    	
    	//turnCheck();
    	
    	//g.drawImage(img, x, y, width, height, observer)
    	
    	for(int i = 0; i < grid.length; i++) {
			
			for(int j = 0; j < grid[0].length; j++) {
				
				if(grid[i][j].passable()) {
					drawTile(j, i, g, Color.BLUE);
				}
				else {
					drawTile(j, i, g, Color.ORANGE);
				}
				
			}
			
		}
    	
    	
    	info(g);
    	drawPermanents(g);
    	range(cur.x(), cur.y(), curTurnAP, g);
    	
    }
    
    private void info(Graphics g) {
    	
    	int x = 1010;
    	int y = 150;
    	int w = 240;
    	int h = 20;
  
    	
    	g.clearRect(x - 1, y - 20, w, h);
    	g.drawString("It is " + cur.getName() + "'s turn, " + curTurnAP + " AP left", x, y - 5);
    	
    	y+=10;
    	
    	g.clearRect(x - 1, y, w, h + (10 * (allFighters.length + 1)));
    	g.setColor(Color.BLACK);
    	
    	y+=10;
    	
    	for(Permanent p : allFighters){
    		
    		if(p.isAlive()) g.drawString(p.getName() + " has " + p.HP() + " health points.", x, y);
    		else g.drawString(p.getName() + " (DEAD)", x, y);
    		y+=15;
    	}
    	
    	y+=15;
    	g.clearRect(x - 1, y, w, h + (15 * (console.size() - 1)));
    	y+=15;
    	
    	for(String s : console) {
    		g.drawString(s, x, y);
    		y+=15;
    	}
    }
    
    private void drawPermanents(Graphics g) {
    	
    	for(Permanent p : units) {
    		
    		g.clearRect(p.x() * length, p.y() * length, length, length);
    		drawTile(p.x(), p.y(), g, p.c);
    		
    	}

    }
    
    
    private int width() {
    	
    	return grid[0].length;
    	
    }
    
    private int height() {
    	
    	return grid.length;
    	
    }
			
}

