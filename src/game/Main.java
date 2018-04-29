package game;

import java.awt.Color;
import java.awt.Component;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class Main {
	
	private static Game g;
	private static Hashtable<String, Permanent> fighters = new Hashtable<String, Permanent>();
	private static Hashtable<Integer, Ability> abilities;
	private static Hashtable<String, Board> boards;
	private static JFrame f = new JFrame();
	private static LinkedList<Permanent> t1 = new LinkedList<Permanent>(), t2 = new LinkedList<Permanent>();
	private static Timer t = new Timer();
	protected static boolean testMode = true;
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		initAttacks();
		initFighters();
    	
    	//Set up frame
		f.setTitle("Tactics");
		f.setLocation(0, 0); //okay whose idea was it for the top left corner of the screen to be at x = -10
		f.setUndecorated(true);
		f.setAlwaysOnTop(true);
		f.setSize(1920, 1080);
		f.setBackground(Color.BLACK);
		f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
		
    	
		if(!testMode) {
	
	    	CharacterSelect select = new CharacterSelect();
	    	select.setEnabled(true);
	    	select.setVisible(true);
	    	select.setBackground(Color.DARK_GRAY);
	    	f.add(select);
	    	f.setVisible(true);
	    	select.repaint();
	    		
	    	t.scheduleAtFixedRate(new TimerTask() { //rotates the characters every .8 seconds
	    			
	    		public void run() {
	    				
	    			select.incrementDir();
	    			t.purge();
	    				
	    		}
	    			
	    	}, 0, 800);
	    		
	    	t.scheduleAtFixedRate(new TimerTask() { //redraws (if necessary) every .004 seconds
	    			
	    		public void run() {
	    			
	    			select.checkDone();
	    			select.showFighters();
	    			t.purge();
	    				
	    		}
	    			
	    	}, 0, 05);
		}
		else {
			
			t1.add(fighters.get("Nathaniel Jathien"));
			t1.add(fighters.get("Seraila Endall"));
			t1.add(fighters.get("Claio Eade-il Doras III"));
			t2.add(fighters.get("William Mayden"));
			t2.add(fighters.get("Daniel Bocaild"));
			t2.add(fighters.get("Catherine (Adult)"));
			
			f.setVisible(true);
			endSelection();
			
		}
	}
	
	private static void initFighters() {
		
		fighters.put("Nathaniel Jathien", new Permanent("NATHANIEL", "Sprites/Nathaniel.png", 13, 34, new int[] { 0, 4 }));
    	fighters.put("Seraila Endall", new Permanent("SERAILA", "Sprites/Seraila.png", 11, 34, new int[] { 0, 5 }));
    	fighters.put("Claio Eade-il Doras III", new Permanent("CLAIO III", "Sprites/Claio III.png", 11, 34, new int[] { 1 }));
    	fighters.put("Claio Eade-il Doras I", new Permanent("CLAIO I", "Sprites/Claio I.png", 11, 34, new int[] { 0 }));
    	fighters.put("Catherine (Adult)", new Permanent("CATHERINE", "Sprites/Catherine.png", 11, 33, new int[]{ 3 }));
    	fighters.put("Catherine (Kid)", new Permanent("CAT (STREET)", "Sprites/Catherine (Street Kid).png", 9, 29, new int[]{ 3 }));
    	fighters.put("Catherine (After FotHoM)", new Permanent("CAT (KID)", "Sprites/Catherine (Kid).png", 11, 30, new int[] { 0 }));
    	fighters.put("William Mayden", new Permanent("WILLIAM", "Sprites/William.png", 13, 35, new int[]{ 2, 6 } ));
    	fighters.put("Gedirong \"Jaren\" Illiston", new Permanent("JAREN", "Sprites/Jaren.png", 13, 34, new int[]{ 3 }));
    	fighters.put("High Possessor Kanos", new Permanent("KANOS", "Sprites/Kanos.png", 11, 35, new int[] { 0 }));
    	fighters.put("Morten Yol", new Permanent("MORTEN", "Sprites/Morten.png", 13, 35, new int[] { 0 }));
    	fighters.put("Gael m'Ziilki", new Permanent("GAEL", "Sprites/Gael.png", 11, 34, new int[]{ 3 }));
    	fighters.put("The Burned Mage", new Permanent("GAEL (BURNED)", "Sprites/Gael (Burned).png", 11, 34, new int[]{ 3 }));
    	fighters.put("Daniel Bocaild", new Permanent("DANIEL", "Sprites/Daniel.png", 11, 34, new int[]{ 3 }));
    	fighters.put("High Assassin Caerzel", new Permanent("CAERZEL", "Sprites/Caerzel.png", 11, 35, new int[] { 0 }));
    	fighters.put("Andus Uwren", new Permanent("ANDUS", "Sprites/Andus.png", 11, 35, new int[]{ 3 }));
    	fighters.put("Seamus", new Permanent("SEAMUS", "Sprites/Seamus.png", 11, 34, new int[] { 0  }));
    	fighters.put("Tara", new Permanent("TARA", "Sprites/Tara.png", 11, 33, new int[] { 0 }));
    	fighters.put("Jae Jathien", new Permanent("JAE", "Sprites/Jae.png", 11, 32, new int[] { 0 }));
    	fighters.put("Manoreyn", new Permanent("MANOREYN", "Sprites/Manoreyn.png", 11, 35, new int[] { 0 }));
    	fighters.put("Sora Keng", new Permanent("KENG", "Sprites/Sora Keng.png", 11, 34, new int[] { 0 }));
	}
	
	private static void initBoards() {
		
		boards = new Hashtable<String, Board>();
		Tile[][] grid;
	
		if(!testMode)  grid = new Tile[15][15]; 
		else grid = new Tile[4][5]; 
		
		for(int i = 0; i < grid.length; i++){
			
			for(int j = 0; j < grid[0].length; j++){
				
				grid[i][j] = new Tile(i, j, "Sprites/Grass.png", false);
				
			}
			
		}
		
		if(!testMode) for(int i = 0; i < grid.length; i++){
				
				if(i != 3 && i != 4 && i != 10 && i != 11) grid[i][7] = new Tile(i, 7, "Sprites/cobble.png", true);
			
		}
		
		
		boards.put("Plains", new Board(grid));
		
	}
	
	private static void initAttacks() {
		
		abilities = new Hashtable<Integer, Ability>();
		
		//single-target
		//String name, int high, int low, int cost, int critDenominator, int times, int range, int type
		
		//area
		//String name, int high, int low, int cost, int critDenominator, int times, int range, int type, int width, int height
		
		abilities.put(0, new Ability("Swing", 10, 1, 1, 5, 1, 1, 0)); //basic attack 1-10
		abilities.put(1, new Ability("Flurry", 3, 1, 1, 3, 3, 1, 0)); //3 strikes of 1-3, with a chance to crit on each, so could possibly deal up to 18
		abilities.put(2, new Ability("Bash", 8, 4, 1, -1, 1, 1, 0)); //consistent damage 4-8, but will never crit
		abilities.put(3, new Ability("Mana bolt", 5, 1, 1, 5, 1, 3, 0)); //cheap attack 1-5, with range
		abilities.put(4, new Ability("Heal Ally", -6, -1, 1, 5, 1, 3, 2)); //healy boi
		abilities.put(5, new Ability("Banshee Ball", 12, 3, 5, 5, 1, 4, 0, 3, 3)); //cheap attack 1-5, with range
		abilities.put(6, new Ability("Cleave", 7, 2, 3, 5, 1, 1, 0, 3, 1)); //basic cleave attack
		
	}
	
	public static Timer getTimer() { return t; }
	
	public static void removeFromFrame(Component c) { f.remove(c); }
	
	public static void endSelection() {
		
		initBoards();
		g = new Game(boards.get("Plains"), t1, t2, f.getSize());
		g.setBackground(Color.BLACK);
		f.add(g);
		f.paintAll(f.getGraphics());
		g.paint(g.getGraphics());
		
		t= new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			
			public void run() {
				
				g.repaint();
				
			}
			
		}, 0, 5);
		
	}

	public static Game getGame() { return g; }
	
	public static LinkedList<Permanent> getTeam(int teamNum){
		
		if(teamNum == 1) return t1;
		if(teamNum == 2) return t2;
		throw new IllegalArgumentException("Team number was neither one nor two.");
		
	}
	
	public static void endGame(int winner) {
		
		f.dispose();
		System.exit(winner);
		
	}
	
	public static void addToTeam1(Permanent p) { t1.add(p); }
	
	public static void addToTeam2(Permanent p) { t2.add(p); }
	
	public static int getFramewidth() { return f.getWidth(); }
	
	public static int getFrameheight() { return f.getHeight(); }
	
	public static Hashtable<String, Permanent> getFighters(){ return fighters; }
	
	public static Hashtable<Integer, Ability> getAbilities() { return abilities; }
	
	public static void toConsole(String message){
		
		g.toConsole(message);
		
	}
	
}