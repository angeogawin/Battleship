package tp7_threaded_server;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author pfister - connecthive.com
 *
 */

public  class Util {
	
	private static final int MAX_ELEMENTS = 5;

	private static final boolean LOG = true;
	
	
	private final static String TABS = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31;1m";
	public static final String ANSI_GREEN = "\u001B[32;1m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34;1m";
	public static final String ANSI_PURPLE = "\u001B[35;1m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	private static final boolean PRINT_EXCEPTION = true;
	private static final boolean DISPLAY_COLOR = false;
	// turn true if plugin
	// ANSI Escape in
	// Console

	public static boolean globalEnd;	
	
	

	private static List<String> sharedResource = new ArrayList<String>();
	
	public static void addElement(ServiceTask task,String elm) throws InterruptedException {
		synchronized (sharedResource) {
			if (sharedResource.size() >= MAX_ELEMENTS) {
				log(task,"full, wait for room fo at least one element");
				sharedResource.wait();			
			} else {
				sharedResource.add(elm);
				log(task,"element added " + elm);
				sharedResource.notify();
			}				
		}
	}

	public static String removeElement(ServiceTask task) throws InterruptedException {
		String result = null;
		synchronized (sharedResource) {
			if (sharedResource.size() == 0) {
				log(task,"empty, wait for at least one element");
				sharedResource.wait();
			} else {
				result = sharedResource.remove(sharedResource.size() - 1);
				log(task,"removed element " + result);
				sharedResource.notify();
			}
		}
		return result;
	}

	

	static void log(String mesg) {
		if (LOG)
		  log(null,mesg);
	}


	/**
	 * see https://en.wikipedia.org/wiki/ANSI_escape_code
	 * 
	 * @param id
	 *            : index of a color
	 * @return the color depending on the index
	 */
	static String getColor(int id) {
		if (!DISPLAY_COLOR)
			return "";
		switch (id) {
		case 0:
			return ANSI_RESET + ANSI_CYAN;
		case 1:
			return ANSI_RESET + ANSI_BLACK;
		case 2:
			return ANSI_RESET + ANSI_RED;
		case 3:
			return ANSI_RESET + ANSI_GREEN;
		case 4:
			return ANSI_RESET + ANSI_YELLOW;
		case 5:
			return ANSI_RESET + ANSI_BLUE;
		case 6:
			return ANSI_RESET + ANSI_PURPLE;
		case 7:
			return ANSI_RESET + ANSI_CYAN;
		case 8:
			return ANSI_RESET + ANSI_BLACK;
		case 9:
			return ANSI_RESET + ANSI_RED;
		case 10:
			return ANSI_RESET + ANSI_GREEN;
		case 11:
			return ANSI_RESET + ANSI_YELLOW;
		case 12:
			return ANSI_RESET + ANSI_BLUE;
		case 13:
			return ANSI_RESET + ANSI_PURPLE;
		case 14:
			return ANSI_RESET + ANSI_CYAN;
		default:
			return ANSI_RESET + ANSI_BLACK;
		}

	}

	static void delay(ServiceTask task, int millis) {
		if (millis > 0)
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				if (PRINT_EXCEPTION)
					if (task != null)
						log(task, "task interruption:" + task.id);
					else
						log("interruption");
			}
	}

	public static synchronized void logExperimental(ServiceTask task, String mesg) {
		if (!LOG)
			 return;
		int tab = task == null ? 1 : task.id + 1;
		mesg = TABS.substring(0, tab) + mesg;
		for (int i = 0; i < mesg.length(); i++) {
			String color = task == null ? getColor(1) : getColor(task.id);
			System.out.print(color + mesg.charAt(i));
			delay(task, 2);
		}
		System.out.print('\n');
	}
	
	
	public static synchronized void log(ServiceTask task, String mesg) {
		if (!LOG)
			 return;
		System.out.println(mesg);
	}


	/**
	 * 
	 * @param request
	 *            : the parameters x y radius to be parsed
	 * @return a polyline to draw a circle
	 */
	static String circle_v0(String[] request) {
		String x = request[1];
		String y = request[2];
		String radius = request[3];
		Point center = new Point(Integer.parseInt(x), Integer.parseInt(y));
		Point rad = new Point(Integer.parseInt(x) + Integer.parseInt(radius), Integer.parseInt(y));
		List<Point> circle = center.circle(rad, 10);
		String response = "";
		for (Point point : circle) {
			response += point.x + ":" + point.y;
			response += ";";
		}
		return response;
	}
	
	static String circle_v1(String[] request) {
		String x = request[1];
		String y = request[2];
		String radius = request[3];
		Point center = new Point(Integer.parseInt(x), Integer.parseInt(y));
		Point rad = new Point(Integer.parseInt(x) + Integer.parseInt(radius), Integer.parseInt(y));
		List<Point> circle = center.circle(rad, 10);
		String response = "";
		for (Point point : circle) {
			response += point.x + " " + point.y;
			response += " ";
		}
		return response;
	}

}