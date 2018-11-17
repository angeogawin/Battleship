package tp7_common;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

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
public abstract class View implements IBaseView {

	protected Color colorgrid;
	protected Color colorblack;
	protected Color colorred;
	protected IModel model;
	protected int mousex;
	protected int mousey;
	protected int mouse_up_x;
	protected int mouse_up_y;
	protected int mouse_move_x;
	protected int mouse_move_y;
	protected boolean drag;
	private int paintcount;
	private Canvas baseCanvas;

	@Override
	public abstract void open();

	@Override
	public abstract void refresh(Object object);

	@Override
	public abstract void notifyConnected(boolean connected);

	protected abstract Text getTxtLog();

	protected abstract void stopOvni();

	protected abstract IControler getControler();
	

	protected boolean circleTool() {
		return false;
	}

	protected boolean rectangleTool() {
		return false;
	}

	protected boolean lineTool() {
		return false;
	}


	protected boolean bombTool() {
		return false;
	}

	protected boolean shipTool() {
		return false;
	}

	protected void initView(Canvas canvas) {
		this.baseCanvas = canvas;
		Display display = Display.getDefault();
		colorgrid = new Color(display, 158, 180, 190);
		colorblack = new Color(display, 0, 0, 0);
		colorred = new Color(display, 255, 0, 0);
	}

	protected void clearModel() {
		synchronized (model) {
			model.clearFigures();
		}
		mousex = 0;
		mousey = 0;
		mouse_up_x = 0;
		mouse_up_y = 0;
		baseCanvas.redraw();
	}

	protected void paintReticule(GC gc) {
		Rectangle cb = baseCanvas.getBounds();
		gc.setForeground(colorgrid);
		for (int y = 0; y < cb.height; y += 32)
			if (y > 0) {
				gc.drawLine(0, y, 0 + cb.width, y);
				gc.drawText(Integer.toString(y / 32), 0, y - 5);
			}
		for (int x = 0; x < cb.width; x += 32)
			if (x > 0) {
				gc.drawLine(x, 0, x, 0 + cb.height);
				gc.drawText(Integer.toString(x / 32), x - 5, 0);
			}
		gc.setForeground(colorblack);
	}


	protected void paintFigures(GC gc) {
		if (model != null)
			synchronized (model) {
				for (String line : model.getFigures()) {
					String[] args = line.split(" ");
					String kw = args[0];
					int[] a = getControler().parseArguments(args);
					if (kw.equals(IConstants.KW_LINE))
						gc.drawLine(a[0], a[1], a[2], a[3]);
					else if (kw.equals(IConstants.KW_CIRCLE))
						gc.drawOval(a[0], a[1], a[2], a[2]);
					else if (kw.equals(IConstants.KW_POLYLINE))
						gc.drawPolyline(getControler().parsePolyline(args));
					else if (kw.equals(IConstants.KW_POLYCIRCLE))
						gc.drawPolyline(getControler().parsePolyline(args));
					else if (kw.equals(IConstants.KW_RECTANGLE))
						gc.drawRectangle(a[0], a[1], a[2], a[3]);
					else if (kw.equals(IConstants.KW_SHIP_)) {
						gc.setForeground(new Color(Display.getDefault(), a[0], a[1], a[2]));
						//gc.setForeground(colorred);
						gc.setLineWidth(3);
						gc.drawRectangle(a[3], a[4], a[5], a[6]);
						gc.setLineWidth(1);
						gc.setForeground(colorblack);
					} else if (kw.equals(IConstants.KW_BOMB))
						gc.drawOval(a[0], a[1], a[2], a[2]);
				}
				if (drag)
					gc.drawRectangle(mousex, mousey, mouse_move_x - mousex, mouse_move_y - mousey);
				else
					gc.drawRectangle(mousex, mousey, mouse_up_x - mousex, mouse_up_y - mousey);

				gc.drawRectangle((Rectangle) model.getOvni());
			}
	}

	@Override
	public void log(String mesg) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				getTxtLog().append(mesg + '\n');
			}
		});

	}

	public void widgetDisposed(DisposeEvent disposeEvent) {
		getControler().dispose();
	}

	public void mouseDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void mouseDown(MouseEvent e) {
		drag = true;
		mousex = e.x;
		mousey = e.y;
	}

	public void mouseUp(MouseEvent e) {
		drag = false;
		mouse_up_x = e.x;
		mouse_up_y = e.y;
		synchronized (model) {
			if (bombTool()) {
				int[] bombs = getControler().toBomb(mousex, mousey);
				String cmd = IConstants.KW_BOMB + " " + bombs[0] + " " + bombs[1] + " " + bombs[2];
				model.addFigure(cmd);
			} else if (lineTool()) {
				String cmd = IConstants.KW_LINE + " " + mousex + " " + mousey + " " + mouse_up_x + " " + mouse_up_y;
				model.addFigure(cmd);
			} else if (rectangleTool()) {
				String cmd = IConstants.KW_RECTANGLE + " " + mousex + " " + mousey + " "
						+ Integer.toString(mouse_up_x - mousex) + " " + Integer.toString(mouse_up_y - mousey);
				model.addFigure(cmd);
			} else if (shipTool()) {
				model.addFigure(getControler().toShipCmd(mousex, mousey, mouse_up_x - mousex, mouse_up_y - mousey));
			} else if (circleTool()) {
				int[] circles = getControler().toCircle(mousex, mousey, mouse_up_x, mouse_up_y);
				String cmd = IConstants.KW_CIRCLE + " " + circles[0] + " " + circles[1] + " " + circles[2];
				model.addFigure(cmd);
			}
		}
		mouse_up_x = mousex;
		mouse_up_y = mousey;
		baseCanvas.redraw(); //will redraw the whole model
	}

	public void mouseMove(MouseEvent e) {
		mouse_move_x = e.x;
		mouse_move_y = e.y;
		synchronized (model) {
			for (String figure : model.getFigures()) {
//implement hover
			}
		}
		if (drag)
			baseCanvas.redraw();
	}

	public void controlMoved(ControlEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void controlResized(ControlEvent e) {
		IControler ctrl = getControler();
		if (ctrl != null)
			ctrl.setViewSize(baseCanvas.getClientArea());
	}

	@Override
	public void notify(Object changed) {
		IControler ctrl = getControler();
		if (ctrl != null)
			ctrl.notify(changed);
	}

	@Override
	public void notifySocketError() {
		stopOvni();
	}

	@Override
	public void refreshCanvas() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				baseCanvas.redraw();
			}
		});
	}

}
