package gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.enemies.Enemy;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.Flag;
import gamelogic.tiles.Flower;
import gamelogic.tiles.Gas;
import gamelogic.tiles.Portal;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;
import gamelogic.tiles.Water;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();
	private ArrayList<Water> waters = new ArrayList<>();
	private ArrayList<Gas> gases = new ArrayList<>();
	//private ArrayList<Portal> portals = new ArrayList<>();


	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;
	private long timer = System.currentTimeMillis();
	
	private long gasExposureStart = -1; // -1 means not in gas
	private static final long GAS_DEATH_TIME = 5000; // 5 seconds in ms


	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];
		waters = new ArrayList();
		gases = new ArrayList();

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition*tileSize, yPosition*tileSize, this)); // TODO: objects vs tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18) {
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
					waters.add((Water)tiles[x][y]);
				}
				else if (values[x][y] == 19) {
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
					waters.add((Water)tiles[x][y]);
				}
				else if (values[x][y] == 20) {
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
					waters.add((Water)tiles[x][y]);
				}
				else if (values[x][y] == 21) {
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
					waters.add((Water)tiles[x][y]);
				}
				
				/*
				else if (values[x][y] == 22) { // Entrance portal
					tiles[x][y] = new Portal(xPosition, yPosition, tileSize, tileset.getImage("Entrance_Door"), this, true);
				} else if (values[x][y] == 23) { // Exit portal
					tiles[x][y] = new Portal(xPosition, yPosition, tileSize, tileset.getImage("Exit_Door"), this, false);
				}
				*/

			}

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if(flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			boolean didITouchWater = false;
			for(Water w: waters) {
				if(w.getHitbox().isIntersecting(player.getHitbox())) {
					System.out.println("Touching Water");
					didITouchWater = true;
					player.walkSpeed = 200;
				}
			}
			if(!didITouchWater) {
				//System.out.println("Never Touched Water");
				player.walkSpeed = 400;
			}

			boolean inGas = false;

			for (Gas g : gases) {
				if (g.getHitbox().isIntersecting(player.getHitbox())) {
					inGas = true;
					break; // Only need to find one gas tile
				}
			}

			if (inGas) {
				if (gasExposureStart == -1) {
					gasExposureStart = System.currentTimeMillis(); // Start timer
				} else {
					long now = System.currentTimeMillis();
					if (now - gasExposureStart > GAS_DEATH_TIME) {
						onPlayerDeath();
					}
				}
			} else {
				gasExposureStart = -1; // Reset timer when out of gas
			}

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}

		/* 
		// Portal teleportation
		Portal entrance = null;
		Portal exit = null;

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				Tile tile = map.getTiles()[x][y];
				if (tile instanceof Portal) {
					Portal portal = (Portal) tile;
					if (portal.isEntrance()) {
						entrance = portal;
					} else {
						exit = portal;
					}
				}
			}
		}

		if (entrance != null && exit != null && entrance.getHitbox().isIntersecting(player.getHitbox())) {
			// Teleport the player to exit
			player.setX(exit.getX());
			player.setY(exit.getY());
		}
		*/
	}		
	
	//#############################################################################################################
	//Your code goes here! 
	//Please make sure you read the rubric/directions carefully and implement the solution recursively!
	
	/*
 * Recursively adds water tiles starting from (col, row) with given fullness.
 *
 * Preconditions:
 * - col and row are within the map bounds.
 * - fullness is an integer between 0 and 3 inclusive.
 * - map is not null and contains valid tiles.
 *
 * Postconditions:
 * - Water tiles are added starting at (col, row) spreading downwards first, then sideways.
 * - Water tiles placed have fullness values decreasing by 1 as it spreads sideways.
 * - Water does not replace solid tiles or existing water tiles.
 * - Recursion stops when no further spreading is possible or fullness runs out.
 */
	
	private void water(int col, int row, Map map, int fullness) {
	if (col < 0 || col >= map.getWidth() || row < 0 || row >= map.getHeight()) return;

	Tile current = map.getTiles()[col][row];
	if (current.isSolid() || current instanceof Water) return;

	// Determine correct image based on fullness
	String imageName;
	if (fullness >= 3) imageName = "Full_water";
	
	else if (fullness == 2) imageName = "Half_water";
	else if (fullness == 1) imageName = "Quarter_water";
	else imageName = "Falling_water";

	Water w = new Water(col, row, tileSize, tileset.getImage(imageName), this, fullness);
	map.addTile(col, row, w);
	waters.add(w);

	// Try to flow downward
	if (row + 1 < map.getHeight()) {
		Tile below = map.getTiles()[col][row + 1];
		if (!below.isSolid() && !(below instanceof Water)) {
			// Continue falling
			water(col, row + 1, map, 0);
			return; // Don't spread sideways when falling
		} else if (below.isSolid()) {
			// Convert falling water to full water on solid ground
			if (fullness == 0) {
				map.addTile(col, row, new Water(col, row, tileSize, tileset.getImage("Full_water"), this, 3));
				fullness = 3; // Update fullness for lateral spread
			}
		}
	}

	// Sideways spread only if fullness > 1
	if (fullness > 1) {
		// Flow right
		if (col + 1 < map.getWidth()) {
			Tile right = map.getTiles()[col + 1][row];
			if (!right.isSolid() && !(right instanceof Water)) {
				water(col + 1, row, map, fullness - 1);
			}
		}
		// Flow left
		if (col - 1 >= 0) {
			Tile left = map.getTiles()[col - 1][row];
			if (!left.isSolid() && !(left instanceof Water)) {
				water(col - 1, row, map, fullness - 1);
			}
		}
	}
}

	//#############################################################################################################
	/*
 * Recursively adds gas tiles starting at (col, row) up to numSquaresToFill.
 *
 * Preconditions:
 * - col and row are within map bounds.
 * - numSquaresToFill is positive.
 * - map is not null and contains valid tiles.
 * - placedThisRound is not null and tracks newly placed Gas tiles.
 *
 * Postconditions:
 * - Gas tiles are placed starting at (col, row), expanding in order:
 *   up, left, right, down, diagonals.
 * - Gas is only placed on non-solid, non-Gas tiles.
 * - The method stops when numSquaresToFill reaches zero or no more valid tiles exist.
 * - placedThisRound contains all newly placed Gas tiles.
 */

	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		// Bounds check and tile check
		if (col < 0 || col >= map.getWidth() || row < 0 || row >= map.getHeight()) return;
		Tile[][] tiles = map.getTiles();
		Tile startTile = tiles[col][row];
		if (startTile.isSolid() || startTile instanceof Gas) return;
		
	    // Place initial gas tile
		Gas g = new Gas (col, row, tileSize, tileset.getImage("GasOne"), this, 0);
		map.addTile(col, row, g);
		numSquaresToFill--;
		placedThisRound.add(g);
		gases.add(g);


		// Queue of gas tiles to expand from
		ArrayList<Gas> curr = new ArrayList<>();
		curr.add(g);

		while (!curr.isEmpty() && numSquaresToFill > 0) {
			ArrayList<Gas> newCurr = new ArrayList<>();

			for (Gas gas : curr) {
				int gx = gas.getCol();
				int gy = gas.getRow();

				// Try to add gas in the order: up, left, right, down
				int[][] directions = {
					{0, -1}, // up
					{-1, 0}, // left
					{1, 0},  // right
					{0, 1},  // down
					{-1, -1}, //left-up
					{-1, 1}, //left-down
					{1, -1}, //right-up
					{1, 1}  //right-down
				};

				for (int[] dir : directions) {
					int nx = gx + dir[0];
					int ny = gy + dir[1];

					// Skip if out of bounds
					if (nx < 0 || nx >= map.getWidth() || ny < 0 || ny >= map.getHeight())
						continue;

					Tile nextTile = tiles[nx][ny];
					// Place gas if tile is not solid or already gas
					if (!nextTile.isSolid() && !(nextTile instanceof Gas)) {
						Gas newGas = new Gas(nx, ny, tileSize, tileset.getImage("GasOne"), this, 0);
						map.addTile(nx, ny, newGas);
						placedThisRound.add(newGas); 
						newCurr.add(newGas);
						numSquaresToFill--;
						


						if (numSquaresToFill == 0)
							return; // Stop once we've placed enough
					}
				}
			}

    	    	curr = newCurr;
    	}
	}	

