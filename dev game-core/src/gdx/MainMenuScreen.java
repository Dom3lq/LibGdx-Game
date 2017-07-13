package gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class MainMenuScreen implements Screen, GestureListener {

	final DeveloperGame game;
	OrthographicCamera camera;
	RectButton newGameButton;

	public MainMenuScreen(final DeveloperGame game) {
		GestureDetector gd = new GestureDetector(this);
		Gdx.input.setInputProcessor(gd);

		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, Gdx.graphics.getHeight() / 2);

		newGameButton = new RectButton("New Game", new Clickable() {

			@Override
			public void onClick() {
				game.setScreen(new GameScreen(game));
				dispose();
			}

		}).setH(100).setW(200).setX(Gdx.graphics.getWidth() / 2).setY(Gdx.graphics.getHeight() / 2 + 50);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		game.guiShapeRenderer.begin(ShapeType.Filled);
		game.enableAlpha();
		game.guiShapeRenderer.setColor(game.DOWN_BAR_COLOR);
		game.guiShapeRenderer.rect(Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() / 2 - 200, 400, 400);
		game.guiShapeRenderer.end();

		game.guiBatch.begin();
		game.font.getData().setScale(0.8f);
		game.font.draw(game.guiBatch, "Welcome to DeveloperGame!", Gdx.graphics.getWidth() / 2 - 150,
				Gdx.graphics.getHeight() / 2 + 150);
		game.guiBatch.end();

		newGameButton.draw(game.guiShapeRenderer, game.guiBatch, game.font);
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

	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		System.out.println((int) x + " " + (int) y + newGameButton);
		if (newGameButton.isClicked((int) x, (int) y))
			newGameButton.click();

		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pinchStop() {
		// TODO Auto-generated method stub

	}

}
