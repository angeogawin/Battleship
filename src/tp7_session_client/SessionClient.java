
package tp7_session_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import tp7_api.IClientControler;
import tp7_api.IConstants;
import tp7_api.IModel;
import tp7_api.ISessionClient;

/**
 * 
 * @author pfister - connecthive.com initial api and implementation
 *
 */
public class SessionClient implements ISessionClient {

	private static final boolean LOG = false;

	private IModel model;
	private String serverName = "-1";
	private int serverPort = -1;
	private String response;
	private PrintWriter out;
	private BufferedReader in;
	private Socket clientSocket;
	private SessionThread sessionTask;
	private String host;
	private boolean pending;

	private IClientControler controler;
	private boolean keepConnection = true; // by default

	private List<String> keywords = new ArrayList<String>();

	public SessionClient() {
		keywords.add(IConstants.KW_CIRCLE + " ");
		keywords.add(IConstants.KW_BOMB + " ");
		keywords.add(IConstants.KW_LINE + " ");
		keywords.add(IConstants.KW_POLYCIRCLE + " ");
		keywords.add(IConstants.KW_POLYLINE + " ");
		keywords.add(IConstants.KW_RECTANGLE + " ");
		keywords.add(IConstants.KW_SHIP_ + " ");
	}

	@Override
	public int getLocalPort() {
		return clientSocket.getLocalPort();
	}

	@Override
	public IModel getModel() {
		return model;
	}

	@Override
	public void setModel(IModel model) {
		this.model = model;
	}

	@Override
	public void dispose() {
		info("ending session");
		disconnect(true); // FP170325
	}

	@Override
	public boolean checkConnection() {
		boolean result = isConnected();
		if (!result)
			viewClearConnexion();// error("plus de connexion");
		return result;
	}

	private void openSocket() throws UnknownHostException, IOException {
		if (isConnected()) {
			info("déjà connecté");
			return;
		}
		//info("try to open socket " + serverName + "-" + serverPort);
		if (serverPort == -1)
			throw new IOException("port error");

		if (serverName == null || serverName.equals("-1"))
			throw new IOException("host error");

		try {
			clientSocket = new Socket(serverName, serverPort);
			if (LOG)
				clog("success for " + serverName + "." + serverPort);
			// pendingSessions.remove(this);
			pending = false;
			host = clientSocket.getLocalAddress().getHostAddress();
			viewSetLocal(host, serverPort);// port);

		} catch (ConnectException e) {
			if (pending) { // FP170325
				error("No connection " + e.toString());// + e.toString());
				error("No server: " + serverName + "-" + serverPort);
				controler.noServerAvailable();
				if (LOG)
					clog("No server: " + serverName + "-" + serverPort);
				in = null;
				out = null;
				removePendingSession();
				sessionTask.interrupt();
				throw new IOException("connection error");
			}
		}
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		out = new PrintWriter(clientSocket.getOutputStream(), true); // autoflush

		sendIdentification();
		info("Connected to the server");
	}

	private void sendIdentification() {
		String ids = "";
		int[] id = controler.getId();
		for (int i : id) {
			ids += Integer.toString(i);
			ids += " ";
		}
		sendCommand("Helo " + ids.trim());
	}

	@Override
	public void removePendingSession() {
		pending = false;
		/*
		 * try { SessionThread pending = pendingSessions.get(0);
		 * pending.abortConnection(); pending.interrupt(); pending = null; //
		 * FP170325 } catch (Exception e) {
		 * 
		 * } pendingSessions.clear();
		 */
	}

