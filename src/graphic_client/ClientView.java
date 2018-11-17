package graphic_client;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;
import tp7_api.IClientControler;
import tp7_api.IClientView;
import tp7_api.IControler;
import tp7_common.View;

/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public class ClientView extends View
		implements IClientView, PaintListener, ControlListener, MouseMoveListener, MouseListener, DisposeListener {

	private static final boolean CONNECT_AT_STARTUP = true;
	protected Shell shell;
	private Canvas canvas;
	private Button btnLine;
	private Button btnCircle;
	private Button btnRectangle;
	private Spinner speedSpinner;
	private Text txtCmd;
	private Text textPort;
	private Text textIp;
	private Button btnKeepconnection;
	private IClientControler controler;
	private Button btnBomb;
	private Composite composite_2;
	private Button btnShip;
	private Text txtId;
	private Composite composite_id;
	private Composite composite_4;
	private Button btnConnect;
	private Button btnDisconnect;
	private Text textLog;
	private boolean firstDone;

	public void setClientControler(IClientControler controler) {
		this.controler = controler;
		model = controler.getModel();
	}

	public static void ma_in(String[] args) {
		try {
			ClientView window = new ClientView();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void initView(Canvas canvas) {
		super.initView(canvas);
		//lblOvni.setVisible(false);
		textIp.setText(controler.getDefaultHost());
		textPort.setText(Integer.toString(controler.getDefaultPort()));
		btnKeepconnection.setSelection(((IClientControler) controler).isKeepConnection());
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);
		speedSpinner.setVisible(false);
		if (CONNECT_AT_STARTUP)
			connectIfNot();

	}

	@Override
	public void setId(int[] id) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				txtId.setText(id[0] + "." + id[1] + "." + id[2]);
				composite_id.setBackground(SWTResourceManager.getColor(id[0], id[1], id[2]));
			}
		});
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

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(600, 558);
		shell.setText("Client Application");
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
		speedSpinner.setBounds(2, 31, 10, 22);

		Composite composite_north = new Composite(shell, SWT.NONE);
		composite_north.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		composite_north.setLayoutData(BorderLayout.NORTH);
		composite_north.setLayout(new GridLayout(1, false));

		Composite composite_header = new Composite(composite_north, SWT.NONE);
		GridData gd_composite_header = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_header.heightHint = 60;
		gd_composite_header.widthHint = 576;
		composite_header.setLayoutData(gd_composite_header);

		Composite composite = new Composite(composite_header, SWT.NONE);
		composite.setBounds(0, 27, 576, 28);

		Button btnClear = new Button(composite, SWT.NONE);
		btnClear.setBounds(286, 2, 37, 25);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearModel();
			}
		});
		btnClear.setText("clear");

		txtCmd = new Text(composite, SWT.BORDER);
		txtCmd.setBounds(329, 4, 147, 21);
		txtCmd.setText("circle 10 20 30");

		Button btnSend = new Button(composite, SWT.NONE);
		btnSend.setBounds(482, 2, 40, 25);
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand();
			}
		});
		btnSend.setText("send");

		composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setBounds(10, 4, 270, 21);

		btnLine = new Button(composite_2, SWT.RADIO);
		btnLine.setBounds(3, 3, 40, 16);
		btnLine.setText("line");

		btnRectangle = new Button(composite_2, SWT.RADIO);
		btnRectangle.setBounds(46, 3, 65, 16);
		btnRectangle.setText("rectangle");

		btnCircle = new Button(composite_2, SWT.RADIO);
		btnCircle.setBounds(114, 3, 48, 16);
		btnCircle.setText("circle");

		btnBomb = new Button(composite_2, SWT.RADIO);
		btnBomb.setBounds(165, 3, 53, 16);
		btnBomb.setText("bomb");

		btnShip = new Button(composite_2, SWT.RADIO);
		btnShip.setSelection(true);
		btnShip.setBounds(221, 3, 43, 16);
		btnShip.setText("ship");
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showlog();
			}
		});
		btnNewButton.setBounds(528, 2, 38, 25);
		btnNewButton.setText("log");

		Composite composite_1 = new Composite(composite_header, SWT.NONE);
		composite_1.setBounds(0, 0, 576, 28);

		textPort = new Text(composite_1, SWT.BORDER);
		textPort.setText("8051");
		textPort.setBounds(370, 3, 36, 21);

		textIp = new Text(composite_1, SWT.BORDER);
		//textIp.setText("192.168.1.95");
		textIp.setText("127.0.0.1");
		textIp.setBounds(276, 3, 87, 21);

		btnConnect = new Button(composite_1, SWT.NONE);
		btnConnect.setBounds(413, 1, 48, 25);
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] ip = new String[2];
				ip[0] = textIp.getText();
				ip[1] = textPort.getText();
				controler.connect(ip);
			}
		});
		btnConnect.setText("connect");

		btnDisconnect = new Button(composite_1, SWT.NONE);
		btnDisconnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controler.disconnect();
			}
		});
		btnDisconnect.setBounds(499, 1, 67, 25);
		btnDisconnect.setText("disconnect");

		btnKeepconnection = new Button(composite_1, SWT.CHECK);
		btnKeepconnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controler.setKeepConnection(btnKeepconnection.getSelection());
			}
		});
		btnKeepconnection.setSelection(true);
		btnKeepconnection.setBounds(161, 5, 108, 16);
		btnKeepconnection.setText("keepConnection");

		txtId = new Text(composite_1, SWT.BORDER);
		txtId.setBounds(78, 3, 76, 21);

		composite_id = new Composite(composite_1, SWT.NONE);
		composite_id.setLocation(7, 5);
		composite_id.setSize(64, 16);
		composite_id.setBackground(SWTResourceManager.getColor(238, 130, 238));

		composite_4 = new Composite(composite_1, SWT.NONE);

		composite_4.setBounds(471, 6, 22, 15);

		Composite composite_center = new Composite(shell, SWT.NONE);
		composite_center.setBackground(SWTResourceManager.getColor(SWT.COLOR_MAGENTA));
		composite_center.setLayoutData(BorderLayout.CENTER);
		composite_center.setLayout(new FillLayout(SWT.HORIZONTAL));

		canvas = new Canvas(composite_center, SWT.NONE);
		
		Composite composite_3 = new Composite(shell, SWT.NONE);
		composite_3.setLayoutData(BorderLayout.SOUTH);
		composite_3.setLayout(null);
		
		textLog = new Text(composite_3, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		textLog.setBounds(0, 0, 584, 144);

		canvas.addControlListener(this);
		canvas.addPaintListener(this);
		canvas.addMouseMoveListener(this);
		canvas.addMouseListener(this);
	}

	protected void showlog() {
	
		
	}

	private boolean connectIfNot() {
		String[] ip = new String[2];
		ip[0] = textIp.getText();
		ip[1] = textPort.getText();
		if (!controler.isConnected(ip))
			controler.connect(ip);
		return controler.isConnected(ip);
	}

	protected void sendCommand() {
		if (connectIfNot()) {
			String cmd = txtCmd.getText().trim();
			controler.cmd(cmd);
		} else
			log("unable to connect server " + textIp.getText() + ":" + textPort.getText());
	}

	protected void sendCommand(String cmd) {
		if (connectIfNot()) {
			txtCmd.setText(cmd);
			controler.cmd(cmd);
		} else
			log("unable to connect server " + textIp.getText() + ":" + textPort.getText());
	}

	protected void setOvniSpeed() {
		synchronized (model) {
			model.setOvniSpeed(speedSpinner.getSelection());
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		paintReticule(e.gc);
		// System.out.println("paint " + " (" + paintcount++ + ")");
		paintFigures(e.gc);
	}

	@Override
	protected Text getTxtLog() {
		return textLog;
	}

	protected boolean circleTool() {
		return btnCircle.getSelection();
	}

	protected boolean rectangleTool() {
		return btnRectangle.getSelection();
	}

	protected boolean lineTool() {
		return btnLine.getSelection();
	}

	protected boolean bombTool() {
		return btnBomb.getSelection();
	}

	@Override
	protected boolean shipTool() {
		return btnShip.getSelection();
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
	protected void stopOvni() {
		model.setOvniSpeed(0);
		controler.stopClock();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				speedSpinner.setSelection(model.getOvniSpeed());
			}
		});
	}

	@Override
	protected IControler getControler() {
		return controler;
	}

	public void mouseUp(MouseEvent e) {
		super.mouseUp(e);
		synchronized (model) {
			if (lineTool() || rectangleTool() || circleTool() || bombTool() || shipTool()) {
				sendCommand(model.getFigures().get(model.getFigures().size() - 1));
			}
		}
		if (!firstDone){
			firstDone = true;
			btnBomb.setSelection(true);
			btnShip.setSelection(false);
		}
	}

	@Override
	public void notifyConnected(boolean connected) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (connected) {
					composite_4.setBackground(SWTResourceManager.getColor(255, 0, 0));
					btnConnect.setEnabled(false);
					btnDisconnect.setEnabled(true);
				} else {
					composite_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
					btnConnect.setEnabled(true);
					btnDisconnect.setEnabled(false);
				}
			}
		});
	}
	
	
	

	
	
}
