/*
 * The Apache Software License, Version 1.1
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
 * originally based on software copyright (c) 2002, International
 * Business Machines Corporation., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.impl.parser;

import org.apache.xpath.expression.NodeTest;
import org.apache.xpath.expression.StepExpr;
import org.apache.xpath.impl.KindTestImpl;
import org.apache.xpath.impl.StepExprImpl;


/**
 *
 */
public class Singletons extends SimpleNode
{
    /**
     * DotDot singleton
     */
    protected static final Singletons DOTDOT = new Singletons(XPathTreeConstants.JJTDOTDOT);

    /**
     * Slash singleton
     */
    protected static final Singletons SLASH = new Singletons(XPathTreeConstants.JJTSLASH);

    /**
     * At singleton
     */
    protected static final Singletons AT = new Singletons(XPathTreeConstants.JJTAT);

    /**
     * NodeTest singleton
     */
    protected static final Singletons NODETEST = new Singletons(XPathTreeConstants.JJTNODETEST);

    /**
     * Root singleton
     */
    protected static final Singletons ROOT = new Singletons(XPathTreeConstants.JJTROOT);

    /**
     * Root descendants singleton
     */
    protected static final Singletons ROOTDESCENDANT = new Singletons(XPathTreeConstants.JJTROOTDESCENDANTS);

    /**
     * Minus singleton
     */
    protected static final Singletons MINUS = new Singletons(XPathTreeConstants.JJTMINUS);

    /**
     * Plus singleton (arithmetic and occurrence indicator)
     */
    protected static final Singletons PLUS = new Singletons(XPathTreeConstants.JJTPLUS);

    /**
     * SlashSlash singleton
     */
    static final public StepExprImpl SLASHSLASH;

    /**
     * Empty singleton (appear in sequencetype)
     */
    protected static final Singletons EMPTY = new Singletons(XPathTreeConstants.JJTEMPTY);

 
    /**
     * Element type singleton (appear in sequencetype)
     */
    protected static final Singletons ELEMENT = new Singletons(XPathTreeConstants.JJTELEMENTTYPE);

    /**
     * Attribute type singleton (appear in sequencetype)
     */
    protected static final Singletons ATTRIBUTE = new Singletons(XPathTreeConstants.JJTATTRIBUTETYPE);

    /**
     * Atomic type singleton (appear in sequencetype)
     */
    protected static final Singletons ATOMIC = new Singletons(XPathTreeConstants.JJTATOMICTYPE);

    /**
     * Node type singleton (appear in sequencetype)
     */
    protected static final Singletons NODE = new Singletons(XPathTreeConstants.JJTNODE);

    /**
     * PI type singleton (appear in sequencetype)
     */
    protected static final Singletons PI = new Singletons(XPathTreeConstants.JJTPROCESSINGINSTRUCTION);

    /**
     * Comment type singleton (appear in sequencetype)
     */
    protected static final Singletons COMMENT = new Singletons(XPathTreeConstants.JJTCOMMENT);

    /**
     * Text type singleton (appear in sequencetype)
     */
    protected static final Singletons TEXT = new Singletons(XPathTreeConstants.JJTTEXT);

    /**
     * Document type singleton (appear in sequencetype)
     */
    protected static final Singletons DOCUMENT = new Singletons(XPathTreeConstants.JJTDOCUMENT);

    /**
     * Item type singleton (appear in sequencetype)
     */
    protected static final Singletons ITEM = new Singletons(XPathTreeConstants.JJTITEM);
    
	/**
		* Multiply occurrence indicator singleton (appear in sequencetype)
		*/
	   protected static final Singletons MULTIPLY = new Singletons(XPathTreeConstants.JJTMULTIPLY);
	   
	/**
	 * Question mark occurrence indicator singleton (appear in sequencetype)
	 */
	protected static final Singletons QMARK = new Singletons(XPathTreeConstants.JJTQMARK);

    /**
     * Dot kind test singleton
     */
    static final public KindTestImpl DOT_KIND_TEST;

    static
    {
        KindTestImpl kt = new KindTestImpl();
        kt.setKindTest(NodeTest.ANY_KIND_TEST);

        SLASHSLASH = new StepExprImpl(StepExpr.AXIS_DESCENDANT_OR_SELF, kt);

        DOT_KIND_TEST = new KindTestImpl(XPathTreeConstants.JJTDOT);
        DOT_KIND_TEST.setKindTest(NodeTest.CONTEXT_ITEM_TEST);
    }

    /**
     * Constructor for Singletons.
     *
     * @param i
     */
    private Singletons(int i)
    {
        super(i);
    }

    /**
     * Constructor for Singletons.
     *
     * @param p
     * @param i
     */
    private Singletons(XPath p, int i)
    {
        super(p, i);
    }
}