package com.developer.game.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Worker implements Runnable {

	private Element element;
	private Building building;
	private int power = 10;
	private int posX, posY;
	private boolean working = false;
	private Element toBuild;
	private STATE state;

	public Worker(Element element) {
		building = element.getConnectionPoints().get(0).getBuilding();
		this.element = element;
		posX = element.getX() * 200 + 100;
		posY = element.getY() * 200;
		state = STATE.WAITING;
	}

	public int build(Element element) {
		if (!element.getConnectionPoints().get(0).getBuilding().getWorkers().contains(this))
			throw new RuntimeException("Worker doesn't belong to this building");

		if (element.isReachableByHandFrom(this.element)) {
			return simulateBuilding(element);
		} else {
			Element aroundToBuild = findElementAroundReachable(element);

			if (aroundToBuild != null) {
				goTo(findPathTo(aroundToBuild));
				return build(element);
			} else
				return -1;
		}

	}

	private Element findElementAroundReachable(Element element) {
		List<Element> toGo = new ArrayList<Element>();

		for (Element e : element.getConnectionPoints().get(0).getBuilding().getElements())
			if (!toGo.contains(e) && element.isReachableByHandFrom(e))
				toGo.add(e);

		for (Element e : toGo) {
			List<Element> waypoints = findPathTo(e);
			if (waypoints == null)
				break;
			else
				return e;
		}

		return null;
	}

	private enum STATE {
		WALKING_RIGHT(null), WALKING_LEFT(null), WALKING_UP(null), WALKING_DOWN(null), WAITING(null), BUILDING_LEFT(
				null), BUILDING_RIGHT(null), BUILDING_TOP(null), BUILDING_DOWN(null);

		private String animation;

		private STATE(String animation) {
			this.animation = animation;
		}
	}

	private int simulateBuilding(Element element) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		state = STATE.WAITING;
		return element.build(power);
	}

	public String goTo(List<Element> waypoints) {
		String report = new String();

		if (waypoints == null)
			return "Unreachable";
		else
			for (Element e : waypoints) {
				report = report + moveTo(e);
				if (report.endsWith("X")) {
					state = STATE.WAITING;
					break;
				}

			}
		state = STATE.WAITING;

		return report;
	}

	public List<Element> findPathTo(final Element target) {
		List<Waypoint> open = new ArrayList<Waypoint>();
		List<Waypoint> closed = new ArrayList<Waypoint>();
		open.add(new Waypoint(this.element));
		Waypoint current = null;

		while (!open.isEmpty()) {
			Collections.sort(open, new Comparator<Waypoint>() {

				@Override
				public int compare(Waypoint w1, Waypoint w2) {
					return Integer.compare(w1.getElement().getFCost(element, target),
							w2.getElement().getFCost(element, target));
				}

			});

			current = open.get(0);

			open.remove(current);
			closed.add(current);

			if (current.getElement().equals(target)) {
				List<Element> waypoints = new ArrayList<Element>();

				while (current.getParent() != null) {
					waypoints.add(current.getElement());
					current = current.getParent();
				}

				Collections.reverse(waypoints);

				return waypoints;
			}

			for (Element neightbour : current.getElement().getElementNeightbours()) {
				searching: {
					for (Waypoint c : closed) {
						if (c.getElement().equals(neightbour))
							break searching;

					}

					for (Waypoint o : open)
						if (o.getElement().equals(neightbour))
							break searching;

					Waypoint neightbourW = new Waypoint(neightbour);
					neightbourW.setParent(current);
					open.add(neightbourW);
				}
			}

		}

		return null;
	}

	public Element getElement() {
		return this.element;
	}

	public String moveTo(Element w) {
		String report;
		if (w.isWalkableFrom(element)) {
			try {
				if (w.getX() == element.getX()) {
					if (w.getY() > element.getY()) {
						report = simulateMoveToUp(w);
					} else {
						report = simulateMoveToDown(w);
					}
				} else {
					if (w.getX() < element.getX()) {
						report = simulateMoveToLeft(w);
					} else {
						report = simulateMoveToRight(w);
					}
				}

				element = w;
				return report;
			} catch (InterruptedException e5) {
				return "InterruptedException";
			}
		} else {
			return "X";
		}
	}

	private String simulateMoveToRight(Element element) throws InterruptedException {
		while (posX < element.getX() * 200 + 100) {
			posX++;
			Thread.sleep(5);
		}
		return "R";
	}

	private String simulateMoveToLeft(Element element) throws InterruptedException {
		while (posX > element.getX() * 200 + 100) {
			posX--;
			Thread.sleep(5);
		}
		return "L";
	}

	private String simulateMoveToDown(Element element) throws InterruptedException {
		while (posY > element.getY() * 200) {
			posY--;
			Thread.sleep(10);
		}
		return "D";
	}

	private String simulateMoveToUp(Element element) throws InterruptedException {
		while (posY < element.getY() * 200) {
			posY++;
			Thread.sleep(10);
		}
		return "U";
	}

	public Building getBuilding() {
		return building;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	@Override
	public void run() {
		this.working = true;
		while (working) {
			if (this.toBuild == null && building.getElementsToBuild().isEmpty()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			} else {
				if (this.toBuild == null) {
					List<Element> elementsToBuild = new ArrayList<Element>();

					for (Element e : building.getElementsToBuild())
						if (this.findElementAroundReachable(e) != null)
							elementsToBuild.add(e);

					final Worker thisWorker = this;

					Collections.sort(elementsToBuild, new Comparator<Element>() {

						@Override
						public int compare(Element e1, Element e2) {
							return Integer.compare(thisWorker.getElement().getDistanceTo(e1),
									thisWorker.getElement().getDistanceTo(e2));
						}

					});

					if (!elementsToBuild.isEmpty()) {
						Element closest = elementsToBuild.get(0);

						for (Element e : elementsToBuild)
							if (this.element.getDistanceTo(e) < this.element.getDistanceTo(closest))
								closest = e;

						this.toBuild = closest;
					}

				} else {
					int actual = build(toBuild);
					if (actual >= 100 || actual == -1)
						toBuild = null;
				}
			}
		}
	}

	public void setPower(int power) {
		this.power = power;
	}

	public Boolean isWorking() {
		return this.working;
	}

	public Thread work() {
		if (working)
			throw new RuntimeException("Already working");

		Thread thread = new Thread(this);
		thread.start();
		return thread;
	}

	public void stopWorking() {
		this.working = false;
	}

}
