package gamelogic.tiles;

import java.awt.image.BufferedImage;

import gameengine.hitbox.RectHitbox;
import gamelogic.level.Level;

public class Water extends Tile{

	private int fullness;
	
	public Water(float x, float y, int size, BufferedImage image, Level level, int fullness) {
		super(x, y, size, image, false, level);
		this.fullness = fullness;
		this.hitbox = new RectHitbox(x*size , y*size, 0, 10, size, size);
	}
	
	public int getFullness() {
		return fullness;
	}
	
	public void setIntensity(int fullness) {
		this.fullness = fullness;
	}

    public static void add(Water w) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'add'");
    }
}
