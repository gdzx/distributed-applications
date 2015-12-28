import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;

class EchoImpl extends EchoPOA {
  public String echoString(String msg) {
    System.out.println("msg: " + msg);
    return msg;
  }
}
