package tp7_common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import tp7_api.IApplication;
import tp7_api.IBaseView;
import tp7_api.IConstants;
import tp7_api.IControler;
import tp7_api.IModel;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public abstract class Controler implements IControler {

	private IApplication application;
	protected IBaseView view;
	protected IModel model;

	private Rectangle viewArea = new Rectangle(10, 10, 10, 10);

	protected int localPort = IConstants.DEFAULT_PORT;
	protected String localHost = IConstants.DEFAULT_HOST;

	private Clock clock;
	private boolean clockStopped;

	protected abstract void disconnect();
	protected abstract int[] getId();
	

	@Override
	public abstract void setNetwork(Object networkpart);

	public String getDefaultHost() {
		return localHost;
	}

	public int getDefaultPort() {
		return localPort;
	}

	public Controler(IApplication application) {
		this.application = application;
	}

	@Override
	public void setViewSize(Object obj) {
		synchronized (viewArea) {
			Rectangle area = (Rectangle) obj;
			viewArea.x = area.x;
			viewArea.y = area.y;
			viewArea.width = area.width;
			viewArea.height = area.height;
			synchronized (model) {
				Rectangle ovni = (Rectangle) model.getOvni();
				if (ovni.x > viewArea.width - 2)
					ovni.x = 0;
			}
		}
	}

	public void tick() throws InterruptedException {
		double delay = 0;
		synchronized (model) {
			int speed = model.getOvniSpeed();
			if (speed < 1)
				speed = 1;
			delay = 1000 / model.getOvniSpeed();
			Rectangle ovni = (Rectangle) model.getOvni();
			ovni.x += 1;
			synchronized (viewArea) {
				if (ovni.x == viewArea.width)
					ovni.x = 0;
			}
			if (view != null)
				view.refresh(ovni);
		}
		Thread.sleep((long) delay);
	}

	@Override
	public void runClock() {
		clock = new Clock("Controler");
		// wait the time for the view to be ready
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					return;
				}
				clock.start();
			}
		}).start();
	}


	@Override
	public void setModel(IModel model) {
		this.model = model;
	}

	@Override
	public IModel getModel() {
		return model;
	}

	/*-------------- Timer --------------------------------------------*/

	class Clock extends Thread {
	
		private boolean interrupted;

		public Clock(String id) {
			super();
			this.setName(id);
		}

		private synchronized void log(String m) {
			System.out.println(m);
		}

		@Override
		public void interrupt() {
			if (!isInterrupted()) {
				interrupted = true;
				log("is interrupted (1)");
				super.interrupt();
			}
		}

		@Override
		public boolean isInterrupted() {
			boolean result = super.isInterrupted();
			result = result || interrupted;
			return result;
		}

		void delay(int millis) {
			if (!interrupted && millis > 0)
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					interrupted = true;
					log("is interrupted " + e.toString());
				}
		}

		public void run() {
			while (!interrupted) {
				try {
					if (!clockStopped)
						tick();
				} catch (InterruptedException e) {
					interrupted = true;
					log("is interrupted (2)");
					break;
				}
				delay(IConstants.MODE_DEV?1:1000);
				if (interrupted)
					return;
			}
		}
	}

	@Override
	public void addFigure(String figure) {
		model.addFigure(figure);
		view.refresh(null);
	}

	@Override
	public void stopClock() {
		clockStopped = true;
	}

	private int[] circleFromStream_nu(String mesg) {
		String[] args = mesg.split(" ");
		String shape = (String) args[0];
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int radius = Integer.parseInt(args[3]);
		int x2 = x + radius;
		int y2 = y + radius;
		int[] result = new int[4];
		result[0] = x;
		result[1] = y;
		result[2] = x2;
		result[3] = y2;
		return result;
	}

	private int[] rectangleFromStream_nu(String mesg) {
		String[] args = mesg.split(" ");
		String shape = (String) args[0];
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int x2 = Integer.parseInt(args[3]);
		int y2 = Integer.parseInt(args[4]);
		int[] result = new int[4];
		result[0] = x;
		result[1] = y;
		result[2] = x2;
		result[3] = y2;
		return result;
	}

	private int[] lineFromStream_nu(String mesg) {
		String[] args = mesg.split(" ");
		String shape = (String) args[0];
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int x2 = Integer.parseInt(args[3]);
		int y2 = Integer.parseInt(args[4]);
		int[] result = new int[4];
		result[0] = x;
		result[1] = y;
		result[2] = x2;
		result[3] = y2;
		return result;
	}

	private int[] pointFromStream(String mesg) {
		String[] args = mesg.split(" ");
		String shape = (String) args[0];
		int x = Integer.parseInt(args[1]);
		int y = Integer.parseInt(args[2]);
		int[] result = new int[2];
		result[0] = x;
		result[1] = y;
		return result;
	}

	@Override
	public void log(String message) {
		view.log(message);
	}

	@Override
	public void log(Object clienttask, String message) {
		// Util.log( clienttask, message);
		log(message);
	}

	@Override
	public void dispose() {
		clock.interrupt();
		disconnect();
	}

	@Override
	public void notify(Object changed) {
		view.notify(changed);
	}

	public void setSocketError() {
		view.notifySocketError();

	}

	public void refreshView() {
		view.refreshCanvas();

	}

	public void clearView() {
		// TODO Auto-generated method stub
	}

	public int[] toCircle(int x, int y, int x2, int y2) {
		int[] result = new int[3];
		Integer w = (x2 - x) * 2;
		Integer h = (y2 - y) * 2;
		int d = Math.max(w, h);
		int r = d / 2;
		result[0] = x;
		result[1] = y;
		result[2] = r;
		return result;
	}

	public int[] toBomb(int x, int y) {
		int[] result = new int[3];
		int module = 32;
		x = x / module;
		x = x * module;
		y = y / module;
		y = y * module;
		int r = module / 2;
		result[0] = x + r / 2;
		result[1] = y + r / 2;
		result[2] = r;
		return result;
	}

	
	private  int[] toShip(int x, int y, int w, int h) { // TODO bug
		int[] result = new int[4];
		int module = 32;
		boolean winf = false;
		boolean hinf = false;
		if (w < module) {
			w = module;
			winf = true;
		}
		if (h < module) {
			h = module;
			hinf = true;
		}
		x = x / module;
		x = x * module;
		y = y / module;
		y = y * module;
		w = w / module;
		w = w * module;
		h = h / module;
		h = h * module;
		result[0] = x + module;
		if (winf)
			result[0] -= module;
		result[1] = y + module;
		if (hinf)
			result[1] -= module;
		result[2] = w;
		result[3] = h;
		return result;
	}

	@Override
	public String toShipCmd( int x, int y, int w, int h) {
		int[] ids = getId();
		int[] ships = toShip(x, y, w, h);
		return IConstants.KW_SHIP_ + " " + ids[0] + " " + ids[1] + " " + ids[2] + " " + ships[0] + " " + ships[1] + " " + ships[2] + " " + ships[3];	
	}


	
	public int[] parsePolyline(String[] args) {
		int i = 0;
		List<Point> points = new ArrayList<Point>();
		int x = 0;
		int y = 0;
		for (String arg : args) {
			if (i > 0) {
				if (i % 2 == 0) {
					y = Integer.parseInt(arg);
					Point current = new Point(x, y);
					points.add(current);
				} else {
					x = Integer.parseInt(arg);
				}
			}
			i++;
		}
		i = 0;
		int[] result = new int[points.size() * 2];
		for (Point point : points) {
			result[i++] = point.x;
			result[i++] = point.y;
		}
		return result;
	}

	public int[] parseArguments(String[] args) {
		int i = 0;
		int[] result = new int[args.length - 1];
		for (String arg : args) {
			if (i < args.length - 1) {
				result[i] = Integer.parseInt(args[i + 1]);
				i++;
			}
		}
		return result;
	}



}
