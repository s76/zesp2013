package rok3.projekt.zespver3.client.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class TestS implements Screen {

	private Stage stage;

	@Override
	public void show() {
		GroupGen1 g1 = new GroupGen1();
		stage = new Stage(1024,648,false);
		stage.addActor(g1);
		Gdx.input.setInputProcessor(stage);
	}

	
	@Override
	public void resize(final int width, final int height) {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}



	@Override
	public void hide() {
	}
}