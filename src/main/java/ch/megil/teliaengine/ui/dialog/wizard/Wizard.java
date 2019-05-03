package ch.megil.teliaengine.ui.dialog.wizard;

import java.util.function.Supplier;

import javafx.beans.value.ObservableValue;
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
		currentPage = currentPage.getPrevious();
		nextButton.setDisable(false);
		onPageChange();
		event.consume();
	}
	
	private void nextPage(ActionEvent event) {
		currentPage = currentPage.getNext();
		nextButton.setDisable(currentPage.getNextState());
		finishButton.setDisable(currentPage.getNextState());
		onPageChange();
		event.consume();
	}
	
	private void onPageChange() {
		getDialogPane().setContent(currentPage.getPage());
		
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
	
	public void addPage(Pane pageContent, Supplier<Boolean> nextDisableCheck) {
		var newPage = new WizardPage(pageContent, nextDisableCheck);
		addPage(newPage);
	}
	
	public void addPage(Pane pageContent) {
		var newPage = new WizardPage(pageContent, false);
		addPage(newPage);
	}
	
	private void addPage(WizardPage page) {
		if (currentPage == null) {
			currentPage = page;
			lastPage = page;

			nextButton.setDisable(currentPage.getNextState());
			finishButton.setDisable(currentPage.getNextState());
		} else if (lastPage != null) {
			page.setPrevious(lastPage);
			lastPage.setNext(page);
			lastPage = page;
		}
		onPageChange();
	}
	
	public <S> void doNextPageCheckListener(ObservableValue<? extends S> obs, S oldVal, S newVal) {
		doNextPageCheck();
	}
	
	public void doNextPageCheck() {
		nextButton.setDisable(currentPage.getNextState());
		finishButton.setDisable(currentPage.getNextState());
	}
}
