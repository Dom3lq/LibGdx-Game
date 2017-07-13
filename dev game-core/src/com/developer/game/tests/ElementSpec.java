package com.developer.game.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.developer.game.main.Building;
import com.developer.game.main.ConnectionPoint;
import com.developer.game.main.Element;
import com.developer.game.main.Room;
import com.developer.game.main.Waypoint;

public class ElementSpec {

	Building building;
	Element element;
	ConnectionPoint pointOne, pointTwo;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setupBefore() {
		building = new Building(10, 10);
		pointOne = building.getConnectionPoints()[2][2];
		pointTwo = building.getConnectionPoints()[3][2];
		element = Element.create(pointOne, pointTwo);
	}


	@Test
	public void whenGetConditionReturnZero() {
		element.getCondition();
	}
	
	@Test
	public void whenCreateTooLongElementThenThrowRuntimeException(){
		exception.expect(RuntimeException.class);
		Element.create(building.getConnectionPoints()[1][3], building.getConnectionPoints()[3][1]);
	}
	

	@Test
	public void whenElementBuildWithPowerEqualsTeenThenConditionEqualsTeen() {
		int actualCondition = element.build(10);
		assertThat(actualCondition).isEqualTo(10);
	}

	@Test
	public void whenElementBuildAndConditionAboveOneHundredThenConditionEqualsOneHundred() {
		int actualCondition = element.build(105);
		assertThat(actualCondition).isEqualTo(100);
	}

	@Test
	public void whenElementHitWithPowerEqualsFiveThenCoditionFallsFiveDown() {
		element.build(10);
		int actualCondition = element.hit(5);
		assertThat(actualCondition).isEqualTo(5);
	}

	@Test
	public void whenElementHitAndConditionIsBelowOrEqualsZeroThenItsRemovedFromPoints() {
		element.build(10);
		element.hit(15);
		assertThat(pointOne.getElements()).doesNotContain(element);
		assertThat(pointTwo.getElements()).doesNotContain(element);
	}

	@Test
	public void whenElementDestroyThenItsRemovedFromPoints() {
		element.build(100);
		element.destroy();
		assertThat(pointOne.getElements()).doesNotContain(element);
		assertThat(pointTwo.getElements()).doesNotContain(element);
	}

	@Test
	public void whenNewElementThenConnectionPointsContainsThisElement() {
		assertThat(pointOne.getElements()).contains(element);
		assertThat(pointTwo.getElements()).contains(element);
	}

	@Test
	public void whenElementOverlapsTheOtherOneThenThrowRuntimeException() {
		ConnectionPoint firstPoint = building.getConnectionPoints()[3][3];
		ConnectionPoint secondPoint = building.getConnectionPoints()[3][4];
		Element.create(firstPoint, secondPoint);
		exception.expect(RuntimeException.class);
		Element.create(firstPoint, secondPoint);
	}

	@Test
	public void givenPointsX0Y0AndX2Y7WhenGetDistanceToThenReturn9() {
		ConnectionPoint firstPoint = new ConnectionPoint(0, 0);
		ConnectionPoint secondPoint = new ConnectionPoint(1, 0);
		ConnectionPoint thirdPoint = new ConnectionPoint(2, 7);
		ConnectionPoint fourthPoint = new ConnectionPoint(3, 7);

		Element startW = Element.create(firstPoint, secondPoint);
		Element endW = Element.create(thirdPoint, fourthPoint);
		assertThat(startW.getDistanceTo(endW)).isEqualTo(9);

	}

	@Test
	public void givenPointsX2Y7AndX1Y1WhenGetDistanceToThenReturn9() {
		ConnectionPoint firstPoint = new ConnectionPoint(2, 7);
		ConnectionPoint secondPoint = new ConnectionPoint(3, 7);
		ConnectionPoint thirdPoint = new ConnectionPoint(1, 1);
		ConnectionPoint fourthPoint = new ConnectionPoint(2, 1);

		Element startW = Element.create(firstPoint, secondPoint);
		Element endW = Element.create(thirdPoint, fourthPoint);
		assertThat(startW.getDistanceTo(endW)).isEqualTo(7);

	}

	@Test
	public void givenHorizontalElementWhenIsElementReturnTrue() {
		Element element = Element.create(new ConnectionPoint(0, 0), new ConnectionPoint(1, 0));
		element.build(100);
		assertTrue(element.isWalkable());
	}

