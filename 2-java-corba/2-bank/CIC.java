import org.omg.CORBA.ORB;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;

public class CIC {
  public static void main(String args[]) {
    try {
      ORB orb = ORB.init(args, null); // create and initialize the ORB
      // get reference to rootpoa & activate the POAManager
      org.omg.CORBA.Object objRef = orb.resolve_initial_references("RootPOA");
      POA rootpoa = POAHelper.narrow(objRef);
      rootpoa.the_POAManager().activate();
      // get the naming service
      objRef = orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
      // get object reference from the servant
      BankImpl bankImpl = new BankImpl(rootpoa);
      objRef = rootpoa.servant_to_reference(bankImpl);
      // convert the CORBA object reference into Echo reference
      Bank bankRef = BankHelper.narrow(objRef);
      // bind the object reference in the naming service
      NameComponent path[] = ncRef.to_name("app.cic"); // id.kind
      ncRef.rebind(path, bankRef);
      orb.run(); // start server...
    } catch (Exception e) {
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }
  }
}
