package game.zelda.usables;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import game.zelda.player.Link;
import engine.FaceDirection;
import engine.Game;
import engine.entity.AbstractSimpleEntity;
import engine.entity.enemy.AbstractEnemy;
import engine.entity.usable.AbstractWeapon;
import engine.graphics.sprite.AnimatedSprite;
import engine.graphics.sprite.SimpleSprite;
import engine.graphics.sprite.SpriteSheet;
import engine.graphics.sprite.SpriteUtils;
import engine.math.PositionVector;

public class BowAndArrow extends AbstractWeapon {
	
	private SimpleSprite bow;
	private AnimatedSprite arrowN; 
	private AnimatedSprite arrowS;
	private AnimatedSprite arrowE;
	private AnimatedSprite arrowW;
	
	private int speed;
	
	private List<Arrow> arrows;
	
	private int numArrows;

	private long lastShotTime;
	
	public BowAndArrow(Link link) {
		super(link);
		SpriteSheet entities = (SpriteSheet) Game.sprites.get("entities");
		
		bow = entities.get(163);
		arrowN = new AnimatedSprite(entities.range(362), 0);
		arrowS = SpriteUtils.flipVertical(arrowN);
		arrowE = new AnimatedSprite(entities.range(363), 0);
		arrowW = SpriteUtils.flipVertical(arrowE);
		
		using = false;
		speed = 8;
		numArrows = 30;
		damage = 2;
		collisionOffset = 6;
		//sound = Game.sounds.get("boomerang");
		arrows = new LinkedList<Arrow>();
	}
	
	public void draw(Graphics2D g) {
		for(Arrow arrow : arrows) {
			arrow.draw(g);
		}
	}
	
	public void use() {
		if(numArrows > 0 && Game.clock.elapsedMillis() - lastShotTime > 750) {
			lastShotTime = Game.clock.elapsedMillis();
			//sound.play();
	
			x = user.x();
			y = user.y();
			numArrows--;
			switch(user.face()) {
				case NORTH:
					arrows.add(new Arrow(arrowN, 0, -speed, x, y));
					break;
				case EAST:
					arrows.add(new Arrow(arrowE, speed, 0, x, y));
					break;
				case SOUTH:
					arrows.add(new Arrow(arrowS, 0, speed, x, y));
					break;
				case WEST:
					arrows.add(new Arrow(arrowW, -speed, 0, x, y));
					break;
			}
		}
	}
	
	public boolean using() {
		return using;
	}
	
	public void handle() {

		Iterator<Arrow> arrowIter = arrows.iterator();
		boolean removeArrow;
		while(arrowIter.hasNext()) {
			removeArrow = false;
			Arrow arrow = arrowIter.next();
			
			arrow.handle();
			
			// enemies
			Iterator<AbstractEnemy> iter = game.map().enemies().iterator();
			while (iter.hasNext()) {
				AbstractEnemy enemy = iter.next();
				if (arrow.rectangleCollide(enemy)) {
					enemy.hit(damage());
					removeArrow = true;
					break;
				}
			}	
			int dX = arrow.x() + game.map().offset().x();
			int dY = arrow.y() + game.map().offset().y();
			if(dX < -arrow.width() || dX > Game.SCREEN_WIDTH || dY < -arrow.height() || dY > Game.SCREEN_HEIGHT) {
				removeArrow = true;
			}
			if(removeArrow) {
				arrowIter.remove();
			}
		}

	}
	
	public void menuDraw(Graphics2D g, int x, int y) {
		bow.draw(g, x - 2, y);
		arrowN.draw(g, x + 4, y);
	}
	
	@Override
	public String menuDisplayName() {
		return String.valueOf(numArrows);
	}
	
	public void face(FaceDirection face) {
		
	}

	private class Arrow extends AbstractSimpleEntity {
		
		private PositionVector acceleration = new PositionVector();
		
		private int speedX;
		
		private int speedY;
		
		public Arrow(AnimatedSprite sprite, int speedX, int speedY, int x, int y) {
			this.sprite = sprite; // make a copy later for case of animated arrows
			acceleration.set(speedX, speedY);
			locate(x, y);
			this.speedX = speedX;
			this.speedY = speedY;
			collisionOffset = 5;
		}
		
		public void handle() {
			x += speedX;
			y += speedY;
		}

		@Override
		public void draw(Graphics2D g) {
			sprite.draw(g, x() + game.map().offset().x(), y() + game.map().offset().y());
		}
		
	}

	@Override
	public int width() {
		return bow.width();
	}

	@Override
	public int height() {
		return bow.height();
	}
	
	public int numArrows() {
		return numArrows;
	}
	
	public void numArrows(int numArrows) {
		this.numArrows = numArrows;
	}
	
	public void reset() {
		super.reset();
		numArrows = 50;
	}
	
}
