package com.developer.game.main;

import com.badlogic.gdx.graphics.Texture;

public class CityMap {

	private Texture texture;
	private Player player;
	private Field[][] fields;
	private final int MAP_WIDTH, MAP_LENGTH, MAX_HEIGHT;
	public final static int METROPOLIS = 20, BIG_CITY = 15, MEDIUM_CITY = 12, SMALL_CITY = 8, TOWN = 5, VILLAGE = 3;

	public CityMap(String playerName, int width, int length, int height) {
		player = new Player(playerName);
		fields = new Field[20][20];

		MAP_WIDTH = width;
		MAP_LENGTH = length;
		MAX_HEIGHT = height;

		for (int i = 0; i < MAP_WIDTH; i++)
			for (int j = 0; j < MAP_LENGTH; j++) {
				fields[i][j] = new Field(MAX_HEIGHT);
			}
	}

	public Field[][] getFields() {
		return fields;
	}

	public Player getPlayer() {
		return player;
	}
}
