/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.types;

import java.io.PrintStream;
import java.util.Vector;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.XType;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.parser.Node;
import org.apache.xpath.parser.SchemaContext;
import org.apache.xpath.parser.SequenceType;
import org.apache.xpath.parser.SimpleNode;
import org.apache.xpath.parser.XPath;

/**
 * This class implements the "castable as" production for XPath 2.0.
 * 
 * Created Jul 17, 2002
 * @author sboag
 */
public class CastableExpr extends Expression implements ExpressionOwner
{
  /** The sequence on which to act. **/
  private Expression m_targetExpr;
  
  /** The node type value, one of DTMFilter.SHOW_ATTRIBUTE, etc. 
   * %REVIEW% Why isn't this just the node type? **/
  private int m_whatToShow;
  
  /** The atomic type ID, one of XType.BOOLEAN, etc. **/
  private int m_atomicTypeID;
  
  // Occurence indicator ID values.
  public static final int ZERO_OR_MORE = 1;
  public static final int ONE_OR_MORE = 2;
  public static final int ZERO_OR_ONE = 3;
  public static final int EMPTY_SEQ = 4;
    
  /** The occurance indicator ID.  One of ZERO_OR_MORE, etc. or EMPTY_SEQ **/
  private int m_occurrenceIndicator = ONE_OR_MORE;
  
  /** The ElemOrAttrType name, meaning the tag QName, may be null. */
  private QName m_elemOrAttrName;

  /** The SchemaType name, meaning the tag QName, may be null. */
  private QName m_schemaTypeName;
  
  /** SchemaContext is not executable for the moment, so leave it 
   * as a NEE until we figure out what we want to do with it. **/
  private SchemaContext m_schemaContext;
  
  /** Flag indicating whether expression should be reduced.
   *  False if this has more than one child. **/
  private boolean m_reduce = true;
	
  public CastableExpr() {
    super();
    
  }

  /** Accept the visitor. **/
  public Object jjtAccept(org.apache.xpath.parser.XPathVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
    if(!(expr instanceof CastableExpr))
      return false;
    return false;
  }

  /**
   * @see Expression#fixupVariables(Vector, int)
   */
  public void fixupVariables(VariableComposeState vcs)
  {
    m_targetExpr.fixupVariables(vcs);
  }


