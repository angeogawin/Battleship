package tp7_api;

import java.util.List;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface IServerControler  extends IControler{
	void setServerView(IServerView view);
	List getClientTasks();
	void connect();
	void disconnect();
	void setServerDown();
	void addFigure(String figure);
	void setHost(String mac, String host, int port);
	List<int[]> getClientIds();
	void consolideNewClient(Object serviceTask);
}
