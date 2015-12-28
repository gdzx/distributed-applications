import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;
import java.util.ArrayList;

class AccountImpl extends AccountPOA {
  static ArrayList<AccountImpl> accounts = new ArrayList<AccountImpl>();
  private int funds = 0;

  public AccountImpl() {
    accounts.add(this);
  }

  public void close() {
    accounts.remove(this);
  }

  public void deposit(int amount) {
    System.out.println("deposit: " + amount);
    this.funds += amount;
  }

  public void withdraw(int amount) {
    System.out.println("withdraw: " + amount);
    this.funds -= amount;
  }

  public int balance() {
    System.out.println("balance: " + this.funds);
    return this.funds;
  }
}
