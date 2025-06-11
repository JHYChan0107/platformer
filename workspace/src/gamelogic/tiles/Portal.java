package gamelogic.tiles;

import java.awt.image.BufferedImage;

import gameengine.hitbox.RectHitbox;
import gamelogic.level.Level;

public class Portal extends Tile {
	private boolean isEntrance;
	//private int col, row;
	//private float x;
	//private float y;

	public Portal(float x, float y, int size, BufferedImage image, Level level, boolean isEntrance) {
		super(x, y, size, image, false, level);
		//this.x = x;
		//this.y = y;
		this.isEntrance = isEntrance;
	}

	public boolean isEntrance() {
		return isEntrance;
	}

	public int getCol() {
		return (int)(getX() / level.getMap().getTileSize());

	}

	public int getRow() {
		return (int)(getY() / level.getMap().getTileSize());
	}
}

