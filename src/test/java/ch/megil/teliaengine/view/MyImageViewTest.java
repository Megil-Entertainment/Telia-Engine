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
import ch.megil.teliaengine.ui.MyImageView;
import javafx.scene.image.Image;

public class MyImageViewTest {
	private MyImageView imageView;
	
	@Mock
	private Image depiction;
	
	@Spy
	private GameElement gameElement;
	
	@Before
	public void setUp() {
		depiction = mock(Image.class);
		gameElement = spy(new GameElement(depiction, null));
		imageView = new MyImageView(gameElement);
	}
	
	@Test
	public void setImageViewLayout() {
		imageView.setImageViewLayoutX(5);
		assertEquals(5, gameElement.getPosX(),0);
		imageView.setImageViewLayoutY(5);
		assertEquals(5, gameElement.getPosY(),0);
	}
	
	@Test
	public void getGameElement() {
		assertEquals(gameElement,imageView.getGameElement());
	}
}
