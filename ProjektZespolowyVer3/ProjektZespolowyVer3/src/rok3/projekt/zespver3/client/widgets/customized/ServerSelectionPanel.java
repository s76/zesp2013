package rok3.projekt.zespver3.client.widgets.customized;

import java.io.IOException;

import rok3.projekt.zespver3.client.Resources;
import rok3.projekt.zespver3.client.Settings;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Client;

/**
 * 
 * @author sheepw76
 *
 */
public class ServerSelectionPanel extends WidgetGroup {
	private List status_list;
	private String[] status_items;
	final Label server_label;

	private Client client=new Client();
	private volatile boolean needUpdate;
	private volatile boolean ping_processing;
	
	
	public ServerSelectionPanel() {
		super();

		server_label = new Label("<Please select server >", Resources._skin);
		
		status_items = new String[Settings._host_address.length];
		
		String[] s = new String[Settings._host_address.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = "";
		}
		
		status_list = new List(s, Resources._skin);
		status_list.setSize(100, status_list.getPrefHeight());
		status_list.setPosition(301, 40);
		status_list.setSelectable(false);
		status_list.setSelectedIndex(-1);

		StringBuilder[] hostlist = new StringBuilder[Settings._host_address.length];
		for (int i = 0; i < Settings._host_address.length; i++) {
			hostlist[i] = new StringBuilder(Settings._host_dns[i]).append(' ')
					.append(' ').append('(').append(Settings._host_address[i])
					.append(')');
		}

		final List list = new List(hostlist, Resources._skin) {
			@Override
			public void act(float delta) {
				status_list.setSelectedIndex(this.getSelectedIndex());
				super.act(delta);
			}
		};
		list.setSize(300, list.getPrefHeight());
		list.setPosition(0, 40);
		
		TextButton refresh_button = new TextButton("Check server status",Resources._skin);
		refresh_button.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ping();
			}
		});
		

		TextButton connect_button = new TextButton("Connect",Resources._skin);
		connect_button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Resources._nclient.tryConnecting(list.getSelectedIndex());
				server_label.setText("Server : "+ list.getSelection());
			}
		});
		connect_button.setPosition(300, 0);
		
		
		addActor(refresh_button);
		addActor(connect_button);
		addActor(status_list);
		addActor(list);
		
	}

	public Label getServerLabel () {
		return server_label;
	}
	public void ping () {
		if (ping_processing ) return;
		ping_processing=true;
		new Thread(){
			public void run() {
				Resources._messbar.setText("Checking servers status",true);
				boolean[] network_status = new boolean[Settings._host_address.length];
				
				for (int i = 0; i < Settings._host_address.length; i++) {
					client.start();
					
					try {
						client.connect(Settings._time_out, Settings._host_address[i],
								Settings._port_tcp, Settings._port_udp);
					} catch (IOException e) {
						System.out.println("server selection# " +e.getMessage());
						network_status[i] = false;
						continue;
					}
					network_status[i] = true;

					client.stop();
				}
				setStatus(network_status);
				ping_processing=false;
				Resources._messbar.setText("Servers status updated");
			};
		}.start();
	}
	

	public void setStatus(boolean[] network_status) {
		for (int i = 0; i < network_status.length; i++) {
			status_items[i] = network_status[i] ? "ONLINE" : "OFF";
		}
		needUpdate=true;
	}
	
	@Override
	public void act(float delta) {
		if (needUpdate) {
			status_list.setItems(status_items);
			needUpdate=false;
		}
		super.act(delta);
	}
}
