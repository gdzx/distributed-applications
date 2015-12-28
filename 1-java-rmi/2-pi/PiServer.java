import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.lang.Integer;

public class PiServer {
  public static void main(String args[]) {
    try {
      // activation de l'objet distribué
      Pi pi = new PiImpl();
      Pi stub = (Pi)UnicastRemoteObject.exportObject(pi, 0);
      // binding de l'objet distribué dans le registre de nom
      Registry registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
      registry.rebind("pi", stub);
    } catch (Exception e) {
      System.out.println("PiServer Exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
