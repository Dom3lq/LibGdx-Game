package com.developer.game.main;

import java.util.ArrayList;
import java.util.List;

import com.developer.game.main.Room.TYPE;

import gdx.CircleButton;

public class Tenant {

	private Apartment apartment;
	private Apartment.TYPE expectedApartmentType;
	private List<TYPE> expectedRoomsTypes;
	private CircleButton button;
	int rent;

	public Tenant(Apartment.TYPE expectedApartmentType, List<Room.TYPE> expectedRoomsTypes, int rent) {
		this.expectedApartmentType = expectedApartmentType;
		this.expectedRoomsTypes = expectedRoomsTypes;
		this.rent = rent;
	}

	public void setButton(CircleButton button) {
		this.button = button;
	}

	public void assignTo(Apartment apartment) {
		if (apartment.getType() != this.getExpectedApartmentType())
			throw new RuntimeException("Wrong apartment type.");

		List<Room.TYPE> expectedRoomsTypes = new ArrayList<Room.TYPE>(this.getExpectedRoomsTypes());

		for (Room r : apartment.getRooms()) {
			for (Room.TYPE ert : expectedRoomsTypes) {
				if (r.getType().equals(ert)) {
					expectedRoomsTypes.remove(ert);
					break;
				}
			}
		}

		if (expectedRoomsTypes.size() > 0)
			throw new RuntimeException("Apartment has no enaugh expected rooms.");

		if (apartment.getTenant() != null)
			throw new RuntimeException("Apartment is already owned.");

		if (!apartment.getRooms().get(0).getBuilding().getWillingTenants().contains(this))
			throw new RuntimeException("This tenant doesnt want an apartment in this building.");

		apartment.getRooms().get(0).getBuilding().getWillingTenants().remove(this);
		this.apartment = apartment;
		apartment.setTenant(this);
	}

	public Apartment getApartment() {
		return this.apartment;
	}

	public void leave() {
		if (this.getButton() != null)
			this.setButton(null);

		if (this.getApartment() != null) {
			this.apartment.setTenant(null);
			this.apartment = null;
		}
	}

	public void payRent() {
		Player owner = this.apartment.getRooms().get(0).getBuilding().getOwner();
		owner.setCash(owner.getCash() + this.rent);
	}

	public CircleButton getButton() {
		return this.button;
	}

	public Apartment.TYPE getExpectedApartmentType() {
		return expectedApartmentType;
	}

	public List<TYPE> getExpectedRoomsTypes() {
		return expectedRoomsTypes;
	}
}
