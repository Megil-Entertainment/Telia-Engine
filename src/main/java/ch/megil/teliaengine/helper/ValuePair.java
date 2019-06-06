package ch.megil.teliaengine.helper;

public class ValuePair<A,B> {
	private A a;
	private B b;
	
	public ValuePair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A getA() {
		return a;
	}
	
	public B getB() {
		return b;
	}
}
