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
import com.developer.game.main.Player;
import com.developer.game.main.Worker;

public class WorkerSpec {
	Building building;
	String report;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setupBefore() {
		building = new Building(5, 5);
		Player player = new Player("Player");
		player.buyBuilding(building);
	}

	@Test
	public void givenNewWorkerInBuildingWhenGetBuildingThenReturnBuilding() {
		Worker worker = new Worker(building.getElement(0, 0));
		assertThat(worker.getBuilding()).isEqualTo(building);
	}

	@Test
	public void givenElementCondition0WhenWorkerBuildElementWithPower10ThenElementConditionEquals100() {
		Building buildingOne = new Building(10, 10);
		Player player = new Player("Player");
		player.buyBuilding(buildingOne);
		ConnectionPoint pointOne = buildingOne.getConnectionPoints()[1][0];
		ConnectionPoint pointTwo = buildingOne.getConnectionPoints()[1][1];
		Element element = Element.create(pointOne, pointTwo);
		Worker worker = buildingOne.hireNewWorker();
		worker.setPower(10);
		worker.build(element);
		assertThat(element.getCondition()).isEqualTo(10);
	}

	@Test
	public void givenElementAndBuildingWhenWorkerBuildElementAndWorkerBuildingDonntEqualsBuildingThenThrowRuntimeException() {
		Player player = new Player("PlayerTwo");
		Building buildingOne = new Building(10, 10);
		Building buildingTwo = new Building(10, 10);
		player.buyBuilding(buildingOne);
		player.buyBuilding(buildingTwo);
		ConnectionPoint pointOne = buildingOne.getConnectionPoints()[1][1];
		ConnectionPoint pointTwo = buildingOne.getConnectionPoints()[2][1];
		Element element = Element.create(pointOne, pointTwo);
		Worker worker = buildingTwo.hireNewWorker();
		exception.expect(RuntimeException.class);
		worker.build(element);
	}

	@Test
	public void givenWorkerOnFirstElementWithPosX0WhenMoveToSecondElementWithPosX1ThenWorkerGetElementEqualsSecondElement() {
		Element elementFirst = building.getElement(0, 0);
		Element elementSecond = building.getElement(1, 0);
		Worker worker = new Worker(elementFirst);
		worker.moveTo(elementSecond);
		assertThat(worker.getElement()).isEqualTo(elementSecond);
	}

	@Test
	public void givenWorkerOnFirstElementWithPosX0WhenMoveToSecondElementWithPosX1ThenResultIsR() {
		Element elementFirst = building.getElement(0, 0);
		Element elementSecond = building.getElement(1, 0);
		Worker worker = new Worker(elementFirst);
		assertThat(worker.moveTo(elementSecond)).isEqualTo("R");
	}

	@Test
	public void givenWorkerPosX0WhenMoveToPosX1ThenReturnR() {
		Worker worker = new Worker(building.getElement(0, 0));
		String report = worker.moveTo(building.getElement(1, 0));
		assertThat(report).isEqualTo("R");
	}

	@Test
	public void givenWorkerPosX0WhenGotoPosX3ThenResultOfGotoIsRRR() {
		Worker worker = building.hireNewWorker();
		String report = worker.goTo(worker.findPathTo(building.getElement(3, 0)));
		assertThat(report).isEqualTo("RRR");
	}

	@Test
	public void givenWorkerPosX0WhenGotoPosX3ThenWorkerPosXIs3() {
		Worker worker = new Worker(building.getElement(0, 0));
		worker.goTo(worker.findPathTo(building.getElement(3, 0)));
		assertThat(worker.getElement().getX()).isEqualTo(3);
	}

	@Test
	public void givenAwayElementWhenWorkerBuildThenWorkerGotoNearAndBuild() {
		Building building = new Building(5, 5);
		Player player = new Player("Player");
		player.buyBuilding(building);
		Worker worker = building.hireNewWorker();
		Element element = Element.create(building.getConnectionPoints()[3][1], building.getConnectionPoints()[4][1]);
		element.hit(60);
		worker.build(element);
		assertThat(worker.getElement().getDistanceTo(element)).isLessThanOrEqualTo(1);
	}

	@Test
	public void givenStandingWorkerWhenGetPosXThenResultEqualsWorkerElementPosXx100Plus50() {
		Worker worker = building.hireNewWorker();
		assertThat(worker.getPosX()).isEqualTo(worker.getElement().getX() * 200 + 100);
	}

	@Test
	public void givenStandingWorkerWhenGetPosYThenResultEqualsWorkerElementPosYx100() {
		Worker worker = building.hireNewWorker();
		assertThat(worker.getPosY()).isEqualTo(worker.getElement().getY() * 100);
	}

	@Test
	public void whenMoveToRightToElementXEquals1AndGetPosXThenResultEquals150() {
		Worker worker = building.hireNewWorker();
		worker.moveTo(building.getElement(1, 0));
		assertThat(worker.getPosX()).isEqualTo(300);
	}

	@Test
	public void WhenMoveToLeftToElementXEquals0AndGetPosXThenResultEquals50() {
		Worker worker = building.hireNewWorker();
		worker.moveTo(building.getElement(1, 0));
		worker.moveTo(building.getElement(0, 0));
		assertThat(worker.getPosX()).isEqualTo(100);
	}

	@Test
	public void whenMoveToTopToElementYEquals1AndGetPosYThenResultEquals100() {
		Worker worker = building.hireNewWorker();
		Element element = Element.create(building.getConnectionPoints()[0][1], building.getConnectionPoints()[1][1]);
		element.build(100);
		building.getElement(0, 0).setLadder(true);
		worker.moveTo(element);
		assertThat(worker.getPosY()).isEqualTo(200);
	}

