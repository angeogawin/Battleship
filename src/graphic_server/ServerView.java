package graphic_server;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;
import tp7_api.IControler;
import tp7_api.IServerControler;
import tp7_api.IServerView;
import tp7_common.View;
import tp7_threaded_server.ServiceTask;

/**
 * 
 * @author pfister - connecthive.com initial api and implementation
 *
 */
public class ServerView extends View
		implements IServerView, PaintListener, ControlListener, MouseMoveListener, MouseListener, DisposeListener {

	private static final boolean CONNECT_AT_STARTUP = true;
	protected Shell shell;
	private Canvas canvas;
	private Button btnLine;
	private Button btnCircle;
	private Button btnRectangle;
	private Text txtLog;
	private Spinner speedSpinner;
	private Text textClientsIp;
	private Button btnConnect;
	private Button btnDisconnect;
	private IServerControler controler;
	private Composite composite_header;
	private Label lblIp;
	private Composite composite_connection;
	private Composite composite;

	protected Text getTxtLog() {
		return txtLog;
	}

	public static void ma_in(String[] args) {
		try {
			ServerView window = new ServerView();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void initView(Canvas canvas) {
		super.initView(canvas);
		btnRectangle.setVisible(false);
		btnLine.setVisible(false);
		btnCircle.setVisible(false);
		composite_connection.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);
		lblIp.setText("");
		speedSpinner.setVisible(false);
		if (CONNECT_AT_STARTUP)
			controler.connect();
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		initView(canvas);
		shell.addDisposeListener(this);
		shell.open();
		shell.layout();

		while (!shell.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	protected void setOvniSpeed() {
		synchronized (model) {
			model.setOvniSpeed(speedSpinner.getSelection());
		}
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(614, 433);
		shell.setText("Server Application");
		shell.setLayout(new BorderLayout(0, 0));

		Composite composite_west = new Composite(shell, SWT.NONE);
		composite_west.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		composite_west.setLayoutData(BorderLayout.WEST);

		speedSpinner = new Spinner(composite_west, SWT.BORDER);
		speedSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setOvniSpeed();
			}
		});
		speedSpinner.setMinimum(10);
		speedSpinner.setSelection(50);
		speedSpinner.setBounds(2, 34, 10, 22);

		Composite composite_north = new Composite(shell, SWT.NONE);
		composite_north.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		composite_north.setLayoutData(BorderLayout.NORTH);
		composite_north.setLayout(new GridLayout(1, false));

		composite_header = new Composite(composite_north, SWT.NONE);
		GridData gd_composite_header = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_header.widthHint = 542;
		composite_header.setLayoutData(gd_composite_header);

		btnLine = new Button(composite_header, SWT.RADIO);
		btnLine.setSelection(true);

		btnLine.setBounds(14, 7, 40, 16);
		btnLine.setText("line");

		btnCircle = new Button(composite_header, SWT.RADIO);

		btnCircle.setBounds(43, 7, 26, 16);
		btnCircle.setText("circle");

		btnRectangle = new Button(composite_header, SWT.RADIO);
		btnRectangle.setBounds(25, 7, 37, 16);
		btnRectangle.setText("rectangle");

		Button btnClear = new Button(composite_header, SWT.NONE);
		btnClear.setBounds(10, 3, 37, 25);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearModel();
			}
		});
		btnClear.setText("clear");

		btnConnect = new Button(composite_header, SWT.NONE);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controler.connect();
			}
		});
		btnConnect.setBounds(68, 3, 50, 25);
		btnConnect.setText("start");

		btnDisconnect = new Button(composite_header, SWT.NONE);
		btnDisconnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controler.disconnect();
			}
		});
		btnDisconnect.setBounds(173, 3, 50, 25);
		btnDisconnect.setText("stop");

		lblIp = new Label(composite_header, SWT.NONE);
		lblIp.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblIp.setBounds(237, 5, 287, 20);

		composite_connection = new Composite(composite_header, SWT.NONE);
		composite_connection.setBounds(132, 4, 27, 22);
		composite_connection.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));

		Composite composite_center = new Composite(shell, SWT.NONE);
		composite_center.setBackground(SWTResourceManager.getColor(SWT.COLOR_MAGENTA));
		composite_center.setLayoutData(BorderLayout.CENTER);
		composite_center.setLayout(new FillLayout(SWT.HORIZONTAL));

		canvas = new Canvas(composite_center, SWT.NONE);

		composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.SOUTH);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));

		textClientsIp = new Text(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		textClientsIp.setLayoutData(new RowData(180, 140));

		txtLog = new Text(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtLog.setLayoutData(new RowData(350, 122));
		canvas.addControlListener(this);
		canvas.addPaintListener(this);
		canvas.addMouseMoveListener(this);
		canvas.addMouseListener(this);
	}

	@Override
	public void paintControl(PaintEvent e) {
		paintReticule(e.gc);
		paintFigures(e.gc);
	}

	@Override
	public void refresh(Object changed) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				canvas.redraw();
			}
		});
	}

	@Override
	public void notify(Object changed) {
		if (changed instanceof ServiceTask) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					textClientsIp.setText("");
					List<ServiceTask> clientTasks = controler.getClientTasks();
					for (ServiceTask clientTask : clientTasks) {
						String m = clientTask.toString();
						System.out.println(m);
						textClientsIp.append(m + "\n");
					}
				}
			});
		}
	}

	@Override
	protected void stopOvni() {
		model.setOvniSpeed(0);
		controler.stopClock();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				speedSpinner.setSelection(0);
			}
		});
	}

	@Override
	protected IControler getControler() {
		return controler;
	}

	@Override
	public void setServerControler(IServerControler serverControler) {
		this.controler = serverControler;
		model = controler.getModel();
	}

	@Override
	public void notifyId(String mesg) {
		if (mesg.equals("server down")) {
			notifyConnected(false);
		}
	}

	@Override
	public void setHost(String mac, String host, int port) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				lblIp.setText(host + ":" + port);
				notifyConnected(true);
			}
		});

	}

	@Override
	public void notifyConnected(boolean connected) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (connected) {
					composite_connection.setBackground(SWTResourceManager.getColor(255, 0, 0));
					btnConnect.setEnabled(false);
					btnDisconnect.setEnabled(true);
				} else {
					composite_connection.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
					btnConnect.setEnabled(true);
					btnDisconnect.setEnabled(false);
					lblIp.setText("");
				}
			}
		});

	}

	public void mouseDown(MouseEvent e) {
		// server is not allowed to draw
	}

	public void mouseUp(MouseEvent e) {
		// server is not allowed to draw
	}

}
