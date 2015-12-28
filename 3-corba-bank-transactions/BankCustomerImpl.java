import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;

class BankCustomerImpl extends Banking.BankCustomerPOA {
  org.omg.CORBA.Object objRef;
  POA poa;
  //Banking.InterBank interbank;
  String name;

  //public BankCustomerImpl(POA r, Banking.InterBank interbank, String name) {
  public BankCustomerImpl(POA r, String name) {
    this.poa = r;
    //this.interbank = interbank;
    this.name = name;
  }

  public Banking.Account create() {
    System.out.println("bank " + this.name + ": creating account");
    //AccountImpl ai = new AccountImpl(this.interbank, this.name);
    try {
      objRef = poa.create_reference(Banking.AccountHelper.id());
    } catch (Exception e) {
    }
    return Banking.AccountHelper.narrow(objRef);
  }

  public void destroy(Banking.Account a) {
    System.out.println("bank " + this.name + ": destroying account");
    try {
      Servant servant = poa.reference_to_servant(a);
      AccountImpl ai = (AccountImpl) servant;
      ai.close();
      //rootpoa.deactivate_object(servant._object_id());
    } catch(Exception e) {
      System.out.println("ERROR : " + e);
    }
  }
}
