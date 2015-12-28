import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloClient {
  public static void main(String args[]) {
    try {
      // recherche de l'objet distribué dans le registre de nom
      Registry registry = LocateRegistry.getRegistry(args[0]); // ← machine serveur
      Hello hello = (Hello) registry.lookup("hello");
      // appel de méthode à distance
      String s = hello.say("Hello World!");
      System.out.println(s);
    } catch (Exception e) {
      System.out.println("HelloClient exception: " + e.getMessage());
      //e.printStackTrace();
    }
  }
}
