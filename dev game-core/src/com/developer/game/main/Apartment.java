package com.developer.game.main;

import java.util.ArrayList;
import java.util.List;

public class Apartment {
	private List<Room> rooms;
	private TYPE type;
	private Tenant tenant;

	public static enum TYPE {
		Flat("Flat"), Office("Office"), Shop("Shop"), Stockroom("Stockroom");

		private String name;

		private TYPE(String name) {
			this.name = name;
		}

	}

	private Apartment(Apartment.TYPE type, List<Room> rooms) {
		this.rooms = new ArrayList<Room>(rooms);
		this.type = type;
		for (Room r : rooms)
			r.setApartment(this);
	}

	public static Apartment create(Apartment.TYPE type, List<Room> rooms) {
		for (Room r : rooms)
			if (r == null)
				throw new RuntimeException("Room is null");

		return new Apartment(type, rooms);
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void destroy() {
		for(Room r: this.rooms)
			r.setApartment(null);
	}

	public Tenant getTenant() {
		
		return this.tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public TYPE getType() {
		return this.type;
	}

}
