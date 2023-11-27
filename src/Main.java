
public class Main {
  public static void main(String[] args) {
    try {
      if (args.length < 1)
        return;
      int port = Integer.parseInt(args[0]);
      new MyServerSocket(port);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Main.main(): Error trying to get port on args. Please try again.");
      return;
    }
  }
}