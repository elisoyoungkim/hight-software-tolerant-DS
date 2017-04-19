package Requests;

/** 
 * Helper class for : requestInterface
 *  
 * @author OpenORB Compiler
 */ 
public class requestInterfaceHelper
{
    /**
     * Insert requestInterface into an any
     * @param a an any
     * @param t requestInterface value
     */
    public static void insert(org.omg.CORBA.Any a, Requests.requestInterface t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract requestInterface from an any
     *
     * @param a an any
     * @return the extracted requestInterface value
     */
    public static Requests.requestInterface extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return Requests.requestInterfaceHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the requestInterface TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "requestInterface" );
        }
        return _tc;
    }

    /**
     * Return the requestInterface IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:Requests/requestInterface:1.0";

    /**
     * Read requestInterface from a marshalled stream
     * @param istream the input stream
     * @return the readed requestInterface value
     */
    public static Requests.requestInterface read(org.omg.CORBA.portable.InputStream istream)
    {
        return(Requests.requestInterface)istream.read_Object(Requests._requestInterfaceStub.class);
    }

    /**
     * Write requestInterface into a marshalled stream
     * @param ostream the output stream
     * @param value requestInterface value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, Requests.requestInterface value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to requestInterface
     * @param obj the CORBA Object
     * @return requestInterface Object
     */
    public static requestInterface narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof requestInterface)
            return (requestInterface)obj;

        if (obj._is_a(id()))
        {
            _requestInterfaceStub stub = new _requestInterfaceStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to requestInterface
     * @param obj the CORBA Object
     * @return requestInterface Object
     */
    public static requestInterface unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof requestInterface)
            return (requestInterface)obj;

        _requestInterfaceStub stub = new _requestInterfaceStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
