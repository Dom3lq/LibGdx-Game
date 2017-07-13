package com.developer.game.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.developer.game.main.Apartment;
import com.developer.game.main.Building;
import com.developer.game.main.ConnectionPoint;
import com.developer.game.main.Element;
import com.developer.game.main.Room;

public class ApartmentSpec {

	private Building building;
	private Room roomOne;
	private Room roomTwo;
	private Room nullRoom;


	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setupBefore() {
		building = new Building(10, 10);
		ConnectionPoint pointOne = building.getConnectionPoints()[1][1];
		ConnectionPoint pointTwo = building.getConnectionPoints()[2][1];
		ConnectionPoint pointThree = building.getConnectionPoints()[2][2];
		ConnectionPoint pointFour = building.getConnectionPoints()[1][2];
		ConnectionPoint pointFive = building.getConnectionPoints()[2][1];
		ConnectionPoint pointSix = building.getConnectionPoints()[3][1];
		ConnectionPoint pointSeven = building.getConnectionPoints()[3][2];
		ConnectionPoint pointEight = building.getConnectionPoints()[2][2];

		Element.create(pointOne, pointTwo);
		Element.create(pointTwo, pointThree);
		Element.create(pointThree, pointFour);
		Element.create(pointFour, pointOne);

		roomOne = Room.create(Room.TYPE.Room, Arrays.asList(pointOne, pointTwo, pointThree, pointFour));

		Element.create(pointFive, pointSix);
		Element.create(pointSix, pointSeven);
		Element.create(pointSeven, pointEight);

		roomTwo = Room.create(Room.TYPE.Room, Arrays.asList(pointFive, pointSix, pointSeven, pointEight));
	}
	
	@Test
	public void whenApartmentBuildWithNullRoomThenReturnRuntimeException () {
		exception.expect(RuntimeException.class);
		Apartment.create(Apartment.TYPE.Flat, Arrays.asList(nullRoom));
	}
	
	@Test
	public void whenApartmentBuildWithRoomsThenReturnNewApartment() {
		Apartment apartment = Apartment.create(Apartment.TYPE.Flat, Arrays.asList(roomOne, roomTwo));
		assertThat(apartment).isNotNull();
	}

	@Test
	public void whenApartmentBuildWithRoomsThenApartmentRoomsListGotTheseRooms() {
		Apartment apartment = Apartment.create(Apartment.TYPE.Flat, Arrays.asList(roomOne, roomTwo));
		assertThat(apartment.getRooms()).contains(roomOne, roomTwo);
	}
	
	@Test
	public void whenApartmentBuildWithRoomsAndRoomsGetApartmentThenReturnThisApartment() {
		Apartment apartment = Apartment.create(Apartment.TYPE.Flat, Arrays.asList(roomOne, roomTwo));
		assertThat(roomOne.getApartment()).isEqualTo(apartment);
	}
	
	@Test
	public void givenApartmentWhenDestroyAndItsRoomGetApartmentThenReturnNull() {
		Apartment apartment = Apartment.create(Apartment.TYPE.Flat, Arrays.asList(roomOne, roomTwo));
		apartment.destroy();
		assertThat(roomOne.getApartment()).isNull();
	}
}
