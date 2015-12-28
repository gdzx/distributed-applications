import org.omg.CORBA.ORB;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NameComponent;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.*;
import java.util.Properties;

public class InterBank {
  public static void main(String args[]) {
    Properties properties = System.getProperties();
    properties.put("org.omg.CORBA.ORBInitialHost", "localhost");
    properties.put("org.omg.CORBA.ORBInitialPort", "2810");
    try {
      ORB orb = ORB.init(args, properties); // create and initialize the ORB

      InterBankImpl bankImpl = new InterBankImpl(orb);

      // Step 3-1: Get the rootPOA 
      POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      // Step 3-2: Create the Persistent Policy
      Policy[] persistentPolicy = new Policy[1];
      persistentPolicy[0] = rootPOA.create_lifespan_policy(
          LifespanPolicyValue.PERSISTENT);
      // Step 3-3: Create a POA by passing the Persistent Policy
      POA persistentPOA = rootPOA.create_POA("childPOA", null, 
          persistentPolicy ); 
      // Step 3-4: Activate PersistentPOA's POAManager, Without this
      // All calls to Persistent Server will hang because POAManager
      // will be in the 'HOLD' state.
      persistentPOA.the_POAManager().activate();
      // ***********************

      // Step 4: Associate the servant with PersistentPOA
      persistentPOA.activate_object(bankImpl);

      // Step 5: Resolve RootNaming context and bind a name for the
      // servant.
      // NOTE: If the Server is persistent in nature then using Persistent
      // Name Service is a good choice. Even if ORBD is restarted the Name
      // Bindings will be intact. To use Persistent Name Service use
      // 'NameService' as the key for resolve_initial_references() when
      // ORBD is running.
      org.omg.CORBA.Object obj = orb.resolve_initial_references(
          "NameService" );
      NamingContextExt rootContext = NamingContextExtHelper.narrow(obj);

      NameComponent[] nc = rootContext.to_name("PersistentInterBank");
      rootContext.rebind(nc, persistentPOA.servant_to_reference(bankImpl));

      orb.run(); // start server...
    } catch (Exception e) {
      System.out.println("ERROR : " + e);
      e.printStackTrace(System.out);
    }
  }
}
