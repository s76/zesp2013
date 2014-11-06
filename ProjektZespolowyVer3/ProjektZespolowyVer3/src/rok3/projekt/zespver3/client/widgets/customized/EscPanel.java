package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EscPanel extends Group {

	public EscPanel ( float w, float h) {
		super();
		
		TextButton settings = new TextButton("Settings",Resources._skin);
		settings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
			}
		});
		
		TextButton quitgame = new TextButton("Quit Game",Resources._skin);
		quitgame.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Resources._nclient.sendQuitGameSignal();
				super.clicked(event, x, y);
			}
		});
		
		TextButton quitprogram = new TextButton("Quit Program",Resources._skin);
		quitprogram.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		float ver_pad=6;
		Table buttons = new Table();
		buttons.defaults().pad(ver_pad,10,ver_pad,10);
		buttons.defaults().width(w-20);
		buttons.defaults().height((h-ver_pad*2*3)/3);
		buttons.add(settings);
		buttons.row();
		buttons.add(quitgame);
		buttons.row();
		buttons.add(quitprogram);
		buttons.pack();
		Image background = new Image(new Texture(Gdx.files.internal(Settings._serverselection_bar)));
		background.setSize(w, buttons.getHeight());
		
		addActor(background);
		addActor(buttons);
	}

}
