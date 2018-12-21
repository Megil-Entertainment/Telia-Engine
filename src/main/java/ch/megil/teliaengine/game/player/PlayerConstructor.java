package ch.megil.teliaengine.game.player;

import ch.megil.teliaengine.game.Hitbox;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public interface PlayerConstructor {
	Player invoke(Image depiction, Hitbox hitbox, Color color);
}
