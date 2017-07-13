package com.developer.game.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.developer.game.main.Building;
import com.developer.game.main.Element;
import com.developer.game.main.Player;

public class PlayerSpec {

	private Building building;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setupBefore() {
		building = new Building(10, 10);
		building.setPrice(0);
	}

	@Test
	public void whenBuyBuildingAndPlayerGetBuildingsThenReturnContainsBuilding() {
		Player player = new Player("");
		player.buyBuilding(building);
		assertThat(player.getBuildings()).contains(building);
	}

	@Test
	public void givenBuildingWithOwnerAndBuyBuildingThenThrowRuntimeException() {
		Player player = new Player("");
		player.buyBuilding(building);
		Player playerTwo = new Player("");
		exception.expect(RuntimeException.class);
		playerTwo.buyBuilding(building);
	}

	@Test
	public void givenPlayerCash10whenBuyBuildingPriced5AndGetCashThenReturn5() {
		building.setPrice(5);
		Player player = new Player("");
		player.setCash(10);
		player.buyBuilding(building);
		assertThat(player.getCash()).isEqualTo(5);
	}

	@Test
	public void whenInitiateNewPlayerWithNameEqualsNickAndGetNameThenReturnNick() {
		Player player = new Player("Nick");
		assertThat(player.getName()).isEqualTo("Nick");
	}
	
	@Test
	public void givenPlayersBuildingWhenCreateGrassThenPlayerCashIsLowerByGrassPrice() {
		Player testPlayer = new Player("TestPlayer");
		testPlayer.setCash(1000);
		testPlayer.buyBuilding(building);
		
		int actualCash = testPlayer.getCash();
		
		Element.createGrass(building.getConnectionPoints()[0][0], building.getConnectionPoints()[1][1]);
		
		int expected = actualCash - Element.TYPE.GRASS.getPrice();
		
		assertThat(testPlayer.getCash()).isEqualTo(expected);
	}
	
	@Test
	public void givenPlayersBuildingWhenUpgradeElementToBrickThenPlayerCashIsLowerByBrickPrice() {
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		element.build(100);		
		
		Player testPlayer = new Player("TestPlayer");
		testPlayer.setCash(1000);
		testPlayer.buyBuilding(building);
		
		int actualCash = testPlayer.getCash();
		
		element.upgradeTo(Element.TYPE.BRICK);
		
		int expected = actualCash - Element.TYPE.BRICK.getPrice();
		
		assertThat(testPlayer.getCash()).isEqualTo(expected);
	}
	
	@Test
	public void givenPlayersBuildingWhenUpgradeElementToDoorsThenPlayerCashIsLowerByDoorsPrice() {
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		element.build(100);		
		
		Player testPlayer = new Player("TestPlayer");
		testPlayer.setCash(1000);
		testPlayer.buyBuilding(building);
		
		int actualCash = testPlayer.getCash();
		
		element.upgradeTo(Element.TYPE.DOORS);
		
		int expected = actualCash - Element.TYPE.DOORS.getPrice();
		
		assertThat(testPlayer.getCash()).isEqualTo(expected);
	}
	
}
