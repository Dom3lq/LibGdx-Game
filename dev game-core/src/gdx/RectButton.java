package gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class RectButton {
	private Clickable clickable;
	private int x, y, w, h;
	private Color color;
	private Texture texture;
	private String text;

	public RectButton(String icon, Clickable clickable) {
		this.clickable = clickable;

		try {
			this.texture = new Texture(Gdx.files.internal("icon"));
		} catch (GdxRuntimeException e) {
			this.text = icon;

			System.out.println(e.getMessage());
		}
	}

	public void click() {
		clickable.onClick();
	}

	public RectButton setX(int x) {
		this.x = x;
		return this;
	}

	public RectButton setY(int y) {
		this.y = y;
		return this;
	}

	public RectButton setH(int h) {
		this.h = h;
		return this;
	}

	public RectButton setW(int w) {
		this.w = w;
		return this;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void draw(ShapeRenderer guiShapeRenderer, SpriteBatch guiBatch, BitmapFont font) {
		guiShapeRenderer.begin(ShapeType.Filled);

		if (this.color != null)
			guiShapeRenderer.setColor(this.color);
		else
			guiShapeRenderer.setColor(Color.ORANGE);

		guiShapeRenderer.rect(x - (w / 2), y - (h / 2), w, h);

		guiShapeRenderer.end();

		if (this.texture != null) {
			guiBatch.begin();

			guiBatch.draw(texture, (this.x - (texture.getWidth() / 2)), (this.y - (texture.getHeight() / 2)));

			guiBatch.end();
		} else if (this.text != null) {
			guiBatch.begin();

			font.getData().setScale(0.5f);
			font.setColor(Color.WHITE);
			font.draw(guiBatch, this.text, this.x - (text.length() / 2 * 10), this.y + 5);

			guiBatch.end();

		}
	}

	public Boolean isClicked(int x, int y) {
		y = (Gdx.graphics.getHeight() - y);
		if (x >= (this.x - (this.w / 2)) && x <= (this.x + (this.w / 2)) && y >= (this.y - (this.h / 2))
				&& y <= (this.y + (this.h / 2)))
			return true;
		else
			return false;
	}

	public Texture getTexture() {
		return this.texture;
	}
}
