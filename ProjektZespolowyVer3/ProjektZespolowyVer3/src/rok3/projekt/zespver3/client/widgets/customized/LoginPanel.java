package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.network.packet.LoginInfo__;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LoginPanel extends Table {
	private float current_time;
	
	public LoginPanel() {
		final TextField username_field = new TextField("", Resources._skin);
//		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/SuperRound.ttf"));
//		username_field.getStyle().font = generator.generateFont(12);
		username_field.setMaxLength(Settings._max_reister_username_length);
		username_field.setMessageText("username ");
		
		
		final TextField password_field = new TextField( "", Resources._skin);
		password_field.setPasswordMode(true);
		password_field.setPasswordCharacter('*');
		password_field.setMessageText("password ");
		password_field.setMaxLength(30);
		
		TextButton login_button = new TextButton("Login", Resources._skin);
		login_button.addListener(new ClickListener(){
			LoginInfo__ li = new LoginInfo__();
			float last_try;
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if ( current_time - last_try > 1 ) {
					last_try = current_time;
					if ( !Resources._nclient.isConnected()){
						Resources._messbar.setText("Error: no connection");
						return;
					}
					
					String raw_username = username_field.getText();
					String raw_password = password_field.getText();
					
					if ( raw_username.length() < Settings._min_reister_username_length ) {
						Resources._messbar.setText("Error: username is too short, needs at least "+Settings._min_reister_username_length+" characters");
						return;
					}
					if ( raw_password.length() < Settings._min_reister_password_length ) {
						Resources._messbar.setText("Error: password is too short, needs at least "+Settings._min_reister_password_length+" characters");
						return;
					}
					
					li.username = raw_username.getBytes();
					li.one_time_hashed_password = Resources._hash.sha1(raw_password).getBytes();
					
					Resources._nclient.sendLoginInfo(li);
					Resources._messbar.setText("Trying loging in", true);
				} else {
					Resources._messbar.setText("Please slow down click speed");
				}
			}
		});
		
		defaults().pad(3, 0, 3, 0);
		defaults().left();
		
		add(username_field).width(200).row();
		add(password_field).width(200).row();
		add(login_button).colspan(2).right();
		
		//debug();
	}
	@Override
	public void act(float delta) {
		current_time += delta;
		super.act(delta);
	}
}
