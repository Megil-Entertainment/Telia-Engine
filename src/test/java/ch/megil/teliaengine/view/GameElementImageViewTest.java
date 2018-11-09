package ch.megil.teliaengine.view;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import ch.megil.teliaengine.game.GameElement;
import ch.megil.teliaengine.ui.GameElementImageView;
import javafx.scene.image.Image;

public class GameElementImageViewTest {
	private GameElementImageView imageView;
	
	@Mock
	private Image depiction;
	
	private GameElement gameElement;
	
	@Before
	public void setUp() {
		depiction = mock(Image.class);
		gameElement = new GameElement(depiction, null);
		imageView = new GameElementImageView(gameElement);
	}
	
	@Test
	public void setImageViewLayout() {
		imageView.setImageViewLayoutX(5);
		assertEquals(5, gameElement.getPosition().getX(),0);
		imageView.setImageViewLayoutY(5);
		assertEquals(5, gameElement.getPosition().getY(),0);
	}
	
	@Test
	public void getGameElement() {
		assertEquals(gameElement,imageView.getGameElement());
	}
}
