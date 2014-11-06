package rok3.projekt.zespver3.client.widgets.models;

import rok3.projekt.zespver3.client.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public abstract class FloatingPanel extends Group {
	boolean one_time_trigger;
	float float_timer = 0;
	
	protected float float_duration = 2;
	protected boolean lock_float;

	public FloatingPanel(float width, float height) {

		Image background = new Image(new Texture(
				Gdx.files.internal(Settings._serverselection_background)));
		background.setSize(width, height);

		setSize(width, height);

		addActor(background);
	}

	protected boolean doFloating(float x, float y) {
		if (lock_float) {
			System.out.println("float lok");
			return false;
		}
		one_time_trigger=true;
		lock_float = true;
		
		addAction(Actions.moveTo(x, y, float_duration, Interpolation.exp10Out));
		return true;
	}

	protected void floatingDone() {
	}
	
	@Override
	public void act(float delta) {
		if (float_timer > float_duration) {
			lock_float = false;
		}
		if (lock_float) {
			float_timer += delta;
		} else {
			float_timer = 0;
		}
		super.act(delta);
		if(one_time_trigger && !lock_float ) {
			one_time_trigger=false;
			floatingDone();
		}
	}

	
}
