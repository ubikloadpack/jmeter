/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 * "Apache JMeter" must not be used to endorse or promote products
 * derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * "Apache JMeter", nor may "Apache" appear in their name, without
 * prior written permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 */

// The developers of JMeter and Apache are greatful to the developers
// of HTMLParser for giving Apache Software Foundation a non-exclusive
// license. The performance benefits of HTMLParser are clear and the
// users of JMeter will benefit from the hard work the HTMLParser
// team. For detailed information about HTMLParser, the project is
// hosted on sourceforge at http://htmlparser.sourceforge.net/.
//
// HTMLParser was originally created by Somik Raha in 2000. Since then
// a healthy community of users has formed and helped refine the
// design so that it is able to tackle the difficult task of parsing
// dirty HTML. Derrick Oswald is the current lead developer and was kind
// enough to assist JMeter.


package org.htmlparser.parserapplications;
import java.util.Enumeration;
import java.util.Vector;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;


/**
 * MailRipper will rip out all the mail addresses from a given web page
 * Pass a web site (or html file on your local disk) as an argument.
 */
public class MailRipper
{
    private org.htmlparser.Parser parser;
    /**
     * MailRipper c'tor takes the url to be ripped
     * @param resourceLocation url to be ripped
     */
    public MailRipper(String resourceLocation)
    {
        try
        {
            parser = new Parser(resourceLocation, new DefaultParserFeedback());
            parser.registerScanners();
        }
        catch (ParserException e)
        {
            System.err.println("Could not create parser object");
            e.printStackTrace();
        }
    }
    public static void main(String[] args)
    {
        System.out.println("Mail Ripper v" + Parser.getVersion());
        if (args.length < 1 || args[0].equals("-help"))
        {
            System.out.println();
            System.out.println(
                "Syntax : java -classpath htmlparser.jar org.htmlparser.parserapplications.MailRipper <resourceLocn/website>");
            System.out.println();
            System.out.println(
                "   <resourceLocn> the name of the file to be parsed (with complete path ");
            System.out.println(
                "                  if not in current directory)");
            System.out.println("   -help This screen");
            System.out.println();
            System.out.println(
                "HTML Parser home page : http://htmlparser.sourceforge.net");
            System.out.println();
            System.out.println(
                "Example : java -classpath htmlparser.jar com.kizna.parserapplications.MailRipper http://htmlparser.sourceforge.net");
            System.out.println();
            System.out.println(
                "If you have any doubts, please join the HTMLParser mailing list (user/developer) from the HTML Parser home page instead of mailing any of the contributors directly. You will be surprised with the quality of open source support. ");
            System.exit(-1);
        }
        String resourceLocation = "http://htmlparser.sourceforge.net";
        if (args.length != 0)
            resourceLocation = args[0];

        MailRipper ripper = new MailRipper(resourceLocation);
        System.out.println("Ripping Site " + resourceLocation);
        try
        {
            for (Enumeration e = ripper.rip(); e.hasMoreElements();)
            {
                LinkTag tag = (LinkTag) e.nextElement();
                System.out.println("Ripped mail address : " + tag.getLink());
            }
        }
        catch (ParserException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Rip all mail addresses from the given url, and return an enumeration of such mail addresses.
     * @return Enumeration of mail addresses (a vector of LinkTag)
     */
    public Enumeration rip() throws ParserException
    {
        Node node;
        Vector mailAddresses = new Vector();
        for (NodeIterator e = parser.elements(); e.hasMoreNodes();)
        {
            node = e.nextNode();
            if (node instanceof LinkTag)
            {
                LinkTag linkTag = (LinkTag) node;
                if (linkTag.isMailLink())
                    mailAddresses.addElement(linkTag);
            }
        }
        return mailAddresses.elements();
    }
}
