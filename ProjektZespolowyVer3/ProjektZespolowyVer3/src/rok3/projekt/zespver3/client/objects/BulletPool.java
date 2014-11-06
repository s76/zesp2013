package rok3.projekt.zespver3.client.objects;

import rok3.projekt.zespver3.client.Resources;

import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pool;

public class BulletPool<T> extends Pool<T> {
	private DelayedRemovalArray<T> controlarray;
	
	public BulletPool() {
		controlarray= new DelayedRemovalArray<T>(false, 64);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected T newObject() {
		return (T)new TmpBullet(Resources._bullet_texture);
	}

	@Override
	public T obtain() {
		T object= super.obtain();
		controlarray.add(object);
		return object;
	}
	
	@Override
	public void free(T object) {
		controlarray.removeValue(object, true);
		super.free(object);
	}
	public DelayedRemovalArray<T> getObjectArray() {
		return controlarray;
	}
	
	public void begin() {
		controlarray.begin();
	}
	
	public void end() {
		controlarray.end();
	}
}
