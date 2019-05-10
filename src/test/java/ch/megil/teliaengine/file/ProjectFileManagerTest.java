package ch.megil.teliaengine.file;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.megil.teliaengine.configuration.ConfigurationContstants;
import ch.megil.teliaengine.configuration.GameConfiguration;
import ch.megil.teliaengine.configuration.PhysicsConstants;
import ch.megil.teliaengine.configuration.data.GameConfigData;
import ch.megil.teliaengine.configuration.data.PhysicsConstData;
import ch.megil.teliaengine.file.exception.AssetLoadException;
import ch.megil.teliaengine.project.Project;

public class ProjectFileManagerTest {
	@Rule
	public TemporaryFolder tempProjects = new TemporaryFolder(new File("."));
	
	private GameConfigData gameConfigData;
	private PhysicsConstData physicsConstData;
	
	private Project testProject;
	private ProjectFileManager projecFileManager;
	
	@Before
	public void setUp() throws Exception {
		gameConfigData = new GameConfigData();
		gameConfigData.setMapWidth(1280);
		gameConfigData.setMapHeight(720);
		gameConfigData.setMapGridWidth(10);
		gameConfigData.setMapGridHeight(10);
		
		physicsConstData = new PhysicsConstData();
		physicsConstData.setWalkSpeed(10);
		physicsConstData.setJumpStrength(10);
		physicsConstData.setGravityStrength(5);
		physicsConstData.setTerminalSpeed(20);
		
		projecFileManager = new ProjectFileManager();
		testProject = new Project("test", tempProjects.newFolder("test"));
	}
	
	@Test
	public void testInitProject() throws Exception {
		projecFileManager.initProject(testProject, gameConfigData, physicsConstData);
		
		var projectDir = testProject.getLocationPath();
		
		var fileProjectInfo = new File(projectDir + "/test.teliaproject");
		assertTrue(fileProjectInfo.exists());
		
		var projectProps = new Properties();
		try (var projectInfo = new FileInputStream(fileProjectInfo)) {
			projectProps.load(projectInfo);
			
			assertEquals("test", projectProps.get("pName"));
		}
		
		var dirAssetsMap = new File(projectDir + "/assets/maps");
		assertTrue(dirAssetsMap.exists());
		var dirAssetsObj = new File(projectDir + "/assets/object");
		assertTrue(dirAssetsObj.exists());
		var dirAssetsTex = new File(projectDir + "/assets/texture");
		assertTrue(dirAssetsTex.exists());
		var dirConfig = new File(projectDir + "/config");
		assertTrue(dirConfig.exists());
		var dirConst = new File(projectDir + "/const");
		assertTrue(dirConst.exists());
		
		var origProps = new Properties();
		
		var fileProjectConfigGame = new File(projectDir + ConfigurationContstants.GAME_CONFIGURATION);
		assertTrue(fileProjectConfigGame.exists());
		
		var fileProjectConstPhysics = new File(projectDir + ConfigurationContstants.PHYSIC_CONSTANTS);
		assertTrue(fileProjectConstPhysics.exists());
		
		try (var projectConfigGame = new FileInputStream(fileProjectConfigGame);
				var projectConstPhysics = new FileInputStream(fileProjectConstPhysics);) {
			projectProps.clear();
			origProps.clear();
			
			projectProps.load(projectConfigGame);
			GameConfiguration.writeDataToProperties(origProps, gameConfigData);
			assertEquals("Game configuration", origProps, projectProps);
			
			projectProps.clear();
			origProps.clear();
			
			projectProps.load(projectConstPhysics);
			PhysicsConstants.writeDataToProperties(origProps, physicsConstData);
			assertEquals("Physics Constants", origProps, projectProps);
		}
	}

	@Test
	public void testLoadProjectCorrectFile() throws Exception {
		projecFileManager.initProject(testProject, gameConfigData, physicsConstData);
		var projectDir = testProject.getLocationPath();
		var fileProjectInfo = new File(projectDir + "/test.teliaproject");
		
		var loadedProject = projecFileManager.loadProject(fileProjectInfo);
		assertEquals(testProject.getName(), loadedProject.getName());
		assertEquals(testProject.getLocationPath(), loadedProject.getLocationPath());
	}
	
	@Test(expected = AssetLoadException.class)
	public void testLoadProjectIncorrectFile() throws Exception {
		projecFileManager.initProject(testProject, gameConfigData, physicsConstData);
		var projectDir = testProject.getLocationPath();
		var fileProjectInfo = new File(projectDir + "/test.test");
		
		@SuppressWarnings("unused")
		var loadedProject = projecFileManager.loadProject(fileProjectInfo);
	}
}
