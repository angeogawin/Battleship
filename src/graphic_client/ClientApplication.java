package graphic_client;


import tp7_api.IApplication;
import tp7_api.IClientControler;
import tp7_api.IClientView;
import tp7_api.IModel;
import tp7_api.ISessionClient;
import tp7_common.Model;
import tp7_session_client.SessionClient;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public class ClientApplication implements IApplication {
	
	public static void main(String[] arg){
		ClientApplication app = new ClientApplication();
		app.run();
	}

	private void run(){
		IModel model = Model.getInstance();
		IClientControler controler = new ClientControler(this);
		controler.setModel(model);
		ISessionClient client = new SessionClient();
		controler.setNetwork(client);
		client.setClientControler(controler);
		IClientView view = new ClientView();
		controler.setClientView(view);
		controler.runClock();
		view.open();	
	}



}
