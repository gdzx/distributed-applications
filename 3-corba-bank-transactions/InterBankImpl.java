import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;
import java.util.HashMap;
import java.util.ArrayList;

class InterBankImpl extends Banking.InterBankPOA {
  public HashMap<String, Banking.BankTransaction> banks = new HashMap<String, Banking.BankTransaction>();
  public ArrayList<Banking.Transaction> trans = new ArrayList<Banking.Transaction>();
  private ORB orb;

  public InterBankImpl(ORB orb) {
    this.orb = orb;
  }

  public void transaction(Banking.Transaction transaction) {
    System.out.println("transaction: " + transaction.sender + " -> " + transaction.recipient + ": " + transaction.amount + " BTC");
    String recipientBank = this.bankNameFromAddress(transaction.recipient);
    if (banks.containsKey(recipientBank)) {
      banks.get(recipientBank).deposit(transaction.recipient, transaction.amount);
      trans.add(transaction);
      String senderBank = this.bankNameFromAddress(transaction.sender);
      banks.get(senderBank).withdraw(transaction.sender, transaction.amount);
    }
  }

  public void register(Banking.BankTransaction bank) {
    System.out.println("registered: " + bank.name());
    banks.put(bank.name(), bank);
  }

  public Banking.Transaction[] transactions() {
    return this.trans.toArray(new Banking.Transaction[this.trans.size()]);
  }

  private String bankNameFromAddress(String address) {
    return address.substring(0, 3);
  }
}
