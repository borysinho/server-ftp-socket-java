import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

  // private String getNewFileName(String bufferedFileName) {
  // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
  // LocalDateTime now = LocalDateTime.now();
  // StringBuilder newFileName = new StringBuilder(bufferedFileName);
  // newFileName.insert(bufferedFileName.lastIndexOf("."), " " + dtf.format(now));
  // return newFileName.toString();
  // }

  /**
   * @implNote Acepta nuevas conexiones y luego notifica a todos los observadores
   *           o subscriptores. Este es quien dispara el evento.
   */
  @Override
  public void run() {
    System.out.println("Ejecutando data.run");
    try (
        // Crear flujos de entrada para la recepción de datos
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());) {

      // Leer nombre del archivo
      int fileNameLength = dataInputStream.readInt();
      byte[] fileNameBytes = new byte[fileNameLength];
      dataInputStream.readFully(fileNameBytes);
      String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);

      // Leer longitud del archivo
      long fileLength = dataInputStream.readLong();
      long originalFileLength = fileLength;

      // System.out.println("Recibiendo archivo: " + fileName +
      // ", Longitud: " + fileLength + " bytes");

      // Creamos un buffer de 64 KB cada elemento
      byte[] buffer = new byte[64 * 1024];
      int bytesRead;

      // ByteArrayOutputStream para capturar los bytes escritos
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      // Leer datos del archivo en bloques de 64 KB
      // Leemos del buffer a partir de la posición 0, N elementos y se repite hasta
      // que el Stream queda vacío. El último elemento lo toma con la función Math.min
      while ((bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileLength))) != -1) {
        // fileOutputStream.write(buffer, 0, bytesRead);
        fileLength -= bytesRead;

        // Captura los bytes escritos en el ByteArrayOutputStream
        byteArrayOutputStream.write(buffer, 0, bytesRead);

        if (fileLength == 0) {
          break;
        }
      }

      ReceivedData data = new ReceivedData(fileName, byteArrayOutputStream.toByteArray(), originalFileLength);

      notifyObservers((Object) data);

    } catch (Exception e) {
      e.printStackTrace();

      // TODO: handle exception
    }

    // try (
    // // Crear flujos de entrada para la recepción de datos
    // DataInputStream dataInputStream = new
    // DataInputStream(socket.getInputStream());) {

    // // Obtenemos la longitud del nombre del archivo
    // int fileNameLength = dataInputStream.readInt();

    // // Obtenemos el nombre del archivo
    // byte[] fileNameBytes = new byte[fileNameLength];
    // dataInputStream.readFully(fileNameBytes);
    // String fileName = getNewFileName(new String(fileNameBytes,
    // StandardCharsets.UTF_8));

    // // Leemos la longitud del archivo
    // long fileLength = dataInputStream.readLong();

    // byte[] buffer = new byte[64 * 1024];
    // int bytesRead;

    // System.out.println("Recibiendo archivo: " + fileName +
    // ", Longitud: " + fileLength + " bytes");
    // try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
    // while ((bytesRead = dataInputStream.read(buffer, 0, (int)
    // Math.min(buffer.length, fileLength))) != -1) {
    // fileOutputStream.write(buffer, 0, bytesRead);
    // fileLength -= bytesRead;

    // if (fileLength == 0) {
    // break;
    // }
    // }
    // }

    // notifyObservers(buffer);
    // System.out.println("Archivo recibido del cliente.");

    // } catch (IOException e) {
    // e.printStackTrace();
    // }
  }

}
