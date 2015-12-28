import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class EchoClient {
  public static void main(String args[]) {
    try {
      org.omg.CORBA.Object objRef;
      ORB orb = ORB.init(args, null);
      // create and initialize the ORB
      // get the naming service
      objRef = orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
      // resolve the object reference from the naming service
      objRef = ncRef.resolve_str("echo.echo");
      // convert the CORBA object reference into Echo reference
      Echo echoRef = EchoHelper.narrow(objRef);
      // remote method invocation
      String response = echoRef.echoString("coucou");
      System.out.println(response);
    } catch(Exception e) {
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }
  }
}
