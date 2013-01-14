/*
 * PhaseVector.java
 *
 * Created on March 19, 2003, 2:13 PM
 * Modified
 *      10/06   - CKA added indexing 
 */

package xal.tools.beam;

import  java.io.PrintWriter;
import  java.util.StringTokenizer;

import xal.tools.data.DataAdaptor;
import xal.tools.data.DataFormatException;
import xal.tools.data.IArchive;
import xal.tools.math.r3.R3;



/**
 *  <p>
 *  Represents a vector of homogeneous phase space coordinates for three spatial 
 *  dimensions.  Thus, each phase vector is an element of R7, the set of real 
 *  7-tuples.  
 *  </p>
 *  <p>
 *  The coordinates are as follows:
 *  <pre>
 *      (x, xp, y, yp, z, zp, 1)'
 *  </pre>
 *  where the prime indicates transposition and
 *  <pre>
 *      x  = x-plane position
 *      xp = x-plane momentum
 *      y  = y-plane position
 *      yp = y-plane momentum
 *      z  = z-plane position
 *      zp = z-plane momentum
 *  </pre>
 *  </p>
 *  <p>
 *  Homogeneous coordinates are parameterizations of the projective spaces <i>P<sup>n</sup></i>.  
 *  They are
 *  useful here to allow vector transpositions, normally produced by vector addition, to 
 *  be represented as matrix multiplications.  These operations can be embodied by the class
 *  <code>PhaseMatrix</code>.  Thus, <code>PhaseVector</code>'s are not intended to support
 *  vector addition.
 *  </p>
 *
 *
 * @author  Christopher Allen
 *
 *  @see    Jama.Matrix
 *  @see    xal.tools.math.Vector
 *  @see    PhaseMatrix
 */
public class PhaseVector implements java.io.Serializable, IArchive {
    
    
    /** Serialization Identifier  */
    private static final long serialVersionUID = 1L;
    
    /** attribute marker for data */
    public static final String     ATTR_DATA   = "values";
    
    
    
    
    /*
     *  Global Methods
     */
    
    /**
     *  Create a new instance of a zero phase vector.
     *
     *  @return         zero vector
     */
    public static PhaseVector  zero()   {
        Jama.Matrix     mat;
        mat = new Jama.Matrix(7, 1, 0.0);
        mat.set(6,0, 1.0);
        
        return new PhaseVector( mat );
    }
    
    /**
     * Creates a deep copy of the given <code>PhaseVector</code>
     * object.  Thus, the argument is unmodified and unreferenced.
     * 
     * @param   vec2Copy    vector object to clone
     * 
     * @return              deep copy of the argument
     */
    public static PhaseVector copy(final PhaseVector vec2Copy)    {
        return new PhaseVector(vec2Copy);
    }
    
    /**
     * Create a new instance of PhaseVector with initial value determined
     * by the formatted string argument.  The string should be formatted as
     * 
     *  "(x,x',y,y',z,zp')"
     * 
     * where x, x', y, y', z, z' are floating point representations.
     *
     *  @param  strTokens   six-token string representing values phase coordinates
     *
     *  @exception  IllegalArgumentException    wrong number of tokens in argument (must be 6)
     *  @exception  NumberFormatException       bad numeric value, unparseable
     */
    public static PhaseVector parse(String   strTokens)    
        throws NumberFormatException, IllegalArgumentException
    {
        return new PhaseVector(strTokens);
    }

    
    
    /*
     *  Local Attributes
     */
    
    /** internal vector representation */
    private Jama.Matrix     jamaVector;
    
    
    
    /**
     *  Creates a new instance of PhaseVector with zero initial value.
     */
    public PhaseVector() {
        jamaVector = new Jama.Matrix(7, 1, 0.0);
        jamaVector.set(6,0, 1.0);
    }
    
    /**
     *  Create a new instance of PhaseVector with specified initial value.
     *
     *  @param  x   x-plane position
     *  @param  xp  x-plane momentum
     *  @param  y   y-plane position
     *  @param  yp  y-plane momentum
     *  @param  z   z-plane position
     *  @param  zp  z-plane momentum
     *
     */
    public PhaseVector(double x, double xp, double y, double yp, double z, double zp)    {
        double      arrVec[] = { x, xp, y, yp, z, zp, 1.0};
        jamaVector = new Jama.Matrix(arrVec, 7);
    }
    
    /**
     *  Copy Constructor
     *
     *  Creates new <code>PhaseVector</code> object which is a <b>deep copy</b> of the
     *  given argument.
     *
     *  @param  vecInit     initial value
     */
    public PhaseVector(PhaseVector vecInit) {
        jamaVector = vecInit.getMatrix().copy();
    };
    
    
    
