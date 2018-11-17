package graphic_server;


import java.util.List;

import tp7_api.IApplication;
import tp7_api.IServerControler;
import tp7_api.IServerView;
import tp7_api.IThreadedServer;
import tp7_common.Controler;
import tp7_threaded_server.ServiceTask;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public class ServerControler extends Controler implements IServerControler  {
	
	private IThreadedServer server;
	private IServerView serverView;


	public ServerControler(IApplication application) {
		super(application);
	}

	@Override
	public void setNetwork(Object server) {
		this.server = (IThreadedServer) server;	
	}

	@Override
	public List getClientTasks() {
		return server.getClientTasks();
	}

	@Override
	public void connect() {
		server.connect();
	}

	@Override
	public void disconnect() {
		server.disconnect();
	}

	@Override
	public void setServerView(IServerView view) {
		this.serverView = view;
		this.view = view;
		serverView.setServerControler(this);
	}

	@Override
	public void setServerDown() {
		serverView.notifyId("server down");
	}

	
	@Override
	public void addFigure(String figure) {
		super.addFigure(figure);	
	}

	@Override
	public void setHost(String mac, String host, int port) {
		serverView.setHost(mac, host, port);	
	}

	@Override
	public List<int[]> getClientIds() {
		return server.getClientIds();
	}

	@Override
	protected int[] getId() {
		return null;
	}

	@Override
	public void consolideNewClient(Object serviceTask) {
		ServiceTask st = (ServiceTask) serviceTask;
		List<String> figures = getModel().getFigures();
		for (String figure : figures) 
			st.prepare(figure);
		st.flush();
		
	}
	

	
	
	
}
