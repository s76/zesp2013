package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.network.packet.CreateRoom__;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CreateGamePanel extends Group {
	float row_height=40;
	float col_width=50;
	boolean password_button_isOn;
	boolean restriction_button_isOn;
	
	public CreateGamePanel () {
		super();
		
		Label mapname_label = new Label("Map",Resources._skin);
		final SelectBox map_box = new SelectBox(Settings._map_names, Resources._skin);
		
		Label maxpl_label = new Label("Max Player",Resources._skin);
		final SelectBox maxpl_box = new SelectBox(Settings._maxplayer_options,Resources._skin);
		
		Label maxscore_label = new Label("Max Score",Resources._skin);
		final SelectBox maxscore_box = new SelectBox(Settings._maxscore_options,Resources._skin);
		
		Label password_label = new Label("Password",Resources._skin);
		final TextField password_field = new TextField("", Resources._skin);
		password_field.setPasswordMode(true);
		password_field.setPasswordCharacter('*');
		password_field.setVisible(false);
		
		final TextButton password_button = new TextButton("OFF",Resources._skin){
			@Override
			public boolean isPressed() {
				return password_button_isOn;
			}
		};
		password_button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				password_button_isOn= !password_button_isOn;
				password_button.setText(password_button_isOn?"ON":"OFF");
				if (!password_button_isOn) {
					password_field.setText("");
					password_field.setVisible(false);
				} else {
					password_field.setVisible(true);
				}
			};
		} );
		
		
		
		Label restriction_label = new Label("LVL Restriction",Resources._skin);
		
		final Label res_label_min = new Label("min",Resources._skin);
		res_label_min.setVisible(false);
		
		final Label res_label_max = new Label("max",Resources._skin);
		res_label_max.setVisible(false);
		
		final TextField var_field2 = new TextField("0",Resources._skin);
		var_field2.addListener(new InputListener(){
			String last="";
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if( ('0' >character || character > '9') && character != 8  && character != 0) {
					var_field2.setText(last);
				} else {
					last = var_field2.getText();
				}
				return false;
			}
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				var_field2.setText("");
				return false;
			}
		});
		var_field2.setMaxLength(3);
		var_field2.setVisible(false);
		
		final TextField var_field3 = new TextField("999",Resources._skin);
		var_field3.addListener(new InputListener(){
			String last="";
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				System.out.println((int) character);
				if( ('0' >character || character > '9') && character != 8  && character != 0) {
					var_field3.setText(last);
				} else {
					last = var_field3.getText();
				}
				return false;
			}
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				var_field3.setText("");
				return false;
			}
		});
		var_field3.setMaxLength(3);
		var_field3.setVisible(false);
		
		final TextButton restriction_button = new TextButton("OFF",Resources._skin){
			@Override
			public boolean isPressed() {
				return restriction_button_isOn;
			}
		};
		restriction_button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				restriction_button_isOn= !restriction_button_isOn;
				restriction_button.setText(restriction_button_isOn?"ON":"OFF");
				if (!restriction_button_isOn) {
					var_field3.setText("999");
					var_field2.setText("0");
					var_field3.setVisible(false);
					var_field2.setVisible(false);
					res_label_min.setVisible(false);
					res_label_max.setVisible(false);
					
				} else {
					var_field3.setVisible(true);
					var_field2.setVisible(true);
					res_label_min.setVisible(true);
					res_label_max.setVisible(true);
				}
			};
		} );
		
		TextButton create = new TextButton ("Create Game", Resources._skin);
		create.addListener(new ClickListener(){
			CreateRoom__ cr = new CreateRoom__();
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(password_button_isOn){
					String pw = password_field.getText();
					if( pw.length() < Settings._min_room_password_length ) {
						Resources._messbar.setText("Error: password too short");
						return;
					}
					cr.password = pw.getBytes();
				} else {
					cr.password = null;
				}
				if (restriction_button_isOn) {
					String smin = var_field2.getText();
					String smax = var_field3.getText();
					short min = smin.length()==0?0: Short.parseShort(smin);
					short max = smax.length()==0?999: Short.parseShort(smax);
					System.out.println("min="+min+" max="+max);
					if ( min > max ) {
						Resources._messbar.setText("Error: value of min cant be bigger than value of max");
						return;
					}
					cr.res_min = min;
					cr.res_max = max;
				} else {
					cr.res_min = 0;
					cr.res_max = 999;
				}
				
				cr.mapIndexSelection = (byte) map_box.getSelectionIndex();
				cr.max_player = Settings._maxplayer_options[maxpl_box.getSelectionIndex()];
				cr.max_score = Settings._maxscore_options[maxscore_box.getSelectionIndex()];
				
				Resources._nclient.sendCreateRoomSignal(cr);
				Resources._messbar.setText("Sent create room request, please wait",true);
				
			}
		});
		
		Table table = new Table();
		
		table.defaults().pad(3, 0, 3, 0);
		
		table.add(mapname_label).left().colspan(2);table.add(map_box).width(col_width*3).colspan(6).right().padLeft(10);table.row();
		
		table.add(maxpl_label).left().colspan(2);table.add(maxpl_box).width(col_width*3).colspan(6).right().padLeft(10);table.row();	
		
		table.add(maxscore_label).left().colspan(2);table.add(maxscore_box).width(col_width*3).colspan(6).right().padLeft(10);table.row();
		
		table.add(password_label).left().colspan(2);table.add().width(col_width);table.add(password_button).width(col_width);table.add(password_field).width(col_width*3).colspan(4).right().padLeft(10);table.row();
		
		table.add(restriction_label).left().colspan(2);table.add().width(col_width);table.add(restriction_button).width(col_width);
		
		table.add(res_label_min).padLeft(10);table.add(var_field2).width(40);table.add(res_label_max);table.add(var_field3).width(40);table.row();
		table.add(create).colspan(8).padLeft(300);
		
		table.pack();
		
		addActor(table);
	}
}
