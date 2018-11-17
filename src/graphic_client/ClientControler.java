package graphic_client;

import java.util.Random;

import tp7_api.IApplication;
import tp7_api.IClientControler;
import tp7_api.IClientView;
import tp7_api.ISessionClient;
import tp7_common.Controler;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public class ClientControler extends Controler implements IClientControler{
	
	private ISessionClient client ;
	private IClientView clientView;
	private int[] id;
	private Random rand = new Random();	
	
	public ClientControler(IApplication application) {
		super(application);
	}

	@Override
	public void connect(String[] ip) {
		localHost = ip[0];
		localPort = Integer.parseInt(ip[1]);
		boolean connected = client.connect(localHost, localPort);
		if (connected)
			view.notifyConnected(true);
	}

	@Override
	public void cmd(String cmd) {
		client.sendCommand(cmd);	
	}

	@Override
	public void setNetwork(Object  networkObject) {
		this.client = (ISessionClient) networkObject;	
	}

	@Override
	public boolean isConnected(String[] ip) {
		return client.isConnected(ip);
	}

	@Override
	public void disconnect() {
		client.disconnect(true);	
	}

	@Override
	public void setKeepConnection(boolean keep) {
		client.setKeepConnection(keep);
	}

	@Override
	public boolean isKeepConnection() {
		return client.isKeepConnection();
	}

	@Override
	public void setClientView(IClientView view) {
		this.clientView = view;
		this.view = view;
		clientView.setClientControler(this);
		int maxc = 180;
		int id1 = rand.nextInt(maxc);
		int id2 = rand.nextInt(maxc);
		int id3 = rand.nextInt(maxc);
		id = new int[3];
		id[0] = id1;
		id[1] = id2;
		id[2] = id3;
		clientView.setId(id);
	}

	
	@Override
	public void dispose() {
		super.dispose();
		disconnect();
	}


	@Override
	public int[] getId() {
		return id;
	}

	@Override
	public void noServerAvailable() {
		//clientView.noServerAvailable();	
	}

	@Override
	public void notifySocketClosed() {
		view.notifyConnected(false);	
	}




}
