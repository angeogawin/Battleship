package tp7_api;



/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface IControler {
	void setModel(IModel model);
	void setNetwork(Object networkpart);
	void setViewSize(Object obj);
	IModel getModel();
	void log(String message);
	void log(Object changed, String mesg);
	void dispose();
	void notify(Object changed);
	String getDefaultHost();
	int getDefaultPort();
	void stopClock();
	void setSocketError();
	void addFigure(String figure);
	int[] parseArguments(String[] args);
	int[] parsePolyline(String[] args);
	int[] toBomb(int x, int y);
	int[] toCircle(int x, int y, int x2, int y2);
	String toShipCmd(int x, int y, int w, int h);
	void runClock();

}