    /**
     *  Create a new instance of PhaseVector with specified initial value.
     *
     *  @param  arrVal      length 6 array of initial values
     *
     *  @exception  ArrayIndexOutOfBoundsException  argument must be a length-six array
     */
    public PhaseVector(double arrVal[])    {
        this(arrVal[0], arrVal[1], arrVal[2], arrVal[3], arrVal[4], arrVal[5]);
    }
    
    /**
     *  Create a new instance of PhaseVector with specified initial value.
     *
     *  @param  vecPos  position vector (x,y,z) in R3
     *  @param  vecMom  momentum vector (xp, yp, zp) in R3
     */
    public PhaseVector(R3 vecPos, R3 vecMom)    {
        this(vecPos.getx(), vecMom.getx(), vecPos.gety(), vecMom.gety(), vecPos.getz(), vecMom.getz());
    }
    
    /**
     * Create a new <code>PhaseVector</code> object and initialize it with the
     * data behind the <code>DataAdaptor</code> data source.
     * 
     * @param   daSource    data source containing initialization data
     * 
     * @throws DataFormatException      malformed data
     * 
     * @see xal.tools.data.IArchive#load(xal.tools.data.DataAdaptor)
     */
    public PhaseVector(DataAdaptor daSource) throws DataFormatException {
        this();
        this.load(daSource);
    }
    
    /**
     * Create a new instance of PhaseVector with specified initial value specified 
     * by the formatted string argument.  The input
     * string may or may not contain the final coordinate which always has value 1.
     * 
     * The string should be formatted as
     * 
     *  "(x,x',y,y',z,zp')"
     * 
     * where x, x', y, y', z, z' are floating point representations.
     * 
     * @param  strTokens   token string representing values phase coordinates
     *
     * @exception  IllegalArgumentException    wrong number of tokens in argument (must be 6 or 7)
     * @exception  NumberFormatException       bad numeric value, un-parseable
     * 
     * @see PhaseVector#setVector(java.lang.String)
     */
    public PhaseVector(String   strTokens)    
        throws NumberFormatException, IllegalArgumentException
    {
        this();
        this.setVector(strTokens);
        
//        // Error check the number of token strings
//        StringTokenizer     tokArgs = new StringTokenizer(strTokens, " ,()");
//        
//        if (tokArgs.countTokens() < 6)
//            throw new IllegalArgumentException("PhaseVector(strTokens) - wrong number of token strings: " + strTokens);
//        
//        
//        // Extract initial phase coordinate values
//        int                 i;      // loop control
//        
//        for (i=0; i<5; i++)  {
//            String  strVal = tokArgs.nextToken();
//            double  dblVal = Double.valueOf(strVal).doubleValue();
//            
//            this.getMatrix().set(i,0, dblVal);
//        }
//        
//        this.getMatrix().set(6,0, 1.0);
    }
    

    /**
     * Return a deep copy object of the current <code>PhaseVector<code> object.
     * Thus, the current object is unmodified and unreferenced.
     * 
     * @return      deep copy of the current object
     */
    public PhaseVector  copy()  {
        return new PhaseVector(this);
    }
    
    /*
     *  Assignment
     */
    

    /**
     * Create a new instance of PhaseVector with specified initial value specified 
     * by the formatted string argument.  The input
     * string may or may not contain the final coordinate which always has value 1.
     * 
     * The string should be formatted as
     * 
     *  "(x,x',y,y',z,zp')"
     * 
     * where x, x', y, y', z, z' are floating point representations.
     * 
     * @param  strValues   token string representing values phase coordinates
     *
     * @exception  IllegalArgumentException    wrong number of tokens in argument (must be 6 or 7)
     * @exception  NumberFormatException       bad numeric value, unparseable
     */
    public void setVector(String strValues) 
        throws DataFormatException, IllegalArgumentException
    {
        // Error check the number of token strings
        StringTokenizer     tokArgs = new StringTokenizer(strValues, " ,()");
        
        if (tokArgs.countTokens() < 6)
            throw new IllegalArgumentException("PhaseVector#setVector - wrong number of token strings: " + strValues);
        
        
        // Extract initial phase coordinate values
        int                 i;      // loop control
        
        for (i=0; i<5; i++)  {
            String  strVal = tokArgs.nextToken();
            double  dblVal = Double.valueOf(strVal).doubleValue();
            
            this.getMatrix().set(i,0, dblVal);
        }
        
        this.getMatrix().set(6,0, 1.0);
    }


