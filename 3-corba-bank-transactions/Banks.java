import org.omg.CORBA.*;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;
import java.util.Properties;
import org.omg.PortableServer.*;


public class Banks {
  public static void main(String args[]) {
    Properties properties = System.getProperties();
    properties.put("org.omg.CORBA.ORBInitialHost", "localhost");
    properties.put("org.omg.CORBA.ORBInitialPort", "2810");
    try {
      ORB orb = ORB.init(args, properties); // create and initialize the ORB

      org.omg.CORBA.Object obj = orb.string_to_object("corbaname::localhost:2810#PersistentInterBank");
      Banking.InterBank interbank = Banking.InterBankHelper.narrow(obj);

      // get reference to rootpoa & activate the POAManager
      org.omg.CORBA.Object objRef = orb.resolve_initial_references("RootPOA");
      POA rootPoa = POAHelper.narrow(objRef);
      rootPoa.the_POAManager().activate();
      // get the naming service
      objRef = orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      Policy poaPolicy[] = new Policy[2];
      poaPolicy[0] = rootPoa.create_servant_retention_policy(
          ServantRetentionPolicyValue.RETAIN);
      poaPolicy[1] = rootPoa.create_request_processing_policy(
          RequestProcessingPolicyValue.USE_SERVANT_MANAGER);

      String[] banks = {"cic", "bnp"};
      for (String b: banks) {
        POA poa1 = rootPoa.create_POA(b + "AccountPoa", null, poaPolicy);
        poa1.the_POAManager().activate();

        poa1.set_servant_manager(new PoaServantActivator(poa1, interbank, b));

        // Register Transactional to the InterBank
        BankTransactionImpl bankTImpl = new BankTransactionImpl(b);
        objRef = rootPoa.servant_to_reference(bankTImpl);
        Banking.BankTransaction bankTRef = Banking.BankTransactionHelper.narrow(objRef);
        interbank.register(bankTRef);

        //Register Customer to the NameService
        BankCustomerImpl bankImpl = new BankCustomerImpl(poa1, b);
        objRef = rootPoa.servant_to_reference(bankImpl);
        Banking.BankCustomer bankRef = Banking.BankCustomerHelper.narrow(objRef);
        NameComponent path[] = ncRef.to_name("banks." + b);
        ncRef.rebind(path, bankRef);
      }

      orb.run();
    } catch (Exception e) {
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }
  }
}

class PoaServantActivator extends LocalObject implements ServantActivator {
  POA poa;
  Banking.InterBank interbank;
  String name;

  public PoaServantActivator(POA poa, Banking.InterBank interbank, String name) {
    this.poa = poa;
    this.interbank = interbank;
    this.name = name;
  }

  public Servant incarnate(byte[] oid, POA adapter) throws ForwardRequest {
    try {
      AccountImpl servantObj = new AccountImpl(interbank, name);
      return servantObj;
    } catch (Exception e) {
      System.err.println("incarnate: Caught exception - " + e);
    }
    return null;
  }

  public void etherealize(byte[] oid, POA adapter, Servant serv,
      boolean cleanup_in_progress,
      boolean remaining_activations) {
    AccountImpl.accounts.put(oid.toString(), (AccountImpl) serv);
  }
}
