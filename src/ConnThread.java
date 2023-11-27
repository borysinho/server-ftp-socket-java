import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ConnThread implements Observable, Runnable {
  ServerSocket serverSocket;
  Set<Observer> observerSet = new HashSet<>();

  ConnThread(int port) {
    try {
      // Creamos un objeto de la clase java.net.ServerSocket
      this.serverSocket = new ServerSocket(port);

      // Establecemos la opción para el reuso del puerto
      this.serverSocket.setReuseAddress(true);

      // Mensaje indicando la escucha de nuevas conexiones en el puerto indicado
      System.out.println("ConnThread(): Listenning connections on port " + String.valueOf(port));
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(
          "ConnThread(): Error trying to create a ServerSocket object. Please read carefully the warning and try again.");

      return;
    }
  }

  /**
   * @implNote Acepta nuevas conexiones y luego notifica a todos los observadores
   *           o subscriptores. Este es quien dispara el evento.
   */

  @Override
  public void run() {
    while (true) {
      try {
        // Quedamos en espera aceptando nuevas conexiones
        Socket client = this.serverSocket.accept();

        if (client.isConnected()) {
          System.out.println("New connection ID " + client.hashCode());
        }

        // Mostramos mensaje indicando que un nuevo cliente se ha conectado
        

        // Evento disparado. Notificamos a todos los observadores o subscriptores
        notifyObservers((Object) client);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        System.out.println("ConnThread.Run(): Error trying to accept new connections. Please restart the service");
        return;
      }
    }
  }

  /**
   * @implNote Implementa la clase Observer
   * @param o Adiciona un observador a la lista de observadores o subscriptores
   */
  @Override
  public void addObserver(Observer o) {
    observerSet.add(o);
  }

  /**
   * @implNote Implementa la clase Observer
   * @param o Elimina un observador de la lista de observadores o subscriptores
   */
  @Override
  public void deleteObserver(Observer o) {
    observerSet.remove(o);
  }

  /**
   * @implNote Se notifica a todos los observadores o subscriptores
   * @param obj Objeto que enviará a todos los observadores o subscriptores.
   */
  @Override
  public void notifyObservers(Object obj) {
    // System.out.println("Notificando subscriptores");
    for (Observer observer : observerSet) {
      observer.on("connection", obj);
    }
  }
  // ======================SIRVE==========================
  // public ConnThread(int port) throws SocketException {

  // // Creamos el hilo
  // new Thread(() -> {
  // try {
  // ConnThread.launch(port);
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }).start();
  // }

  // static void launch(int port) throws IOException {
  // System.out.println("Iniciamos hilo escuchador de conexiones");
  // System.out.println("Escuchando puerto " + port);
  // ServerSocket serverSocket = new ServerSocket(port);
  // // Habilitamos la reutilización de la dirección
  // serverSocket.setReuseAddress(true);
  // try {
  // // while (!Thread.currentThread().isInterrupted()) {
  // // while (true) {
  // Socket client = serverSocket.accept();
  // System.out.println("Nuevo cliente conectado: " + serverSocket.hashCode());
  // notifyObservers(client);
  // // }
  // } catch (IOException e) {
  // e.printStackTrace();
  // } finally {
  // if (serverSocket != null) {
  // try {
  // serverSocket.close();
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  // }
  // }

  // }
  // ====================================
  // public ConnThread(int port) throws IOException {
  // try {
  // System.out.println("Creamos servidor en puerto " + port);
  // this.serverSocket = new ServerSocket(port);

  // // Habilitamos la reutilización de la dirección
  // this.serverSocket.setReuseAddress(true);

  // // Habilitamos el reuso de la dirección
  // this.serverSocket.setReuseAddress(true);
  // // Creamos el runnable
  // Runnable runnable = new ConnThread(port);

  // // Creamos el hilo
  // Thread thread = new Thread(runnable);

  // // Empezamos el hilo
  // thread.start();
  // System.out.println();
  // } catch (Exception e) {
  // e.printStackTrace();
  // // TODO: handle exception
  // } finally {
  // if (this.serverSocket != null) {
  // try {
  // this.serverSocket.close();
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  // }
  // }

  // }

  // @Override
  // public void run() {

  // System.out.println("Iniciamos hilo

  // // Habilitamos la reutili
  // serverSocket.setReuseAddress(true)

  // }

  // e.printStackTrace();
  // }

  // while (!Thread.currentTh
  // try (Socket socket = serv
  // System.out.println("Conexión acept
  // notifyObservers(socket);
  // } catch (IOException
  // /
  // System.out.println("Hilo Interrumpido");
  // e.printStackTrace();
  // }
  // }
  // }
}
