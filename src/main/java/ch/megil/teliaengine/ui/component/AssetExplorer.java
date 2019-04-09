package ch.megil.teliaengine.ui.component;

import java.io.File;
import java.util.function.Consumer;

import ch.megil.teliaengine.configuration.IconConfiguration;
import ch.megil.teliaengine.file.IconLoader;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class AssetExplorer extends TreeView<String>{
	private int INDEX_NOT_FOUND = 0;
	private int DOUBLE_CLICK = 2;
	
	private Consumer<String> onMapLoad;
	
	public void initialize(String rootPath, Consumer<String> onMapLoad) throws AssetNotFoundException {
		var root = new File(rootPath);
		this.onMapLoad = onMapLoad;
		
		var treeRoot = new TreeItem<>(root.getName(), null);
		treeRoot.setGraphic(new ImageView(IconLoader.get().load(IconConfiguration.FOLDER_ICON.getConfiguration(), 32, 32)));
		
		if(root.isDirectory()) {
			for (var child : root.listFiles()) {
				addNewTreeEntryFile(treeRoot, child);
			}
		}
		
		this.setRoot(treeRoot);
		
		setOnMouseClicked(me -> {if (me.getClickCount() == DOUBLE_CLICK) onEntryOpen();});
		setOnKeyTyped(ke -> {if (ke.getCode() == KeyCode.ENTER) onEntryOpen();});
	}
	
	private void addNewTreeEntryFile(TreeItem<String> parent, File file) throws AssetNotFoundException {
		var treeItem = new TreeItem<>(stripFilename(file.getName()));		
		if (file.isDirectory()) {
			treeItem.setGraphic(new ImageView(IconLoader.get().load(IconConfiguration.FOLDER_ICON.getConfiguration(), 32, 32)));
			for (var child : file.listFiles()) {
				addNewTreeEntryFile(treeItem, child);
			}
		} else {
			treeItem.setGraphic(new ImageView(IconLoader.get().load(IconConfiguration.FILE_ICON.getConfiguration(), 32, 32)));
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
			onMapLoad.accept(item.getValue().toString());
		}
	}
}
