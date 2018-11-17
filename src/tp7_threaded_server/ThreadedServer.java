package tp7_threaded_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import tp7_api.IServerControler;
import tp7_api.IThreadedServer;

/**
 * identique à cs_2017_tp6_threaded_server mais implémente une interface
 * 
 * @author pfister - connecthive.com
 * 
 *
 */
public class ThreadedServer implements IThreadedServer {

	private IServerControler controler;
	private ServerThread serverThread;

	private ServerSocket socketEcoute;
	private List<ServiceTask> clientServices = new ArrayList<ServiceTask>();
	// a client has a triplet identifier (color) i order to handle
	// reconnections where the remote port changes while the identifier remains
	private List<int[]> clientIdentifiers = new ArrayList<int[]>();

	@Override
	public boolean registerCLient(Object serviceTask, int[] id) {
		if (!registered(id)) {
			clientIdentifiers.add(id);
			controler.consolideNewClient(serviceTask);
			return true;
		} else
			 return false;
	}

	private boolean registered(int[] pid) {
		for (int[] id : clientIdentifiers)
			if (id[0] == pid[0] && id[1] == pid[1] && id[2] == pid[2])
				return true;
		return false;
	}

	@Override
	public void broadcast(Object serviceTask, String data) {
		ServiceTask current = (ServiceTask) serviceTask;
		for (ServiceTask client : clientServices) {
			if (client != current) 
				client.send(data);
		}
	}

	public void runServer() throws IOException {
		int port = controler.getDefaultPort();
		socketEcoute = new ServerSocket(port);
		NetIdentity ni = new NetIdentity();
		String host = ni.getHostAddress();
		String mac = ni.getMacAddress();
		controler.setHost(mac, host, port);
		String log = "[serveur multiclient multisession] démarré sur :" + host + ":" + port;
		controler.log(null, log);
		int numClient = 0;
		while (!Util.globalEnd) {
			controler.log(null, "en attente d'une connexion");
			Socket socketService = socketEcoute.accept(); // bloquant ici
			ServiceTask clientTask = new ServiceTask(numClient++, this);
			clientTask.setSocketService(socketService);
			clientServices.add(clientTask);
			clientTask.start();
		}
		socketEcoute.close();
		Util.log(null, "arrêt du serveur");
	}

	public static void ma_in(String[] args) {
		ThreadedServer threadedServer = new ThreadedServer();
		try {
			threadedServer.runServer();
		} catch (Exception e) {
			Util.log(e.toString());
		}
	}

	@Override
	public void setServerControler(IServerControler controler) {
		this.controler = controler;
	}

	@Override
	public void logRequest(int id, String request) {
		controler.log("client " + id + " request=" + request);
	}

	@Override
	public void logStatus(Object clientTask, int id, String status) {
		controler.log(clientTask, "client " + id + " request=" + status);
	}

	@Override
	public List getClientTasks() {
		return clientServices;
	}

	@Override
	public void endSession(Object clientTask) {
		// ClientTask ct = (ClientTask) clientTask;
		clientServices.remove(clientTask);
		controler.notify(clientTask);
	}

	class ServerThread extends Thread {
		private boolean interrupted;

		@Override
		public void interrupt() {
			if (!isInterrupted()) {
				interrupted = true;
				controler.log("server " + " will shut down");
				super.interrupt();
			}
		}

		@Override
		public boolean isInterrupted() {
			boolean result = super.isInterrupted();
			result = result || interrupted;
			return result;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(500);
				runServer();
			} catch (InterruptedException e) {
				// System.out.println("interrupted");
				return;
			} catch (SocketException e) {
				// e.printStackTrace();
				controler.log("server is down");
				System.out.println("server is down");
				controler.setServerDown();
				return;
			} catch (Exception e) {
				e.printStackTrace();
				controler.log("2 unable to start server");
				System.out.println("2 unable to start server");
				controler.setSocketError();
				return;
			}
		}
	}

	@Override
	public void dispose() {
		disconnect();
	}

	@Override
	public void connect() {
		serverThread = new ServerThread();
		serverThread.start();
	}

	@Override
	public void disconnect() {
		for (ServiceTask clientTask : clientServices)
			clientTask.closeSocket();
		for (ServiceTask clientTask : clientServices)
			clientTask.interrupt();
		try {
			if (socketEcoute != null && socketEcoute.isBound()) {
				socketEcoute.close();
			}
		} catch (IOException e) {

		}
		if (serverThread != null)
			serverThread.interrupt();
	}

	@Override
	public void log(String log) {
		controler.log(log);
	}

	@Override
	public void addFigure(String figure) {
		controler.addFigure(figure);
	}

	@Override
	public void notify(Object serviceTask) {
		controler.notify(serviceTask);
	}

	@Override
	public List<int[]> getClientIds() {
		return clientIdentifiers;
	}

}