	@Test
	public void whenMoveToBotToElementYEquals0AndGetPosYThenResultEquals0() {
		Worker worker = building.hireNewWorker();
		Element element = Element.create(building.getConnectionPoints()[0][1], building.getConnectionPoints()[1][1]);
		building.getElement(0, 0).setLadder(true);
		worker.moveTo(element);
		worker.moveTo(building.getElement(0, 0));
		assertThat(worker.getPosY()).isEqualTo(0);
	}

	@Test
	public void givenUnreachableElementAndWorkerWhenGotoThenResultEqualsUnreachable() {
		Worker worker = building.hireNewWorker();
		Element element = Element.create(building.getConnectionPoints()[1][4], building.getConnectionPoints()[2][4]);
		assertThat(worker.goTo(worker.findPathTo(element))).isEqualTo("Unreachable");
	}

	@Test
	public void givenUnreachableVerticalElementWhenBuildThenResultIsMinus1() {
		Element element = Element.create(building.getConnectionPoints()[1][3], building.getConnectionPoints()[1][4]);
		Worker worker = building.hireNewWorker();
		assertThat(worker.build(element)).isEqualTo(-1);
	}

	@Test
	public void givenUnreachableHorizontalElementWhenBuildThenResultIsMinus1() {
		Element element = Element.create(building.getConnectionPoints()[1][3], building.getConnectionPoints()[2][3]);
		Worker worker = building.hireNewWorker();
		assertThat(worker.build(element)).isEqualTo(-1);
	}

	@Test
	public void givenInterruptedTaskWhenBuildReachableByHandElementThenElementIsBuilded() {
		Worker worker = building.hireNewWorker();
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		Thread.currentThread().interrupt();
		worker.build(element);
		assertThat(element.getCondition()).isGreaterThan(0);
	}

	@Test
	public void whenMoveToUnreachableElementResultIsX() {
		Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		Worker worker = building.hireNewWorker();
		String report = worker.moveTo(building.getElement(1, 0));
		assertThat(report).isEqualTo("X");
	}

	@Test
	public void givenBuildingAndWorkerWhenGoToTargetThenResultIsURU() {
		Worker worker = building.hireNewWorker();
		Element first = Element.create(building.getConnectionPoints()[0][1], building.getConnectionPoints()[1][1]);
		Element second = Element.create(building.getConnectionPoints()[1][1], building.getConnectionPoints()[2][1]);
		Element target = Element.create(building.getConnectionPoints()[1][2], building.getConnectionPoints()[2][2]);
		first.build(100);
		second.build(100);
		target.build(100);
		second.setLadder(true);
		building.getElement(0, 0).setLadder(true);
		building.getElement(1, 0).setLadder(true);
		String report = worker.goTo(worker.findPathTo(target));
		assertThat(report).isEqualTo("URU");
	}

	@Test
	public void givenNotRunningWorkerWhenIsWorkingReturnFalse() {
		Worker worker = building.hireNewWorker();
		assertFalse(worker.isWorking());
	}

	@Test
	public void givenWorkerWhenWorkAndIsWorkingReturnTrue() {
		Worker worker = building.hireNewWorker();
		worker.work();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(worker.isWorking());
	}

	@Test
	public void givenWorkerWhenInteruptThreadAndMoveToThenReturnInterruptedException() {
		Worker worker = building.hireNewWorker();
		Thread.currentThread().interrupt();
		String report = worker.moveTo(building.getElement(1, 0));
		assertThat(report).isEqualTo("InterruptedException");
	}

	@Test
	public void givenWorkingWorkerWhenWorkThenThrowRuntimeException() {
		Worker worker = building.hireAndRunNewWorker();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		exception.expect(RuntimeException.class);
		worker.work();
	}

	@Test
	public void givenElementCondition50WhenWorkerWorkThenElementConditionIsHigherThan50() {
		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		building.hireAndRunNewWorker();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertThat(element.getCondition()).isGreaterThan(0);
	}

	@Test
	public void givenWorkingWorkerWhenStopWorkingAndIsWorkingReturnFalse() {
		Worker worker = building.hireAndRunNewWorker();
		worker.stopWorking();
		assertFalse(worker.isWorking());
	}

	@Test
	public void givenWorkingWorkerWhenStopWorkingAndBuildNewElementAndGetConditionThenResultIs0() {
		Worker worker = building.hireAndRunNewWorker();
		worker.stopWorking();
		while (worker.isWorking())
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		Element element = Element.create(building.getConnectionPoints()[1][0], building.getConnectionPoints()[1][1]);
		assertThat(element.getCondition()).isEqualTo(0);
	}

	@Test
	public void whenHireAndRun2NewWorkersThenThreadsCountIsHigherByTwo() {
		int expected = Thread.activeCount() + 2;
		building.hireAndRunNewWorker();
		building.hireAndRunNewWorker();
		assertThat(Thread.activeCount()).isEqualTo(expected);
	}

	@Test
	public void givenWorkerWhenGoToFarElementAndBuildElementBetweenThenWorkerStopsAndResultContainsX() {
		final Worker worker = building.hireNewWorker();

		new Thread(new Runnable() {
			public void run() {
				report = worker.goTo(worker.findPathTo(building.getElement(4, 0)));
			}
		}).start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		Element.create(building.getConnectionPoints()[2][0], building.getConnectionPoints()[2][1]);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertThat(report).contains("X");
	}

	@Test
	public void givenWorkerWhenGetPathToElementThenReturnRRR() {
		Worker worker = building.hireNewWorker();
		assertThat(worker.findPathTo(building.getElement(3, 0))).containsExactlyElementsOf(
				Arrays.asList(building.getElement(1, 0), building.getElement(2, 0), building.getElement(3, 0)));
	}

}