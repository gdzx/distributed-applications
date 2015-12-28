import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class Client {
  public static void main(String args[]) {
    try {
      org.omg.CORBA.Object objRef;
      ORB orb = ORB.init(args, null);
      // create and initialize the ORB
      // get the naming service
      objRef = orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
      // resolve the object reference from the naming service
      objRef = ncRef.resolve_str("app.cic");
      // convert the CORBA object reference into Echo reference
      Bank cicRef = BankHelper.narrow(objRef);
      // resolve the object reference from the naming service
      objRef = ncRef.resolve_str("app.bnp");
      // convert the CORBA object reference into Echo reference
      Bank bnpRef = BankHelper.narrow(objRef);
      // remote method invocation
      //String response = echoRef.echoString("coucou");
      //System.out.println(response);
      //accountRef.deposit(150);
      //accountRef.withdraw(20);
      //System.out.println("I have " + accountRef.balance() + " bitcoins !");
      Account a = cicRef.create();
      a.deposit(500);
      a.withdraw(300);
      a = cicRef.move(bnpRef, a);
      a.withdraw(200);
      cicRef.destroy(a);
    } catch(Exception e) {
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }
  }
}
