package tp7_threaded_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tp7_api.IConstants;
import tp7_api.IThreadedServer;

/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public class ServiceTask extends Thread {

	int id;
	int tic;
	boolean interrupted;
	private boolean ended;
	private Socket socketService;
	private ServiceTask thistask = this;
	private IThreadedServer server;
	private SocketAddress remoteAddress;
	private PrintStream output;
	private int[] ident;
	private List<String> buffer = new ArrayList<String>();

	public ServiceTask(int id, IThreadedServer threadedServer) {
		this.id = id;
		this.server = threadedServer;
	}

	@Override
	public void interrupt() {
		if (!isInterrupted()) {
			interrupted = true;
			Util.log(this, "task " + id + " is interrupted");
			super.interrupt();
		}
	}

	@Override
	public boolean isInterrupted() {
		boolean result = super.isInterrupted();
		result = result || interrupted;
		return result;
	}

	// KW_CIRCLE,KW_LINE,KW_RECTANGLE,KW_BOMB,KW_SHIP

	private boolean parseFigures(String[] req, String request) {
		boolean result = true;
		if (req[0].equals(IConstants.KW_POLYCIRCLE)) {
			try {
				String figure = Util.circle_v1(req);
				figure = req[0] + " " + figure;
				server.addFigure(figure);
				server.broadcast(this, figure);
				server.log(figure);
				output.println(figure);
			} catch (Exception e) {
				output.println("erreur dans la commande " + request + " format attendu:" + "x y radius");
			}
		} else if (req[0].equals(IConstants.KW_CIRCLE)) {
			try {
				String figure = req[0] + " ";
				figure += req[1] + " " + req[2] + " " + req[3];
				server.addFigure(figure);
				server.broadcast(this, figure);
				server.log(figure);
				output.println(figure);
			} catch (Exception e) {
				output.println("erreur dans la commande " + request + " format attendu:" + "x y radius");
			}
		} else if (req[0].equals(IConstants.KW_BOMB)) {
			try {
				String figure = req[0] + " ";
				figure += req[1] + " " + req[2] + " " + req[3];
				server.addFigure(figure);
				server.broadcast(this, figure);
				server.log(figure);
				output.println(figure);
			} catch (Exception e) {
				output.println("erreur dans la commande " + request + " format attendu:" + "x y radius");
			}
		} else if (req[0].equals(IConstants.KW_LINE)) {
			try {
				String figure = req[0] + " ";
				figure += req[1] + " " + req[2] + " " + req[3] + " " + req[4];
				server.addFigure(figure);
				server.broadcast(this, figure);
				server.log(figure);
				output.println(figure);
			} catch (Exception e) {
				output.println("erreur dans la commande " + request);
			}
		} else if (req[0].equals(IConstants.KW_RECTANGLE)) {
			try {
				String figure = req[0] + " ";
				figure += req[1] + " " + req[2] + " " + req[3] + " " + req[4];
				server.addFigure(figure);
				server.broadcast(this, figure);
				server.log(figure);
				output.println(figure);
			} catch (Exception e) {
				output.println("erreur dans la commande " + request);
			}
		} else if (req[0].equals(IConstants.KW_SHIP_)) {
			try {
				String figure = req[0] + " ";
				figure += req[IConstants.ID0] + " " + req[IConstants.ID1] + " " + req[IConstants.ID2] + " ";
				figure += req[4] + " " + req[5] + " " + req[6] + " " + req[7];
				server.addFigure(figure);
				server.broadcast(this, figure);
				server.log(figure);
				output.println(figure);
			} catch (Exception e) {
				output.println("erreur dans la commande " + request);
			}
		} else
			result = false;

		return result;

	}

	private void parseIdentification(String[] req, String request) {
		ident = new int[3];
		ident[0] = Integer.parseInt(req[IConstants.ID0]);
		ident[1] = Integer.parseInt(req[IConstants.ID1]);
		ident[2] = Integer.parseInt(req[IConstants.ID2]);
		server.log("Welcome " + ident[0] + "." + ident[1] + "." + ident[2]);
		if (server.registerCLient(this, ident)) {

		}
	}

	boolean action(String request) {
		server.logRequest(id, request);
		if (request.toLowerCase().contains("quit")) {
			output.println("stopping session");
			return false;
		} else {
			String[] req = request.split(" ");
			if (req[0].toLowerCase().equals("stop")) {
				output.println("the srver will shutdown in 5 seconds");
				new Thread(new Runnable() {
					@Override
					public void run() {
						Util.delay(thistask, 5000);
						Util.globalEnd = true;
					}
				}).start();
			}
			if (req[0].toLowerCase().equals("date")) {
				Date now = new Date();
				output.println("il est " + now.toString());
			} else if (req[0].equals("Helo")) {
				parseIdentification(req, request);
			} else if (req[0].toLowerCase().startsWith("add") && req.length >= 2) {
				String element = req[1];
				try {
					Util.addElement(this, element);
					String response = "element added ";
					output.println(response);
				} catch (InterruptedException e) {
					interrupted = true;
					return false;
				}
			} else if (req[0].toLowerCase().startsWith("remove")) {
				try {
					String element = Util.removeElement(this);
					String response = "element removed: " + element;
					output.println(response);
				} catch (InterruptedException e) {
					interrupted = true;
					return false;
				}
			} else if (!parseFigures(req, request)) {
				String response = "commande inconnue ";
				/*
				 * response += "'Je connais les commandes suivantes:"; response
				 * += "'date => je retourne la date"; response +=
				 * "'circle x y radius => je calcule un cercle de rayon radius à la coordonnée x y"
				 * ; response += "'quit => fin de la session"; response +=
				 * "'add [element name]"; response += "'remove";
				 */
				output.println(response);
			}

		}
		return true;
	}

	public String toString() {
		String result = "";
		result += id;
		result += " ";
		result += ident[0];
		result += ".";
		result += ident[1];
		result += ".";
		result += ident[2];
		result += remoteAddress;
		return result;
	}

	public void send(String data) {
		if (!socketService.isClosed())
			output.println(data);
	}

	public void prepare(String figure) {
		buffer.add(figure);
	}

	public void flush() {
		if (!socketService.isClosed())
			for (String figure : buffer) {
				output.println(figure);
			}
		buffer.clear();
	}

	@Override
	public void run() {
		Util.log(this, "task " + id + " starts");
		try {
			this.remoteAddress = socketService.getRemoteSocketAddress();
			String status = "le client " + id;
			status += " " + remoteAddress + " s'est connecté";
			Util.log(this, status);
			server.logStatus(this, id, status);
			// server.notify(this);
			output = new PrintStream(socketService.getOutputStream(), true); // autoflush
			BufferedReader networkIn = new BufferedReader(new InputStreamReader(socketService.getInputStream()));
			server.notify(this);
			while (!Util.globalEnd && !isInterrupted()) {
				String requeteclient = networkIn.readLine();
				if (requeteclient == null) {
					status = "le client " + id + " " + remoteAddress + " s'est déconnecté, fin de la session";
					Util.log(this, status);
					server.logStatus(this, id, status);
					break;
				} else {
					Util.log(this, "le client demande: " + (requeteclient == null ? "null" : requeteclient));
					if (!action(requeteclient))
						break;
				}
			}
		} catch (IOException e) {
			Util.log(this, "IO error in ServiceTask " + e.toString());
		} finally {
			try {
				socketService.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Util.log(this, "arrêt de la session");
			server.logStatus(this, id, "session end");
		}
		ended = true;
		server.endSession(this);
		Util.log(this, "session " + id + " ends");
	}

	public void setSocketService(Socket socketService) {
		this.socketService = socketService;
	}

	public void closeSocket() {
		try {
			if (socketService != null)
				socketService.close();
		} catch (IOException e) {

		}
	}

}