package ch.megil.teliaengine.ui.component;

import java.io.File;

import ch.megil.teliaengine.file.IconLoader;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class AssetExplorer extends TreeView<String>{	
	public void initialize(String rootPath) throws AssetNotFoundException {
		var root = new File(rootPath);
		
		var treeRoot = new TreeItem<>(root.getName(), null);
		treeRoot.setGraphic(new ImageView(new IconLoader().load("folderExpandIcon", 32, 32)));
		
		if(root.isDirectory()) {
			for (var child : root.listFiles()) {
				addNewTreeEntryFile(treeRoot, child);
			}
		}
		
		this.setRoot(treeRoot);
		
		setOnMouseClicked(me -> {if (me.getClickCount() == 2) onEntryOpen();});
		setOnKeyTyped(ke -> {if (ke.getCode() == KeyCode.ENTER) onEntryOpen();});
	}
	
	private void addNewTreeEntryFile(TreeItem<String> parent, File file) throws AssetNotFoundException {
		var treeItem = new TreeItem<>(stripFilename(file.getName()));		
		if (file.isDirectory()) {
			treeItem.setGraphic(new ImageView(new IconLoader().load("folderExpandIcon", 32, 32)));
			for (var child : file.listFiles()) {
				addNewTreeEntryFile(treeItem, child);
			}
		} else {
			treeItem.setGraphic(new ImageView(new IconLoader().load("textIcon", 32, 32)));
		}
		parent.getChildren().add(treeItem);
	}
	
	private String stripFilename(String filename) {
		var newFilename = filename;
		if(newFilename.contains(".")) {
			int extIndex = newFilename.lastIndexOf(".");
			newFilename = newFilename.substring(0, extIndex);
			System.out.println(newFilename);
		}
		return newFilename;
	}
	
	private void onEntryOpen() {
		var item = getSelectionModel().getSelectedItem();
		
		if (item.isLeaf()) {
		}
	}
}
