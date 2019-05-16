package ch.megil.teliaengine.game.player;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import ch.megil.teliaengine.file.ProjectFileManager;
import ch.megil.teliaengine.game.Hitbox;
import ch.megil.teliaengine.game.Vector;
import ch.megil.teliaengine.project.ProjectController;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PlayerTest {
	private static File parentDir = new File(".");
	
	@Rule
	public TemporaryFolder testProjectDir = new TemporaryFolder(parentDir);
	
	@Mock
	private Image depiction;
	private Hitbox playerHitbox;
	
	private List<Hitbox> collision;
	
	@Before
	public void setUp() throws Exception {
		depiction = mock(Image.class);
		playerHitbox = new Hitbox(Vector.ZERO, 50, 50);
		
		collision = new ArrayList<>();
		collision.add(new Hitbox(new Vector(0, 83), 100, 50));
		
		//create folders
		testProjectDir.newFolder("assets");
		testProjectDir.newFolder("assets", "texture");
		testProjectDir.newFolder("const");
		
		//create player
		var player = testProjectDir.newFile("assets/player.tobj");
		try (var writer = new BufferedWriter(new FileWriter(player))) {
			writer.write("10.0:10.0:player:#000000");
		}
		testProjectDir.newFile("assets/texture/player.png");
		
		//create player
		var physics = testProjectDir.newFile("const/physics.properties");
		try (var writer = new BufferedWriter(new FileWriter(physics))) {
			writer.write("terminalFallVelocity = 0/20");
		}

		//create info and load project
		var projectInfo = testProjectDir.newFile("test.teliaproject");
		var project = new ProjectFileManager().loadProject(projectInfo);
		ProjectController.get().openProject(project);
	}

	@Test
	public void testUpdate() {
		var player = new Player("depiction", depiction, playerHitbox, Color.BLACK);
		
		assertEquals(0, player.getPosition().getX(), 0);
		assertEquals(0, player.getPosition().getY(), 0);
		
		player.applyForce(new Vector(5, 3));
		player.update(collision);
		assertEquals(5, player.getPosition().getX(), 0);
		assertEquals(3, player.getPosition().getY(), 0);
		
		player.update(collision);
		assertEquals(15, player.getPosition().getX(), 0);
		assertEquals(9, player.getPosition().getY(), 0);
		
		player.applyForce(new Vector(-5, -3));
		player.update(collision);
		assertEquals(25, player.getPosition().getX(), 0);
		assertEquals(15, player.getPosition().getY(), 0);
		
		player.applyAcceleration(new Vector(-10, -6));
		player.update(collision);
		assertEquals(25, player.getPosition().getX(), 0);
		assertEquals(15, player.getPosition().getY(), 0);
		
		player.update(collision);
		assertEquals(25, player.getPosition().getX(), 0);
		assertEquals(15, player.getPosition().getY(), 0);
		
		player.applyForce(new Vector(0, 5));
		player.applyAcceleration(new Vector(3, 2));
		player.update(collision);
		assertEquals(28, player.getPosition().getX(), 0);
		assertEquals(22, player.getPosition().getY(), 0);
		
		player.applyForce(new Vector(0, -5));
		player.update(collision);
		assertEquals(31, player.getPosition().getX(), 0);
		assertEquals(29, player.getPosition().getY(), 0);
		
		player.update(collision);
		assertEquals(34, player.getPosition().getX(), 0);
		assertEquals(33, player.getPosition().getY(), 0);
	}
}
