package gdx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ShortArray;
import com.developer.game.main.Apartment;
import com.developer.game.main.Building;
import com.developer.game.main.ConnectionPoint;
import com.developer.game.main.Element;
import com.developer.game.main.Player;
import com.developer.game.main.Room;
import com.developer.game.main.Tenant;
import com.developer.game.main.Worker;

public class GameScreen implements Screen, GestureListener {

	final DeveloperGame game;
	SpriteBatch batch;
	SpriteBatch guiBatch;
	PolygonSpriteBatch polyBatch;
	ShapeRenderer shapeRenderer;
	ShapeRenderer guiShapeRenderer;
	BitmapFont font;
	private Sprite elementSprite;

	OrthographicCamera camera;
	Float currentZoom;

	Texture undergroundTexture;
	Texture panoramaTexture;

	private Vector3 touchPoint;
	private Vector2 guiTouchPoint;

	private ConnectionPoint selectedPoint;
	private ConnectionPoint targetPoint;

	Building building;
	Player player;

	private Room selectedRoom;
	private Room targetRoom;

	private Room.TYPE selectedRoomType;
	private Element.TYPE selectedUpgradeType;
	private Tenant selectedTenant;
	private Tenant tenantToDescribe;

	private CircleButton createRoomButton;
	private CircleButton selectedRoomTypeButton;
	private CircleButton selectedElementUpgradeTypeButton;
	private CircleButton upgradeElementButton;
	private CircleButton selectedTenantButton;

	List<CircleButton> mainGuiButtons;
	List<CircleButton> roomTypeButtons;
	List<CircleButton> elementUpgradeTypeButtons;
	List<CircleButton> tenantsButtons;

	private List<ConnectionPoint> newRoomPoints;

	Map<String, Texture> textures = new HashMap<String, Texture>();

	public GameScreen(final DeveloperGame game) {
		this.game = game;
		game.enableAlpha();

		batch = game.batch;
		guiBatch = game.guiBatch;
		polyBatch = game.polyBatch;
		font = game.font;
		shapeRenderer = game.shapeRenderer;
		guiShapeRenderer = game.guiShapeRenderer;
		elementSprite = new Sprite();

		//////////// INIT GAME
		player = new Player("Player");
		player.setCash(1000);
		building = new Building(10, 10);
		building.setPrice(100);
		player.buyBuilding(building);

		new Thread(building).start();

		//////////////////////

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = game.DRAWABLE_SCALE * 2;
		camera.position.y = game.DRAWABLE_SCALE;

		undergroundTexture = new Texture("underground.jpeg");
		panoramaTexture = new Texture("panorama1.jpeg");

		for (Element.TYPE t : Element.TYPE.values()) {
			if (t.getTexture() != null) {
				try {
					textures.put(t.getTexture(), new Texture(Gdx.files.internal(t.getTexture())));
				} catch (GdxRuntimeException e) {
					System.out.println(e.getMessage());
				}
			}
		}

		touchPoint = new Vector3();
		guiTouchPoint = new Vector2();

		GestureDetector gd = new GestureDetector(this);
		Gdx.input.setInputProcessor(gd);

		newRoomPoints = new ArrayList<ConnectionPoint>();

		setupMainGuiButtons();
		setupRoomTypeButtons();
		setupElementUpgradeTypeButtons();

		tenantsButtons = new ArrayList<CircleButton>();

		setupButtonsPositions();

		camera.zoom = 0.5f;
		currentZoom = camera.zoom;
	}

