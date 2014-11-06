package rok3.projekt.zespver3.client.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RectangleBullet extends TmpBullet {
	private Vector2 topleft;
	private Vector2 topright;
	private Vector2 bottomleft;
	private Vector2 bottomright;

	public RectangleBullet(Texture texture) {
		this(texture,800,30);
	}
	
	public RectangleBullet(Texture texture,float speed, float damage) {
		super(texture,speed,damage);
		topleft=new Vector2(0,getHeight());
		topright=new Vector2(getWidth(),getHeight());
		bottomleft=new Vector2(0,0);
		bottomright=new Vector2(getWidth(),0);
	}
	
	
	@Override
	public void act(float delta) {
		float k = delta * speed;
		bottomleft.add(0, k);
		topleft.add(0, k);
		topright.add(0, k);
		bottomleft.add(0, k);
		bottomright.add(0, k);
		Vector2 tmp = getBottomLeft();
		setPosition(tmp.x,tmp.y);
		super.act(delta);
	}
	
	private Vector2 getBottomLeft() {
		return bottomleft.cpy().rotate(getRotation());
	}
	private Vector2 getBottomRight() {
		return bottomright.cpy().rotate(getRotation());
	}
	private Vector2 getTopLeft() {
		return topleft.cpy().rotate(getRotation());
	}
	private Vector2 getTopRight() {
		return topright.cpy().rotate(getRotation());
	}
	
	public boolean collideWith( Rectangle r ) {
		return r.contains(getBottomLeft()) || r.contains(getBottomRight())|| r.contains(getTopLeft())|| r.contains(getTopRight());
	}
	
	public boolean collideWith( Circle c ) {
		return  c.contains(getBottomLeft()) || c.contains(getBottomRight())|| c.contains(getTopLeft()) || c.contains(getTopRight() );
	}
}
