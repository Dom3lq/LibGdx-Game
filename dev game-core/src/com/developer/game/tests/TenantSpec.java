package com.developer.game.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.developer.game.main.Apartment;
import com.developer.game.main.Building;
import com.developer.game.main.Element;
import com.developer.game.main.Player;
import com.developer.game.main.Room;
import com.developer.game.main.Tenant;

public class TenantSpec {

	Building building;
	Apartment apartment;
	Tenant tenant;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setupBefore() {
		Player player = new Player("player");
		player.setCash(500);
		
		building = new Building(10, 10);
		
		building.setPrice(100);
		player.buyBuilding(building);
		
		tenant = building.addTenant();

		Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]).build(100);
		Element.create(building.getConnectionPoints()[2][0], building.getConnectionPoints()[2][1]).build(100);
		Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][1]).build(100);
		Room room = Room.create(Room.TYPE.Room,
				Arrays.asList(building.getConnectionPoints()[1][0], building.getConnectionPoints()[2][0],
						building.getConnectionPoints()[2][1], building.getConnectionPoints()[1][1]));
		apartment = Apartment.create(Apartment.TYPE.Flat, Arrays.asList(room));
	}

	@Test
	public void whenAssignToApartmentAndGetApartmentThenReturnThisApartment() {
		tenant.assignTo(apartment);
		assertThat(tenant.getApartment()).isEqualTo(apartment);
	}

	@Test
	public void whenAssignToApartmentFromOtherBuildingThenThrowRuntimeException() {
		Building otherBuilding = new Building(10, 10);
		Element.create(otherBuilding.getConnectionPoints()[1][0], otherBuilding.getConnectionPoints()[1][1]).build(100);
		Element.create(otherBuilding.getConnectionPoints()[2][0], otherBuilding.getConnectionPoints()[2][1]).build(100);
		Element.create(otherBuilding.getConnectionPoints()[1][1], otherBuilding.getConnectionPoints()[2][1]).build(100);
		Room room = Room.create(Room.TYPE.Room,
				Arrays.asList(otherBuilding.getConnectionPoints()[1][0], otherBuilding.getConnectionPoints()[2][0],
						otherBuilding.getConnectionPoints()[2][1], otherBuilding.getConnectionPoints()[1][1]));
		Apartment otherApartment = Apartment.create(Apartment.TYPE.Flat, Arrays.asList(room));

		exception.expect(RuntimeException.class);
		tenant.assignTo(otherApartment);
	}

	@Test
	public void whenAssignToOwnedApartmentThenThrowRuntimeException() {
		tenant.assignTo(apartment);
		Tenant secondTenant = building.addTenant();

		exception.expect(RuntimeException.class);
		secondTenant.assignTo(apartment);
	}

	@Test
	public void givenTenantWantingOfficeWithOneRoomAndOneToiletWhenAssignToFlatWithOneRoomThrowRuntimeException() {
		Tenant tenant = new Tenant(Apartment.TYPE.Office,
				new ArrayList<Room.TYPE>(Arrays.asList(Room.TYPE.Room, Room.TYPE.Toilet)), 100);

		building.getWillingTenants().add(tenant);

		exception.expect(RuntimeException.class);
		tenant.assignTo(apartment);
	}

	@Test
	public void givenTenantWantingFlatWithOneRoomAndOneToiletWhenAssignToFlatWithOneRoomThrowRuntimeException() {
		Tenant tenant = new Tenant(Apartment.TYPE.Flat,
				new ArrayList<Room.TYPE>(Arrays.asList(Room.TYPE.Room, Room.TYPE.Toilet)), 100);

		building.getWillingTenants().add(tenant);

		exception.expect(RuntimeException.class);
		tenant.assignTo(apartment);
	}

	@Test
	public void givenTenantWantingFlatWithOneRoomWhenAssignToFlatWithOneRoomAndGetTenantApartmentThenReturnApartment() {
		Tenant tenant = new Tenant(Apartment.TYPE.Flat, new ArrayList<Room.TYPE>(Arrays.asList(Room.TYPE.Room)), 100);

		building.getWillingTenants().add(tenant);

		tenant.assignTo(apartment);

		assertThat(tenant.getApartment().equals(apartment));
	}

	@Test
	public void givenTenantWithApartmentWhenTenantLeaveAndGetApartmentTenantThenReturnNull() {
		tenant.assignTo(apartment);
		tenant.leave();
	}

	@Test
	public void givenTenantWithApartmentInBuildingWhenTenantPayRentThenBuildingOwnerCashIsHigherByTenantRent() {
		int before = building.getOwner().getCash();

		int rent = 100;
		Tenant tenant = new Tenant(Apartment.TYPE.Flat, new ArrayList<Room.TYPE>(Arrays.asList(Room.TYPE.Room)), rent);
		building.getWillingTenants().add(tenant);
		tenant.assignTo(apartment);
		tenant.payRent();

		assertThat(building.getOwner().getCash()).isEqualTo((before + rent));

	}
}
