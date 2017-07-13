package com.developer.game.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.developer.game.main.Building;
import com.developer.game.main.ConnectionPoint;
import com.developer.game.main.Element;

public class ConnectionPointSpec {

	private ConnectionPoint firstPoint, secondPoint, thirdPoint, fourthPoint;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setupBefore() {
		firstPoint = new ConnectionPoint(1, 1);
		secondPoint = new ConnectionPoint(1, 2);
		thirdPoint = new ConnectionPoint(2, 2);
		fourthPoint = new ConnectionPoint(2, 1);

		Element.create(firstPoint, secondPoint);
	}

	@Test
	public void whenGetElementsReturnsElementsArrayList() {
		assertThat(firstPoint.getElements()).isInstanceOf(ArrayList.class).first().isInstanceOf(Element.class);
	}

	@Test
	public void givenNotConnectedPointWhenIsConnectedWithThenReturnFalse() {
		assertThat(firstPoint.isConnectedWith(new ConnectionPoint(1, 1))).isFalse();
	}

	@Test
	public void givenConnectedPointWhenIsConnectedWithThenReturnTrue() {
		assertThat(firstPoint.isConnectedWith(secondPoint)).isTrue();
		assertThat(secondPoint.isConnectedWith(firstPoint)).isTrue();
	}

	@Test
	public void givenNotConnectedPointsWhenAreConnectedThenReturnFalse() {
		assertThat(ConnectionPoint.areConnected(Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint)))
				.isFalse();
	}

	@Test
	public void whenAreConnectedAndArgumentListIsEmptyThenThrowRuntimeException() {
		exception.expect(RuntimeException.class);
		ConnectionPoint.areConnected(new ArrayList<ConnectionPoint>());
	}

	@Test
	public void givenConnectedPointsWhenAreConnectedThenReturnTrue() {
		Element.create(secondPoint, thirdPoint);
		Element.create(thirdPoint, fourthPoint);
		Element.create(fourthPoint, firstPoint);

		assertThat(ConnectionPoint.areConnected(Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint)))
				.isTrue();
	}

	@Test
	public void givenConnectedPointsAndLastIsNotConnectedToFirstWhenAreConnectedThenReturnFalse() {
		Element.create(secondPoint, thirdPoint);
		Element.create(thirdPoint, fourthPoint);

		assertThat(ConnectionPoint.areConnected(Arrays.asList(firstPoint, secondPoint, thirdPoint, fourthPoint)))
				.isFalse();
	}

	@Test
	public void givenDistantPointWhenIsNearThenReturnFalse() {
		ConnectionPoint distantPoint = new ConnectionPoint(3, 3);
		assertThat(distantPoint.isNear(firstPoint)).isFalse();
		assertThat(firstPoint.isNear(distantPoint)).isFalse();
	}

	@Test
	public void givenNearPointWhenIsNearReturnTrue() {
		assertThat(firstPoint.isNear(thirdPoint)).isTrue();
		assertThat(secondPoint.isNear(firstPoint)).isTrue();
	}

	@Test
	public void givenTwoPointsWhenFirstConnectToSecondThenTheyreConnected() {
		Building building = new Building(5, 5);
		ConnectionPoint firstPoint = building.getConnectionPoints()[0][0];
		ConnectionPoint secondPoint = building.getConnectionPoints()[0][1];
		firstPoint.connectTo(secondPoint);

		assertTrue(firstPoint.isConnectedWith(secondPoint));
	}

	@Test
	public void givenTwoPoints00And05WhenGetDistanceToThenReturn5() {
		ConnectionPoint firstPoint = new ConnectionPoint(0, 0);
		ConnectionPoint secondPoint = new ConnectionPoint(0, 5);
		assertThat(firstPoint.getDistanceTo(secondPoint)).isEqualTo(5);
	}

	@Test
	public void givenTwoPoints15And41WhenGetDistanceToThenReturn5() {
		ConnectionPoint firstPoint = new ConnectionPoint(1, 5);
		ConnectionPoint secondPoint = new ConnectionPoint(4, 1);
		assertThat(firstPoint.getDistanceTo(secondPoint)).isEqualTo(5);
	}

	@Test
	public void givenPointX2WhenGetDrawableXThenReturn200() {
		ConnectionPoint point = new ConnectionPoint(2, 0);
		assertThat(point.getDrawableX()).isEqualTo(400);
	}

	@Test
	public void givenPointY5WhenGetDrawableYThenReturn500() {
		ConnectionPoint point = new ConnectionPoint(0, 5);
		assertThat(point.getDrawableY()).isEqualTo(1000);
	}

	@Test
	public void givenTwoPointX100Y100AndX1Y2WhenGetDistanceToDrawableThenReturn100() {
		ConnectionPoint firstPoint = new ConnectionPoint(200, 200);
		ConnectionPoint secondPoint = new ConnectionPoint(1, 2);
		assertThat(firstPoint.getDistanceToDrawable(secondPoint)).isEqualTo(200);
	}

	@Test
	public void givenTwoPointsWithNoElementsWhenCreateElementBetweenThrowRuntimeException() {
		ConnectionPoint firstPoint = new ConnectionPoint(0, 1);
		ConnectionPoint secondPoint = new ConnectionPoint(1, 1);

		exception.expect(RuntimeException.class);
		firstPoint.connectTo(secondPoint);
	}
}