//---------------------------------------------------------PORTAL MODIFICATIONS--------------------------------------------------------//
	/*
	public void portals(int col, int row, Map map) {
		if (col < 0 || col >= map.getWidth() || row < 0 || row >= map.getHeight()) return;
		Tile[][] tiles = map.getTiles();
		Tile startTile = tiles[col][row];
		if (startTile.isSolid() || startTile instanceof Portal) return;
		
		Portal p1 = new Portal(col, row, tileSize, tileset.getImage("Entrance_Door"), this);
		Portal p2 = new Portal(col, row, tileSize, tileset.getImage("Exit_Door"), this);

		map.addTile(col, row, p1);
		map.addTile(col, row, p2);

		
	}

	*/
	
	public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			 if(tile instanceof Gas) {
	   				
	   				 int adjacencyCount =0;
	   				 for(int i=-1; i<2; i++) {
	   					 for(int j =-1; j<2; j++) {
	   						 if(j!=0 || i!=0) {
	   							 if((x+i)>=0 && (x+i)<map.getTiles().length && (y+j)>=0 && (y+j)<map.getTiles()[x].length) {
	   								 if(map.getTiles()[x+i][y+j] instanceof Gas) {
	   									 adjacencyCount++;
	   								 }
	   							 }
	   						 }
	   					 }
	   				 }
	   				 if(adjacencyCount == 8) {
	   					 ((Gas)(tile)).setIntensity(2);
	   					 tile.setImage(tileset.getImage("GasThree"));
	   				 }
	   				 else if(adjacencyCount >5) {
	   					 ((Gas)(tile)).setIntensity(1);
	   					tile.setImage(tileset.getImage("GasTwo"));
	   				 }
	   				 else {
	   					 ((Gas)(tile)).setIntensity(0);
	   					tile.setImage(tileset.getImage("GasOne"));
	   				 }
	   			 }
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
	   	 }


	   	 // Draw the enemies
	   	 for (int i = 0; i < enemies.length; i++) {
	   		 enemies[i].draw(g);
	   	 }


	   	 // Draw the player
	   	 player.draw(g);

	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 g.translate((int) +camera.getX(), (int) +camera.getY());

		 // Draw gas timer if the player is exposed
		if (gasExposureStart != -1) {
			long now = System.currentTimeMillis();
			long timeLeft = GAS_DEATH_TIME - (now - gasExposureStart);

			if (timeLeft < 0) timeLeft = 0;

			g.setColor(java.awt.Color.RED);
			g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
			g.drawString("Gas Timer: " + (timeLeft / 1000.0) + "s", (int)camera.getX() + 20, (int)camera.getY() + 40);
		}

	    }


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}