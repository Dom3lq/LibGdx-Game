package com.developer.game.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Building implements Runnable {
	private int width;
	private int height;
	private ConnectionPoint[][] connectionPoints;
	private Player owner;
	private int price;
	private List<Worker> workers;
	private List<Room> rooms;
	private List<Tenant> willingTenants;
	private boolean isBeingPlayed;
	private Float placeValue = 1f;
	private Permissions permissions;

	public Building(int width, int height) {
		this.workers = new ArrayList<Worker>();
		this.willingTenants = new ArrayList<Tenant>();
		this.width = width;
		this.height = height;
		connectionPoints = new ConnectionPoint[width + 1][height + 1];
		rooms = new ArrayList<Room>();

		for (int i = 0; i < width + 1; i++) {
			for (int j = 0; j < height + 1; j++) {
				connectionPoints[i][j] = new ConnectionPoint(i, j, this);
				if (j == 0 && i > 0) {
					Element.createGrass(connectionPoints[i - 1][j], connectionPoints[i][j]).build(100);
				}

			}
		}
	}

	public static Building create(int size, int max_height, Permissions permissions) {
		int height;
		if(permissions.max_height > max_height)
			height = max_height;
		else
			height = permissions.max_height;
		
		return new Building(size, height);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ConnectionPoint[][] getConnectionPoints() {
		return connectionPoints;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player player) {
		this.owner = player;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getPrice() {
		return price;
	}

	public List<Worker> getWorkers() {
		return this.workers;
	}

	public List<Element> getElements() {
		List<Element> elements = new ArrayList<Element>();

		for (int i = 0; i < this.width; i++)
			for (int j = 0; j < this.height; j++)
				for (Element e : this.getConnectionPoints()[i][j].getElements())
					if (!elements.contains(e))
						elements.add(e);

		return elements;
	}

	public Element getElement(int x, int y) {

		for (Element e : getElements())
			if (e.getX() == x && e.getY() == y)
				return e;

		return null;

	}

	public Worker hireNewWorker() {
		Worker worker = new Worker(this.getElement(0, 0));
		this.owner.setCash(this.owner.getCash() - getNewWorkerPrice());
		this.workers.add(worker);
		return worker;
	}

	public void hireNewWorker(Worker worker) {
		this.owner.setCash(this.owner.getCash() - getNewWorkerPrice());
		this.workers.add(worker);
	}

	public int getNewWorkerPrice() {
		return 50 + this.workers.size() * 50;
	}

	public List<Element> getElementsToBuild() {
		List<Element> elementsToBuild = new ArrayList<Element>();

		for (Element e : getElements())
			if (e.getCondition() < 100)
				elementsToBuild.add(e);

		return elementsToBuild;
	}

	public Worker hireAndRunNewWorker() {
		Worker worker = this.hireNewWorker();
		worker.work();
		return worker;
	}

	public List<Room> getRooms() {
		return this.rooms;
	}

	public List<Tenant> getWillingTenants() {
		return this.willingTenants;
	}

	public Tenant addTenant() {
		Random rand = new Random();

		Apartment.TYPE apartmentType = null;

		int aType = rand.nextInt(4) + 1;
		switch (aType) {
		case 1:
			apartmentType = Apartment.TYPE.Stockroom;
			break;
		case 2:
			apartmentType = Apartment.TYPE.Flat;
			break;
		case 3:
			apartmentType = Apartment.TYPE.Shop;
			break;
		case 4:
			apartmentType = Apartment.TYPE.Office;
			break;
		}

		List<Room.TYPE> roomTypes = new ArrayList<Room.TYPE>();

		int roomsNumber = rand.nextInt(5) + 1;
		for (int i = 0; i < roomsNumber; i++) {
			int rType = rand.nextInt(5);
			switch (rType) {
			case 0:
				roomTypes.add(Room.TYPE.Garage);
				break;
			case 1:
				roomTypes.add(Room.TYPE.Kitchen);
				break;
			case 2:
				roomTypes.add(Room.TYPE.Room);
				break;
			case 3:
				roomTypes.add(Room.TYPE.Storeroom);
				break;
			case 4:
				roomTypes.add(Room.TYPE.Toilet);
				break;
			}
		}

		int rent = aType * 10;
		for (Room.TYPE rt : roomTypes)
			rent = rent + (rt.getPrice() / 5);

		rent = (int) (rent * placeValue);
		Tenant tenant = new Tenant(apartmentType, roomTypes, rent);
		this.willingTenants.add(tenant);
		return tenant;

	}

	public boolean isBeingPlayed() {
		return isBeingPlayed;
	}

	public void setBeingPlayed(boolean isBeingPlayed) {
		this.isBeingPlayed = isBeingPlayed;
	}

	@Override
	public void run() {
		this.isBeingPlayed = true;

		while (this.isBeingPlayed) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (this.willingTenants.size() < 5)
				this.addTenant();
		}
	}
}
