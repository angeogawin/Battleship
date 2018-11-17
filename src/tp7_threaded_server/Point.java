package tp7_threaded_server;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public class Point {
	int x,y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point rotate(Point point, double theta) {
		double cos = Math.cos(theta);
		double sin = Math.sin(theta);
		int dx = point.x - this.x;
		int dy = point.y - this.y;
		return  new Point(this.x + (int) (dx * cos - dy * sin),this.y + (int) (dx * sin + dy * cos));
	}
	
	public double distance(Point other) {
		return Math.sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y));
	}	
	
	public List<Point> circle(Point radiusPoint, int step){
		List<Point> result = new ArrayList<Point>();
		for (int th = 0; th < 361; th += step) 
			result.add(rotate(radiusPoint, Math.toRadians(th)));
		return result;
	}

}
