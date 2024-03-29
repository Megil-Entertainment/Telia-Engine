package ch.megil.teliaengine.ui.component;

import java.io.File;
import java.util.function.Consumer;

import ch.megil.teliaengine.configuration.FileConfiguration;
import ch.megil.teliaengine.configuration.IconConfiguration;
import ch.megil.teliaengine.file.IconFileManager;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import ch.megil.teliaengine.helper.NamedValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class AssetExplorer extends TreeView<NamedValue<File>>{
	private int INDEX_NOT_FOUND = 0;
	private int DOUBLE_CLICK = 2;
	
	private Consumer<String> onMapLoad;
	
	public void initialize(Consumer<String> onMapLoad) {
		this.onMapLoad = onMapLoad;
		
		this.setShowRoot(false);
		
		setOnMouseClicked(me -> {if (me.getClickCount() == DOUBLE_CLICK) onEntryOpen();});
		setOnKeyTyped(ke -> {if (ke.getCode() == KeyCode.ENTER) onEntryOpen();});
	}
	
	public void changeRoots(String... rootPaths) throws AssetNotFoundException {
		var treeRoot = new TreeItem<NamedValue<File>>(null);
		for (var rootPath : rootPaths) {
			var root = new File(rootPath);
			
			var pseudoRoot = new TreeItem<>(new NamedValue<>(root.getName(), root));
			pseudoRoot.setGraphic(new ImageView(IconFileManager.get().load(IconConfiguration.FOLDER_ICON.getConfiguration(), 32, 32)));
			
			treeRoot.getChildren().add(pseudoRoot);
		}
		
		this.setRoot(treeRoot);
		
		reload();
	}
	
	public void reload() throws AssetNotFoundException {
		for (var pseudoRoot : this.getRoot().getChildren()) {
			var file = pseudoRoot.getValue().getValue();
			if (file.isDirectory()) {
				pseudoRoot.getChildren().clear();
				for (var child : file.listFiles()) {
					addNewTreeEntryFile(pseudoRoot, child);
				}
			}
		}
	}
	
	private void addNewTreeEntryFile(TreeItem<NamedValue<File>> parent, File file) throws AssetNotFoundException {
		var treeItem = new TreeItem<>(new NamedValue<>(stripFilename(file.getName()), file));
		if (file.isDirectory()) {
			treeItem.setGraphic(new ImageView(IconFileManager.get().load(IconConfiguration.FOLDER_ICON.getConfiguration(), 32, 32)));
			for (var child : file.listFiles()) {
				addNewTreeEntryFile(treeItem, child);
			}
			if (treeItem.getChildren().isEmpty()) {
				return;
			}
		} else {
			if (!checkAllowedExtensions(file.getName())) {
				return;
			}
			treeItem.setGraphic(new ImageView(IconFileManager.get().load(IconConfiguration.FILE_ICON.getConfiguration(), 32, 32)));
		}
		parent.getChildren().add(treeItem);
	}
	
	private boolean checkAllowedExtensions(String filename) {
		return filename.endsWith(FileConfiguration.FILE_EXT_MAP.getConfiguration());
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
