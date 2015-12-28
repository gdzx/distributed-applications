import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Pi extends Remote {
  int digit(int n) throws RemoteException;
  int[] digitRange(int start, int end) throws RemoteException;
}
