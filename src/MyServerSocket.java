import java.io.FileOutputStream;
import java.net.Socket;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class MyServerSocket implements Observer {
  int port;
  Thread connThread, dataThread;
  HashMap<Integer, Socket> clients;

  public MyServerSocket(int port) {
    // Iniciamos el hilo de datos en vacío hasta que se reciba una nueva conexión
    dataThread = null;

    // Iniciamos los clientes en vacío hasta que se reciba una nueva conexión
    clients = new HashMap<Integer, Socket>();

    // Creamos un observador concreto llamado conObservable y le enviamos el puerto
    ConnThread conObservable = new ConnThread(port);

    // Añadimos como observador al mismo objeto de la clase
    conObservable.addObserver(this);

    // Creamos un nuevo hilo y le enviamos el observador que acabamos de crear
    this.connThread = new Thread(conObservable);

    // Iniciamos el hilo
    this.connThread.start();
  }

  @Override
  public void on(String event, Object data) {
    // Recibimos las notificaciones del observable

    switch (event) {
      case "connection":
        // Hacemos un DownCasting de Object a Socket
        Socket socket = (Socket) data;

        // Agregamos el socket a la lista de clientes
        clients.put(socket.hashCode(), socket);

        // Creamos un Observable y le enviamos el socket del cliente
        DataThread dataObservable = new DataThread(socket);

        // Agregamos como observador al mismo objeto enviado a la clase
        dataObservable.addObserver(this);

        // Creamos un hilo y le enviamos el observador con el socket que le habíamos
        // enviado antes
        this.dataThread = new Thread(dataObservable);

        // Iniciamos el hilo para que empieze a escuchar los datos.
        this.dataThread.start();
        break;
      case "data":
        // Obtenemos los datos que envía el observable y lo recibimos como un
        // downcasting de ReceivedData
        ReceivedData file = (ReceivedData) data;

        // Mostramos mensaje con el archivo que estamos recibiendo y su longitud.
        System.out.println("Recibiendo archivo " + file.getFileName() +
            ", Longitud: " + file.getFileLength() + " bytes");

        // Generamos un nuevo nombre para el archivo con el formato: "NombreOriginal +
        // DiaMesAño_HoraMinutoSegundo+Extensión"
        String newFileName = getNewFileName(file.getFileName());
        try (
            // Creamos un OutputStream y le pasamos la ubicación y el nombre del archivo
            // donde se escribirá.
            FileOutputStream fileOutputStream = new FileOutputStream("./data/" + newFileName);) {

          // Escribimos el buffer en el disco
          fileOutputStream.write(file.getBuffer());

        } catch (Exception e) {
          e.printStackTrace();
          // TODO: handle exception

        }
        break;

      default:
        break;
    }
  }

  private String getNewFileName(String bufferedFileName) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
    LocalDateTime now = LocalDateTime.now();
    StringBuilder newFileName = new StringBuilder(bufferedFileName);
    newFileName.insert(bufferedFileName.lastIndexOf("."), " " + dtf.format(now));
    return newFileName.toString();
  }

}
