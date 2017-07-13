package com.developer.game.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Room {

	private List<Element> elements;
	private List<ConnectionPoint> connectionPoints;
	private Apartment apartment;
	private Building building;
	private TYPE type;

	public static enum TYPE {
		Room("Room", 200), Garage("Garage", 50), Storeroom("Storeroom", 100), Kitchen("Kitchen", 500), Toilet("Toilet",
				300);

		private String name;
		private int price;

		private TYPE(String name, int price) {
			this.name = name;
			this.price = price;
		}

		public int getPrice() {
			return price;
		}

	}

	private ConnectionPoint actualPoint;

	private Room(TYPE type, List<ConnectionPoint> connectionPoints) {

		connectionPoints = new ArrayList<ConnectionPoint>(connectionPoints);

		this.connectionPoints = new ArrayList<>();

		actualPoint = connectionPoints.get(0);
		this.connectionPoints.add(actualPoint);
		connectionPoints.remove(actualPoint);

		while (!connectionPoints.isEmpty()) {

			for (ConnectionPoint p : connectionPoints)
				if (actualPoint.isConnectedWith(p)) {
					actualPoint = p;
					break;
				}

			this.connectionPoints.add(actualPoint);
			connectionPoints.remove(actualPoint);
		}

		List<Element> allElements = new ArrayList<Element>();
		/*
		 * this.connectionPoints.stream().flatMap(cp ->
		 * cp.getElements().stream()).forEach(e -> { if
		 * (!allElements.contains(e)) allElements.add(e); });
		 */

		for (ConnectionPoint p : this.connectionPoints)
			for (Element e : p.getElements())
				if (!allElements.contains(e))
					allElements.add(e);

		elements = new ArrayList<Element>();

		for (Element e : allElements)
			if (this.connectionPoints.containsAll(e.getConnectionPoints()))
				elements.add(e);

		// elements = allElements.stream().filter(e ->
		// this.connectionPoints.containsAll(e.getConnectionPoints()))
		// .collect(Collectors.toList());

		// elements.stream().forEach(e -> e.getRooms().add(this));

		for (Element e : elements)
			e.getRooms().add(this);

		building = this.connectionPoints.get(0).getBuilding();
		building.getRooms().add(this);
		this.type = type;
	}

	public static Room create(TYPE type, List<ConnectionPoint> points) {
		if (ConnectionPoint.areConnected(points))
			return new Room(type, points);
		else
			throw new RuntimeException("PlaceIsNotSurrounded");
	}

	public void destroy() {
		// elements.forEach(e -> e.getRooms().remove(this));

		for (Element e : elements)
			e.getRooms().remove(this);

		building.getRooms().remove(this);
	}

	public Apartment getApartment() {
		return this.apartment;
	}

	public void setApartment(Apartment apartment) {
		this.apartment = apartment;
	}

	public Boolean contains(ConnectionPoint p) {
		Array<Vector2> polygon = new Array<Vector2>();
		// this.connectionPoints.stream().forEach(cp -> polygon.add(new
		// Vector2(cp.getDrawableX(), cp.getDrawableY())));
		for (ConnectionPoint cp : this.connectionPoints)
			polygon.add(new Vector2(cp.getDrawableX(), cp.getDrawableY()));

		return Intersector.isPointInPolygon(polygon, new Vector2(p.getX(), p.getY()));
	}

	public Building getBuilding() {
		return this.building;
	}

	public Boolean contains(Vector3 touchPoint) {
		return contains(new ConnectionPoint((int) touchPoint.x, (int) touchPoint.y));
	}

	public List<ConnectionPoint> getConnectionPoints() {
		return this.connectionPoints;
	}

	public void connectTo(Room secondRoom) {
		if (this.apartment != null) {
			if (secondRoom.getApartment() != null) {
				secondRoom.getApartment().getRooms().remove(secondRoom);
			}
			this.apartment.getRooms().add(secondRoom);
			secondRoom.setApartment(this.apartment);
		} else if (secondRoom.getApartment() != null) {
			secondRoom.getApartment().getRooms().add(this);
			this.setApartment(secondRoom.getApartment());
		} else {
			Apartment.create(Apartment.TYPE.Flat, Arrays.asList(this, secondRoom));
		}
	}

	public TYPE getType() {
		return this.type;
	}

}
