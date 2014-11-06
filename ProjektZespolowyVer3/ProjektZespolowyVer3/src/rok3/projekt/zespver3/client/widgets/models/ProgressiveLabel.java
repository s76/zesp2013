package rok3.projekt.zespver3.client.widgets.models;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * 
 * @author sheepw76
 *
 */
public class ProgressiveLabel extends Label {
	private final float dot_time = 0.35f;
	private final int max_dot=20;
	private int dot_counter=0;
	private float loading_timer=0;
	private boolean loading;
	private CharSequence originText;
	StringBuilder textbuilder;
	
	public ProgressiveLabel(CharSequence text, Skin skin) {
		this(text, skin.get(LabelStyle.class));
	}
	
	public ProgressiveLabel(CharSequence text, LabelStyle style) {
		super(text, style);
	}
	
	/**
	 * DO NOT CALL THIS METHOD DIRECTLY, USE {@link #setText(CharSequence text, boolean loading)} INSTEAD !
	 */
	public void setText(CharSequence newText) {
		super.setText(newText);
	}
	
	/**
	 * Expanded method of {@link #setText(CharSequence newText)}
	 * @param text
	 * @param loading
	 * 		show graphical presentation of progress if <b>loading==true</b>
	 */
	public void setText(CharSequence text,boolean loading) {
		setText(text);
		this.loading= loading;
		if (loading ) {
			originText= text;
			loading_timer = 0;
			textbuilder = new StringBuilder(text).append(' ');
		}
	}
	
	@Override
	public void act(float delta) {
		if (loading ) {
			loading_timer+= delta;
			if ( loading_timer > dot_time ) {
				loading_timer -= dot_time;
				dot_counter++;
				textbuilder.append('.');
				setText(textbuilder);
			}
			if ( dot_counter > max_dot ) {
				textbuilder = new StringBuilder(originText).append(' ');
				dot_counter= 0;
			}
		} else {
			loading_timer=0;
			dot_counter=0;
		}
		super.act(delta);
	}
}
