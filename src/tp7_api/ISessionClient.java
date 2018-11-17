package tp7_api;




/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface ISessionClient {
	void dispose();
	boolean checkConnection();
	void removePendingSession();
	String getResponse();
	void disconnect(boolean verbose);
	boolean connect(String host, int port);
	boolean sendCommand(String cmd);
	void addCommand(String cmd);
	void clearModel();
	void setModel(IModel model);
	IModel getModel();
	int getLocalPort();
	void setClientControler(IClientControler controler);
	void runClient(String host, int port);
	boolean isConnected(String[] ip);
	void setKeepConnection(boolean keep);
	boolean isKeepConnection();
}
