package tp7_api;



/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface IBaseView {
	void open();
	void log(String message);
	void refresh(Object object);
	void notifySocketError();
	void refreshCanvas();
	void notifyConnected(boolean connected);
	void notify(Object changed);

}
