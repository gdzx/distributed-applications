import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;

class BankTransactionImpl extends Banking.BankTransactionPOA {
  private String name;

  public BankTransactionImpl(String name) {
    this.name = name;
  }

  public void deposit(String account, int amount) {
    AccountImpl.accounts.get(account).deposit(amount);
  }

  public void withdraw(String account, int amount) {
    AccountImpl.accounts.get(account).withdraw(amount);
  }

  public String name() {
    return this.name;
  }
}
