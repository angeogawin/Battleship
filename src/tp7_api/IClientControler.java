package tp7_api;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface IClientControler  extends IControler{
	void connect(String[] ip);
	void setClientView(IClientView view);
	void cmd(String cmd);
	boolean isConnected(String[] ip);
	void disconnect();
	void setKeepConnection(boolean keep);
	boolean isKeepConnection();
	void refreshView();
	void clearView();
	int[] getId();
	void noServerAvailable();
	void notifySocketClosed();
}
