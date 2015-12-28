import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;

class BankImpl extends BankPOA {
  org.omg.CORBA.Object objRef;
  POA rootpoa;

  public BankImpl(POA r) {
    this.rootpoa = r;
  }

  public Account create() {
    System.out.println("creating new account");
    AccountImpl ai = new AccountImpl();
    try {
      objRef = rootpoa.servant_to_reference(ai);
    } catch (Exception e) {
    }
    return AccountHelper.narrow(objRef);
  }

  public void destroy(Account a) {
    System.out.println("destroying account with " + a.balance() + " bitcoins");
    AccountImpl ai = (AccountImpl) a;
    ai.close();
  }

  public Account move(Bank target, Account a) {
    System.out.println("moving account with " + a.balance() + " bitcoins");
    Account newa = target.create();
    newa.deposit(a.balance());
    AccountImpl ai = (AccountImpl) a;
    ai.close();
    return newa;
  }
}
