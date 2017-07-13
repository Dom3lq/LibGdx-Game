package com.developer.game.main;

import java.util.Random;

public class Field {
	private int SIZE, MAX_HEIGHT, price;
	private Player owner;
	private Player attemptingPlayer;
	private Building building;

	public Field(int maxHeight) {
		this.MAX_HEIGHT = maxHeight;

		Random rand = new Random();
		SIZE = rand.nextInt(10) + 5;
		price = SIZE * 1000 * MAX_HEIGHT;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Building getBuilding() {
		return building;
	}

	public void createBuilding(Permissions permissions) {
		this.building = Building.create(SIZE, MAX_HEIGHT, permissions);
	}

}
