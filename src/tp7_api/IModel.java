package tp7_api;

import java.util.List;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface IModel {
	int getOvniSpeed();
	void setOvniSpeed(int ovniSpeed);
	Object getOvni();
	void addFigure(String figure);
	List<String> getFigures();
	void clearFigures();
}
