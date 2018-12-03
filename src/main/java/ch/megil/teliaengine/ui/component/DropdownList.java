package ch.megil.teliaengine.ui.component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class DropdownList extends VBox{
	private ListView<String> dropDownList;
	
	private static final double LISTVIEW_HEIGHT = 50.0;
	private static final String DELETE = "Delete";
	
	public DropdownList() {
		dropDownList = new ListView<String>();
		dropDownList.getItems().add(DELETE);
		dropDownList.setMaxHeight(LISTVIEW_HEIGHT);
		
		dropDownList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<? super String>) new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			System.out.println("ListView selection changed from oldValue = " 
		                + oldValue + " to newValue = " + newValue);
			}
		});
		
		getChildren().add(dropDownList);
	}
}
