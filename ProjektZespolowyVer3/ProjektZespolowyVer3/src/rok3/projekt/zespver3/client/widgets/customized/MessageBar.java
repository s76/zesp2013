package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.widgets.models.ProgressiveLabel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MessageBar extends Group{
	private float messagebar_height=30;
	private ProgressiveLabel mess;
	public MessageBar (float width, float height ){
		super();
		
		Image img = new Image(new Texture(Gdx.files.internal(Settings._messagebar_background)));
		img.setSize(width, messagebar_height);
		
		mess = new ProgressiveLabel("",Resources._skin);
		mess.setPosition(10, (messagebar_height-mess.getHeight())/2);
		
		this.addActor(img);
		this.addActor(mess);
	}
	
	public void setText(CharSequence text) {
		mess.setText(text, false);
	}
	
	public void setText(CharSequence text, boolean loading) {
		mess.setText(text, loading);
	}
}
