package rok3.projekt.zespver3.client.objects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TmpBullet extends Image implements Poolable {
	public DummieCharacter source;
	protected float cos_alfa;
	protected float sin_alfa;
	protected float speed;
	protected float damage;
	
	/* tmp */
	private Vector2 topleft;
	private Vector2 topright;

	private Vector2 bottomleft;
	private Vector2 bottomright;
	
	private Vector2 absolute_origin;
	public TmpBullet(Texture texture ) {
		this(texture,900,30);
	}

	public TmpBullet(Texture texture,float speed, float damage ) {
		super(texture);
		this.speed = speed;
		this.damage = damage;
		
		topleft=new Vector2();
		topright=new Vector2();

		bottomleft=new Vector2();
		bottomright=new Vector2();
		
		absolute_origin = new Vector2();
		setRotation(0);
	}
	
	public float getDamage() {
		return damage;
	}
	
	public void setSource(DummieCharacter source) {
		this.source = source;
	}
	
	@Override
	public void setRotation(float degrees) {
		cos_alfa = MathUtils.cosDeg(degrees);
		sin_alfa = MathUtils.sinDeg(degrees);
		super.setRotation(degrees);
	}

	@Override
	public void act(float delta) {
		float k = delta * speed;
		setPosition(getX() + cos_alfa * k, getY() + sin_alfa * k);
		
		super.act(delta);

		absolute_origin.set(getX()+getOriginX(), getY() + getOriginY());
		topleft.set(getX(), getY()+super.getHeight()*super.getScaleY()).sub(absolute_origin).rotate(getRotation()).add(absolute_origin);
		topright.set(getX()+super.getWidth()*super.getScaleX(), getY()+super.getHeight()*super.getScaleY()).sub(absolute_origin).rotate(getRotation()).add(absolute_origin);
		bottomleft.set(getX(),getY()).sub(absolute_origin).rotate(getRotation()).add(absolute_origin);
		bottomright.set(getX()+super.getWidth()*super.getScaleX(), getY()).sub(absolute_origin).rotate(getRotation()).add(absolute_origin);
	}

	@Override
	public void reset() {
		this.remove();
		source = null;
	}
	
	public void initiate (DummieCharacter source, float pos_x, float pos_y, float origin_x, float origin_y, float rotation) {
		setOrigin(origin_x, origin_y);
		setRotation(rotation);
		setPosition(pos_x,pos_y);
		setSource(source);
	}
	
	public boolean collideWith( Rectangle r ) {
		return r.contains(bottomleft) || r.contains(bottomright)|| r.contains(topleft)|| r.contains(topleft);
	}
	
	public boolean collideWith( Circle c ) {
		return c.contains(bottomleft) || c.contains(bottomright)|| c.contains(topleft)|| c.contains(topleft);
	}
}
