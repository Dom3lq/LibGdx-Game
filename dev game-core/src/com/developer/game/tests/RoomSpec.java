package com.developer.game.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

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

public class RoomSpec {
	Room room;
	Building building;
	ConnectionPoint firstPoint, secondPoint, thirdPoint, fourthPoint, fifthPoint, sixthPoint;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setupBefore() {
		building = new Building(10, 10);
		firstPoint = building.getConnectionPoints()[0][1];
		secondPoint = building.getConnectionPoints()[0][2];
		thirdPoint = building.getConnectionPoints()[1][2];
		fourthPoint = building.getConnectionPoints()[1][1];
		fifthPoint = building.getConnectionPoints()[2][1];
		sixthPoint = building.getConnectionPoints()[2][2];
		Element.create(firstPoint, secondPoint);
		Element.create(secondPoint, thirdPoint);
		Element.create(thirdPoint, fourthPoint);
		Element.create(fourthPoint, firstPoint);
	}

	@Test
	public void whenCreateNewRoomAndPlaceIsNotSurroundedByElementsThenThrowRuntimeException() {
		exception.expect(RuntimeException.class);
		Room.create(Room.TYPE.Room, Arrays.asList(thirdPoint, fourthPoint, fifthPoint, sixthPoint));
	}

	@Test
	public void whenCreateNewRoomAndPlaceIsSurroundedByElementsThenReturnNewRoom() {
		assertThat(Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint))).isNotNull();
	}
	
	@Test
	public void whenCreateNewRoomAndBuildingGetRoomsThenResultContainsIt() {
		Room room = Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint));
		assertThat(firstPoint.getBuilding().getRooms()).contains(room);
	}
	
	@Test
	public void whenCreateNewRoomAndGetBuildingThenReturnItsBuilding() {
		Room room = Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint));
		assertThat(room.getBuilding()).isEqualTo(firstPoint.getBuilding());
	}
	
	@Test
	public void whenDestroyRoomAndBuildingGetRoomsThenResultContainsNotThis() {
		Room room = Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint));
		room.destroy();
		assertThat(firstPoint.getBuilding().getRooms()).doesNotContain(room);
	}
	
	@Test
	public void givenRoomAndPointInsideWhenContainsThenReturnTrue() {
		Room room = Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint));
		ConnectionPoint p = new ConnectionPoint(100, 300);
		
		assertTrue(room.contains(p));
	}
	
	@Test
	public void givenTwoRoomsFirstWithApartmentWhenFirstConnectToSecondAndGetSecondsApartmentThenReturnFirstsApartment() {
		Element.create(thirdPoint, sixthPoint);
		Element.create(fifthPoint, sixthPoint);
		Element.create(fourthPoint, fifthPoint);
		Room firstRoom = Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint));
		Room secondRoom = Room.create(Room.TYPE.Room, Arrays.asList(fifthPoint, sixthPoint, thirdPoint, fourthPoint));
		
		Apartment apartment = Apartment.create(Apartment.TYPE.Flat,Arrays.asList(firstRoom));
		
		firstRoom.connectTo(secondRoom);
		
		assertThat(secondRoom.getApartment()).isEqualTo(apartment);
	}
	
	@Test
	public void givenTwoRoomsWithApartmentsWhenFirstConnectToSecondAndGetSecondsApartmentRoomsThenResultDoesntContainsFirstRoom() {
		Element.create(thirdPoint, sixthPoint);
		Element.create(fifthPoint, sixthPoint);
		Element.create(fourthPoint, fifthPoint);
		Room firstRoom = Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint));
		Room secondRoom = Room.create(Room.TYPE.Room, Arrays.asList(fifthPoint, sixthPoint, thirdPoint, fourthPoint));
		
		Apartment apartment = Apartment.create(Apartment.TYPE.Flat,Arrays.asList(firstRoom));
		Apartment secondApartment = Apartment.create(Apartment.TYPE.Flat, Arrays.asList(secondRoom));
		
		firstRoom.connectTo(secondRoom);
		
		assertThat(secondApartment.getRooms()).doesNotContain(secondRoom);
	}
	
	@Test
	public void givenTwoRoomsSecondWithApartmentWhenFirstConnectToSecondAndGetFirstsApartmentThenReturnSecondsApartment() {
		Element.create(thirdPoint, sixthPoint);
		Element.create(fifthPoint, sixthPoint);
		Element.create(fourthPoint, fifthPoint);
		Room firstRoom = Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint));
		Room secondRoom = Room.create(Room.TYPE.Room, Arrays.asList(fifthPoint, sixthPoint, thirdPoint, fourthPoint));
		
		Apartment apartment = Apartment.create(Apartment.TYPE.Flat,Arrays.asList(secondRoom));
		
		firstRoom.connectTo(secondRoom);
		
		assertThat(firstRoom.getApartment()).isEqualTo(apartment);
	}
	
	@Test
	public void givenTwoRoomsWithNoApartmentsWhenFirstConnectToSecondAndGetSecondApartmentThenReturnFirstsApartment() {
		Element.create(thirdPoint, sixthPoint);
		Element.create(fifthPoint, sixthPoint);
		Element.create(fourthPoint, fifthPoint);
		Room firstRoom = Room.create(Room.TYPE.Room, Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint));
		Room secondRoom = Room.create(Room.TYPE.Room, Arrays.asList(fifthPoint, sixthPoint, thirdPoint, fourthPoint));
		
		firstRoom.connectTo(secondRoom);
		
		assertThat(secondRoom.getApartment()).isEqualTo(firstRoom.getApartment());
	}


	
}
