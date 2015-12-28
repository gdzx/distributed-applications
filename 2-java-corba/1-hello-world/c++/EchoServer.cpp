#include <iostream>
#include <omniORB4/CORBA.h>
#include <omniORB4/Naming.hh>
#include "Echo.hh"

using namespace std;

class EchoImpl : public POA_Echo, public PortableServer::RefCountServantBase {
  public:
    virtual char* echoString(const char* msg) {
      cout << msg << endl;
      return CORBA::string_dup(msg);
    }
};

int main(int argc, char** argv) {
  CORBA::Object_var objRef;
  // create and initialize the ORB
  CORBA::ORB_var orb = CORBA::ORB_init(argc, argv);
  // get reference to rootpoa & activate the POAManager
  objRef = orb->resolve_initial_references("RootPOA");
  PortableServer::POA_var poaRef = PortableServer::POA::_narrow(objRef);
  poaRef->the_POAManager()->activate();
  // get the naming service
  objRef = orb->resolve_initial_references("NameService");
  CosNaming::NamingContext_var ncRef =
    CosNaming::NamingContext::_narrow(objRef);
  // instantiate the Echo CORBA object
  EchoImpl * echoImpl = new EchoImpl();
  Echo_var echoRef = echoImpl->_this();
  // bind the object reference in the naming service
  CosNaming::Name name;
  name.length(1);
  name[0].id
    = (const char*)"echo";
  name[0].kind = (const char*)"echo";
  ncRef->rebind(name, echoRef);
  // start server...
  orb->run();

  return 0;
}
