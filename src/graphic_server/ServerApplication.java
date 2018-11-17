package graphic_server;


import tp7_api.IApplication;
import tp7_api.IModel;
import tp7_api.IServerControler;
import tp7_api.IServerView;
import tp7_api.IThreadedServer;
import tp7_common.Model;
import tp7_threaded_server.ThreadedServer;

/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public class ServerApplication implements IApplication {
	

	
	public static void main(String[] arg){
		ServerApplication app = new ServerApplication();
		app.run();
	}

	private void run(){
		IModel model = Model.getInstance();
		IServerControler controler = new ServerControler(this);
		controler.setModel(model);
		IThreadedServer server = new ThreadedServer();
		controler.setNetwork(server);
		server.setServerControler(controler);
		IServerView view = new ServerView();
		controler.setServerView(view);
		controler.runClock();
		view.open();
	}


}
