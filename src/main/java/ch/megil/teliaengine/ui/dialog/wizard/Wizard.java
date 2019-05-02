package ch.megil.teliaengine.ui.dialog.wizard;

import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;

public class Wizard<T> extends Dialog<T> {
	private WizardPage currentPage;
	private WizardPage lastPage;
	
	private Node previousButton;
	private Node nextButton;
	private Node finishButton;
	
	public Wizard() {
		getDialogPane().getButtonTypes().setAll(ButtonType.PREVIOUS, ButtonType.NEXT, ButtonType.FINISH, ButtonType.CANCEL);
		
		previousButton = getDialogPane().lookupButton(ButtonType.PREVIOUS);
		previousButton.addEventFilter(ActionEvent.ACTION, this::previousPage);
		nextButton = getDialogPane().lookupButton(ButtonType.NEXT);
		nextButton.addEventFilter(ActionEvent.ACTION, this::nextPage);
		finishButton = getDialogPane().lookupButton(ButtonType.FINISH);
	}
	
	private void previousPage(ActionEvent event) {
		nextButton.setDisable(false);
		onPageChange();
		event.consume();
	}
	
	private void nextPage(ActionEvent event) {
		nextButton.setDisable(currentPage.getNextState());
		onPageChange();
		event.consume();
	}
	
	private void onPageChange() {
		if (currentPage.getPrevious() == null) {
			previousButton.setDisable(true);
		} else {
			previousButton.setDisable(false);
		}
		
		if (currentPage.getNext() == null) {
			nextButton.setVisible(false);
			finishButton.setVisible(true);
		} else {
			nextButton.setVisible(true);
			finishButton.setVisible(false);
		}
	}
	
	public void addPage(Pane pageContent, Supplier<Boolean> nextStateCheck) {
		var newPage = new WizardPage(pageContent, nextStateCheck);
		addPage(newPage);
	}
	
	public void addPage(Pane pageContent, boolean nextState) {
		var newPage = new WizardPage(pageContent, nextState);
		addPage(newPage);
	}
	
	private void addPage(WizardPage page) {
		if (currentPage == null) {
			currentPage = page;
		}
		if (lastPage != null) {
			page.setPrevious(lastPage);
			lastPage.setNext(page);
			lastPage = page;
		}
		onPageChange();
	}
	
	public void doNextPageCheck() {
		nextButton.setDisable(currentPage.getNextState());
	}
}