	@Test
	public void givenVerticalElementWhenIsElementReturnTrue() {
		Element element = Element.create(new ConnectionPoint(0, 0), new ConnectionPoint(0, 1));
		assertFalse(element.isWalkable());
	}

	@Test
	public void givenDiagonalElementWhenIsElementReturnFalse() {
		Element element = Element.create(new ConnectionPoint(0, 0), new ConnectionPoint(1, 1));
		assertFalse(element.isWalkable());
	}

	@Test
	public void givenElementsSameYWhenIsElementFromThenReturnTrue() {
		ConnectionPoint firstPoint = new ConnectionPoint(1, 0);
		ConnectionPoint secondPoint = new ConnectionPoint(2, 0);
		ConnectionPoint thirdPoint = new ConnectionPoint(3, 0);

		Element startW = Element.create(firstPoint, secondPoint);
		Element endW = Element.create(secondPoint, thirdPoint);
		startW.build(100);
		endW.build(100);
		assertTrue(endW.isWalkableFrom(startW));
	}

	@Test
	public void givenElementsSameXAndYDifferenceEquals1AndDownElementWithLadderWhenIsElementFromDownThenReturnTrue() {
		ConnectionPoint firstPoint = new ConnectionPoint(1, 0);
		ConnectionPoint secondPoint = new ConnectionPoint(2, 0);
		ConnectionPoint thirdPoint = new ConnectionPoint(1, 1);
		ConnectionPoint fourthPoint = new ConnectionPoint(2, 1);

		Element downW = Element.create(firstPoint, secondPoint);
		downW.build(100);
		downW.setLadder(true);
		Element topW = Element.create(thirdPoint, fourthPoint);
		topW.build(100);
		assertTrue(topW.isWalkableFrom(downW));
	}

	@Test
	public void givenElementsSameXAndYDifferenceEquals1AndDownElementWithLadderWhenIsElementFromTopThenReturnTrue() {
		ConnectionPoint firstPoint = new ConnectionPoint(1, 0);
		ConnectionPoint secondPoint = new ConnectionPoint(2, 0);
		ConnectionPoint thirdPoint = new ConnectionPoint(1, 1);
		ConnectionPoint fourthPoint = new ConnectionPoint(2, 1);

		Element downW = Element.create(firstPoint, secondPoint);
		downW.build(100);
		downW.setLadder(true);
		Element topW = Element.create(thirdPoint, fourthPoint);
		topW.build(100);
		assertTrue(downW.isWalkableFrom(topW));
	}

	@Test
	public void givenNearElementsWithSameYAndHorizontalNotElementElementBetweenWhenIsElementFromLeftThenReturnFalse() {
		ConnectionPoint firstPoint = new ConnectionPoint(1, 0);
		ConnectionPoint secondPoint = new ConnectionPoint(2, 0);
		ConnectionPoint thirdPoint = new ConnectionPoint(3, 0);
		ConnectionPoint fourthPoint = new ConnectionPoint(2, 1);

		Element startW = Element.create(firstPoint, secondPoint);
		Element endW = Element.create(secondPoint, thirdPoint);
		Element.create(secondPoint, fourthPoint);
		assertFalse(endW.isWalkableFrom(startW));
	}

