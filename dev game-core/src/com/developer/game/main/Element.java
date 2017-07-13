package com.developer.game.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Element {

	private List<ConnectionPoint> connectionPoints;
	private int condition = 0;
	private List<Room> rooms;
	private boolean ladder = false;
	private TYPE type = TYPE.SCAFFOLDING;
	private TYPE desiredType;
	private boolean doors = false;

	protected Element(List<ConnectionPoint> connectionPoints, TYPE type) {
		rooms = new ArrayList<Room>();
		Collections.sort(connectionPoints, new Comparator<ConnectionPoint>() {
			@Override
			public int compare(ConnectionPoint p1, ConnectionPoint p2) {
				return Integer.compare(p1.getX() + p1.getY(), p2.getX() + p2.getY());
			}
		});

		this.connectionPoints = connectionPoints;

		for (ConnectionPoint cp : connectionPoints)
			if (!cp.getElements().contains(this))
				cp.getElements().add(this);

		if (type != null) {
			this.type = type;
		} else
			this.type = TYPE.SCAFFOLDING;

		Player owner = this.getConnectionPoints().get(0).getBuilding().getOwner();

		if ((owner) != null) {
			owner.setCash(owner.getCash() - this.type.getPrice());
		}
	}

	public static enum TYPE {
		SCAFFOLDING("scaffolding.png", 100), WOOD("wood.jpg", 200), BRICK("brick.jpeg", 300), BETON("beton.jpg",
				400), SLABS("slabs.png",
						500), GRASS("grass.png", 50), ASPHALT("aslphalt.png", 200), LADDER(null, 50), DOORS(null, 50);

		String texture;
		int price;

		private TYPE(String texture, int price) {
			this.texture = texture;
			this.price = price;
		}

		public String getTexture() {
			return this.texture;
		}

		public int getPrice() {
			return this.price;
		}
	}

	public List<ConnectionPoint> getConnectionPoints() {
		return this.connectionPoints;
	}

	public int getCondition() {
		return condition;
	}

	public int build(int i) {

		if (condition + i >= 100) {
			condition = 100;
			if (this.desiredType != null) {
				this.type = this.desiredType;
			}
			this.desiredType = null;
		} else
			condition = condition + i;

		return condition;
	}

	public static Element create(ConnectionPoint first, ConnectionPoint second) {
		if (Math.abs(first.getX() - second.getX()) > 1 || Math.abs(first.getY() - second.getY()) > 1)
			throw new RuntimeException("Element is too long.");

		for (Element e : first.getElements()) {
			if (second.getElements().contains(e))
				throw new RuntimeException("Element: " + first.getX() + first.getY() + "-" + second.getX()
						+ second.getY() + "overlaps element: " + e.getConnectionPoints().get(0).getX()
						+ e.getConnectionPoints().get(0).getY() + "-" + e.getConnectionPoints().get(1).getX()
						+ e.getConnectionPoints().get(1).getY());

		}

		return new Element(Arrays.asList(first, second), null);
	}

	public static Element createGrass(ConnectionPoint first, ConnectionPoint second) {
		for (Element e : first.getElements()) {
			if (second.getElements().contains(e))
				throw new RuntimeException("Element: " + first.getX() + first.getY() + "-" + second.getX()
						+ second.getY() + "overlaps element: " + e.getConnectionPoints().get(0).getX()
						+ e.getConnectionPoints().get(0).getY() + "-" + e.getConnectionPoints().get(1).getX()
						+ e.getConnectionPoints().get(1).getY());

		}

		return new Element(Arrays.asList(first, second), TYPE.GRASS);
	}

	public int hit(int i) {
		condition = condition - i;

		if (condition <= 0)
			destroy();

		return condition;
	}

	public void destroy() {
		condition = 0;

		for (ConnectionPoint p : connectionPoints)
			p.getElements().remove(this);

		List<Room> roomsToBeDeleted = new ArrayList<Room>(rooms);
		for (Room r : roomsToBeDeleted)
			r.destroy();
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public int getX() {
		if (connectionPoints.get(0).getX() <= connectionPoints.get(1).getX())
			return connectionPoints.get(0).getX();
		else
			return connectionPoints.get(1).getX();

	}

	public int getY() {
		if (connectionPoints.get(0).getY() <= connectionPoints.get(1).getY())
			return connectionPoints.get(0).getY();
		else
			return connectionPoints.get(1).getY();
	}

	public Boolean isWalkable() {
		if (connectionPoints.get(0).getY() == connectionPoints.get(1).getY() && this.condition > 0)
			return true;
		else
			return false;
	}

	public boolean isWalkableFrom(Element start) {
		if (isWalkable()) {
			if (getY() == start.getY() && Math.abs(getX() - start.getX()) == 1) {

				for (ConnectionPoint c : connectionPoints)
					if (start.getConnectionPoints().contains(c))
						for (Element e : c.getElements())
							if (e.getY() == getY() && !e.isWalkable() && !e.hasDoors())
								return false;

				return true;

			} else if (getX() == start.getX() && Math.abs(getY() - start.getY()) == 1) {
				if (start.getY() < getY() && start.getLadder())
					return true;
				else if (getLadder())
					return true;
				else
					return false;
			} else
				return false;

		} else
			return false;
	}

	public boolean hasDoors() {
		return doors;
	}

	public int getDistanceTo(Element Element) {
		return Math.abs(Element.getX() - this.getX()) + Math.abs(Element.getY() - this.getY());
	}

	public int getFCost(Element start, Element end) {
		return this.getDistanceTo(start) + this.getDistanceTo(end);
	}

	public void setLadder(boolean ladder) {
		this.ladder = ladder;
	}

	public boolean getLadder() {
		return ladder;
	}

	public List<Element> getElementNeightbours() {
		List<Element> neightbours = new ArrayList<Element>();

		for (Element e : this.getConnectionPoints().get(0).getBuilding().getElements())
			if (e.isWalkableFrom(this))
				neightbours.add(e);

		return neightbours;
	}

	public TYPE getType() {
		return type;
	}

	public void upgradeTo(TYPE type) {
		if (this.condition < 100)
			throw new RuntimeException("Can't upgrade, condition below 100!");

		if (type == TYPE.LADDER) {
			this.createLadder();
		} else if (type == TYPE.DOORS) {
			this.createDoors();
		} else if (this.type == TYPE.SCAFFOLDING || this.type == TYPE.GRASS) {
			this.desiredType = type;
			this.condition = 0;
		} else
			throw new RuntimeException("You can upgrade only scaffoldings or grass!");

		Player owner = this.getConnectionPoints().get(0).getBuilding().getOwner();

		if (owner != null)
			owner.setCash(owner.getCash() - type.getPrice());

	}

	private void createLadder() {
		this.ladder = true;
	}

	public void createDoors() {
		this.doors = true;
	}

	public boolean isReachableByHandFrom(Element element) {
		if ((element.getX() == this.getX() && element.getY() == this.getY())
				|| (connectionPoints.get(0).getY() == connectionPoints.get(1).getY() && element.getX() == this.getX()
						&& (element.getY() + 1) == this.getY())
				|| (getY() == element.getY() && getX() == (element.getX() + 1)
						&& getConnectionPoints().get(0).getX() == getConnectionPoints().get(1).getX()))
			return true;
		else
			return false;
	}

	public static Element getDrawable(Element element) {
		ConnectionPoint first = new ConnectionPoint(element.getConnectionPoints().get(0).getDrawableX(),
				element.getConnectionPoints().get(0).getDrawableY());
		ConnectionPoint second = new ConnectionPoint(element.getConnectionPoints().get(1).getDrawableX(),
				element.getConnectionPoints().get(1).getDrawableY());

		for (Element e : first.getElements()) {
			if (second.getElements().contains(e))
				throw new RuntimeException("Element: " + first.getX() + first.getY() + "-" + second.getX()
						+ second.getY() + "overlaps element: " + e.getConnectionPoints().get(0).getX()
						+ e.getConnectionPoints().get(0).getY() + "-" + e.getConnectionPoints().get(1).getX()
						+ e.getConnectionPoints().get(1).getY());

		}

		return new Element(Arrays.asList(first, second), null);

	}

}