	private void setupButtonsPositions() {
		for (CircleButton sb : mainGuiButtons) {
			sb.setX((mainGuiButtons.indexOf(sb) * (game.MEDIUM_BUTTON_SIZE + 32)) + 16 + (game.MEDIUM_BUTTON_SIZE / 2));
			sb.setY(16 + (game.MEDIUM_BUTTON_SIZE / 2));
			sb.setR(game.MEDIUM_BUTTON_SIZE / 2);
		}

		for (CircleButton rtb : roomTypeButtons) {
			rtb.setX((roomTypeButtons.indexOf(rtb) * (game.MEDIUM_BUTTON_SIZE + 32)) + 16
					+ (game.MEDIUM_BUTTON_SIZE / 2));
			rtb.setY(game.MEDIUM_BUTTON_SIZE + 32 + (game.MEDIUM_BUTTON_SIZE / 2));
			rtb.setR(game.MEDIUM_BUTTON_SIZE / 2);
		}

		for (CircleButton eutb : elementUpgradeTypeButtons) {
			eutb.setX((elementUpgradeTypeButtons.indexOf(eutb) * (game.MEDIUM_BUTTON_SIZE + 32)) + 16
					+ (game.MEDIUM_BUTTON_SIZE / 2));
			eutb.setY(game.MEDIUM_BUTTON_SIZE + 32 + (game.MEDIUM_BUTTON_SIZE / 2));
			eutb.setR(game.MEDIUM_BUTTON_SIZE / 2);
		}
	}

	private void setupWillingTenantButtonsPositions() {
		for (CircleButton tButton : tenantsButtons) {
			tButton.setX(16 + game.MEDIUM_BUTTON_SIZE / 2
					+ (game.MEDIUM_BUTTON_SIZE + 16) * tenantsButtons.indexOf(tButton));
			tButton.setY(Gdx.graphics.getHeight() - (game.MEDIUM_BUTTON_SIZE / 2 + 16));
			tButton.setR(game.MEDIUM_BUTTON_SIZE / 2);
		}

	}

	private void setupElementUpgradeTypeButtons() {
		this.elementUpgradeTypeButtons = new ArrayList<CircleButton>();

		elementUpgradeTypeButtons.add(new CircleButton("Wood", new Clickable() {

			@Override
			public void onClick() {
				selectedUpgradeType = Element.TYPE.WOOD;
			}

		}));

		elementUpgradeTypeButtons.add(new CircleButton("Asphalt", new Clickable() {

			@Override
			public void onClick() {
				selectedUpgradeType = Element.TYPE.ASPHALT;
			}

		}));

		elementUpgradeTypeButtons.add(new CircleButton("Beton", new Clickable() {

			@Override
			public void onClick() {
				selectedUpgradeType = Element.TYPE.BETON;
			}

		}));

		elementUpgradeTypeButtons.add(new CircleButton("Brick", new Clickable() {

			@Override
			public void onClick() {
				selectedUpgradeType = Element.TYPE.BRICK;
			}

		}));

		elementUpgradeTypeButtons.add(new CircleButton("Slabs", new Clickable() {

			@Override
			public void onClick() {
				selectedUpgradeType = Element.TYPE.SLABS;
			}

		}));

		elementUpgradeTypeButtons.add(new CircleButton("Ladder", new Clickable() {

			@Override
			public void onClick() {
				selectedUpgradeType = Element.TYPE.LADDER;
			}

		}));

	}

	private void setupRoomTypeButtons() {
		roomTypeButtons = new ArrayList<CircleButton>();

		roomTypeButtons.add(new CircleButton("room.png", new Clickable() {

			@Override
			public void onClick() {
				selectedRoomType = Room.TYPE.Room;
			}

		}));

		roomTypeButtons.add(new CircleButton("garage.png", new Clickable() {

			@Override
			public void onClick() {
				selectedRoomType = Room.TYPE.Garage;
			}

		}));

		roomTypeButtons.add(new CircleButton("storeroom.png", new Clickable() {

			@Override
			public void onClick() {
				selectedRoomType = Room.TYPE.Storeroom;
			}

		}));

		roomTypeButtons.add(new CircleButton("kitchen.png", new Clickable() {

			@Override
			public void onClick() {
				selectedRoomType = Room.TYPE.Kitchen;
			}

		}));

		roomTypeButtons.add(new CircleButton("toilet.png", new Clickable() {

			@Override
			public void onClick() {
				selectedRoomType = Room.TYPE.Toilet;
			}

		}));
	}

