/**
 * 
 */
package rok3.projekt.zespver3.client;

import rok3.projekt.zespver3.client.widgets.customized.MessageBar;
import rok3.projekt.zespver3.utils.GameNetworkClient;
import rok3.projekt.zespver3.utils.Hasher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * @author sheepw76
 *
 */
public class Resources {
	public static Texture _bullet_texture; 
	public static Skin _skin;
	public static GameNetworkClient _nclient;
	public static MessageBar _messbar;
	public static Hasher _hash;
	public static Texture _base_texture;
	public static Texture _portal_texture2;
	public static Texture _portal_texture1;
	
	public static void initialize(MyGame game )  {
		_skin = new Skin (Gdx.files.internal("skin/uiskin.json"));
		_nclient = new GameNetworkClient(game);
		_bullet_texture = new Texture(Gdx.files.internal("misc/bullet.png"));
		_base_texture = new Texture(Gdx.files.internal("misc/base.png"));
		_portal_texture1 = new Texture(Gdx.files.internal("misc/loading1.png"));
		_portal_texture2 = new Texture(Gdx.files.internal("misc/loading2.png"));
		_messbar = new MessageBar(Settings._window_width, 40);
		_hash = new Hasher();
		System.out.println("Resources#initialize - OK");
	}
	
	
}
