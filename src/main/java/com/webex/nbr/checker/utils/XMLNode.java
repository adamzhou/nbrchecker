package com.webex.nbr.checker.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simple version of XML Node works only for UTF-8
 *
 * @author linzhou
 */
public class XMLNode {
    public String name;
    public List<XMLNode> children;
    public List<String> content;
    public Map<String, String> attrs;
    public XMLNode(String name) {
        this.name = name;
        children = new LinkedList<XMLNode>();
        attrs = new HashMap<String, String>();
        content = new LinkedList<String>();
    }

    public XMLNode addChild(String name) {
        XMLNode r = new XMLNode(name);
        children.add(r);
        return r;
    }

    public boolean has(String... es) {
        return find(es) != null;
    }

    public XMLNode find(String... es) {
        XMLNode c = this;
        for (String e : es) {
            boolean matched = false;
            for (XMLNode x : c.children) {
                if (x.name.equals(e)) {
                    c = x;
                    matched = true;
                    continue;
                }
            }
            if (!matched) {
                return null;
            }
        }
        return c;
    }

    public String toText() {
        return new String(generate());
    }

    public byte[] generate() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            generate(os);
        } catch (XMLStreamException e) {
            // should never happen
        	System.out.println(e.getMessage());
        }
        return os.toByteArray();
    }

    private void generate(OutputStream os) throws XMLStreamException {
        XMLOutputFactory xof = XMLOutputFactory.newFactory();
        XMLStreamWriter xsw = xof.createXMLStreamWriter(os);
        xsw.writeStartDocument();
        generate(xsw, os, this);
        xsw.writeEndDocument();
        xsw.flush();
        xsw.close();
    }

    private static void generate(XMLStreamWriter xsw, OutputStream os, XMLNode x) throws XMLStreamException {
        xsw.writeStartElement(x.name);
        for (Map.Entry<String, String> e : x.attrs.entrySet()) {
            xsw.writeAttribute(e.getKey(), e.getValue());
        }
        assert x.content.isEmpty() || x.children.isEmpty();
        if (x.content.isEmpty() && !x.children.isEmpty()) {
            for (XMLNode x0 : x.children) {
                generate(xsw, os, x0);
            }
        } else {
            for (String c : x.content) {
                xsw.writeCharacters(c);
            }
        }
        xsw.writeEndElement();
    }

    public static XMLNode parse(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        SAXParser p = SAXParserFactory.newInstance().newSAXParser();
        final AtomicReference<XMLNode> first = new AtomicReference<XMLNode>();
        final AtomicReference<XMLNode> current = new AtomicReference<XMLNode>();
        final LinkedList<XMLNode> stack = new LinkedList<XMLNode>();
        p.parse(is, new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) {
                XMLNode x = new XMLNode(qName);
                for (int i = 0; i < atts.getLength(); i++) {
                    x.attrs.put(atts.getQName(i), atts.getValue(i));
//                    System.out.println("startElement.atts(localName=" + atts.getLocalName(i) + ", qName="
//                          + atts.getQName(i) + ", type=" + atts.getType(i)
//                          + ", uri=" + atts.getURI(i) + ", value=" + atts.getValue(i) + ")");
                }
                if (first.get() == null) {
                    first.set(x);
                }
                if (current.get() != null) {
                    stack.push(current.get());
                    current.get().children.add(x);
                }
                current.set(x);
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
//                System.out.println("endElement(uri=" + uri + ", localName="
//                + localName + ", qName=" + qName +")");
                current.set(stack.isEmpty() ? null : stack.pop());
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                boolean isBlank = true;
                for (int i = start; i < start + length; i++) {
                    if (ch[i] != ' ' && ch[i] != '\r' && ch[i] != '\n' && ch[i] != '\t') {
                        isBlank = false;
                        break;
                    }
                }
                if (isBlank) {
//                    System.out.println("characters(BLANK): " + new String(ch, start, length));
                    return;
                }
                String value = new String(ch, start, length);
//                System.out.println("characters(" + value + "), length=" + length);
                current.get().content.add(value);
            }
        });
        return first.get();
    }
}
