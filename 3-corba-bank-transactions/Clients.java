import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.util.Properties;

public class Clients {
  public static void main(String args[]) {
    Properties properties = System.getProperties();
    properties.put("org.omg.CORBA.ORBInitialHost", "localhost");
    properties.put("org.omg.CORBA.ORBInitialPort", "2810");
    try {
      org.omg.CORBA.Object objRef;
      ORB orb = ORB.init(args, properties);
      // create and initialize the ORB
      // get the naming service
      objRef = orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
      // resolve the object reference from the naming service
      objRef = ncRef.resolve_str("banks.cic");
      Banking.BankCustomer cicRef = Banking.BankCustomerHelper.narrow(objRef);
      Banking.Account a = cicRef.create();
      a.deposit(500);
      a.withdraw(300);


      objRef = ncRef.resolve_str("banks.bnp");
      Banking.BankCustomer bnpRef = Banking.BankCustomerHelper.narrow(objRef);
      Banking.Account b = bnpRef.create();
      b.deposit(500);

      b.order(a.address(), 200);
      a.order(b.address(), 25);

      cicRef.destroy(a);
      bnpRef.destroy(b);

      org.omg.CORBA.Object obj = orb.string_to_object("corbaname::localhost:2810#PersistentInterBank");
      Banking.InterBank interbank = Banking.InterBankHelper.narrow(obj);

      Banking.Transaction[] transactions = interbank.transactions();
      System.out.println("== TRANSACTIONS BOOK ==");
      for (Banking.Transaction t: transactions) {
        System.out.println(t.sender + " -> " + t.recipient + ": " + t.amount + " BTC");
      }
    } catch(Exception e) {
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }
  }
}
