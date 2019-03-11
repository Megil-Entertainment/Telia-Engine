package ch.megil.teliaengine.ui.component;

import java.io.File;
import java.util.logging.Level;

import ch.megil.teliaengine.configuration.SystemConfiguration;
import ch.megil.teliaengine.file.IconLoader;
import ch.megil.teliaengine.file.MapSaveLoad;
import ch.megil.teliaengine.file.exception.AssetFormatException;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.logging.LogHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class AssetExplorer extends TreeView<String>{
	private int INDEX_NOT_FOUND = 0;
	private int DOUBLE_CLICK = 2;
	
	private MapSaveLoad mapSaveLoad;
	private MapEditor mapEditor;
	
	public void initialize(String rootPath, MapEditor mapEditor) throws AssetNotFoundException {
		var root = new File(rootPath);
		this.mapEditor = mapEditor;
		
		var treeRoot = new TreeItem<>(root.getName(), null);
		treeRoot.setGraphic(new ImageView(new IconLoader().load(SystemConfiguration.FOLDER_EXPANDED_ICON.getConfiguration(), 32, 32)));
		
		if(root.isDirectory()) {
			for (var child : root.listFiles()) {
				addNewTreeEntryFile(treeRoot, child);
			}
		}
		
		this.setRoot(treeRoot);
		
		setOnMouseClicked(me -> {if (me.getClickCount() == DOUBLE_CLICK) onEntryOpen();});
		setOnKeyTyped(ke -> {if (ke.getCode() == KeyCode.ENTER) onEntryOpen();});
		mapSaveLoad = new MapSaveLoad();
	}
	
	private void addNewTreeEntryFile(TreeItem<String> parent, File file) throws AssetNotFoundException {
		var treeItem = new TreeItem<>(stripFilename(file.getName()));		
		if (file.isDirectory()) {
			treeItem.setGraphic(new ImageView(new IconLoader().load(SystemConfiguration.FOLDER_EXPANDED_ICON.getConfiguration(), 32, 32)));
			for (var child : file.listFiles()) {
				addNewTreeEntryFile(treeItem, child);
			}
		} else {
			treeItem.setGraphic(new ImageView(new IconLoader().load(SystemConfiguration.FILE_ICON.getConfiguration(), 32, 32)));
		}
		parent.getChildren().add(treeItem);
	}
	
	private String stripFilename(String filename) {
		var newFilename = filename;
		int extIndex = newFilename.lastIndexOf(".");
		if(extIndex > INDEX_NOT_FOUND) {
			newFilename = newFilename.substring(0, extIndex);
		}
		return newFilename;
	}
	
	private void onEntryOpen() {
		var item = getSelectionModel().getSelectedItem();
		if (item.isLeaf()) {
			try {
				mapEditor.setMap(mapSaveLoad.load(item.getValue().toString(), false));
			} catch (AssetNotFoundException| AssetFormatException e) {
				LogHandler.log("Failure with the assets", Level.SEVERE);;
			}
		}
	}
}
