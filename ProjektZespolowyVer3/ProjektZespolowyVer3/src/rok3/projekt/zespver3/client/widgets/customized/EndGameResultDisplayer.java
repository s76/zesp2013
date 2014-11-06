package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EndGameResultDisplayer extends Group {
	private Label text3;
	private Label text1;
	
	public EndGameResultDisplayer() {
		super();
		Image overlay = new Image(new Texture(
				Gdx.files.internal("misc/bg_result_displayer.png")));
		overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Image bg = new Image(new Texture(
				Gdx.files.internal("misc/severselection_bar.png")));
		bg.setSize(300, 250);
		bg.setPosition(Gdx.graphics.getWidth() / 2 - 150,
				Gdx.graphics.getHeight() / 2 - 100);

		text1 = new Label("",Resources._skin);
		text1.setPosition(bg.getX() + 10, bg.getY() + 200);

		Label text2 = new Label("Winners +50 exp", Resources._skin);
		text2.setPosition(bg.getX() + 10, bg.getY() + 160);

		text3 = new Label("", Resources._skin);
		text3.setPosition(bg.getX() + 10, bg.getY() + 120);

		TextButton okbutton = new TextButton("OK", Resources._skin);
		okbutton.setPosition(bg.getX() + 10, bg.getY() + 80);
		okbutton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Resources._nclient.sendEndGameConfirmation();
				super.clicked(event, x, y);
			}
		});
		okbutton.setWidth(80);

		addActor(overlay);
		addActor(bg);
		addActor(text1);
		addActor(text2);
		addActor(text3);
		addActor(okbutton);	
	}

	public void setMvp(String[] name) {
		if ( name.length == 0 ) return;
		StringBuilder mvp = new StringBuilder();
		for (String s : name)
			mvp.append(s).append(" ");
		text3.setText("MVP : " + mvp + "  +30 exp");
	}
	
	public void setWinTeam(byte winteam) {
		text1.setText(winteam == 0 ? "Team 1 wins" : "Team 2 wins");
	}
	
	
}
