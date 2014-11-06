package rok3.projekt.zespver3.client.widgets.customized;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.network.packet.RoomChat__;
import rok3.projekt.zespver3.network.packet.__RoomChatBuffor;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class RoomChatPanel extends Group {
	static int max_row=30;
	static int clear_row=10;
	static int max_char_per_row=64;
	static int max_char_per_mess=128;
	
	
	Label chatprint;
	StringBuilder content;
	TextField inputfield;
	
	float w;
	float h;
	int last_newline;
	int content_index;
	int row_counter;

	private boolean content_change;
	private ScrollPane scrpane;
	private boolean need_clear;
	
	public RoomChatPanel (float w, float h) {
		super();
		
		content = new StringBuilder();
		chatprint= new Label(content,Resources._skin);
		
		inputfield = new TextField("",Resources._skin);
		inputfield.setWidth(w);
		inputfield.setMaxLength(max_char_per_mess);
		inputfield.setMessageText("Enter to send ...");
		inputfield.addListener(new InputListener() {
			RoomChat__ chat = new RoomChat__();
			public boolean keyTyped(InputEvent event, char character) {
				if ( character == 13) {
					chat.content = inputfield.getText().getBytes();
					Resources._nclient.sendRoomChat(chat);
					inputfield.setText("");
				}
				return false;
			};
		} );
		
		scrpane = new ScrollPane(chatprint, Resources._skin);
		scrpane.setSize(w,h-inputfield.getHeight()-1);
		scrpane.setFadeScrollBars(false);
		scrpane.setPosition(0,inputfield.getHeight()+1);
	
		addActor(scrpane);
		addActor(inputfield);
		
		
		this.h = h;
		this.w = w;
	}
	
	private void newRow(){
		row_counter ++;
		if ( row_counter >= max_row ) {
			need_clear = true;
		}
	}
	
	public void updateContent(__RoomChatBuffor chat) {
		content_change=true;
		
		for(int i=0;i<chat.userid.length;i++){
			content.append((char)chat.userid[i]);
		}
		content.append(' ').append('@').append(':').append(' ');
		
		if ( chat.content.length == 0 ) {
			content.append('\n');
			newRow();
			return;
		}
		
		int current_index = chat.userid.length +4;
		int k=0;
		while ( k < chat.content.length ) {
			for ( ;current_index< max_char_per_row; current_index++ ) {
				if( k == chat.content.length ) break;
				content.append((char)chat.content[k]);
				content_index++;
				k++;
			}
			current_index=0;
			content.append('\n');
			newRow();
		}
	}
	
	@Override
	public void act(float delta) {
		if (need_clear) {
			int counter=0;
			int i;
			for( i=0;i< content.length(); i++ ) {
				if ( content.charAt(i) == '\n') {
					counter++;
				}
				if(counter == clear_row ) {
					break;
				}
			}
			content.delete(0,i+1);
			row_counter -= clear_row;
		}
		if ( content_change || need_clear ) {
			chatprint.setText(content);
			scrpane.layout();
			scrpane.setScrollPercentY(1f);
		}
		super.act(delta);
		
		if ( content_change ) content_change=false;
		if (need_clear ) need_clear=false;
	}
}