    /**
     *  Set the element at index.  Note that you cannot change the last element value,
     *  it must remain 1.
     *
     *  @param  i       index of new element value
     *
     *  @exception  ArrayIndexOutOfBoundsException  index must be in {0,1,2,3,4,5}
     */
    public void setElem(int i, double dblVal)
        throws ArrayIndexOutOfBoundsException   
    {
        if (i>5) 
            throw new ArrayIndexOutOfBoundsException("PhaseMatrix#set() - index greater than 5.");
        
        this.getMatrix().set(i,0, dblVal);
    }
    
    /**
     *  Set the element at index.  Note that you cannot change the last element value,
     *  it must remain 1.
     *
     *  @param  i       index of new element value
     */
    public void setElem(PhaseIndex i, double dblVal)
    {
        this.getMatrix().set(i.val(),0, dblVal);
    }
    
    /**
     *  Set the x position coordinate
     */
    public void setx(double dblVal)  { this.setElem(0, dblVal); };
    
    /**
     *  Set the x momentum coordinate
     */
    public void setxp(double dblVal)  { this.setElem(1, dblVal); };
    
    /**
     *  Set the y position coordinate
     */
    public void sety(double dblVal)  { this.setElem(2, dblVal); };
    
    /**
     *  Set the y momentum coordinate
     */
    public void setyp(double dblVal)  { this.setElem(3, dblVal); };
    
    /**
     *  Set the z position coordinate
     */
    public void setz(double dblVal)  { this.setElem(4, dblVal); };
    
    /**
     *  Set the z momentum coordinate
     */
    public void setzp(double dblVal)  { this.setElem(5, dblVal); };
    
    
    /** 
     *  Return the element at index.
     *
     *  @return     the i-th element of the phase vector
     *
     *  @exception  ArrayIndexOutOfBoundsException  index must be in {0,1,2,3,4,5,6}
     */
    public double   getElem(int i)  
        throws ArrayIndexOutOfBoundsException
    {
        return this.getMatrix().get(i, 0);
    }
    
    /** 
     *  Return the element at index.
     *
     *  @return     the i-th element of the phase vector
     */
    public double   getElem(PhaseIndex i)  
    {
        return this.getMatrix().get(i.val(), 0);
    }
    
    /**
     *  Return the x position coordinate
     */
    public double   getx()  { return this.getElem(0); };
    
    /**
     *  Return the x momentum coordinate
     */
    public double   getxp() { return this.getElem(1); };
    
    /**
     *  Return the y position coordinate
     */
    public double   gety()  { return this.getElem(2); };
    
    /**
     *  Return the y momentum coordinate
     */
    public double   getyp() { return this.getElem(3); };
    
    /**
     *  Return the z momentum coordinate
     */
    public double   getz()  { return this.getElem(4); };
    
    /**
     *  Return the z momentum coordinate
     */
    public double   getzp() { return this.getElem(5); };
    
    /**
     *  Get position coordinates in R3.
     *
     *  @return     (x,y,z)
     */
    public R3   getPosition()   { return new R3(getx(), gety(), getz()); };
    
    /**
     *  Get momentum coordinate in R3.
     *
     *  @return     (xp,yp,zp)
     */
    public  R3  getMomentum()   { return new R3(getxp(), getyp(), getzp()); };
        
    
    
    
    /*
     * IArchive Interface
     */    

    /**
     * Save the value of this <code>PhaseVector</code> to disk.
     * 
     * @param daptArchive   interface to data sink 
     * 
     * @see xal.tools.data.IArchive#save(xal.tools.data.DataAdaptor)
     */
    public void save(DataAdaptor daptArchive) {
        daptArchive.setValue(PhaseVector.ATTR_DATA, this.toString());
    }

    /**
     * Restore the value of the this <code>PhaseVector</code> from the
     * contents of a data archive.
     * 
     * @param daptArchive   interface to data source
     * 
     * @throws DataFormatException      malformed data
     * 
     * @see xal.tools.data.IArchive#load(xal.tools.data.DataAdaptor)
     */
    public void load(DataAdaptor daptArchive) throws DataFormatException {
        if ( daptArchive.hasAttribute(PhaseVector.ATTR_DATA) )  {
            String  strValues = daptArchive.stringValue(PhaseVector.ATTR_DATA);
            this.setVector(strValues);         
        }
    }
    
    
    
    /*
     *  Object method overrides
     */
     
