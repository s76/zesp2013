package rok3.projekt.zespver3.client.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Base extends Image {
	public Rectangle bound;
	
	public Base (Rectangle r , Texture t ) {
		super(t);
		bound = r;
		setPosition(r.x-9,r.y-9);
		addAction(Actions.fadeOut(0.01f));
	}

	public void showEffect() {
		clearActions();
		addAction(Actions.sequence(Actions.fadeIn(0.1f, Interpolation.exp5Out), Actions.fadeOut(0.1f, Interpolation.exp5In)));
	}	
}
