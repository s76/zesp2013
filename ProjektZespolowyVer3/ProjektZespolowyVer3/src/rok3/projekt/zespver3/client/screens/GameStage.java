package rok3.projekt.zespver3.client.screens;

import static rok3.projekt.zespver3.client.Settings._window_height;
import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.objects.Base;
import rok3.projekt.zespver3.client.objects.BulletPool;
import rok3.projekt.zespver3.client.objects.DummieCharacter;
import rok3.projekt.zespver3.client.objects.Generator;
import rok3.projekt.zespver3.client.objects.TmpBullet;
import rok3.projekt.zespver3.client.widgets.customized.EndGameResultDisplayer;
import rok3.projekt.zespver3.client.widgets.customized.HudDisplayer;
import rok3.projekt.zespver3.network.packet.CharacterState__;
import rok3.projekt.zespver3.network.packet.KillRecord__;
import rok3.projekt.zespver3.network.packet.ShootEvent__;
import rok3.projekt.zespver3.network.packet.__EndGame;
import rok3.projekt.zespver3.network.packet.__GameInitializer;
import rok3.projekt.zespver3.network.packet.__Update;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;


public class GameStage extends Stage {

	public static BulletPool<TmpBullet> bulletpool = new BulletPool<TmpBullet>();
	
	private final CharacterState__ state__;
	private final Array<ShootEvent__> event;	
	
	private boolean state_changed;
	private __Update __updates;
	private boolean event_changed;
	
	private DummieCharacter maincharacter;

	private IntMap<DummieCharacter> charactermap;
	private Array<Rectangle> walls;
	
	/* const */
	private float radius = 15;
	private float diagonal_speed = MathUtils.cosDeg(45);
	
	/* display */
	private OrthogonalTiledMapRenderer mapRenderer;
	private HudDisplayer hud;
	
	/* tmp */
	Vector2 tmp_count_angle = new Vector2();
	Circle tmp_circle = new Circle();
	Vector2 tmp_vector=new Vector2();
	boolean needApplyStateChanges = false;
	
	
	/* debug tools */
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	private Label fps_label;
	private float dx;
	private float dy;
	private float rotation;

	private Generator[] generators;

	private EndGameResultDisplayer egrd;

	
	public GameStage(__GameInitializer gameInit, float width, float height) {
		super(width, height, false);
		

		egrd = new EndGameResultDisplayer();
		egrd.setVisible(false);
		hud = new HudDisplayer();
		hud.setPosition(0, height-hud.getHeight());
		
		OrthographicCamera mapcam = new OrthographicCamera(width, height);
		mapcam.position.x = width/2;
		mapcam.position.y = height/2;
		mapcam.update();
		
		float unitScale = 1f;
		this.mapRenderer = new OrthogonalTiledMapRenderer(new TmxMapLoader().load(Settings._map_url[gameInit.mapselection]), unitScale);
		this.mapRenderer.setView(mapcam);

		Array<RectangleMapObject> recBound = mapRenderer.getMap().getLayers().get("walls").getObjects().getByType(RectangleMapObject.class);

		walls = new Array<Rectangle>(recBound.size);
		for (RectangleMapObject r: recBound) {
			walls.add(r.getRectangle());
		}
		generators = new Generator[2];
		recBound = mapRenderer.getMap().getLayers().get("generators").getObjects().getByType(RectangleMapObject.class);
		generators[0] = new Generator(this,recBound.get(0).getRectangle());
		generators[1]= new Generator(this,recBound.get(1).getRectangle());
		
		addActor(generators[0]);
		addActor(generators[1]);
		
		Base[] base = new Base[2];
		recBound = mapRenderer.getMap().getLayers().get("base1").getObjects().getByType(RectangleMapObject.class); 
		base[0] = new Base(recBound.get(0).getRectangle(),Resources._base_texture);
		
		recBound = mapRenderer.getMap().getLayers().get("base2").getObjects().getByType(RectangleMapObject.class); 
		base[1] = new Base(recBound.get(0).getRectangle(),Resources._base_texture);
		
		this.charactermap = new IntMap<>(gameInit.player_ids.length);

		for(int i=0; i < gameInit.player_ids.length;i++ ) {

			DummieCharacter tmp;
				
			if ( gameInit.team[i] == 0 ) {
				tmp = new DummieCharacter(new Texture(Gdx.files.internal("misc/c4.png")), gameInit.player_ids[i],0,new String(gameInit.player_userids[i]), base[0]);
				tmp.setOrigin(17, 17);
			} else if ( gameInit.team[i] == 1 ){
				tmp = new DummieCharacter(new Texture(Gdx.files.internal("misc/c1.png")), gameInit.player_ids[i],1,new String(gameInit.player_userids[i]),base[1]);
				tmp.setOrigin(18, 17);
			} else throw new GdxRuntimeException("IngameStage: invalid team" );
			
			if ( gameInit.player_ids[i] == Resources._nclient.my_id ) {
				this.maincharacter = tmp;
				this.maincharacter.goAlive();
				hud.watch(this.maincharacter);
			} else {
				addActor(tmp);
			}
			charactermap.put(gameInit.player_ids[i], tmp);
		}
		
		event = new Array<ShootEvent__>(false,16,ShootEvent__.class);
		
		this.state__ = new CharacterState__();
		
		/*
		 * debug 
		 */

		TextButton debugbutton = new TextButton("Debug ON/OFF",Resources._skin);
		debugbutton.addListener(new ClickListener() {
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				Settings._debug_on = !Settings._debug_on;
				super.clicked(event, x, y);
			};
		} );
		debugbutton.setPosition(width-140, height - 50);
		fps_label = new Label("",Resources._skin);
		fps_label.setPosition(width-200, height - 50);
		