    /**
     *  Convert the vector contents to a string.
     *
     *  @return     vector value as a string (v0, v1, ..., v5)
     */
    @Override
    public String   toString()  {

        // Create vector string
        String  strVec = "(";

        for (int i=0; i<6; i++)
            strVec = strVec + this.getElem(i) + ",";
        strVec = strVec + this.getElem(6) + ")";
        
        return strVec;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this) { return true; }
        if(! (o instanceof PhaseVector)) { return false; }
        
        PhaseVector target = (PhaseVector)o;
        return getElem(0) == target.getElem(0) &&
            getElem(1) == target.getElem(1) &&
            getElem(2) == target.getElem(2) &&
            getElem(3) == target.getElem(3) &&
            getElem(4) == target.getElem(4) &&
            getElem(5) == target.getElem(5);
    }
    
    /**
     * "Borrowed" implementation from AffineTransform, since it is based on
     * double attribute values.  Must implement hashCode to be consistent with
     * equals as specified by contract of hashCode in <code>Object</code>.
     * 
     * @return a hashCode for this object
     */
    @Override
    public int hashCode() {
        long bits = Double.doubleToLongBits(getElem(0));
        bits = bits * 31 + Double.doubleToLongBits(getElem(1));
        bits = bits * 31 + Double.doubleToLongBits(getElem(2));
        bits = bits * 31 + Double.doubleToLongBits(getElem(3));
        bits = bits * 31 + Double.doubleToLongBits(getElem(4));
        bits = bits * 31 + Double.doubleToLongBits(getElem(5));
        return (((int) bits) ^ ((int) (bits >> 32)));
    }
    

    
    
    
    /*
     *  Algebraic Operations
     */
    
    /**
     * Element by element in-place negation.  The current 
     * <code>PhaseVector</code> object is changed in place.
     * 
     */
    public void    negateEquals() {
        double val;
        for (PhaseIndex i : PhaseIndex.values()) {
            val = this.getElem(i);
            
            this.setElem(i, -val);
        }
    }
    
    /**
     * Element by element negation.  A new object is returned and the
     * current one is unmodified.
     * 
     * @return     antipodal vector of the current object
     */
    public PhaseVector negate()    {
        PhaseVector    vecNeg = this.copy();
        
        vecNeg.negateEquals();
        return vecNeg;
    }
    
    /**
     *  Vector nondestructive addition.  Note only the phase coordinates are added.
     *
     *  @param  vec     vector to be added
     *  
     *  @return         vector sum (componentwise)
     */
    public PhaseVector  plus(PhaseVector vec)    {
        Jama.Matrix     matRes = this.getMatrix().plus( vec.getMatrix() );
        matRes.set(6,0, 1.0);
        
        return new PhaseVector( matRes );
    }
    
    /**
     *  Vector in-place addition.  Note only the phase coordinates are added.
     *
     *  @param  vec     vector to be added
     */
    public void plusEquals(PhaseVector vec)    {
        this.getMatrix().plusEquals( vec.getMatrix() );
        this.getMatrix().set(6,0, 1.0);
    }
    
    /** 
     *  Nondestructive scalar multiplication
     *
     *  @param  s   scalar
     *
     *  @return     result of scalar multiplication
     */
    public PhaseVector times(double s) {
        Jama.Matrix     matRes = this.getMatrix().times(s);
        matRes.set(6,0, 1.0);
        
        return new PhaseVector( matRes );
    }
    
    /** 
     *  In-place scalar multiplication
     *
     *  @param  s   scalar
     */
    public void timesEquals(double s) {
        this.getMatrix().times(s);
        this.getMatrix().set(6,0, 1.0);
    }
    
    /** 
     *  Premultiply PhaseVector by a PhaseMatrix.
     *
     *  @param  mat     matrix operator
     *  @return         result of matrix-vector product
     */
    public PhaseVector times(PhaseMatrix mat) {
        Jama.Matrix  matRes = this.getMatrix().times( mat.getMatrix() );
        
        matRes = matRes.transpose();
        
        return new PhaseVector( matRes );
    }
    
    /**
     *  Vector inner product operation using ONLY the <b>phase coordinates</b>.
     *
     *  @param  vec     second argument to inner product operation
     *
     *  @return         inner product (this,vec)
     */
    public double   innerProd(PhaseVector vec)  {
        int     i;          // loop control
        double  dblSum;     // running sum
        
        dblSum = 0.0;
        for (i=0; i<6; i++) 
            dblSum += this.getElem(i)*vec.getElem(i);
        
        return dblSum;
    }
    
    /**
     *  Vector outer product operation.  Returns the tensor outer product
     *  as a <code>PhaseMatrix</code> object
     *
     *  @param  vec     second argument to tensor product
     *
     *  @return         outer product = [ this_i*vec_j ]
     */
    public PhaseMatrix outerProd(PhaseVector vec)   {
        int         i, j;       // loop control
        PhaseMatrix matRes;     // resultant outer product
        
        matRes = new PhaseMatrix();
        
        double v1[][] = jamaVector.getArray();
        double v2[][] = vec.getMatrix().getArray();
        
        /*def
        for (i=0; i<7; i++) {
            for (j=0; j<7; j++) {
             //   matRes.setElem(i,j, jamaVector.get(i,0)*vec.getElem(j));
                matRes.setElem(i,j, this.getElem(i)*vec.getElem(j));
            }
        }*/
        
        for (i=0; i<7; i++) {
            for (j=0; j<7; j++) {
                matRes.setElem(i,j, v1[i][0]*v2[j][0]);
            }
        }
        return matRes;
    }
    
    
    
    /*
     *  Topological Operations
     */
    
    /**
     *  Return the l1 norm of the vector's <b>phase components</b>.
     *
     *  @return     ||z||_1 = Sum |z_i|
     */
    public double   norm1()     { 
        int         i;          // loop control
        double      dblSum;     // running sum
        
        dblSum = 0.0;
        for (i=0; i<6; i++) 
            dblSum += Math.abs( this.getElem(i) );
        
        return dblSum;
    };
    
    /**
     *  Return the l2 norm of the vector.
     *
     *  @return     ||z||_2 = [ Sum |z_i| ]^1/2
     */
    public double   norm2()     { 
        int         i;          // loop control
        double      dblSum;     // running sum
        
        dblSum = 0.0;
        for (i=0; i<6; i++) 
            dblSum += this.getElem(i)*this.getElem(i);
        
        return dblSum;
    }
    
    /**
     *  Return the l-infinity norm of the vector.
     *
     *  @return     ||z||_inf = Sup_i |z_i|
     */
    public double   normInf()     { 
        int         i;          // loop control
        double      dblMax;     // running maximum
        
        dblMax = 0.0;
        for (i=0; i<6; i++) 
            if (Math.abs( this.getElem(i) ) > dblMax ) 
                dblMax = Math.abs( this.getElem(i) );
        
        return dblMax;
    }
    
    
    
    /*
     *  Internal Support
     */
    
    /**
     *  Construct a PhaseVector from a suitable Jama.Matrix.  Note that the
     *  argument should be a new object not owned by another object, because
     *  the internal matrix representation is assigned to the target argument.
     *
     *  @param  matInit     a 7x1 Jama.Matrix object
     */
    PhaseVector(Jama.Matrix matInit)  {
        jamaVector = matInit;
    }
    
    /**
     *  Return the internal vector representation
     *
     *  @return     the Jama.Matrix object
     */
    Jama.Matrix   getMatrix()     { return jamaVector; }
    


    
    /*
     *  Testing and Debugging
     */


    /**
     * Print this vector to standard out.
     */
    public void print() {
        jamaVector.print( 10, 5 );
    }
    
    
    /**
     *  Print the vector contents to an output stream,
     *  does not add new line.
     *
     *  @param  os      output stream object 
     */
    public void print(PrintWriter os)   {

        // Create vector string
        String  strVec = this.toString();

        // Send to output stream
        os.print(strVec);
    }
            
    /**
     *  Print the vector contents to an output stream, 
     *  add new line character.
     *
     *  @param  os      output stream object 
     */
    public void println(PrintWriter os)   {

        // Create vector string
        String  strVec = this.toString();

        // Send to output stream
        os.println(strVec);
    };
    
    /**
     * Print the vector contents to a String.
     */
    public String printString() {
        String strVec = "";
        for (int i=0; i<5; i++)
            strVec = strVec + this.getElem(i) + ",";
        strVec = strVec + this.getElem(6);
        return strVec;
    }
    
    
    
    
    
    /**
     *  Test driver
     */
    public static void main(String arrArgs[])   {
        PrintWriter     os = new PrintWriter( System.out );
        
        PhaseVector     z1 = new PhaseVector();
        os.print("Vector #1 = ");
        z1.println(os);
        
        PhaseVector     z2 = new PhaseVector(1.0, 0.0, 2.0, 0.0, 3.0, 0.0);
        os.print("Vector #2 = ");
        z2.println(os);
        
        PhaseVector     z3 = new PhaseVector("1.0 2.0 3.0 4.0 5.0 6.0");
        os.print("Vector #3 = ");
        z3.println(os);
        
        os.flush();
    }
}
