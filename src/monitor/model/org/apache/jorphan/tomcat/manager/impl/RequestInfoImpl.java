//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2004.03.03 at 07:33:19 GMT-05:00 
//


package org.apache.jorphan.tomcat.manager.impl;

public class RequestInfoImpl implements org.apache.jorphan.tomcat.manager.RequestInfo, com.sun.xml.bind.unmarshaller.UnmarshallableObject, com.sun.xml.bind.serializer.XMLSerializable, com.sun.xml.bind.validator.ValidatableObject
{

    protected boolean has_BytesReceived;
    protected long _BytesReceived;
    protected boolean has_BytesSent;
    protected long _BytesSent;
    protected boolean has_RequestCount;
    protected long _RequestCount;
    protected boolean has_ErrorCount;
    protected long _ErrorCount;
    protected boolean has_MaxTime;
    protected int _MaxTime;
    protected boolean has_ProcessingTime;
    protected int _ProcessingTime;
    private final static com.sun.msv.grammar.Grammar schemaFragment = com.sun.xml.bind.validator.SchemaDeserializer.deserialize("\u00ac\u00ed\u0000\u0005sr\u0000\u001fcom.sun.msv.grammar.SequenceExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\u001dcom.sun.msv.grammar.BinaryExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0004exp1t\u0000 Lcom/sun/msv/grammar/Expression;L\u0000\u0004exp2q\u0000~\u0000\u0002xr\u0000\u001ecom.sun.msv.grammar.Expression\u00f8\u0018\u0082\u00e8N5~O\u0002\u0000\u0003I\u0000\u000ecachedHashCodeL\u0000\u0013epsilonReducibilityt\u0000\u0013Ljava/lang/Boolean;L\u0000\u000bexpandedExpq\u0000~\u0000\u0002xp\u000b\u0091\n\u00dfppsq\u0000~\u0000\u0000\nL-\u00d3ppsq\u0000~\u0000\u0000\u0007z+\u00b5ppsq\u0000~\u0000\u0000\u0005$3Wppsq\u0000~\u0000\u0000\u0003<\u001a\u009bppsr\u0000 com.sun.msv.grammar.AttributeExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\u0003expq\u0000~\u0000\u0002L\u0000\tnameClasst\u0000\u001fLcom/sun/msv/grammar/NameClass;xq\u0000~\u0000\u0003\u0001\u00c3\u0015\u00c6ppsr\u0000\u001bcom.sun.msv.grammar.DataExp\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\u0002dtt\u0000\u001fLorg/relaxng/datatype/Datatype;L\u0000\u0006exceptq\u0000~\u0000\u0002L\u0000\u0004namet\u0000\u001dLcom/sun/msv/util/StringPair;xq\u0000~\u0000\u0003\u0000]\u00ce\u00d6ppsr\u0000!com.sun.msv.datatype.xsd.LongType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000+com.sun.msv.datatype.xsd.IntegerDerivedType\u0099\u00f1]\u0090&6k\u00be\u0002\u0000\u0000xr\u0000*com.sun.msv.datatype.xsd.BuiltinAtomicType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000%com.sun.msv.datatype.xsd.ConcreteType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xr\u0000\'com.sun.msv.datatype.xsd.XSDatatypeImpl\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0003L\u0000\fnamespaceUrit\u0000\u0012Ljava/lang/String;L\u0000\btypeNameq\u0000~\u0000\u0016L\u0000\nwhiteSpacet\u0000.Lcom/sun/msv/datatype/xsd/WhiteSpaceProcessor;xpt\u0000 http://www.w3.org/2001/XMLSchemat\u0000\u0004longsr\u0000.com.sun.msv.datatype.xsd.WhiteSpaceProcessor$2\u0087z9\u00ee\u00f8,N\u0005\u0002\u0000\u0000xr\u0000,com.sun.msv.datatype.xsd.WhiteSpaceProcessor\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpsr\u00000com.sun.msv.grammar.Expression$NullSetExpression\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0003\u0000\u0000\u0000\nppsr\u0000\u001bcom.sun.msv.util.StringPair\u00d0t\u001ejB\u008f\u008d\u00a0\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000\u0016L\u0000\fnamespaceURIq\u0000~\u0000\u0016xpq\u0000~\u0000\u001aq\u0000~\u0000\u0019sr\u0000#com.sun.msv.grammar.SimpleNameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0002L\u0000\tlocalNameq\u0000~\u0000\u0016L\u0000\fnamespaceURIq\u0000~\u0000\u0016xr\u0000\u001dcom.sun.msv.grammar.NameClass\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xpt\u0000\nerrorCountt\u0000\u0000sq\u0000~\u0000\n\u0001y\u0004\u00d0ppsq\u0000~\u0000\r\u0001d\u00db\u00e5ppsr\u0000 com.sun.msv.datatype.xsd.IntType\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0000xq\u0000~\u0000\u0012q\u0000~\u0000\u0019t\u0000\u0003intq\u0000~\u0000\u001dq\u0000~\u0000\u001fsq\u0000~\u0000 q\u0000~\u0000+q\u0000~\u0000\u0019sq\u0000~\u0000\"t\u0000\u000eprocessingTimeq\u0000~\u0000&sq\u0000~\u0000\n\u0001\u00e8\u0018\u00b7ppq\u0000~\u0000\u0010sq\u0000~\u0000\"t\u0000\frequestCountq\u0000~\u0000&sq\u0000~\u0000\n\u0002U\u00f8Yppq\u0000~\u0000\u0010sq\u0000~\u0000\"t\u0000\tbytesSentq\u0000~\u0000&sq\u0000~\u0000\n\u0002\u00d2\u0002\u0019ppq\u0000~\u0000(sq\u0000~\u0000\"t\u0000\u0007maxTimeq\u0000~\u0000&sq\u0000~\u0000\n\u0001D\u00dd\u0007ppq\u0000~\u0000\u0010sq\u0000~\u0000\"t\u0000\rbytesReceivedq\u0000~\u0000&sr\u0000\"com.sun.msv.grammar.ExpressionPool\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0002\u0000\u0001L\u0000\bexpTablet\u0000/Lcom/sun/msv/grammar/ExpressionPool$ClosedHash;xpsr\u0000-com.sun.msv.grammar.ExpressionPool$ClosedHash\u00d7j\u00d0N\u00ef\u00e8\u00ed\u001c\u0002\u0000\u0004I\u0000\u0005countI\u0000\tthresholdL\u0000\u0006parentq\u0000~\u0000<[\u0000\u0005tablet\u0000![Lcom/sun/msv/grammar/Expression;xp\u0000\u0000\u0000\u0005\u0000\u0000\u00009pur\u0000![Lcom.sun.msv.grammar.Expression;\u00d68D\u00c3]\u00ad\u00a7\n\u0002\u0000\u0000xp\u0000\u0000\u0000\u00bfppppppppppppq\u0000~\u0000\u0007ppppppppppppq\u0000~\u0000\u0005ppppppppppppppppppppppppppppq\u0000~\u0000\bpppppppppppppppq\u0000~\u0000\tppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppq\u0000~\u0000\u0006ppppppppppppppppppppppppppppp");

