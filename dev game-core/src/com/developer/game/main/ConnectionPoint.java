package com.developer.game.main;

import java.util.ArrayList;
import java.util.List;

public class ConnectionPoint {
	private int x, y;
	private List<Element> elements;
	private Building building;

	public ConnectionPoint(int x, int y) {
		this.elements = new ArrayList<Element>();
		this.x = x;
		this.y = y;
	}

	public ConnectionPoint(int x, int y, Building building) {
		this.elements = new ArrayList<Element>();
		this.x = x;
		this.y = y;
		this.building = building;
	}

	public List<Element> getElements() {
		return elements;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Boolean isConnectedWith(ConnectionPoint connectionPoint) {
		for (Element e : elements) {
			if (connectionPoint.getElements().contains(e))
				return true;
		}
		return false;
	}

	public static Boolean areConnected(List<ConnectionPoint> points) {
		if (points.size() == 0)
			throw new RuntimeException("No points given");
		else {
			for (ConnectionPoint p1 : points) {
				int connections = 0;
				for (ConnectionPoint p2 : points) {
					if (connections >= 2)
						break;
					else if (p1 != p2 && p1.isConnectedWith(p2))
						connections++;
				}
				if (connections < 2)
					break;
				else if (points.indexOf(p1) + 1 >= points.size())
					return true;
			}

			return false;

			/*
			 * if (points.stream().filter(p -> points.stream().filter(po ->
			 * po.isConnectedWith(p) && po != p).count() == 2) .count() ==
			 * points.size()) return true; else return false;
			 */
		}
	}

	public Boolean isNear(ConnectionPoint p1) {
		if (Math.abs(x - p1.getX()) <= 1 && Math.abs(y - p1.getY()) <= 1)
			return true;
		else
			return false;
	}

	public Building getBuilding() {
		return this.building;
	}

	public Element connectTo(ConnectionPoint secondPoint) {
		if (this.elements.isEmpty() && secondPoint.getElements().isEmpty())
			throw new RuntimeException("Can't build in air!");

		return Element.create(this, secondPoint);
	}

	public int getDistanceTo(ConnectionPoint secondPoint) {
		return (int) Math
				.sqrt(Math.pow(this.getX() - secondPoint.getX(), 2) + Math.pow(this.getY() - secondPoint.getY(), 2));
	}

	public int getDistanceToDrawable(ConnectionPoint secondPoint) {
		return (int) Math.sqrt(Math.pow(this.getX() - secondPoint.getDrawableX(), 2)
				+ Math.pow(this.getY() - secondPoint.getDrawableY(), 2));
	}

	public int getDrawableX() {
		return this.x * 200;
	}

	public int getDrawableY() {
		return this.y * 200;
	}
}
