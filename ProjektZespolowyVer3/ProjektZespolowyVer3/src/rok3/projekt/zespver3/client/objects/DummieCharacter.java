package rok3.projekt.zespver3.client.objects;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;
import rok3.projekt.zespver3.client.screens.GameStage;
import rok3.projekt.zespver3.network.packet.__CharacterState;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class DummieCharacter extends Group {
	private float speed=160;
	private int hp;
	
	public int team;
	public int id;
	
	private int kill_number;
	private int death_number;
	private short team_score;
	private boolean onbase;
	public float res_time;
	private Base base;
	private Image displayimage;
	private boolean alive;
	private Label charname;
	private Image healbar;
	


	public DummieCharacter( Texture texture, int id,int team, String name, Base base ) {
		super();
		displayimage = new Image(texture);
		charname = new Label(name,Resources._skin);
		healbar = new Image(new Texture("misc/healbar.png"));
		healbar.setSize(displayimage.getWidth(), 4);
		this.base= base;
		
		addActor(displayimage);
		setName(name);
		setPosition(base.bound.x+base.bound.width/2-displayimage.getWidth()/2, base.bound.y+base.bound.height/2-displayimage.getHeight()/2);
		this.team = team;
		this.id = id;
		this.hp = 100;
		
		
	}
	
	@Override
	public void act(float delta) {
		if (alive ) {
			if( hp <= 0 ) {
				res_time += delta;
				if( res_time >= Settings._respawn_time) {
					setHp(100);
					res_time=0;
				}
				else return;
			}
		}
		super.act(delta);
		charname.setPosition(getX(),getY()+ displayimage.getHeight()+3);
		healbar.setPosition(getX(),getY()+ displayimage.getHeight()+1);
		healbar.setWidth(((float)getHp()/100)*(displayimage.getWidth()-10));
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		healbar.draw(batch, parentAlpha);
		charname.draw(batch, parentAlpha);
	}
	public void shoot() {
		if ( hp <=0 ) return;
		TmpBullet bullet = GameStage.bulletpool.obtain();
		bullet.initiate(this, getX()+58, getY()+11.5f-Resources._bullet_texture.getHeight()/2,-(58-getOriginX()),
				-(11.5f-Resources._bullet_texture.getHeight()/2-getOriginY()),getRotation());
		getStage().addActor(bullet);
	}
	
	public void move(float x, float y) {
		if (hp <= 0 ) return;
		setPosition(x,y);
	}
	
	
	public void displayGetKillAnimation() {
		Label score_label = new Label("+"+Settings._score_per_kill,Resources._skin);
		this.getStage().addActor(score_label);
		score_label.setPosition(getX()+10, getY()+15);
		score_label.addAction(Actions.sequence(Actions.moveBy(0, 60,0.6f),Actions.removeActor()));
	}
	
	public float getSpeed(){
		return speed;
	}

	public void applyState(float delta, __CharacterState state ) {
		if( alive ) {
			death_number = state.deaths;
			for(int i=0;i<(state.kills-kill_number);i++) {
				displayGetKillAnimation();
			}
			kill_number = state.kills;
			return;
		}
		if(state.hp == -1 ) {
			die();
			return;
		}
		this.clearActions();
		this.addAction(Actions.moveTo(state.x, state.y,0.1f ));
		this.addAction(Actions.rotateBy(state.rotation-this.getRotation(), 0.1f/*(float) (0.002*Math.abs(state.rotation- this.getRotation()))*/));
		this.setHp(state.hp);
	}

	@Override
	public void setVisible(boolean visible) {
		charname.setVisible(visible);
		healbar.setVisible(visible);
		super.setVisible(visible);
	}
	private void die() {
		setVisible(false);
		setPosition(base.bound.x+base.bound.width/2-displayimage.getWidth()/2, base.bound.y+base.bound.height/2-displayimage.getHeight()/2);
	}
	
	public void setHp(int i) {
		if ( hp == 0 && i == 100 ) {
			respawn();
			System.out.println("characer# respawn");
		}
		this.hp = i;
		if( hp == 0 ) {
			die();
		}
	}
	
	private void respawn() {
		setVisible(true);
	}

	public float getRespawnTime() {
		return hp>0?0:Settings._respawn_time-res_time;
	}
	
	public int getHp(){
		return hp;
	}

	public void setOnBase(boolean b) {
		onbase = b;
	}

	public boolean isOnBase() {
		return onbase;
	}

	public int getKills() {
		return kill_number;
	}

	public int getDeaths() {
		return death_number;
	}

	public void setTeamScore(short score) {
		team_score = score;
	}
	
	public short getTeamScore(){
		return team_score;
	}

	public void goAlive() {
		alive = true;
	}

	public boolean isAlive() {
		return alive;
	}
	public Base getBase() {
		return base;
	}

	public void score(int score) {
		Resources._nclient.sendScoreWithGenerator();
	}
}
