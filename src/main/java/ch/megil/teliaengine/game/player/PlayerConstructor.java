package ch.megil.teliaengine.game.player;

import ch.megil.teliaengine.game.Hitbox;
import javafx.scene.Node;

public interface PlayerConstructor {
	Player invoke(Node depiction, Hitbox hitbox);
}
