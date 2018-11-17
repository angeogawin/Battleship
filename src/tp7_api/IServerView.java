package tp7_api;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface IServerView extends IBaseView {
	void setServerControler(IServerControler serverControler);
	void notifyId(String mesg);
	void setHost(String mac, String host, int port);
}