	@Test
	public void givenElementsTopElementAndDownNotElementAndRightElementAndLeftNotElementWhenGetElementNeightboursThenResultContainsTopAndRight() {
		Building building = new Building(5, 5);
		Element center = Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][1]);
		center.build(100);
		Element right = Element.create(building.getConnectionPoints()[2][1], building.getConnectionPoints()[3][1]);
		right.build(100);
		Element.create(building.getConnectionPoints()[0][1], building.getConnectionPoints()[1][1]).build(100);
		Element top = Element.create(building.getConnectionPoints()[1][2], building.getConnectionPoints()[2][2]);
		top.build(100);
		top.setLadder(true);
		Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[1][2]).build(100);

		assertThat(center.getElementNeightbours()).containsOnly(top, right);
	}

	@Test
	public void whenBuildRoomThenElementsAroundHasItInRoomsList() {
		ConnectionPoint pointNe = building.getConnectionPoints()[3][3];
		ConnectionPoint pointSe = building.getConnectionPoints()[3][2];
		ConnectionPoint pointSw = building.getConnectionPoints()[2][2];
		ConnectionPoint pointNw = building.getConnectionPoints()[2][3];

		Element elementE = Element.create(pointNe, pointSe);
		//Element.create(pointSe, pointSw);
		Element.create(pointSw, pointNw);
		Element.create(pointNw, pointNe);

		Room room = Room.create(Room.TYPE.Room, Arrays.asList(pointNe, pointSe, pointSw, pointNw));

		assertThat(elementE.getRooms()).contains(room);
	}

	@Test
	public void givenConnectedRoomWhenDestroyElementThenDestroyRoom() {
		ConnectionPoint pointNe = building.getConnectionPoints()[3][3];
		ConnectionPoint pointSe = building.getConnectionPoints()[3][2];
		ConnectionPoint pointSw = building.getConnectionPoints()[2][2];
		ConnectionPoint pointNw = building.getConnectionPoints()[2][3];

		Element elementE = Element.create(pointNe, pointSe);
		Element elementS = building.getElement(2, 2);//Element.create(pointSe, pointSw);
		Element.create(pointSw, pointNw);
		Element.create(pointNw, pointNe);

		Room room = Room.create(Room.TYPE.Room, Arrays.asList(pointNe, pointSe, pointSw, pointNw));

		elementE.destroy();
		assertThat(elementS.getRooms()).doesNotContain(room);

	}

	@Test
	public void givenRoomWhenDestroyThenElementsDoesntHaveThisRoomInRoomLists() {
		ConnectionPoint pointNe = building.getConnectionPoints()[3][3];
		ConnectionPoint pointSe = building.getConnectionPoints()[3][2];
		ConnectionPoint pointSw = building.getConnectionPoints()[2][2];
		ConnectionPoint pointNw = building.getConnectionPoints()[2][3];

		Element.create(pointNe, pointSe);
		Element elementS = building.getElement(2, 2);//Element.create(pointSe, pointSw);
		Element.create(pointSw, pointNw);
		Element.create(pointNw, pointNe);

		Room room = Room.create(Room.TYPE.Room, Arrays.asList(pointNe, pointSe, pointSw, pointNw));
		room.destroy();

		assertThat(elementS.getRooms()).doesNotContain(room);
	}

	@Test
	public void givenElementWhenUpgradeToBrickAndGetTypeThenReturnBrick() {
		element.build(100);
		element.upgradeTo(Element.TYPE.BRICK);
		element.build(100);
		assertThat(element.getType()).isEqualTo(Element.TYPE.BRICK);
	}

	@Test
	public void givenElementWhenUpgradeToBetonAndGetTypeThenReturnBeton() {
		element.build(100);
		element.upgradeTo(Element.TYPE.BETON);
		element.build(100);
		assertThat(element.getType()).isEqualTo(Element.TYPE.BETON);
	}

	@Test
	public void givenElementWhenUpgradeToAsphaltAndGetTypeThenReturnAsphalt() {
		element.build(100);
		element.upgradeTo(Element.TYPE.ASPHALT);
		element.build(100);
		assertThat(element.getType()).isEqualTo(Element.TYPE.ASPHALT);
	}

	@Test
	public void givenBuildingWhenBuildNewElementAndBuildingGetElementsToBuildThenResultContainsElement() {

		assertThat(building.getElementsToBuild()).contains(element);
	}

	@Test
	public void givenElementWithCondition100WhenUpgradeAndGetConditionThenResultEquals0() {
		element.build(100);
		element.upgradeTo(Element.TYPE.SLABS);
		assertThat(element.getCondition()).isEqualTo(0);
	}

	@Test
	public void whenBuildElementTypeGrassAndElementGetTypeThenResultEqualsGrass() {
		Element element = Element.createGrass(building.getConnectionPoints()[0][0],
				building.getConnectionPoints()[0][1]);
		assertThat(element.getType()).isEqualTo(Element.TYPE.GRASS);
	}

	@Test
	public void givenElementWithDoorsBetweenWhenIsWalkableFromThenReturnTrue() {
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		element.createDoors();
		assertTrue(building.getElement(1, 0).isWalkableFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenElementWithoutDoorsBetweenWhenIsWalkableFromThenReturnFalse() {
		Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		assertFalse(building.getElement(1, 0).isWalkableFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenRightVerticalElementWhenIsReachableByHandThenReturnTrue() {
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		assertTrue(element.isReachableByHandFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenRightDiagonalElementWhenIsReachableByHandThenReturnFalse() {
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[2][1]);
		assertFalse(element.isReachableByHandFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenLeftVerticalElementWhenIsReachableByHandThenReturnTrue() {
		Element element = Element.create(building.getConnectionPoints()[0][0], building.getConnectionPoints()[0][1]);
		assertTrue(element.isReachableByHandFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenRightTopHorizontalElementWhenIsReachableByHandThenReturnTrue() {
		Element element = Element.create(building.getConnectionPoints()[0][1], building.getConnectionPoints()[1][1]);
		assertTrue(element.isReachableByHandFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenCenterDiagonalElementWhenIsReachableByHandThenReturnTrue() {
		Element element = Element.create(building.getConnectionPoints()[0][0], building.getConnectionPoints()[1][1]);
		assertTrue(element.isReachableByHandFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenCenterReverseDiagonalElementWhenIsReachableByHandThenReturnTrue() {
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[0][1]);
		assertTrue(element.isReachableByHandFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenLeftAwayVerticalElementWhenIsReachableByHandThenReturnFalse() {
		Element element = Element.create(building.getConnectionPoints()[0][0], building.getConnectionPoints()[0][1]);
		assertFalse(element.isReachableByHandFrom(building.getElement(1, 0)));
	}

	@Test
	public void givenRightAwayVerticalElementWhenIsReachableByHandThenReturnFalse() {
		Element element = Element.create(building.getConnectionPoints()[2][0], building.getConnectionPoints()[2][1]);
		assertFalse(element.isReachableByHandFrom(building.getElement(0, 0)));
	}

	@Test
	public void givenElementsStartx0y1andActualx3y1andEndx4y2WhenActualGetFCostThenReturn5() {
		Element start = Element.create(building.getConnectionPoints()[0][1], building.getConnectionPoints()[1][1]);
		Element actual = Element.create(building.getConnectionPoints()[3][1], building.getConnectionPoints()[4][1]);
		Element end = Element.create(building.getConnectionPoints()[4][2], building.getConnectionPoints()[5][2]);
		assertThat(actual.getFCost(start, end)).isEqualTo(5);
	}

	@Test
	public void givenElementFirstAndSecondWhenFirstSetParentSecondAndFirstGetParentThenReturnSecond() {
		Waypoint parent = new Waypoint(Element.create(building.getConnectionPoints()[3][1], building.getConnectionPoints()[4][1]));
		Waypoint actual = new Waypoint(element);
				actual.setParent(parent);
		assertThat(actual.getParent()).isEqualTo(parent);
	}

	@Test
	public void givenElementWhenGetDrawableAndGetFirstPointXThenResultIsEqualToXMultiplicatedBy100() {
		Element element = Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][2]);
		Element drawable = Element.getDrawable(element);
		assertThat(drawable.getConnectionPoints().get(0).getX())
				.isEqualTo(element.getConnectionPoints().get(0).getDrawableX());
	}

	@Test
	public void givenElementWhenGetDrawableAndGetFirstPointXThenResultIsEqualToDrawableY() {
		Element element = Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][2]);
		Element drawable = Element.getDrawable(element);
		assertThat(drawable.getConnectionPoints().get(0).getY())
				.isEqualTo(element.getConnectionPoints().get(0).getDrawableY());
	}

	@Test
	public void givenElementWhenGetDrawableAndGetSecondPointXThenResultIsEqualToXMultiplicatedBy100() {
		Element element = Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][2]);
		Element drawable = Element.getDrawable(element);
		assertThat(drawable.getConnectionPoints().get(1).getX())
				.isEqualTo(element.getConnectionPoints().get(1).getDrawableX());
	}

	@Test
	public void givenElementWhenGetDrawableAndGetSecondPointXThenResultIsEqualToDrawableY() {
		Element element = Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][2]);
		Element drawable = Element.getDrawable(element);
		assertThat(drawable.getConnectionPoints().get(1).getY())
				.isEqualTo(element.getConnectionPoints().get(1).getDrawableY());
	}

	@Test
	public void givenElementWithCondition0WhenIsWalkableReturnFalse() {
		Element element = Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][2]);
		assertFalse(element.isWalkable());
	}
}