	private void setupMainGuiButtons() {
		mainGuiButtons = new ArrayList<CircleButton>();

		createRoomButton = new CircleButton("createRoom.png", new Clickable() {

			@Override
			public void onClick() {
				createRoomButton.switchState();
			}

		});

		mainGuiButtons.add(createRoomButton);

		mainGuiButtons.add(new CircleButton("worker.png", new Clickable() {
			@Override
			public void onClick() {
				building.hireAndRunNewWorker();
			}
		}));

		upgradeElementButton = new CircleButton("upgradeElement.png", new Clickable() {

			@Override
			public void onClick() {
				upgradeElementButton.switchState();
			}

		});

		mainGuiButtons.add(upgradeElementButton);

	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		camera.update();
		polyBatch.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);
		shapeRenderer.setProjectionMatrix(camera.combined);
		draw();

	}

	private void draw() {

		setupTenantButtons();

		drawBackground();

		for (Element e : building.getElements())
			drawElement(e);

		for (int i = 0; i < building.getConnectionPoints().length; i++)
			for (int j = 0; j < building.getConnectionPoints()[i].length; j++)
				drawConnectionPoint(building.getConnectionPoints()[i][j]);

		for (Worker w : building.getWorkers())
			drawWorker(w);

		if (selectedPoint != null) {

			highlightPoint(selectedPoint);

			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(new Color(0, 0, 1, 0.6f));

			if (targetPoint != null) {
				shapeRenderer.rectLine(selectedPoint.getDrawableX(), selectedPoint.getDrawableY(),
						targetPoint.getDrawableX(), targetPoint.getDrawableY(), 20);

			} else {

				shapeRenderer.rectLine(selectedPoint.getDrawableX(), selectedPoint.getDrawableY(), touchPoint.x,
						touchPoint.y, 20);

			}

			shapeRenderer.end();

			if (targetPoint != null) {
				highlightPoint(targetPoint);
			}

		} else if (selectedRoom != null) {
			highlightRoom(selectedRoom);
			if (targetRoom != null)
				highlightRoom(targetRoom);
		}

		drawGui();

		if (selectedRoomTypeButton != null) {
			selectedRoomTypeButton.setX((int) guiTouchPoint.x);
			selectedRoomTypeButton.setY((int) (Gdx.graphics.getHeight() - guiTouchPoint.y));
			selectedRoomTypeButton.draw(guiShapeRenderer, guiBatch, font);
		}

		if (selectedElementUpgradeTypeButton != null) {
			selectedElementUpgradeTypeButton.setX((int) guiTouchPoint.x);
			selectedElementUpgradeTypeButton.setY((int) (Gdx.graphics.getHeight() - guiTouchPoint.y));
			selectedElementUpgradeTypeButton.draw(guiShapeRenderer, guiBatch, font);
		}

		if (selectedTenantButton != null) {
			selectedTenantButton.setX((int) guiTouchPoint.x);
			selectedTenantButton.setY((int) (Gdx.graphics.getHeight() - guiTouchPoint.y));
			selectedTenantButton.draw(guiShapeRenderer, guiBatch, font);
		}
	}

	private void setupTenantButtons() {
		List<Tenant> tenantsWithoutButton = new ArrayList<Tenant>();

		for (Tenant t : this.building.getWillingTenants())
			if (t.getButton() == null)
				tenantsWithoutButton.add(t);

		for (Tenant t : tenantsWithoutButton) {

			final Tenant tenant = t;
			CircleButton tButton = new CircleButton("Tenant", new Clickable() {

				@Override
				public void onClick() {
					selectedTenant = tenant;
				}

			});
			tenantsButtons.add(tButton);
			t.setButton(tButton);

			setupWillingTenantButtonsPositions();
		}

	}

	private void drawGui() {
		game.enableAlpha();
		guiShapeRenderer.begin(ShapeType.Filled);
		guiShapeRenderer.setColor(game.DOWN_BAR_COLOR);
		guiShapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), (game.MEDIUM_BUTTON_SIZE + 32));
		guiShapeRenderer.end();

		for (CircleButton sb : mainGuiButtons) {
			sb.draw(guiShapeRenderer, guiBatch, font);
		}

		if (createRoomButton.isEnabled()) {
			for (CircleButton rtb : roomTypeButtons)
				rtb.draw(guiShapeRenderer, guiBatch, font);
		}

		if (upgradeElementButton.isEnabled()) {
			for (CircleButton eutb : this.elementUpgradeTypeButtons)
				eutb.draw(guiShapeRenderer, guiBatch, font);
		}

		for (CircleButton tb : tenantsButtons) {
			tb.draw(guiShapeRenderer, guiBatch, font);
		}

		if (tenantToDescribe != null) {

			int posY = (int) (Gdx.graphics.getHeight());
			int posX = (int) (Gdx.graphics.getWidth() - 260);

			int roomsNumber = 0, garagesNumber = 0, kitchensNumber = 0, toiletsNumber = 0, storeroomsNumber = 0;

			for (Room.TYPE ert : tenantToDescribe.getExpectedRoomsTypes())
				switch (ert) {
				case Room:
					roomsNumber++;
					break;
				case Garage:
					garagesNumber++;
					break;
				case Kitchen:
					kitchensNumber++;
					break;
				case Toilet:
					toiletsNumber++;
					break;
				case Storeroom:
					storeroomsNumber++;
					break;
				}

			game.enableAlpha();
			guiShapeRenderer.begin(ShapeType.Filled);
			guiShapeRenderer.setColor(game.DOWN_BAR_COLOR);
			guiShapeRenderer.rect(posX, posY - 252, 260, 252);
			guiShapeRenderer.end();

			guiBatch.begin();

			font.getData().setScale(1.6f);
			font.draw(guiBatch, tenantToDescribe.getExpectedApartmentType().name(), posX + 16, posY - 12);
			font.getData().setScale(1f);
			font.draw(guiBatch, "Rooms:      " + roomsNumber, posX + 16, posY - 32);
			font.draw(guiBatch, "Kitchens:   " + kitchensNumber, posX + 16, posY - 48);
			font.draw(guiBatch, "Toilets:    " + toiletsNumber, posX + 16, posY - 64);
			font.draw(guiBatch, "Garages:    " + garagesNumber, posX + 16, posY - 80);
			font.draw(guiBatch, "Storerooms: " + storeroomsNumber, posX + 16, posY - 96);

			guiBatch.end();
		}
	}

	private void drawBackground() {
		batch.begin();

		batch.draw(panoramaTexture, 0 - panoramaTexture.getWidth(), 0);
		batch.draw(panoramaTexture, 0, 0);
		batch.draw(panoramaTexture, 0 + panoramaTexture.getWidth(), 0);

		batch.draw(undergroundTexture, 0 - undergroundTexture.getWidth(), 0 - undergroundTexture.getHeight());
		batch.draw(undergroundTexture, 0, 0 - undergroundTexture.getHeight());
		batch.draw(undergroundTexture, 0 + undergroundTexture.getWidth(), 0 - undergroundTexture.getHeight());

		batch.end();
	}

	private void highlightElement(Element element) {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(new Color(0f, 0f, 1f, 0.6f));
		shapeRenderer.rectLine(element.getConnectionPoints().get(0).getDrawableX(),
				element.getConnectionPoints().get(0).getDrawableY(),
				element.getConnectionPoints().get(1).getDrawableX(),
				element.getConnectionPoints().get(1).getDrawableY(), 40);
		shapeRenderer.end();

	}

	private void highlightPoint(ConnectionPoint point) {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(new Color(0, 0, 1, 0.1f));
		shapeRenderer.circle(point.getDrawableX(), point.getDrawableY(), 10);
		shapeRenderer.end();
	}

	private void highlightRoom(Room room) {
		FloatArray vertices = new FloatArray();

		for (ConnectionPoint cp : room.getConnectionPoints()) {
			vertices.add(cp.getDrawableX());
			vertices.add(cp.getDrawableY());
		}

		EarClippingTriangulator triangulator = new EarClippingTriangulator();
		ShortArray triangleIndices = triangulator.computeTriangles(vertices);
		Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pix.setColor(0f, 0f, 1f, 0.2f);
		pix.fill();
		Texture texture = new Texture(pix);
		TextureRegion textureRegion = new TextureRegion(texture);
		PolygonRegion polyReg = new PolygonRegion(textureRegion, vertices.toArray(), triangleIndices.toArray());
		PolygonSprite polySprite = new PolygonSprite(polyReg);

		polyBatch.begin();
		polySprite.draw(polyBatch);
		polyBatch.end();
	}

	private void drawWorker(Worker w) {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.CORAL);
		shapeRenderer.circle(w.getPosX(), w.getPosY() + 40, 40);
		shapeRenderer.end();
	}

	private void drawConnectionPoint(ConnectionPoint cp) {
		game.enableAlpha();

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(new Color(0.35f, 0.35f, 0.35f, 0.6f));
		shapeRenderer.circle(cp.getDrawableX(), cp.getDrawableY(), 10);
		shapeRenderer.setColor(new Color(0.4f, 0.4f, 0.4f, 0.6f));
		shapeRenderer.circle(cp.getDrawableX() + 1, cp.getDrawableY() + 1, 8);
		shapeRenderer.setColor(new Color(0.5f, 0.5f, 0.5f, 0.6f));
		shapeRenderer.circle(cp.getDrawableX() + 3, cp.getDrawableY() + 3, 4);
		shapeRenderer.end();
	}

	private void drawElement(Element e) {
		// Texture texture = new
		// Texture(Gdx.files.internal(e.getType().getTexture()));
		Texture texture = textures.get(e.getType().getTexture());
		int x1 = e.getConnectionPoints().get(0).getDrawableX();
		int y1 = e.getConnectionPoints().get(0).getDrawableY();
		int dx = e.getConnectionPoints().get(1).getDrawableX() - x1;
		int dy = e.getConnectionPoints().get(1).getDrawableY() - y1;
		int dist = (int) Math.sqrt(dx * dx + dy * dy);
		float rad = (float) Math.atan2(dy, dx);
		elementSprite = new Sprite(texture, dist + 40, 40);
		elementSprite.setPosition(x1 - 20, y1 - 20);
		elementSprite.setOrigin(20, 20);
		elementSprite.setRotation((float) Math.toDegrees(rad));
		batch.begin();
		elementSprite.draw(batch);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		undergroundTexture.dispose();
		panoramaTexture.dispose();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		guiTouchPoint.set(x, y);
		searchingButton: {
			for (CircleButton tb : tenantsButtons) {
				if (tb.isClicked((int) x, Gdx.graphics.getHeight() - (int) y)) {
					this.selectedTenantButton = tb;
					break searchingButton;
				}
			}

			if (createRoomButton.isEnabled()) {
				for (CircleButton rtb : roomTypeButtons) {
					if (rtb.isClicked((int) x, (int) (Gdx.graphics.getHeight() - y))) {
						selectedRoomTypeButton = rtb;
						break;
					}
				}

				createRoomButton.switchState();
				guiTouchPoint.set(x, y);

			} else if (upgradeElementButton.isEnabled()) {
				for (CircleButton eutb : elementUpgradeTypeButtons) {
					if (eutb.isClicked((int) x, (int) (Gdx.graphics.getHeight() - y))) {
						selectedElementUpgradeTypeButton = eutb;
						break;
					}
				}
				upgradeElementButton.switchState();
			} else {
				selectedRoom = findRoom();
				if (selectedRoom == null)
					selectedPoint = findPoint();
			}
		}
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

		searchingClickedButton: {
			for (CircleButton mgb : mainGuiButtons) {
				if (mgb.isClicked((int) x, Gdx.graphics.getHeight() - (int) y)) {
					mgb.click();
					break searchingClickedButton;
				}
			}
		}

		return false;
	}

	@Override
	public boolean longPress(float x, float y) {

		if (selectedTenantButton != null)
			for (Tenant t : this.building.getWillingTenants())
				if (t.getButton().equals(selectedTenantButton)) {
					guiTouchPoint.set(x, y);
					this.tenantToDescribe = t;
				}
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

		if (selectedRoomTypeButton != null || selectedElementUpgradeTypeButton != null
				|| selectedTenantButton != null) {
			guiTouchPoint.set(x, y);
		} else if (selectedRoom != null) {
			targetRoom = findRoom();
		} else if (selectedPoint != null)
			targetPoint = findPoint();
		else if (selectedElementUpgradeTypeButton != null) {
			this.highlightElement(findElement());
		} else if (selectedRoom != null) {
			highlightRoom(selectedRoom);

			if (targetRoom != null)
				highlightRoom(targetRoom);
		} else {
			if ((camera.position.x - deltaX) > 0
					&& (building.getWidth() * game.DRAWABLE_SCALE) > (camera.position.x - deltaX))
				camera.position.x = camera.position.x - (deltaX * camera.zoom);

			if ((camera.position.y + deltaY) > 0
					&& (camera.position.y + deltaY) < (building.getHeight() * game.DRAWABLE_SCALE))
				camera.position.y = camera.position.y + (deltaY * camera.zoom);
		}

		return false;
	}

	private Apartment findApartment() {
		Room room = findRoom();

		if (room != null)
			if (room.getApartment() != null)
				return room.getApartment();

		return null;
	}

	private Room findRoom() {

		for (Room r : building.getRooms()) {
			if (r.contains(touchPoint))
				return r;
		}

		return null;
	}

	private Element findElement() {
		ConnectionPoint ctPoint = new ConnectionPoint((int) touchPoint.x, (int) touchPoint.y);

		for (Element e : building.getElements())
			if ((ctPoint.getDistanceToDrawable(e.getConnectionPoints().get(0))
					+ ctPoint.getDistanceToDrawable(e.getConnectionPoints().get(1))) <= (e.getConnectionPoints().get(0)
							.getDistanceTo(e.getConnectionPoints().get(1)) * 240))
				return e;

		return null;
	}

	private ConnectionPoint findPoint() {
		for (int i = 0; i < building.getWidth(); i++)
			for (int j = 0; j < building.getHeight(); j++)
				if (Math.abs(building.getConnectionPoints()[i][j].getDrawableX() - touchPoint.x) < 30
						&& Math.abs(building.getConnectionPoints()[i][j].getDrawableY() - touchPoint.y) < 30)
					return building.getConnectionPoints()[i][j];

		return null;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

		if (tenantToDescribe != null) {
			tenantToDescribe = null;
		}
		if (selectedTenantButton != null) {
			selectedTenantButton.click();

			Apartment apartmentToAssign = findApartment();
			if (apartmentToAssign != null)
				selectedTenant.assignTo(apartmentToAssign);

			selectedTenant = null;
			selectedTenantButton = null;
			setupWillingTenantButtonsPositions();

		}
		if (selectedRoomTypeButton != null) {
			selectedRoomTypeButton.click();

			try {
				Room.create(selectedRoomType, getPolygon(new Vector2(x, y)));
			} catch (Exception er) {
				System.out.println(er.getMessage());
			}

			selectedRoomType = null;
			selectedRoomTypeButton = null;
			setupButtonsPositions();
		}
		if (selectedElementUpgradeTypeButton != null) {
			selectedElementUpgradeTypeButton.click();

			try {
				findElement().upgradeTo(selectedUpgradeType);
			} catch (RuntimeException re) {
				System.out.println(re.getMessage());
			}

			selectedUpgradeType = null;
			selectedElementUpgradeTypeButton = null;
			setupButtonsPositions();
		}
		if (selectedRoom != null && targetRoom != null) {
			selectedRoom.connectTo(targetRoom);
		}
		if (selectedPoint != null) {
			if (targetPoint != null) {
				try {
					selectedPoint.connectTo(targetPoint);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}

			targetPoint = null;
			selectedPoint = null;
		}
		if (selectedRoom != null)
			selectedRoom = null;

		currentZoom = camera.zoom;

		return false;
	}

	public List<ConnectionPoint> getPolygon(Vector2 centerPoint) {
		int intx = (int) touchPoint.x;
		int inty = (int) touchPoint.y;

		intx = (intx / game.DRAWABLE_SCALE) * game.DRAWABLE_SCALE + (game.DRAWABLE_SCALE / 2);
		inty = (inty / game.DRAWABLE_SCALE) * game.DRAWABLE_SCALE + (game.DRAWABLE_SCALE / 2);
		return getPolygon(new Vector2(intx, inty), new ArrayList<Vector2>());
	}

	public List<ConnectionPoint> getPolygon(Vector2 centerPoint, List<Vector2> closed) {
		List<ConnectionPoint> points = new ArrayList<ConnectionPoint>();
		closed.add(centerPoint);

		if (centerPoint.x < (game.DRAWABLE_SCALE / 2) || centerPoint.y < (game.DRAWABLE_SCALE / 2)
				|| centerPoint.x > (building.getWidth() * game.DRAWABLE_SCALE - (game.DRAWABLE_SCALE / 2))
				|| centerPoint.y > (building.getHeight() * game.DRAWABLE_SCALE - (game.DRAWABLE_SCALE / 2)))
			return points;

		for (int i = (int) (centerPoint.x / game.DRAWABLE_SCALE); i <= (centerPoint.x / game.DRAWABLE_SCALE + 1); i++) {
			for (int j = (int) (centerPoint.y / game.DRAWABLE_SCALE); j <= (centerPoint.y / game.DRAWABLE_SCALE
					+ 1); j++) {

				ConnectionPoint pointToCheck = building.getConnectionPoints()[i][j];
				if (pointToCheck.getElements().size() >= 2 && !newRoomPoints.contains(pointToCheck)) {
					List<Element> elementsToCheck = new ArrayList<Element>();
					for (Element e : building.getElements()) {
						if (!e.getConnectionPoints().contains(pointToCheck))
							elementsToCheck.add(e);
					}

					for (Element e : elementsToCheck) {
						if (Intersector.intersectSegments(centerPoint,
								new Vector2(pointToCheck.getDrawableX(), pointToCheck.getDrawableY()),
								new Vector2(e.getConnectionPoints().get(0).getDrawableX(),
										e.getConnectionPoints().get(0).getDrawableY()),
								new Vector2(e.getConnectionPoints().get(1).getDrawableX(),
										e.getConnectionPoints().get(1).getDrawableY()),
								null))
							break;
						else if ((elementsToCheck.indexOf(e) + 1) >= elementsToCheck.size()) {
							points.add(pointToCheck);
							newRoomPoints.add(pointToCheck);
						}
					}
				}

			}

		}

		Vector2 neightbours[] = { new Vector2(centerPoint.x - 199, centerPoint.y),
				new Vector2(centerPoint.x, centerPoint.y - 199), new Vector2(centerPoint.x + 199, centerPoint.y),
				new Vector2(centerPoint.x, centerPoint.y + 199) };

		for (int i = 0; i < neightbours.length; i++) {
			final Vector2 neightbour = neightbours[i];
			if (!closed.contains(neightbour)) {
				for (Element e : building.getElements()) {
					if (Intersector.intersectSegments(centerPoint, neightbour,
							new Vector2(e.getConnectionPoints().get(0).getDrawableX(),
									e.getConnectionPoints().get(0).getDrawableY()),
							new Vector2(e.getConnectionPoints().get(1).getDrawableX(),
									e.getConnectionPoints().get(1).getDrawableY()),
							null))
						break;
					else if ((building.getElements().indexOf(e) + 1) >= building.getElements().size())
						points.addAll(getPolygon(neightbour, closed));
				}
			}
		}

		return points;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {

		if ((initialDistance / distance) * currentZoom <= 1 && (initialDistance / distance) * currentZoom >= 0.4)
			camera.zoom = (float) ((initialDistance / distance) * currentZoom);

		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {

		return false;
	}

	@Override
	public void pinchStop() {

	}
}
