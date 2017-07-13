package com.developer.game.main;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private List<Building> buildings;
	private int cash = 0;
	private String name;
	
	public Player (String name) {
		this.name = name;
		this.buildings = new ArrayList<Building>();
	}

	public Building buyBuilding(Building building) {
		if(building.getOwner() != null)
			throw new RuntimeException("Budynek ma juz wlasciciela");
		
		this.buildings.add(building);
		building.setOwner(this);
		this.cash = this.cash - building.getPrice();
		
		return building;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public int getCash() {
		
		return cash;
	}

	public String getName() {
		return this.name;
	}

}
