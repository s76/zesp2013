package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.network.packet.RegistrationInfo__;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class RegistrationPanel extends Table{
	private float current_time;
	
	public RegistrationPanel() {
		final TextField userid_field = new TextField("", Resources._skin);
		userid_field.setMaxLength(Settings._max_reister_userid_length);
		userid_field.setMessageText("user id");
		
		final TextField username_field = new TextField("", Resources._skin);
		username_field.setMaxLength(Settings._max_reister_username_length);
		username_field.setMessageText("username");
		
		final TextField password_field = new TextField( "", Resources._skin);
		password_field.setPasswordMode(true);
		password_field.setPasswordCharacter('*');
		password_field.setMaxLength(30);
		password_field.setMessageText("password");
		
		TextButton register_button = new TextButton("Register", Resources._skin);
		register_button.setPosition(180, 320);
		register_button.addListener(new ClickListener(){
			RegistrationInfo__ ri = new RegistrationInfo__();
			float last_try;
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if ( current_time - last_try > 1 ) {
					String raw_username = username_field.getText();
					String raw_userid = userid_field.getText();
					String raw_password = password_field.getText();
					
					if ( raw_username.length() < Settings._min_reister_username_length ) {
						Resources._messbar.setText("Error: username is too short, needs at least "+(Settings._min_reister_username_length+1)+" characters");
						return;
					}
					if ( raw_password.length() < Settings._min_reister_password_length ) {
						Resources._messbar.setText("Error: password is too short, needs at least "+(Settings._min_reister_password_length+1)+" characters");
						return;
					}
					
					if ( raw_password.length() < Settings._min_reister_userid_length ) {
						Resources._messbar.setText("Error: user id is too short, needs at least "+(Settings._min_reister_password_length+1)+" characters");
						return;
					}
					ri.username = raw_username.getBytes();
					ri.one_time_hashed_password = Resources._hash.sha1(raw_password).getBytes();
					ri.userid = raw_userid.getBytes();
					Resources._nclient.sendRegistrationInfo(ri);
					last_try = current_time;
				} else {
					Resources._messbar.setText("Please slow down click speed");
				}
			}
		});
		
		defaults().pad(3, 0, 3, 0);
		defaults().left();
		
		add(username_field).width(200).row();
		add(password_field).width(200).row();
		add(userid_field).width(200).row();
		add(register_button).colspan(2).right();
		
		//debug();
	}
	@Override
	public void act(float delta) {
		current_time += delta;
		super.act(delta);
	}
}
