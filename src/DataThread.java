import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class DataThread implements Observable, Runnable {
  Socket socket;
  Set<Observer> observerSet = new HashSet<>();

  DataThread(Socket socket) {
    this.socket = socket;
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
   * @implNote Lógica de la implementación. Aquí se establece cómo notificar a los
   *           observadores o subscriptores
   * @param obj Objeto que enviará a todos los observadores o subscriptores.
   */
  @Override
  public void notifyObservers(Object obj) {
    for (Observer observer : observerSet) {
      observer.on("data", obj);
    }
  }

  /**
   * @implNote Es un método que agrupa todos los eventos lanzados por la clase y
   *           los manda al observador con su respectivo nombre de evento
   * @param event Nombre del evento que se enviará al observador
   * @param obj   Objeto a enviar al observador
   */
  private void personalNotifier(String event, Object obj) {
    for (Observer observer : observerSet) {
      switch (event) {
        case "data":
          observer.on("data", obj);
          break;
        case "disconnection":
          observer.on("disconnection", obj);
          break;
        default:
          break;
      }
    }
  }

  /**
   * @implNote Acepta nuevas conexiones y luego notifica a todos los observadores
   *           o subscriptores. Este es quien dispara el evento.
   */
  @Override
  public void run() {
    try {
      DataInputStream inputStream = new DataInputStream(socket.getInputStream());

      // Recibir comando del cliente
      String command = inputStream.readUTF();

      if ("SEND".equals(command)) {
        // Recibir nombre de archivo
        String fileName = inputStream.readUTF();
        System.out.println("Recibiendo archivo: " + fileName);

        // Recibir el archivo
        FileOutputStream fileOutputStream = new FileOutputStream("./data/" + getNewFileName(fileName));
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
          fileOutputStream.write(buffer, 0, bytesRead);
        }

        fileOutputStream.close();
        socket.close();
        System.out.println("Archivo recibido con éxito.");
      } else {
        System.out.println("Comando no reconocido.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void test() {
    try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());) {

      while (!this.socket.isClosed()) {
        try {
          // ============================================================================
          // 1ER FLUJO RECIBIDO: Nombre del archivo
          // Lee la longitud del nombre de archivo como un entero de 4 bytes desde el
          // flujo de entrada
          int fileNameLength = dataInputStream.readInt();

          // Crea un array de bytes para almacenar el nombre de archivo basado en la
          // longitud leída anteriormente
          byte[] fileNameBytes = new byte[fileNameLength];

          // Lee completamente la cantidad de bytes especificada para el nombre de archivo
          // y los almacena en el array
          dataInputStream.readFully(fileNameBytes);

          // Convierte el array de bytes a una cadena de caracteres (String) utilizando la
          // codificación UTF-8
          String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);

          // ============================================================================
          // 2DO FLUJO RECIBIDO: Longitud del archivo a recibir
          // Lee un long de 8 bytes que representa la longitud total del archivo
          long fileLength = dataInputStream.readLong();

          // Almacena la longitud original del archivo
          long originalFileLength = fileLength;

          // Crea un buffer de bytes de 64 KB para almacenar temporalmente los datos
          // leídos
          byte[] buffer = new byte[64 * 1024];

          // Variable para almacenar el número de bytes leídos en cada iteración
          int bytesRead;

          // ByteArrayOutputStream para capturar los bytes escritos
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

          // ============================================================================
          // 3ER FLUJO RECIBIDO: El archivo
          // Lee datos del archivo en bloques de 64 KB hasta que se haya leído la longitud
          // total del archivo. El último elemento lo toma con la función Math.min, que
          // devuelve el menor de 2 elementos
          while ((bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileLength))) != -1) {
            fileLength -= bytesRead;

            // Captura los bytes escritos en el ByteArrayOutputStream
            byteArrayOutputStream.write(buffer, 0, bytesRead);

            // Si ya se han leído todos los bytes, sale del bucle
            if (fileLength == 0) {
              break;
            }
          }
          // Creamos un objeto para enviar a los observadores
          ReceivedData data = new ReceivedData(fileName,
              byteArrayOutputStream.toByteArray(), originalFileLength);

          // Enviamos el objeto a los observadores
          notifyObservers((Object) data);
        } catch (EOFException eofException) {
          // El socket fue cerrado en el lado del cliente
          System.out.println("DataThread.Run EOFException:The client socket has been closed");
          personalNotifier("disconnection", (Object) this.socket);
          break;
        } catch (IOException e) {
          // El socket cliente cerró la conexión antes de recibir la respuesta del socket
          e.printStackTrace();
          System.out.println(
              "DataThread.Run IOException: the client closed the socket connection before the response could be returned over the server-socket side.");
          break;
        } finally {
          // Cerramos el flujo de entrada y el socket
          dataInputStream.close();
          this.socket.close();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out
          .println("DataThread.Run IOException: Error en la recepción de flujos de entrada. socket.getInputStream()");

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
