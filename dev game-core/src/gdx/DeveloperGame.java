package gdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DeveloperGame extends Game {

	SpriteBatch batch;
	SpriteBatch guiBatch;
	PolygonSpriteBatch polyBatch;
	ShapeRenderer shapeRenderer;
	ShapeRenderer guiShapeRenderer;
	BitmapFont font;

	public int MEDIUM_BUTTON_SIZE;
	public Color DOWN_BAR_COLOR;
	public int DRAWABLE_SCALE;

	@Override
	public void create() {

		batch = new SpriteBatch();
		guiBatch = new SpriteBatch();
		polyBatch = new PolygonSpriteBatch();
		shapeRenderer = new ShapeRenderer();
		guiShapeRenderer = new ShapeRenderer();

		font = new BitmapFont(Gdx.files.internal("roboto.fnt"), false);

		DRAWABLE_SCALE = 200;
		MEDIUM_BUTTON_SIZE = (Gdx.graphics.getWidth() / 10);
		DOWN_BAR_COLOR = Color.DARK_GRAY;
		DOWN_BAR_COLOR.a = 0.7f;

		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render();
	}

	public void enableAlpha() {
		Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
		Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void dispose() {
		batch.dispose();
		guiBatch.dispose();
		polyBatch.dispose();
		shapeRenderer.dispose();
		guiShapeRenderer.dispose();
		font.dispose();
	}

}
