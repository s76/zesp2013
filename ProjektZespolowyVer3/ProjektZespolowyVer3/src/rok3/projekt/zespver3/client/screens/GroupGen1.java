package rok3.projekt.zespver3.client.screens;

import com.badlogic.gdx.scenes.scene2d.Group;

public class GroupGen1 extends Group {
	/*
	 * cho nay them tuy` y
	 */
	GroupGen2 g2;
	public GroupGen1() {
		super();
		/*
		 * cho nay them tuy` y
		 */
		g2 = new GroupGen2();
		
		addActor(g2);
		System.out.println("GroupGen1 created");
	}
}
