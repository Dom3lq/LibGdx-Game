package com.developer.game.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import com.developer.game.main.Worker;

public class BuildingSpec {

	private Building building;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	final int BUILDING_WIDTH = 10;
	final int BUILDING_HEIGHT = 5;

	@Before
	public void setupBefore() {
		building = new Building(BUILDING_WIDTH, BUILDING_HEIGHT);
	}

	@Test
	public void whenGetWidthThenReturnWidth() {
		assertThat(building.getWidth()).isEqualTo(BUILDING_WIDTH);
	}

	@Test
	public void whenGetHeightThenReturnHeight() {
		assertThat(building.getHeight()).isEqualTo(BUILDING_HEIGHT);
	}

	@Test
	public void givenNewBuildingThenCheckConnectionPointsSize() {
		assertThat(building.getConnectionPoints().length).isEqualTo(BUILDING_WIDTH + 1);
	}
	
	@Test
	public void givenNewBuildingThenCheckConnectionSecondDimensionPointsSize() {
		assertThat(building.getConnectionPoints()[0].length).isEqualTo(BUILDING_HEIGHT + 1);
	}

	@Test
	public void givenPlayerWhenBuyBuildingAndBuildingGetOwnerThenOwnerEqualsPlayer() {
		Player player = new Player("");
		player.buyBuilding(building);
		assertThat(building.getOwner()).isEqualTo(player);
	}

	@Test
	public void givenNewBuildingThenThereIsElementWithPointsX6AndX7() {
		Element optional = null;
		
		for(Element e: building.getElements())
			if(e.getConnectionPoints().contains(building.getConnectionPoints()[6][0]) && e.getConnectionPoints().contains(building.getConnectionPoints()[7][0]))
				optional = e;
		
		assertThat(optional).isNotNull();
	}
	
	@Test
	public void whenHireNewWorkerAndGetWorkersThenResulContainsWorker() {
		Player player  = new Player("Player");
		player.buyBuilding(building);
		Worker worker = building.hireNewWorker();
		assertThat(building.getWorkers()).isNotEmpty().contains(worker);
	}
	
	
	
	@Test
	public void GivenPlayerCash500WhenHireFistWorkerAndPlayerGetCashThenResultEquals450() {
		Player player = new Player("Player");
		player.setCash(500);
		building.setPrice(0);
		player.buyBuilding(building);
		building.hireNewWorker();
		assertThat(player.getCash()).isEqualTo(450);
		
	}
	
	@Test
	public void GivenPlayerCash500WhenHireThreeWorkersAndPlayerGetCashThenResultEquals500MinusWorkersPrice() {
		Player player  = new Player("Player");
		player.setCash(500);
		player.buyBuilding(building);
		int totalPrice = building.getNewWorkerPrice();
		building.hireNewWorker();
		totalPrice = totalPrice + building.getNewWorkerPrice();
		building.hireNewWorker();
		totalPrice = totalPrice + building.getNewWorkerPrice();
		building.hireNewWorker();
		
		assertThat(player.getCash()).isEqualTo((500 - totalPrice));
	}
	
	@Test
	public void givenElementWithConditionEquals100WhenGetElementsToBuildThenResultDoesntContainsThisElement() {
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		element.build(100);
		assertThat(building.getElementsToBuild()).doesNotContain(element);
	}
	
	@Test
	public void givenNewBuildingWhenGetGroundElementGetConditionThenResultEquals100 () {
		assertThat(building.getElement(0, 0).getCondition()).isEqualTo(100);
	}
	
	@Test
	public void givenNewBuildingWhenGetGroundElement () {
		assertThat(building.getElement(0, 0).getType()).isEqualTo(Element.TYPE.GRASS);
	}
	
	@Test
	public void givenBuildingWhenHireNewWorkerWithArgWorkerAndGetWorkersThenResultContainsThisWorker () {
		building.setOwner(new Player("Player"));
		Worker worker = new Worker(building.getElement(0, 0));
		building.hireNewWorker(worker);
		assertThat(building.getWorkers()).contains(worker);
	}
	
	@Test
	public void whenHireAndRunNewWorkerAndWorkerIsWorkingReturnTrue() {
		building.setOwner(new Player("Player"));
		Worker worker = building.hireAndRunNewWorker();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(worker.isWorking());
	}
	
	@Test
	public void givenNewBuildingWhenGetWillingTenantsThenReturnEmptyList() {
		assertThat(building.getWillingTenants()).isEmpty();
	}
	
	@Test
	public void whenBuildingAddTenantAndGetWillingTenantsThenResultContainsTenant() {
		Tenant tenant = building.addTenant();
		assertThat(building.getWillingTenants()).contains(tenant);
	}
	
	@Test
	public void givenApartmentAndWillingTenantWhenAssignToApartmentAndGetWillingTenantsThenResultContainsNotThisTenant() {
		Tenant tenant = building.addTenant();
		Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		Element.create(building.getConnectionPoints()[2][0], building.getConnectionPoints()[2][1]);
		Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][1]);
		Room room = Room.create(Room.TYPE.Room, Arrays.asList(building.getConnectionPoints()[1][0], building.getConnectionPoints()[2][0], building.getConnectionPoints()[2][1], building.getConnectionPoints()[1][1]));
		Apartment apartment = Apartment.create(Apartment.TYPE.Flat, Arrays.asList(room));
		tenant.assignTo(apartment);
		assertThat(building.getWillingTenants()).doesNotContain(tenant);
	}	
	
	@Test
	public void whenBuildingGetElementOnPosWithNoElementThenResultIsNull() {
		assertThat(building.getElement(5, 5)).isNull();
	}
}