		addActor(fps_label);
		addActor(base[0]);
		addActor(base[1]);
		addActor(maincharacter);

		addActor(egrd);
		addActor(hud);
		
	//	addActor(debugbutton);
		

		
		hud.start();
	}


	private void catchInput() {
		if (pause ) return;
		/*
		 *  need to expand
		 */
		dx = 0;
		dy = 0;
		if (Gdx.input.isKeyPressed(Keys.A)) {
			dx--;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			dx++;
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			dy++;
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			dy--;
		}
		if (dx * dy != 0) {
			dx *= diagonal_speed;
			dy *= diagonal_speed;
		}
		
		this.rotation = computeAngle(Gdx.input.getX(), _window_height-Gdx.input.getY(),
				maincharacter.getOriginX() + maincharacter.getX(),
				maincharacter.getOriginY() + maincharacter.getY());
	}


	private void updateToServer (float delta ) {
		if( Resources._nclient.isConnected() ) {
			//System.out.println("gamestage# update to server1");
			Resources._nclient.sendCharacterState(state__);
			Resources._nclient.sendEvents (event.toArray());
			event.clear();
			//System.out.println("gamestage# update to server2");
		}
	}
	
	private void checkWallCollision (float delta) {
		float newX= maincharacter.getX();
		float newY = maincharacter.getY();
		
		tmp_circle.set(newX+ maincharacter.getOriginX(),newY + maincharacter.getOriginY(), radius);
		float k;
		for (Rectangle r : walls) {
			/* 
			 * check if our maincharacter collides with walls
			 */
			if (Intersector.overlaps(tmp_circle, r)) {
				if (  newX+maincharacter.getOriginX() < r.x ) {
					k = Intersector.distanceLinePoint(r.x, r.y, r.x, r.y+r.height, newX+maincharacter.getOriginX(), newY+maincharacter.getOriginY());
					newX -= (radius-k);
				} else if (  newX+maincharacter.getOriginX() + radius*2> r.x + r.width ) {
					k = Intersector.distanceLinePoint(r.x+r.width, r.y, r.x+r.width, r.y+r.height, newX+maincharacter.getOriginX(), newY+maincharacter.getOriginY());
					newX += (radius-k);
				}
				if (  newY+maincharacter.getOriginY() < r.y  ) {
					k =  Intersector.distanceLinePoint(r.x, r.y, r.x+r.width, r.y, newX+maincharacter.getOriginX(), newY+maincharacter.getOriginY());
					newY -= (radius-k);
				} else if ( newY+maincharacter.getOriginY() + radius*2 > r.y + r.height) {
					k = Intersector.distanceLinePoint(r.x, r.y+r.height, r.x+r.width, r.y+r.height, newX+maincharacter.getOriginX(), newY+maincharacter.getOriginY());
					newY += (radius-k);
				}
			}
		}

		maincharacter.move(newX, newY);
	}
	
	Vector2 bottomleft,bottomright,topleft,topright;

	private boolean pause;

	private boolean endGame;

	private void checkBulletCollision () {
		DelayedRemovalArray<TmpBullet> array = bulletpool.getObjectArray();
		/* check if bullet collide with players */
		bulletpool.begin();
		for ( TmpBullet b: array )  {
			boolean collied=false;
			
			for ( DummieCharacter c : charactermap.values() ) {
				if ( c.team == b.source.team ) continue;
				if ( !c.isVisible()) continue;
				
				tmp_circle.set(c.getX()+ c.getOriginX(),c.getY() + c.getOriginY(), radius);
				if (b.collideWith(tmp_circle)) {
					collied = true;
					if ( c.isOnBase()) {
						c.getBase().showEffect();
					} else if (c == maincharacter ) { 
						short hp = (short) (c.getHp() - b.getDamage());
						c.setHp(hp > 0 ? hp : 0);
						if( c.getHp() == 0 ) {
							KillRecord__ kr = new KillRecord__();
							kr.source_id = b.source.id;
							Resources._nclient.sendKillRecord(kr);
						}
					}
				}
			}
			
			if (!collied ) {
				for (Rectangle r : walls) {
					if ( b.collideWith(r)  ) {
						collied = true;
					}
				}
			}
			if (collied ) {
				bulletpool.free(b);
			}
		}
		bulletpool.end();
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!pause && !endGame) {
			maincharacter.shoot();
			ShootEvent__ e = new ShootEvent__();
			event.add(e);
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	private float computeAngle(float cursor_x, float cursor_y, float origin_x,
			float origin_y) {
		return tmp_count_angle.set(cursor_x - origin_x, cursor_y - origin_y)
				.angle();
	}

	public CharacterState__ getState() {
		return state__;
	}
	
	
	@Override
	public void act(float delta) {
		if( state_changed) {
			for(int i=0;i< __updates.states.length;i++) {
				if( __updates == null ) System.out.println("stage# update is null");
				if( __updates.states == null ) System.out.println("stage# update.states is null");
				if( __updates.states[i] == null ) System.out.println("stage# __updates.states[i] is null");
				DummieCharacter k = charactermap.get(__updates.states[i].id);
				k.applyState(delta, __updates.states[i]);
			}
			maincharacter.setTeamScore(__updates.total_team_score[maincharacter.team]);
			state_changed=false;
		}
		if (event_changed) {
			for (int i=0;i<__updates.events.length;i++ ){
				if ( __updates.events[i].source_id ==  Resources._nclient.my_id ) continue;
				charactermap.get(__updates.events[i].source_id).shoot();
			}
			event_changed=false;
		}
		
		if(!endGame ) {
			catchInput();
			
			state__.x =  maincharacter.getX() + dx * delta * maincharacter.getSpeed();;
			state__.y = maincharacter.getY()+ dy* delta * maincharacter.getSpeed();
			state__.rotation = this.rotation;
			state__.hp = (short) maincharacter.getHp();
			
			maincharacter.move(state__.x, state__.y);
			maincharacter.setRotation(state__.rotation);
			
			updateToServer(delta);
		}
		
		super.act(delta);
		
		if ( !endGame) checkCollistion(delta);
		
		if (!Settings._debug_on ) {
			fps_label.setText("");
		} else {
			fps_label.setText("fps: " + Gdx.graphics.getFramesPerSecond());
		}
		
		if ( Gdx.input.getInputProcessor() != this ) System.out.println("stage: input shjt not works again !!");
	}
	
	private void checkCollistion(float delta) {
		for ( DummieCharacter c : charactermap.values() ) {
			tmp_circle.set(c.getX()+ c.getOriginX(),c.getY() + c.getOriginY(), radius);
			if ( Intersector.overlaps(tmp_circle,c.getBase().bound)) {
				c.setOnBase(true);
			} else {
				c.setOnBase(false);
			}
		}
		
		for ( Generator g : generators) {
			boolean isOff=true;
			for ( DummieCharacter c : charactermap.values() ) {
				tmp_circle.set(c.getX()+ c.getOriginX(),c.getY() + c.getOriginY(), 1);
				if ( Intersector.overlaps(tmp_circle,g.getBound()) ) {
					g.isOnBy(c);
					isOff=false;
				} 
			}
			if(isOff) {
				g.isOff();
			}
		}
		
		checkWallCollision(delta);
		checkBulletCollision();
	}


	
	public ShootEvent__[] getEvents () {
		ShootEvent__[] e = new ShootEvent__[event.size];
		for(short i = 0; i < event.size; i++ ) {
			e[i] = event.pop();
		}
		return e;
	}
	
	@Override
	public void draw() {
		mapRenderer.render();
		super.draw();

		if (!Settings._debug_on ) return;
		System.out.println("debug on ");
		shapeRenderer.setProjectionMatrix(getCamera().combined);

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1, 1, 0, 1);
		for (Rectangle r : walls) {
			shapeRenderer.rect(r.x, r.y, r.width, r.height);
		}
		for ( TmpBullet b : bulletpool.getObjectArray() ) {
			shapeRenderer.rect(b.getX(), b.getY(), b.getWidth()*b.getScaleX(), b.getHeight()*b.getScaleY(), b.getOriginX(), b.getOriginY(), b.getRotation(), Color.RED, Color.RED, Color.RED, Color.RED);
		}
		
		for ( DummieCharacter c : charactermap.values() )
			shapeRenderer.circle(c.getX()+c.getOriginX(), c.getY()+c.getOriginY(), radius);
		shapeRenderer.end();
	}

	public void update(__Update __updates) {
		//System.out.printf("gamestage# got update,  %d %d \n)", __updates.states.length, __updates.events.length );
		this.__updates = __updates;
		state_changed=true;
		if( __updates.events.length == 0 ) return;
		event_changed=true;
	}
	
	public void pause() {
		pause = true;
	}
	
	public void resume() {
		pause = false;
	}

	public void endGame(__EndGame endGameObject) {
		endGame= true;
		String[] mvp = new String [endGameObject.mvp_id.length];
		for (int i=0; i<endGameObject.mvp_id.length; i++ ) {
			mvp[i] = charactermap.get(endGameObject.mvp_id[i]).getName();
		}
		egrd.setWinTeam(endGameObject.winteam);
		egrd.setMvp(mvp);
		egrd.setVisible(true);
		hud.stop();
		for ( Generator g : generators ) g.isOff();
	}


	public boolean isGameEnd() {
		return endGame;
	}
}
