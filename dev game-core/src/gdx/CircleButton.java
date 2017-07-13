package gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.developer.game.main.ConnectionPoint;

public class CircleButton {

	private Boolean enabled = false;
	private Clickable clickable;
	private int x, y, r;
	private Color color;
	private Texture texture;
	private String text;

	public CircleButton(String icon, Clickable clickable) {
		this.clickable = clickable;

		try {
			this.texture = new Texture(Gdx.files.internal(icon));
		} catch (GdxRuntimeException e) {
			this.text = icon;
		}
	}

	public void switchState() {
		if (enabled) {
			enabled = false;
			this.color = Color.ORANGE;
		} else {
			enabled = true;
			this.color = Color.GREEN;
		}
	}

	public void click() {
		clickable.onClick();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setR(int r) {
		this.r = r;
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

		guiShapeRenderer.circle(this.x, this.y, this.r);

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
		if (new ConnectionPoint(x, y).getDistanceTo(new ConnectionPoint(this.x, this.y)) <= this.r)
			return true;
		else
			return false;
	}

	public Texture getTexture() {
		return this.texture;
	}

	public static class Builder {
		private Clickable clickable;
		private String icon;

		public Builder clickable(final Clickable clickable) {
			this.clickable = clickable;
			return this;
		}

		public Builder icon(final String icon) {
			this.icon = icon;
			return this;
		}

		public CircleButton build() {
			return new CircleButton(this.icon, this.clickable);
		}
	}
}
