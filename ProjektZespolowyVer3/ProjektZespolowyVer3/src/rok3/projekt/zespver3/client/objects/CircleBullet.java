package rok3.projekt.zespver3.client.objects;

import com.badlogic.gdx.graphics.Texture;

public class CircleBullet extends TmpBullet {

	public CircleBullet(Texture texture) {
		this(texture,800,30);
	}
	
	public CircleBullet(Texture texture,float speed, float damage) {
		super(texture,speed,damage);
	}
}