    private final static java.lang.Class PRIMARY_INTERFACE_CLASS() {
        return org.apache.jorphan.tomcat.manager.RequestInfo.class;
    }

    public long getBytesReceived() {
        return _BytesReceived;
    }

    public void setBytesReceived(long value) {
        _BytesReceived = value;
        has_BytesReceived = true;
    }

    public long getBytesSent() {
        return _BytesSent;
    }

    public void setBytesSent(long value) {
        _BytesSent = value;
        has_BytesSent = true;
    }

    public long getRequestCount() {
        return _RequestCount;
    }

    public void setRequestCount(long value) {
        _RequestCount = value;
        has_RequestCount = true;
    }

    public long getErrorCount() {
        return _ErrorCount;
    }

    public void setErrorCount(long value) {
        _ErrorCount = value;
        has_ErrorCount = true;
    }

    public int getMaxTime() {
        return _MaxTime;
    }

    public void setMaxTime(int value) {
        _MaxTime = value;
        has_MaxTime = true;
    }

    public int getProcessingTime() {
        return _ProcessingTime;
    }

    public void setProcessingTime(int value) {
        _ProcessingTime = value;
        has_ProcessingTime = true;
    }

    public com.sun.xml.bind.unmarshaller.ContentHandlerEx getUnmarshaller(com.sun.xml.bind.unmarshaller.UnmarshallingContext context) {
        return new org.apache.jorphan.tomcat.manager.impl.RequestInfoImpl.Unmarshaller(context);
    }

