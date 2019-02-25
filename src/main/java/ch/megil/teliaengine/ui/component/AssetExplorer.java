package ch.megil.teliaengine.ui.component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AssetExplorer extends TreeItem<String>{
	public static Image folderCollapseImage=new Image("/icons/folderImage.png");
	public static Image folderExpandImage=new Image("/icons/folderExpandIcon.png");
	public static Image fileImage=new Image("/icons/textIcon.png");
	
	//stores the full path to the file or directory
	private String fullPath;
	private boolean isDirectory;
	
	public AssetExplorer(Path file) {
		super(file.toString());
		this.fullPath = file.toString();
		
		//test if this is a directory and set icon
		if(Files.isDirectory(file)) {
			this.isDirectory = true;
			this.setGraphic(new ImageView(folderCollapseImage));
		}else {
			this.isDirectory = false;
			this.setGraphic(new ImageView(fileImage));
		}
		
		//set the value
		if(!fullPath.endsWith(File.separator)) {
			//set the value (which is what is displayed in the tree)
			String value = file.toString();
			int indexOf = value.lastIndexOf(File.separator);
			if(indexOf > 0) {
				this.setValue(value.substring(indexOf+1));
			}else {
				this.setValue(value);
			}
		}
	}
	
	
	
	public String getFullPath() {
		return this.fullPath;
	}
	
	public Boolean getIsDirectory() {
		return this.isDirectory;
	}
}
