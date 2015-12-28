import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;
import java.util.HashMap;
import Banking.AccountPOA;
import java.util.UUID;

class AccountImpl extends AccountPOA {
  private int funds = 0;
  private String address;
  Banking.InterBank interbank;
  public static HashMap<String, AccountImpl> accounts = new HashMap<String, AccountImpl>();

  public AccountImpl(Banking.InterBank interbank, String name) {
    this.interbank = interbank;
    this.address = name + UUID.randomUUID().toString();
    System.out.println("account: " + this.address + ": created");
    accounts.put(this.address, this);
  }

  public void close() {
    System.out.println("account: " + this.address + ": closed, " + this.funds + " BTC lost");
    accounts.remove(this);
  }

  public void deposit(int amount) {
    System.out.println("account: " + this.address + ": + " + amount + " BTC");
    this.funds += amount;
  }

  public void withdraw(int amount) {
    System.out.println("account: " + this.address + ": - " + amount + " BTC");
    this.funds -= amount;
  }

  public int balance() {
    System.out.println("account: " + this.address + ": = " + this.funds + " BTC");
    return this.funds;
  }

  public void order(String recipient, int amount) {
    System.out.println("account: order: " + this.address + " -> " + recipient + ": " + this.funds + " BTC");
    Banking.Transaction trans = new Banking.Transaction(this.address, recipient, amount);
    interbank.transaction(trans);
  }

  public String address() {
    return this.address;
  }
}