  /**
   * @see Expression#execute(XPathContext)
   */
  public XObject execute(XPathContext xctxt) throws TransformerException
  {
 	// m_atomicTypeID: Integer XType primitive, IFF no m_schemaTypeName
	// m_schemaContext: SchemaContext, UNSPECIFIED AT THIS TIME
	//	and hence unsupported.
 	boolean isInstance=true;
 	
 	XObject value=m_targetExpr.execute(xctxt);
 	
 	int type = value.getType();
 	switch(type)
 	{
 		case XObject.CLASS_UNKNOWN:
	 		// If not XSLT type, can't be instance of ...?
	 		// %REVIEW% what SHOW_ITEM and SHOW_UNTYPED mean
 		case XObject.CLASS_NULL:
	 		isInstance=false; 
	 		break;
	 		
		case XObject.CLASS_NODESET:	
		case XObject.CLASS_RTREEFRAG:
			int len=0;
			XSequence valNodeSet= value.xseq();
 			for( XObject next=valNodeSet.next();
 				isInstance && next!=null;
 				next=valNodeSet.next(),++len)
 			{
        int handle = next.getNodeHandle();
        if(DTM.NULL == handle)
          continue;
 				if(m_whatToShow!=0)
 				{
 					int nodetype=xctxt.getDTM(handle).getNodeType(handle);
 					if(0==(m_whatToShow & 1<<(nodetype-1)))
 						isInstance=false;
 					else if(m_elemOrAttrName!=null)
 					{
 						isInstance&=
 							(m_elemOrAttrName.getNamespace/*URI*/()
 								==xctxt.getDTM(handle).getNamespaceURI(handle))
 							&& 
 							(m_elemOrAttrName.getLocalName()
 								==xctxt.getDTM(handle).getLocalName(handle));
 					} 				
 				}
 				if(m_schemaTypeName!=null)
 				{
 					isInstance&=
 						xctxt.getDTM(handle).isNodeSchemaType(
 							handle, 
 							m_schemaTypeName.getNamespaceURI(),
 							m_schemaTypeName.getLocalName());
 				}
 				else if(m_atomicTypeID!=0)
 				{
 					// Unless DTM implements awareness of these
 					// typenumbers, or our sequence/nodeset layers
 					// handle this for us, we need to convert back
 					// to strings. We also have to convert from the
 					// Schema_datatype namespace, used in requesting the
 					// primitive, to the Schema namespace used when
 					// the node was declared.
 					isInstance&=
 						xctxt.getDTM(handle).isNodeSchemaType(
 							handle, 
 							XType.XMLSCHEMA_NAMESPACE,
 							XType.getLocalNameFromType(m_atomicTypeID));
 				} 				
			} // End scan contents
 			switch(m_occurrenceIndicator)
 			{
 				case ZERO_OR_MORE:
 					break; // Anything is OK
 				case ONE_OR_MORE:
 					isInstance&=(len>=1);
 					break;
 				case ZERO_OR_ONE:
 					isInstance&=(len<2);
 					break;
 				case EMPTY_SEQ:
 					isInstance&=(len==0);
 					break;
 			}
 			break;
 	
 		/*case XObject.CLASS_BOOLEAN:
			isInstance&=(m_elemOrAttrName==null);
			isInstance&=(m_schemaTypeName==null);
			isInstance&=(m_whatToShow==0);
			isInstance&=(m_atomicTypeID==XType.BOOLEAN);
			break;
			
		case XObject.CLASS_NUMBER:
			isInstance&=(m_elemOrAttrName==null);
			isInstance&=(m_schemaTypeName==null);
			isInstance&=(m_whatToShow==0);
			// %REVIEW% XNumber is just that, a number.
			// We want *it* to tell *us* whether it's straight
			// decimal or double or whatever...?
			isInstance&=(m_atomicTypeID==XType.DECIMAL
				|| m_atomicTypeID==XType.DOUBLE);
			break;
			
		case XObject.CLASS_STRING:
			isInstance&=(m_elemOrAttrName==null);
			isInstance&=(m_schemaTypeName==null);
			isInstance&=(m_whatToShow==0);
			isInstance&=(m_atomicTypeID==XType.STRING);
			break;
 		*/	
 		default:
 		    isInstance&=(m_elemOrAttrName==null);
			isInstance&=(m_schemaTypeName==null);
			isInstance&=(m_whatToShow==0);
			isInstance&=(m_atomicTypeID==type);
			break;
			// Should never arise
			//isInstance=false;
 			//break;
 	}
 	
 	return isInstance ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }


  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
    m_targetExpr.callVisitors(this, visitor);
  }


  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
    return m_targetExpr;
  }


  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
    m_targetExpr = exp;
  }

  /**
   * @see SimpleNode#shouldReduceIfOneChild()
   */
  public boolean shouldReduceIfOneChild()
  {
    return (m_whatToShow == 0 && m_atomicTypeID == 0 && m_reduce) ? true : false;
  }


  /**
   * Returns the occurrenceIndicator.
   * @return int
   */
  public int getOccurrenceIndicator()
  {
    return m_occurrenceIndicator;
  }

  /**
   * Returns the schemaContext.
   * @return SchemaContext
   */
  public SchemaContext getSchemaContext()
  {
    return m_schemaContext;
  }

  /**
   * Returns the schemaTypeName.
   * @return QName
   */
  public QName getSchemaTypeName()
  {
    return m_schemaTypeName;
  }

  /**
   * Returns the targetExpr.
   * @return Expression
   */
  public Expression getTargetExpr()
  {
    return m_targetExpr;
  }

  /**
   * Returns the whatToShow.
   * @return int
   */
  public int getWhatToShow()
  {
    return m_whatToShow;
  }

  /**
   * Sets the occurrenceIndicator.
   * @param occurrenceIndicator The occurrenceIndicator to set
   */
  public void setOccurrenceIndicator(int occurrenceIndicator)
  {
    m_occurrenceIndicator = occurrenceIndicator;
  }

  /**
   * Sets the schemaContext.
   * @param schemaContext The schemaContext to set
   */
  public void setSchemaContext(SchemaContext schemaContext)
  {
    m_schemaContext = schemaContext;
  }

  /**
   * Sets the schemaTypeName.
   * @param schemaTypeName The schemaTypeName to set
   */
  public void setSchemaTypeName(QName schemaTypeName)
  {
    m_schemaTypeName = schemaTypeName;
  }

  /**
   * Sets the targetExpr.
   * @param targetExpr The targetExpr to set
   */
  public void setTargetExpr(Expression targetExpr)
  {
    m_targetExpr = targetExpr;
  }

  /**
   * Sets the whatToShow.
   * @param whatToShow The whatToShow to set
   */
  public void setWhatToShow(int whatToShow)
  {
    m_whatToShow = whatToShow;
  }
  
  /**
   * @see org.apache.xpath.parser.Node#jjtAddChild(Node, int)
   */
  public void jjtAddChild(Node n, int i)
  {
    if(0 == i)
    {
      n = this.fixupPrimarys(n);
      m_targetExpr = (Expression)n;
    }
    else if(2 == i) // skip the child at index 1, which is the instance of keyword.
    {
      SequenceType stype = (SequenceType)n;
      m_atomicTypeID = stype.getAtomicTypeID();
      m_elemOrAttrName = stype.getElemOrAttrName();
      m_occurrenceIndicator = stype.getOccurrenceIndicator();
      m_schemaContext = stype.getSchemaContext();
      m_schemaTypeName = stype.getSchemaTypeName();
      m_whatToShow = stype.getWhatToShow();
      m_reduce = false;
    }
  }

  /**
   * @see org.apache.xpath.ExpressionNode#exprGetChild(int)
   */
  public ExpressionNode exprGetChild(int i)
  {
    // This doesn't work perfectly, but it's good enough.
    return (0 == i) ? m_targetExpr : (1 == 0) ? m_schemaContext : null;
  }

  /**
   * @see org.apache.xpath.ExpressionNode#exprGetNumChildren()
   */
  public int exprGetNumChildren()
  {
    // This doesn't work perfectly, but it's good enough.
    return ((null == m_targetExpr) ? 1 : 0)+((null == m_schemaContext) ? 1 : 0);
  }

  /**
   * @see org.apache.xpath.parser.SimpleNode#dump(String, PrintStream)
   */
  public void dump(String prefix, PrintStream ps)
  {
    super.dump(prefix, ps);
    prefix = prefix+"   ";
    ps.print(prefix);
    ps.println("m_atomicTypeID: "+m_atomicTypeID);
    ps.print(prefix);
    ps.println("m_elemOrAttrName: "+m_elemOrAttrName);
    ps.print(prefix);
    ps.println("m_occurrenceIndicator: "+m_occurrenceIndicator);
    ps.print(prefix);
    ps.println("m_schemaContext: "+m_schemaContext);
    ps.print(prefix);
    ps.println("m_schemaTypeName: "+m_schemaTypeName);
    ps.print(prefix);
    ps.println("m_whatToShow: "+m_whatToShow);
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return "instance of";
  }

}