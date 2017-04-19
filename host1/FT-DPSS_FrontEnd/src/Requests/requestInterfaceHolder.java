package Requests;

/**
 * Holder class for : requestInterface
 * 
 * @author OpenORB Compiler
 */
final public class requestInterfaceHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal requestInterface value
     */
    public Requests.requestInterface value;

    /**
     * Default constructor
     */
    public requestInterfaceHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public requestInterfaceHolder(Requests.requestInterface initial)
    {
        value = initial;
    }

    /**
     * Read requestInterface from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = requestInterfaceHelper.read(istream);
    }

    /**
     * Write requestInterface into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        requestInterfaceHelper.write(ostream,value);
    }

    /**
     * Return the requestInterface TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return requestInterfaceHelper.type();
    }

}
