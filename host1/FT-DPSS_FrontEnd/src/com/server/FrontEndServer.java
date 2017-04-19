package com.server;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

//this class will generate the corba remote object which will be called by frontend client
public class FrontEndServer {

	public static void main(String[] args) throws ServantAlreadyActive, WrongPolicy, InvalidName, ObjectNotActive, FileNotFoundException, AdapterInactive {
		
		ORB orb = ORB.init(args, null);
		POA rootPOAAS = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

		
		RequestImpl server = new RequestImpl();
		
		
		byte[] id = rootPOAAS.activate_object(server);
		
		org.omg.CORBA.Object ref = rootPOAAS.id_to_reference(id);
		
		String ior = orb.object_to_string(ref);
		
		PrintWriter file = new PrintWriter("ior.txt");
		file.println(ior);
		file.close();
		//System.out.print(ior);
		System.out.println("Corba server running");
		
		rootPOAAS.the_POAManager().activate();
		
		orb.run();

	}

}