    public java.lang.Class getPrimaryInterfaceClass() {
        return PRIMARY_INTERFACE_CLASS();
    }

    public void serializeElements(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
    }

    public void serializeAttributes(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
        context.startAttribute("", "errorCount");
        try {
            context.text(javax.xml.bind.DatatypeConverter.printLong(((long) _ErrorCount)));
        } catch (java.lang.Exception e) {
            com.sun.xml.bind.marshaller.Util.handlePrintConversionException(this, e, context);
        }
        context.endAttribute();
        context.startAttribute("", "processingTime");
        try {
            context.text(javax.xml.bind.DatatypeConverter.printInt(((int) _ProcessingTime)));
        } catch (java.lang.Exception e) {
            com.sun.xml.bind.marshaller.Util.handlePrintConversionException(this, e, context);
        }
        context.endAttribute();
        context.startAttribute("", "requestCount");
        try {
            context.text(javax.xml.bind.DatatypeConverter.printLong(((long) _RequestCount)));
        } catch (java.lang.Exception e) {
            com.sun.xml.bind.marshaller.Util.handlePrintConversionException(this, e, context);
        }
        context.endAttribute();
        context.startAttribute("", "bytesSent");
        try {
            context.text(javax.xml.bind.DatatypeConverter.printLong(((long) _BytesSent)));
        } catch (java.lang.Exception e) {
            com.sun.xml.bind.marshaller.Util.handlePrintConversionException(this, e, context);
        }
        context.endAttribute();
        context.startAttribute("", "maxTime");
        try {
            context.text(javax.xml.bind.DatatypeConverter.printInt(((int) _MaxTime)));
        } catch (java.lang.Exception e) {
            com.sun.xml.bind.marshaller.Util.handlePrintConversionException(this, e, context);
        }
        context.endAttribute();
        context.startAttribute("", "bytesReceived");
        try {
            context.text(javax.xml.bind.DatatypeConverter.printLong(((long) _BytesReceived)));
        } catch (java.lang.Exception e) {
            com.sun.xml.bind.marshaller.Util.handlePrintConversionException(this, e, context);
        }
        context.endAttribute();
    }

    public void serializeAttributeBodies(com.sun.xml.bind.serializer.XMLSerializer context)
        throws org.xml.sax.SAXException
    {
    }

    public java.lang.Class getPrimaryInterface() {
        return (org.apache.jorphan.tomcat.manager.RequestInfo.class);
    }

    public com.sun.msv.verifier.DocumentDeclaration createRawValidator() {
        return new com.sun.msv.verifier.regexp.REDocumentDeclaration(schemaFragment);
    }

