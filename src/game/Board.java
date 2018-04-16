package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.Hashtable;

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
	
	public Tile getTile(int row, int col) {	return grid[row][col]; }
	
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
	
    public void drawTile(Graphics g, int row, int col, ImageObserver observer) { //draws a specific tile
    	
    	g.drawImage(getTile(row, col).getSprite(), (col * length()) + Xoffset, (row * length()) + Yoffset, length(), length(), observer);
    	
    }
    
    public void draw(Graphics g, ImageObserver observer) { //draws the entire grid
	
		for(int i = 0; i < height(); i++) {
			
			for(int j = 0; j < width(); j++) {
				
				g.drawImage(getTile(i, j).getSprite(), (j * length()) + Xoffset, (i * length()) + Yoffset, length(), length(), observer);
				
			}
			
		}
	
    }
    
	public void range(int x, int y, int distance, Graphics g){
    	
    	marked.clear();
    	rangeHelper(x - 1, y, distance, g);
    	rangeHelper(x + 1, y, distance, g);
    	rangeHelper(x, y - 1, distance, g);
    	rangeHelper(x, y + 1, distance, g);
    	
    }
    
    private void rangeHelper(int x, int y, int distance, Graphics g) {
    	
    	if(distance <= 0) return;
    	
    	if(x < 0 || x >= width() || y < 0 || y >= height()) return;
    	
    	if(marked.contains(to1D(x, y))) return;
    	
    	if(!grid[y][x].passable() || grid[y][x].occupier() != null) return;
    	
    	marked.put(to1D(x, y), true);
    	
    	g.setColor(Color.CYAN);
    	
    	g.drawRect((x * length) + Xoffset, (y * length) + Yoffset, length, length);
    	
    	rangeHelper(x - 1, y, distance - 1, g);
    	rangeHelper(x + 1, y, distance - 1, g);
    	rangeHelper(x, y - 1, distance - 1, g);
    	rangeHelper(x, y + 1, distance - 1, g);
    	
    }

}
