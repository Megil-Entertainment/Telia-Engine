package ch.megil.teliaengine.game;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import static org.mockito.Mockito.*;

import javafx.scene.image.Image;

public class GameElementTest {
	private GameElement gameElement;
	
	@Mock
	private Image image;
	
	@Spy
	private Hitbox hitbox;
	
	@Before
	private void setUp() {
		image = mock(Image.class);
		hitbox = spy(new Hitbox(Vector.ZERO, 5, 5));
	}

}
