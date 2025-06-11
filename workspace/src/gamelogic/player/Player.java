package gamelogic.player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import gameengine.PhysicsObject;
import gameengine.graphics.MyGraphics;
import gameengine.hitbox.RectHitbox;
import gamelogic.Main;
import gamelogic.level.Level;
import gamelogic.tiles.Tile;

public class Player extends PhysicsObject {
	public float walkSpeed = 400;
	public float jumpPower = 1350;

	private boolean isDoubleJump = false;
	private boolean isJumping = false;

	// NEW: Added for double jump tracking
	private boolean jumpKeyPreviouslyDown = false;
	private int jumpCount = 0;
	private final int maxJumps = 2;

	private long time;
	private float x;
	private float y;

	public Player(float x, float y, Level level) {
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset = (int)(level.getLevelData().getTileSize()*0.1);
		this.hitbox = new RectHitbox(this, offset, offset, width - offset, height - offset);
		time = System.currentTimeMillis();
	}

	@Override
	public void update(float tslf) {
		super.update(tslf);

		movementVector.x = 0;

		if (PlayerInput.isLeftKeyDown()) {
			movementVector.x = -walkSpeed;
		}
		if (PlayerInput.isRightKeyDown()) {
			movementVector.x = +walkSpeed;
		}

		// Ground check and jump reset
		if (collisionMatrix[BOT] != null) {
			isJumping = false;
			jumpCount = 0; // Reset jump count on ground
		} else {
			isJumping = true;
		}


		/**
		 * Handles edge-triggered jump input and performs jumping or double-jumping.
		 *
		 * Preconditions:
		 * - PlayerInput.isJumpKeyDown() provides correct current input state.
		 * - jumpKeyPreviouslyDown holds the state from the previous frame.
		 * - jumpCount is less than maxJumps to allow jumping.
		 *
		 * Postconditions:
		 * - If jump key is newly pressed and jumpCount < maxJumps:
		 *   - movementVector.y is set to a negative value to initiate jump.
		 *   - jumpCount is incremented.
		 *   - If jumpCount reaches 2, double jump is triggered (isDoubleJump = true).
		 * - jumpKeyPreviouslyDown is updated to the current frame's jump key state.
		 */
		if (PlayerInput.isJumpKeyDown() && !jumpKeyPreviouslyDown) {
			if (jumpCount < maxJumps) {
				movementVector.y = -jumpPower;
				jumpCount++;

				if (jumpCount == 2) {
					isDoubleJump = true;
				}
			}
		}

		// NEW: Track jump key state
		jumpKeyPreviouslyDown = PlayerInput.isJumpKeyDown();
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);

		g.setFont(new Font("Comic Sans MS", Font.PLAIN, 50));
		g.drawString((System.currentTimeMillis() - time) / 1000 + "", (int)getX(), (int)getY());

		if (System.currentTimeMillis() - time > 10000) {
			time = System.currentTimeMillis();
		}

		if (Main.DEBUGGING) {
			for (int i = 0; i < closestMatrix.length; i++) {
				Tile t = closestMatrix[i];
				if (t != null) {
					g.setColor(Color.RED);
					g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
				}
			}
		}

		hitbox.draw(g);
	}

	public void setX(float x) {
		this.x = x;
		if (hitbox != null) hitbox.update(); // Keep the hitbox in sync
	}

	public void setY(float y) {
		this.y = y;
		if (hitbox != null) hitbox.update(); // Keep the hitbox in sync
	}
}


