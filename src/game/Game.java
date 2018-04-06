package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Hashtable;
import java.awt.event.*;

public class Game extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int length = 50; //length of each tile
    private ArrayDeque<Permanent> units;
    private ArrayDeque<String> console;
    private int curTurnAP;
    private Permanent cur;
    private Tile[][] grid;
    private Hashtable<Integer, Boolean> marked;
    
    public Game(Tile[][] arr) {

    	grid = Arrays.copyOf(arr, arr.length);
    	marked = new Hashtable<Integer, Boolean>();
    	console = new ArrayDeque<String>();
    	
    	units = new ArrayDeque<Permanent>();
    	
    	units.add(new Permanent("PINK", 2, 9, Color.PINK));
    	units.add(new Permanent("GREEN", 18, 9, Color.GREEN));
    	units.add(new Permanent("RED", 8, 9, Color.RED));
    	units.add(new Permanent("WHITE", 12, 9, Color.WHITE));
    	
    	for(Permanent p : units){
    		
    		grid[p.y()][p.x()].put(p);
    		
    	}
    	
    	curTurnAP = units.getFirst().getAP();
    	cur = units.getFirst();
    	
    	initListeners();
    		
    }
    
    private void initListeners() {
    	
    	addMouseListener(new MouseAdapter() {
    		
    		public void mousePressed(MouseEvent e) {
    			
    			//System.out.println(e.getButton());
    			
    			int x = toGridScale(e.getX());
    			int y = toGridScale(e.getY());
    			
    			mouseClickInfo(x, y);
    			
    			if(marked.get(to1D(x, y)) != null) {
    				
    				if((x != cur.x() || y != cur.y()) &&  grid[y][x].occupier() != null){
    					
    					toConsole(cur.getName() + " attacks " + grid[y][x].occupier().getName() + " (1 Damage)");
    					changeHP(grid[y][x].occupier(), -1);
    					info(getGraphics());
    					drawGrid(getGraphics());
    					
    				}
    				
    				curTurnAP -= getDistance(cur, x, y);
    				
    				if(grid[y][x].put(cur)){
    					grid[cur.prevY()][cur.prevX()].remove();
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
    
    private void changeHP(Permanent p, int change) {
    	
    	if(p.changeHealth(change)) {
    		
    		if(p.HP() <= 0) {
    			toConsole(p.getName() + " DIES");
    			kill(p);
    			curTurnAP = -1;
    		}
    			
    	}
    	
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
    	
    	//return Util.round(i, length);
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
    	
    	int x = 1050;
    	
    	g.clearRect(x, 165, 165, 60 + (20 * (units.size() + console.size())));
    	g.setColor(Color.BLACK);
    	g.drawString("super very high-tech console", x, 180);
    	g.drawString("It is " + cur.getName() + "'s turn, " + curTurnAP + " AP left", x, 200);
    	
    	int i = 1;
    	for(Permanent p : units){
    		g.drawString(p.getName() + " has " + p.HP() + " health points.", x, 200 + (20 * i));
    		i++;
    	}
    	
    	g.drawString("MESSAGES: ", x, 200 + (20 * i));
    	i++;
    	
    	for(String s : console) {
    		g.drawString(s, x, 200 + (20 * i));
    		i++;
    	}
    }
    
    private void drawPermanents(Graphics g) {
    	
    	for(Permanent p : units) {
    		
    		g.clearRect(p.x() * length, p.y() * length, length, length);
    		drawTile(p.x(), p.y(), g, p.c);
    		
    	}
    	//rangeHelper(units.getFirst().x(), units.getFirst().y(), units.getFirst().getAP());
    	
    }
    
    
    
    private int width() {
    	
    	return grid[0].length;
    	
    }
    
    private int height() {
    	
    	return grid.length;
    	
    }
			
}

