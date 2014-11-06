package rok3.projekt.zespver3.client.widgets.models;

import rok3.projekt.zespver3.client.Resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class HpDisplayer extends Group {
	private float base_with;
	private Label hp;
	private Image bg;
	private FloatNumberDisplayer respawn;
	private boolean is_dead;

	public HpDisplayer (float w, float h){
		super();
		bg = new Image(new Texture(Gdx.files.internal("misc/healbar.png")));
		bg.setSize(w, h);
		base_with = w;

		is_dead = false;
		
		hp = new Label("100",Resources._skin){
			@Override
			public void act(float delta) {
				if(!is_dead) super.act(delta);
			}
			
			@Override
			public void draw(SpriteBatch batch, float parentAlpha) {
				if(!is_dead)
				super.draw(batch, parentAlpha);
			}
		};
		hp.setPosition(w/4, 5);
		
		respawn = new FloatNumberDisplayer(){
			@Override
			public void act(float delta) {
				if(is_dead)
				super.act(delta);
			}
			@Override
			public void draw(SpriteBatch batch, float parentAlpha) {
				if(is_dead)
				super.draw(batch, parentAlpha);
			}
		};
		respawn.setPosition(w/4, 5);
		
		
		addActor(bg);
		addActor(hp);
		addActor(respawn);
	}
	
	@Override
	public void act(float delta) {
		if(is_dead) respawn.update(delta);
		super.act(delta);
	}
	
	public void setHp(int k) {
		bg.setWidth((k/100)*base_with);
		hp.setText(Integer.toString(k));
	}
	
	public void setDead(boolean d) {
		is_dead  = d;
	}
	
	public void setRespawn(float f) {
		respawn.setNumber(f);
	}
}