	private void closeSocket(boolean verbose) {
		if (!isConnected()) {
			if (verbose)
				info("session allready closed");
			return;
		}
		info("closing socket");

		if (clientSocket != null)
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (in != null)
			try {
				in.close();
				in = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (out != null) {
			out.close();
			out = null;
		}
		controler.notifySocketClosed();

	}

	private boolean doAddFigure() {
		for (String kw : keywords) {
			if (response.startsWith(kw)) {
				addFigure();
				return true;
			}
		}
		return false;
	}

	private void handleServerResponse() {
		if (response == null) {
			info("session ended");
			closeSocket(true);
			return;
		} else {
			response = response.trim();
			if (response.contains("STOP-SERVER OK"))
				info("server will shut down");
			else if (!doAddFigure())
				clean();
		}
		viewRefreshModel();
	}

	private void clear() {
		if (LOG)
			clog("clear");
		if (model != null) {
			synchronized (model) {
				model.clearFigures();
			}
		}
		viewClear();
	}

	private void addFigure() {
		if (LOG)
			clog("figure: [" + response + "]");
		addtoNetCommands(response);
		viewRefresh();
	}

	private void refresh() { // kk2
		if (LOG)
			clog("refresh");
		viewRefresh();
	}

	private void readServer() {
		response = null;
		try {
			response = in.readLine(); // bloquant - attendre la réponse du
										// serveur
			if (LOG)
				clog("received " + response);

		} catch (IOException e) {
			info("socket closed");
			closeSocket(false);
			return;
		} catch (Exception e) {
			error("(2) while read response (" + e.toString() + ")");
			closeSocket(false);
			return;
		}
		if (response == null) {
			info("connection end");
			closeSocket(false);
			return;
		}
	}

	private static void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getResponse() {
		return response;
	}

	@Override
	public void disconnect(boolean verbose) {
		if (isConnected()) {
			closeSocket(verbose);
		} else if (verbose)
			clog("allready disconnected !");
		if (sessionTask != null && !sessionTask.isInterrupted())
			sessionTask.interrupt();
	}

	@Override
	public boolean connect(String host, int port) {
		if (isConnected()) {
			if (LOG)
				clog("allready connected !");
			return true;
		}
		if (pending)
			pending = false;
		sessionTask = new SessionThread(host, port);
		sessionTask.start();
		// pendingSessions.add(session);
		pending = true;
		delay(100);
		return isConnected();
	}

	private boolean isConnected() {
		boolean result = clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected();
		if (!result) {
			if (LOG) {
				if (clientSocket == null)
					clog("clientSocket == null");
				if (clientSocket != null && clientSocket.isClosed())
					clog("clientSocket.isClosed()");
				if (clientSocket != null && !clientSocket.isConnected())
					clog("!clientSocket.isConnected()");
			}
		}
		if (LOG && result)
			clog("connected ");
		return result;
	}

	private boolean openConnection() {
		try {
			openSocket();
			return true;
		} catch (UnknownHostException e) {
			error("Unknown server: " + e.getMessage());
		} catch (IOException e) {
			//error("Connection error");
		} catch (Exception e) {
			error("Unknown error) " + e.toString());
		}
		return false;
	}

	@Override
	public boolean sendCommand(String cmd) {
		if (cmd != null && !cmd.isEmpty()) {
			// info("send " + serverName + ":" + serverPort + "[" + cmd + "]");
			if (!isConnected())
				connect(serverName, serverPort);
			if (isConnected()) {
				out.println(cmd);
				if (!keepConnection) {
					soonCloseConnection();
				}
				return true;
			} else
				error("no session");
		} else
			clog("command is empty !");
		return false;
	}

	private void soonCloseConnection() {
		controler.log("will close connection");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					return;
				}
				controler.log("closing connection after 5s");
				disconnect(true);
			}
		}).start();
	}

	class SessionThread extends Thread {

		private boolean interrupted;

		public SessionThread(String host, int port) {
			if (LOG)
				clog("session thread, try to connect " + host + "." + port);
			if (isConnected()) {
				clog("should not happen, allready connected !");
				disconnect(true);
			}
			serverName = host;
			serverPort = port;
		}

		@Override
		public void interrupt() {
			if (!isInterrupted()) {
				interrupted = true;
				//clog("connection " + serverName + " " + serverPort + " is interrupted");
				super.interrupt();
			}
		}

		@Override
		public boolean isInterrupted() {
			boolean result = super.isInterrupted();
			result = result || interrupted;
			return result;
		}

		public void abortConnection_nu() {
			try {
				clientSocket.close();
			} catch (IOException e) {

			}
		}

		@Override
		public void run() {
			if (openConnection()) {
				while (!isInterrupted() && checkConnection()) {
					readServer();
					handleServerResponse();
				}
				disconnect(false);
			}
		}
	}

	private void addtoNetCommands(String cmd) { // kk300
		if (model == null)
			return;
		synchronized (model) {
			model.addFigure(cmd);
		}
	}

	@Override
	public void addCommand(String cmd) { // kk300
		if (cmd != null)
			addtoNetCommands(cmd);
	}

	@Override
	public void clearModel() {
		// netObjects.clear();
		// netStrings.clear();
		if (model == null)
			return;
		synchronized (model) {
			model.clearFigures();
		}
	}

	private void info(String info) {
		controler.log("[i]" + info);
	}

	private void clog(String mesg) {
		controler.log(mesg);
	}

	private void error(String err) {
		controler.log("[err]" + err);
	}

	private void viewSetLocal(String host, int serverPort) {
		//info("viewSetLocal " + host);
	}

	private void viewRefreshModel() {
		// info("viewRefreshModel");
	}

	private void clean() {
		// info("TODO clean");
	}

	private void viewClearConnexion() {
		// info("viewClearConnexion");
	}

	private void viewClear() {
		info("viewClear");
		controler.clearView();
	}

	private void viewRefresh() {
		controler.refreshView();
	}

	@Override
	public void setClientControler(IClientControler controler) {
		this.controler = controler;
		model = controler.getModel();
	}

	@Override
	public void runClient(String host, int port) {
		connect(host, port);
	}

	@Override
	public boolean isConnected(String[] ip) {
		if (clientSocket == null)
			return false;
		if (sessionTask == null || sessionTask.isInterrupted())
			return false;
		boolean idem = ip[0].equals(serverName) && ip[1].equals(Integer.toString(serverPort));
		if (idem) {
			if (clientSocket.isConnected())
				return true;
		} else {
			disconnect(true);
			connect(ip[0], Integer.parseInt(ip[1]));
			return true;
		}
		return false;
	}

	@Override
	public void setKeepConnection(boolean keep) {
		this.keepConnection = keep;
	}

	@Override
	public boolean isKeepConnection() {
		return keepConnection;
	}

}
