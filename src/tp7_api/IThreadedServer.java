package tp7_api;

import java.util.List;

/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface IThreadedServer {
	void runServer() throws Exception;
	void dispose();
	void logRequest(int id, String request);
	void logStatus(Object serviceTask, int id, String status);
	List getClientTasks();
	void endSession(Object serviceTask);
	void connect();
	void disconnect();
	void setServerControler(IServerControler controler);
	void log(String log);
	void addFigure(String figure);
	void broadcast(Object serviceTask, String resp);
	void notify(Object serviceTask);
	List<int[]> getClientIds();
	boolean registerCLient(Object serviceTask, int[] id);
}
