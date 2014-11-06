package rok3.projekt.zespver3.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GroupGen3 extends Group {
	/*
	 * cho nay them tuy` y
	 */

	public GroupGen3() {
		super();
		/*
		 * cho nay them tuy` y
		 */
		
		TextButton tb = new TextButton("Test Button", new Skin (Gdx.files.internal("skin/uiskin.json")));
		tb.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("Click click click...");
				super.clicked(event, x, y);
			}
		});
		addActor(tb);
		
		System.out.println("GroupGen3 created");
	}
}
