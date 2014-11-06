package rok3.projekt.zespver3.client.widgets.models;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class ScrollList extends WidgetGroup {
	private ScrollPane scrollpane;
	private List list;
	
	private boolean isStableSizeSet;
	private float stableWidth;
	private float stableHeight;

	public ScrollList (Skin skin) {
		this(skin.get(ListStyle.class), skin.get(ScrollPaneStyle.class));
	}
	
	public ScrollList (ListStyle liststyle,ScrollPaneStyle scrollpanestyle ) {
		super();
		list = new List (new String[]{""},liststyle);
		scrollpane = new ScrollPane (list,scrollpanestyle);
		scrollpane.setFadeScrollBars(false);
		addActor(scrollpane);
	}

	/**
	 * Automatically call scrollpane.layout() and scrollpane.pack()
	 */
	public void setItems(Object[] items) {
		list.setItems(items);
		scrollpane.layout();
		scrollpane.pack();
		if (isStableSizeSet) {
			scrollpane.setSize(stableWidth, stableHeight);
			/* set isStableSizeSet to false? */
		}
	}

	public void setStableSize(float w, float h) {
		stableWidth = w;
		stableHeight = h;
		scrollpane.setSize(w,h);
		isStableSizeSet = true;
	}
	
	public int getSelectedIndex(){
		return list.getSelectedIndex();
	}
}
