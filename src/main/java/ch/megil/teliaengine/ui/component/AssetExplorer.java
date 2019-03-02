package ch.megil.teliaengine.ui.component;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import ch.megil.teliaengine.file.IconLoader;
import ch.megil.teliaengine.file.exception.AssetNotFoundException;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AssetExplorer extends TreeItem<String>{
	public static Image folderCollapseImage;
	public static Image folderExpandImage;
	public static Image fileImage;
	
	//stores the full path to the file or directory
	private String fullPath;
	private boolean isDirectory;
	
	@SuppressWarnings("unchecked")
	public AssetExplorer(Path file) {
		super(file.toString());
		this.fullPath = file.toString();
		
		try {
			folderCollapseImage = new IconLoader().load("folderImage", 32, 32);
			folderExpandImage = new IconLoader().load("folderExpandIcon", 32, 32);
			fileImage = new IconLoader().load("textIcon", 32, 32);
		}catch(AssetNotFoundException x){
			x.printStackTrace();
		}
		
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
		
		this.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
			@Override
			public void handle(Event e) {
				AssetExplorer source = (AssetExplorer)e.getSource();
				if(source.isDirectory && source.isExpanded()) {
					ImageView iv = (ImageView)source.getGraphic();
					iv.setImage(folderExpandImage);
				}
				try {
					if(source.getChildren().isEmpty()) {
						Path path = Paths.get(source.getFullPath());
						BasicFileAttributes attribs=Files.readAttributes(path,BasicFileAttributes.class);
						if(attribs.isDirectory()) {
							DirectoryStream<Path> dir=Files.newDirectoryStream(path);
							for(Path file:dir) {
								AssetExplorer treeNode = new AssetExplorer(file);
								source.getChildren().add(treeNode);
							}
						}
					}else {
						
					}
				}catch(IOException x) {
					x.printStackTrace();
				}
			}
		});
		
		this.addEventHandler(TreeItem.branchCollapsedEvent(), new EventHandler() {
			@Override
			public void handle(Event e) {
				AssetExplorer source = (AssetExplorer)e.getSource();
				if(source.isDirectory && !source.isExpanded()) {
					ImageView iv = (ImageView)source.getGraphic();
					iv.setImage(folderCollapseImage);
				}
			}
		});
		
	}
	
	
	public String getFullPath() {
		return this.fullPath;
	}
	
	public Boolean getIsDirectory() {
		return this.isDirectory;
	}
}
