package ch.megil.teliaengine.ui.component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import ch.megil.teliaengine.file.IconLoader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFXFileBrowseDemoApp extends Application{
	  private TreeView<String> treeView;
	   
	  public static void main(String[] args){
	    launch(args);
	  }

	@Override
	public void start(Stage primaryStage) throws Exception {
		//create tree pane
	    VBox treeBox=new VBox();
	    treeBox.setPadding(new Insets(10,10,10,10));
	    treeBox.setSpacing(10);
	  //setup the file browser root
	    String hostName="computer";
	    try{
	    	hostName=InetAddress.getLocalHost().getHostName();}
	    catch(UnknownHostException x){}
	    TreeItem<String> rootNode = new TreeItem<>(hostName,new ImageView(new IconLoader().load("computerIcon", 32, 32)));
	    Iterable<Path> rootDirectories=FileSystems.getDefault().getRootDirectories();
	    for(Path name:rootDirectories){
	      AssetExplorer treeNode=new AssetExplorer(name);
	      rootNode.getChildren().add(treeNode);
	    }
	    rootNode.setExpanded(true);
	  //create the tree view
	    treeView=new TreeView<>(rootNode);
	    //add everything to the tree pane
	    treeBox.getChildren().addAll(new Label("File browser"),treeView);
	    VBox.setVgrow(treeView,Priority.ALWAYS);
	     
	    //setup and show the window
	    primaryStage.setTitle("JavaFX File Browse Demo");
	    StackPane root=new StackPane();
	    root.getChildren().addAll(treeBox);
	    primaryStage.setScene(new Scene(root,400,300));
	    primaryStage.show();
	}
}
