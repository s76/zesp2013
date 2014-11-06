package rok3.projekt.zespver3.client.screens;

import com.badlogic.gdx.scenes.scene2d.Group;

public class GroupGen2 extends Group {
	/*
	 * cho nay them tuy` y
	 */
	GroupGen3 g3;
	public GroupGen2() {
		super();
		/*
		 * cho nay them tuy` y
		 */
		g3 = new GroupGen3();
		
		addActor(g3);
		System.out.println("GroupGen2 created");
	}
}
