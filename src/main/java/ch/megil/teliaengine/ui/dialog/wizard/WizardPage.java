package ch.megil.teliaengine.ui.dialog.wizard;

import java.util.function.Supplier;

import javafx.scene.layout.Pane;

class WizardPage {
	private Pane page;
	private WizardPage previous;
	private WizardPage next;
	private Supplier<Boolean> nextStateCheck;
	
	public WizardPage(Pane page, boolean defaultNextState) {
		this(page, () -> defaultNextState);
	}
	
	public WizardPage(Pane page, Supplier<Boolean> nextStateCheck) {
		this.page = page;
		this.nextStateCheck = nextStateCheck;
	}
	
	public WizardPage getPrevious() {
		return previous;
	}
	
	public void setPrevious(WizardPage previous) {
		this.previous = previous;
	}
	
	public WizardPage getNext() {
		return next;
	}
	
	public void setNext(WizardPage next) {
		this.next = next;
	}
	
	public Pane getPage() {
		return page;
	}
	
	public boolean getNextState() {
		return nextStateCheck.get();
	}
}
