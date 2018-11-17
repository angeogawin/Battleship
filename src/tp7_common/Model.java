package tp7_common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import tp7_api.IModel;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public class Model implements IModel{
	
	private List<String> figures = new ArrayList<String>();
	private Rectangle ovni = new Rectangle(10,10,10,10);
	private int ovniSpeed = 100;
	private static Model instance;
	
	private Model() {

	}
	
	public static Model getInstance() {
		if (instance == null)
			 instance = new Model();
		return instance;
	}

	public void addFigure(String cmd) {
		figures.add(cmd);
	}

	public void clearFigures() {
		figures.clear();
	}

	public List<String> getFigures() {
		return figures;
	}

	public Rectangle getOvni() {
		return ovni;
	}

	@Override
	public int getOvniSpeed() {
		return ovniSpeed ;
	}

	@Override
	public void setOvniSpeed(int ovniSpeed) {
		if (ovniSpeed <= 0)
			ovniSpeed = 1;
		this.ovniSpeed = ovniSpeed;	
	}




}