    public class Unmarshaller
        extends com.sun.xml.bind.unmarshaller.ContentHandlerEx
    {


        public Unmarshaller(com.sun.xml.bind.unmarshaller.UnmarshallingContext context) {
            super(context, "-------------");
        }

        protected com.sun.xml.bind.unmarshaller.UnmarshallableObject owner() {
            return org.apache.jorphan.tomcat.manager.impl.RequestInfoImpl.this;
        }

        public void enterElement(java.lang.String ___uri, java.lang.String ___local, org.xml.sax.Attributes __atts)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  0 :
                    revertToParentFromEnterElement(___uri, ___local, __atts);
                    return ;
            }
            super.enterElement(___uri, ___local, __atts);
        }

        public void leaveElement(java.lang.String ___uri, java.lang.String ___local)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  0 :
                    revertToParentFromLeaveElement(___uri, ___local);
                    return ;
            }
            super.leaveElement(___uri, ___local);
        }

        public void enterAttribute(java.lang.String ___uri, java.lang.String ___local)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  0 :
                    if (("" == ___uri)&&("processingTime" == ___local)) {
                        state = 3;
                        return ;
                    }
                    if (("" == ___uri)&&("bytesSent" == ___local)) {
                        state = 7;
                        return ;
                    }
                    if (("" == ___uri)&&("requestCount" == ___local)) {
                        state = 1;
                        return ;
                    }
                    if (("" == ___uri)&&("errorCount" == ___local)) {
                        state = 11;
                        return ;
                    }
                    if (("" == ___uri)&&("maxTime" == ___local)) {
                        state = 9;
                        return ;
                    }
                    if (("" == ___uri)&&("bytesReceived" == ___local)) {
                        state = 5;
                        return ;
                    }
                    revertToParentFromEnterAttribute(___uri, ___local);
                    return ;
            }
            super.enterAttribute(___uri, ___local);
        }

        public void leaveAttribute(java.lang.String ___uri, java.lang.String ___local)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            switch (state) {
                case  2 :
                    if (("" == ___uri)&&("requestCount" == ___local)) {
                        goto0();
                        return ;
                    }
                    break;
                case  8 :
                    if (("" == ___uri)&&("bytesSent" == ___local)) {
                        goto0();
                        return ;
                    }
                    break;
                case  4 :
                    if (("" == ___uri)&&("processingTime" == ___local)) {
                        goto0();
                        return ;
                    }
                    break;
                case  6 :
                    if (("" == ___uri)&&("bytesReceived" == ___local)) {
                        goto0();
                        return ;
                    }
                    break;
                case  12 :
                    if (("" == ___uri)&&("errorCount" == ___local)) {
                        goto0();
                        return ;
                    }
                    break;
                case  0 :
                    revertToParentFromLeaveAttribute(___uri, ___local);
                    return ;
                case  10 :
                    if (("" == ___uri)&&("maxTime" == ___local)) {
                        goto0();
                        return ;
                    }
                    break;
            }
            super.leaveAttribute(___uri, ___local);
        }

        public void text(java.lang.String value)
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            try {
                switch (state) {
                    case  9 :
                        try {
                            _MaxTime = javax.xml.bind.DatatypeConverter.parseInt(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value));
                            has_MaxTime = true;
                        } catch (java.lang.Exception e) {
                            handleParseConversionException(e);
                        }
                        state = 10;
                        return ;
                    case  7 :
                        try {
                            _BytesSent = javax.xml.bind.DatatypeConverter.parseLong(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value));
                            has_BytesSent = true;
                        } catch (java.lang.Exception e) {
                            handleParseConversionException(e);
                        }
                        state = 8;
                        return ;
                    case  3 :
                        try {
                            _ProcessingTime = javax.xml.bind.DatatypeConverter.parseInt(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value));
                            has_ProcessingTime = true;
                        } catch (java.lang.Exception e) {
                            handleParseConversionException(e);
                        }
                        state = 4;
                        return ;
                    case  11 :
                        try {
                            _ErrorCount = javax.xml.bind.DatatypeConverter.parseLong(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value));
                            has_ErrorCount = true;
                        } catch (java.lang.Exception e) {
                            handleParseConversionException(e);
                        }
                        state = 12;
                        return ;
                    case  5 :
                        try {
                            _BytesReceived = javax.xml.bind.DatatypeConverter.parseLong(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value));
                            has_BytesReceived = true;
                        } catch (java.lang.Exception e) {
                            handleParseConversionException(e);
                        }
                        state = 6;
                        return ;
                    case  1 :
                        try {
                            _RequestCount = javax.xml.bind.DatatypeConverter.parseLong(com.sun.xml.bind.WhiteSpaceProcessor.collapse(value));
                            has_RequestCount = true;
                        } catch (java.lang.Exception e) {
                            handleParseConversionException(e);
                        }
                        state = 2;
                        return ;
                    case  0 :
                        revertToParentFromText(value);
                        return ;
                }
            } catch (java.lang.RuntimeException e) {
                handleUnexpectedTextException(value, e);
            }
        }

        private void goto0()
            throws com.sun.xml.bind.unmarshaller.UnreportedException
        {
            int idx;
            state = 0;
            idx = context.getAttribute("", "errorCount");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
            idx = context.getAttribute("", "processingTime");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
            idx = context.getAttribute("", "requestCount");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
            idx = context.getAttribute("", "bytesSent");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
            idx = context.getAttribute("", "maxTime");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
            idx = context.getAttribute("", "bytesReceived");
            if (idx >= 0) {
                context.consumeAttribute(idx);
                return ;
            }
        }

    }

}
