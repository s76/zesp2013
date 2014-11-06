package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.widgets.models.FloatingPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsPanel extends FloatingPanel {
	private TextButton settings_button;
	private float bar_height = 50;
	private boolean isOnTop = true;
	
	private ServerSelectionPanel ssp;
	public SettingsPanel(float width, float height) {
		super(width, height);
		
		Image bottombar = new Image(new Texture(
				Gdx.files.internal(Settings._serverselection_bar)));
		bottombar.setSize(width, bar_height);
		
		settings_button = new TextButton("Settings", Resources._skin);
		settings_button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (isOnTop) {
					doFloating(0,getHeight()- (bar_height+150));
				} else {
					doFloating(0, getHeight() - bar_height);
				}
			}
			
		});
		settings_button.setPosition(width - 100, 0);
		settings_button.setSize(100, bar_height);
	

		ssp = new ServerSelectionPanel();
		ssp.setPosition(10, bar_height+10);
				
		Label serverlabel = ssp.getServerLabel();
		serverlabel.setPosition(10, 10);
		serverlabel.setSize(width - 110, bar_height - 20);
		
		addActor(bottombar);
		addActor(ssp);
		addActor(settings_button);
		addActor(serverlabel);
		ssp.ping();
	}
	
	@Override
	protected boolean doFloating(float x, float y) {
		if ( super.doFloating(x, y) ) {
			isOnTop = !isOnTop;
			return true;
		}
		return false;
	}
	
	@Override
	protected void floatingDone() {
		settings_button.setText(isOnTop?"Settings":"Done");
	}
}
