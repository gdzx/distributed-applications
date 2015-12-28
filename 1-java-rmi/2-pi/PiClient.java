import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.lang.Integer;
import java.lang.Long;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

class Digit {
  public int start;
  public int end;
  public int[] digit;

  public Digit(int start, int end, int[] digit) {
    this.start = start;
    this.end = end;
    this.digit = digit;
  }

  public void print() {
    for (int i = start; i < end; i++) {
      System.out.format("digit #%4d: %X\n", i, digit[i-start]);
    }
  }
}

class DigitTask implements Callable<Digit> {
  public Pi pi;
  public int start;
  public int end;

  public DigitTask(Pi pi, int start, int end) {
    this.pi = pi;
    this.start = start;
    this.end = end;
  }

  public Digit call() {
    int[] result = new int[0];
    try {
      result = pi.digitRange(start, end);
    } catch (Exception e) {
      System.out.println("PiClient exception: " + e.getMessage());
      //e.printStackTrace();
    }
    return new Digit(start, end, result);
  }
}

public class PiClient {
  public static void main(String args[]) {
    try {
      // recherche de l'objet distribué dans le registre de nom
      Registry registry1 = LocateRegistry.getRegistry("localhost", 1099); // ← machine serveur
      Pi pi1 = (Pi)registry1.lookup("pi");
      Registry registry2 = LocateRegistry.getRegistry("localhost", 1100); // ← machine serveur
      Pi pi2 = (Pi)registry2.lookup("pi");

      // create a pool of threads, 10 max jobs will execute in parallel
      ExecutorService threadPool = Executors.newFixedThreadPool(10);
      CompletionService<Digit> pool = new ExecutorCompletionService<Digit>(threadPool);
      // submit jobs to be executing by the pool
      int N = 10000;
      int range = 10;
      int i;
      for (i = 0; i < N; i+=2*range){
        pool.submit(new DigitTask(pi1, i, i+range));
        pool.submit(new DigitTask(pi2, i+range, i+2*range));
      }

      for (i = 0; i < N; i++){
        Digit digit = pool.take().get();
        digit.print();
      }
      threadPool.shutdown();
    } catch (Exception e) {
      System.out.println("PiClient exception: " + e.getMessage());
      //e.printStackTrace();
    }
  }
}
