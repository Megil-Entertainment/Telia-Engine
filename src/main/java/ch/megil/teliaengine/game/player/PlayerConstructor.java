package ch.megil.teliaengine.game.player;

import ch.megil.teliaengine.game.Hitbox;
import javafx.scene.image.Image;

public interface PlayerConstructor {
	Player invoke(Image depiction, Hitbox hitbox);
}
