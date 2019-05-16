package ch.megil.teliaengine.game.player;

import ch.megil.teliaengine.physics.hitbox.Hitbox;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public interface PlayerConstructor {
	Player invoke(String depictionName, Image depiction, Hitbox hitbox, Color color);
}
