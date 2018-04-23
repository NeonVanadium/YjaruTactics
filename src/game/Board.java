package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class Board {
	
	private Tile[][] grid; //the game board
	private final int length;
	private Hashtable<Integer, Boolean> marked; //dfs marked array, but a hashtable so I can clear it in one line
	private int Xoffset; //x offset for the display
	private int Yoffset;
	
	public Board(Tile[][] arr) {
		
		grid = Arrays.copyOf(arr, arr.length);
		marked = new Hashtable<Integer, Boolean>();
		this.length = 60;
		Xoffset = (Main.getFramewidth() / 2) - ((width() * length) / 2);
		Yoffset = (Main.getFrameheight() / 3) - ((height() * length) / 3);
		
	}
	
	public boolean isInBounds(int row, int col) {
		
		if(row < 0 || row >= height() || col < 0 || col >= width()) return false;
		return true;
		
	}
	
	public Tile getTile(int row, int col) {	
		
		if(isInBounds(row, col)) return grid[row][col]; 
		return null;
		
	}
	
	public int toGridScale(int i) { //converts integer i, likely from a mouse event, to the corresponding number within the range of the grid
		
    	return (i - (i % length)) / length;
    	
    }
	
	public int width() {
    	
    	return grid[0].length;
    	
    }
    
    public int height() {
    	
    	return grid.length;
    	
    }
    
    public int length() {
    	
    	return length;
    	
    }
    
    public int Xoffset() {
    	
    	return Xoffset;
    	
    }
    
    public int Yoffset() {
    	
    	return Yoffset;
    	
    }
    
    public int to1D(int x, int y) { //convert row y and column x to a 1-dimensional index
    	
    	return x + (y * length);
    	
    }
    
    public boolean isMarked(int row, int col) {
    	
    	return(marked.get(to1D(col, row)) != null);
    	
    }
    
    public void clearMarked() {
    	
    	marked.clear();
    	
    }
    
    public List<Permanent> inRange(int distance, int row, int col){ //returns a list of all the permanents within range
    	
    	ArrayList<Permanent> l = new ArrayList<Permanent>();

    	inRange(distance + 1, row, col, l);
    	
    	//for(Permanent p : l) System.out.println(p.getName());
    	
    	return l;
    	
    }
    
    private void inRange(int distance, int row, int col, List<Permanent> list) {
    	
    	if(distance <= 0) {
    		return;
    	}
    	
    	
    	if(!isInBounds(row, col)) {
    		
    		return;
    	}
    	
    	if(getTile(row, col).occupier() != null) {
    		
    		if(list.contains(getTile(row, col).occupier())) return;
    		list.add(getTile(row, col).occupier());
    		
    	}
    	
    	inRange(distance - 1, row, col - 1, list);
    	inRange(distance - 1, row, col + 1, list);
    	inRange(distance - 1, row - 1, col, list);
    	inRange(distance - 1, row + 1, col, list);
    	
    }
	
    public void drawTile(Graphics g, int row, int col, ImageObserver observer) { //draws a specific tile
    	
    	g.drawImage(getTile(row, col).getSprite(), (col * length()) + Xoffset, (row * length()) + Yoffset, length(), length(), observer);
    	
    }
    
    public void draw(Graphics g, ImageObserver observer) { //draws the entire grid
	
		for(int i = 0; i < height(); i++) {
			
			for(int j = 0; j < width(); j++) {
				
				g.drawImage(getTile(i, j).getSprite(), (j * length()) + Xoffset, Yoffset + (i * length()), length(), length(), observer);
				
			}
			
		}
	
    }
    
    /*private int perspective(int i) { //returns perspective-compensated height of a tile at row i
    	
    	if(i == 0) return length - (length() / 2);
     	return length()  - ((length() / 2) / (i));
    	
    }*/
    
	public void range(int x, int y, int distance, Graphics g){
    	
    	marked.clear();
    	rangeHelper(x - 1, y, distance, g);
    	rangeHelper(x + 1, y, distance, g);
    	rangeHelper(x, y - 1, distance, g);
    	rangeHelper(x, y + 1, distance, g);
    	
    }
    
    private void rangeHelper(int x, int y, int distance, Graphics g) {
    	
    	if(distance <= 0) return;
    	
    	if(!isInBounds(y, x)) return;
    	
    	if(marked.contains(to1D(x, y))) return;
    	
    	if(!grid[y][x].passable() || grid[y][x].occupier() != null) return;
    	
    	marked.put(to1D(x, y), true);
    	
    	g.setColor(Color.CYAN);
    	
    	g.drawRect((x * length) + Xoffset, (y * length) + Yoffset, length, length());
    	
    	rangeHelper(x - 1, y, distance - 1, g);
    	rangeHelper(x + 1, y, distance - 1, g);
    	rangeHelper(x, y - 1, distance - 1, g);
    	rangeHelper(x, y + 1, distance - 1, g);
    	
    }

}
