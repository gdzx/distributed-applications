#include <iostream>
#include <omniORB4/CORBA.h>
#include <omniORB4/Naming.hh>
#include "Echo.hh"

using namespace std;

int main (int argc, char **argv) {
  CORBA::Object_var objRef;
  // initialize the ORB
  CORBA::ORB_var orb = CORBA::ORB_init(argc, argv);
  // get the naming service
  objRef = orb->resolve_initial_references("NameService");
  CosNaming::NamingContext_var nsRef =
    CosNaming::NamingContext::_narrow(objRef);
  // resolve the "echo" CORBA object from the naming service
  CosNaming::Name name; name.length(1);
  name[0].id
    = (const char*) "echo";
  name[0].kind = (const char*) "echo";
  objRef = nsRef->resolve(name);
  Echo_var echoRef = Echo::_narrow(objRef);
  // remote method invokation
  cout << echoRef->echoString("coucou") << endl;
  return 0;
}
