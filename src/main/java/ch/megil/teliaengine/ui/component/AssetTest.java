package ch.megil.teliaengine.ui.component;

import java.io.File;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class AssetTest extends TreeView<String> {
	public AssetTest(String rootPath) {
		var root = new File(rootPath);
		
		//TODO: add graphic
		var treeRoot = new TreeItem<>(root.getName(), null);
		treeRoot.setExpanded(true);
		
		//TODO: make directory check to prevent NPE
		for (var child : root.listFiles()) {
			addNewTreeEntryFile(treeRoot, child);
		}
		
		this.setRoot(treeRoot);
	}
	
	private void addNewTreeEntryFile(TreeItem<String> parent, File file) {
		//TODO: insert graphic
		var treeItem = new TreeItem<>(file.getName());
		
		if (file.isDirectory()) {
			for (var child : file.listFiles()) {
				addNewTreeEntryFile(treeItem, child);
			}
		}
		parent.getChildren().add(treeItem);
	}
}
