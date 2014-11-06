package rok3.projekt.zespver3.client.widgets.models;

import rok3.projekt.zespver3.client.Resources;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class FloatNumberDisplayer extends Label {

	private int p1;
	private int p2;
	private float counter;

	public FloatNumberDisplayer(){
		super("", Resources._skin);
		p1=0;
		p2=0;
	}
	
	public void update (float delta){
		counter += delta;
		p1 = ((int)counter)/60;
		p2 = ((int)counter)%60;
		this.setText(p1+":"+p2);
	}

	public void setNumber(float f) {
		p1 = ((int)f)/60;
		p2 = ((int)f)%60;
		this.setText(p1+":"+p2);
	}
}
