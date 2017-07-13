package com.developer.game.main;

public class Waypoint {
	private Element element;
	private Waypoint parent;
	
	public Waypoint(Element element){
		this.element = element;
	}
	
	public Waypoint getParent() {
		return parent;
	}
	public void setParent(Waypoint parent) {
		this.parent = parent;
	}
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}
}
