package org.apache.tapestry5.javadoc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reads an XDOC file using SAX and streams its content (with some modifications) to
 * an output stream.
 */
public class XDocStreamer
{
    final File xdoc;

    final Writer writer;

    private static final Runnable NO_OP = new Runnable()
    {
        public void run()
        {
        }
    };

    private void write(String text)
    {
        try
        {
            writer.write(text);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private Runnable writeClose(final String elementName)
    {
        return new Runnable()
        {
            public void run()
            {
                write("</");
                write(elementName);
                write(">");
            }
        };
    }

    public XDocStreamer(File xdoc, Writer writer)
    {
        this.xdoc = xdoc;
        this.writer = writer;
    }

    enum ParserState
    {
        IGNORING, COPYING
    };

    class SaxHandler implements ContentHandler, LexicalHandler
    {
        final Stack<Runnable> endElementHandlers = CollectionFactory.newStack();

        ParserState state = ParserState.IGNORING;

        public void startDTD(String name, String publicId, String systemId) throws SAXException
        {
        }

        public void endDTD() throws SAXException
        {
        }

        public void startEntity(String name) throws SAXException
        {
        }

        public void endEntity(String name) throws SAXException
        {
        }

        public void startCDATA() throws SAXException
        {
            // TODO: Update state
        }

        public void endCDATA() throws SAXException
        {
            // TODO: Restore state
        }

        /** Does nothing; comments are always stripped out. */
        public void comment(char[] ch, int start, int length) throws SAXException
        {
        }

        public void setDocumentLocator(Locator locator)
        {
        }

        public void startDocument() throws SAXException
        {
        }

        public void endDocument() throws SAXException
        {
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException
        {
        }

        public void endPrefixMapping(String prefix) throws SAXException
        {
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
        {
            if (state == ParserState.IGNORING)
            {
                if (localName.equals("body"))
                {
                    state = ParserState.COPYING;
                }

                endElementHandlers.push(NO_OP);

                return;
            }

            if (localName.equals("section"))
            {
                writeSectionHeader(atts, "h3");
                return;
            }

            if (localName.equals("subsection"))
            {
                writeSectionHeader(atts, "h4");
                return;
            }

            // TODO: <source> element

            write("<");
            write(localName);

            for (int i = 0; i < atts.getLength(); i++)
            {
                if (i > 0)
                {
                    write(" ");
                }

                write(String.format("%s=\"%s\"", atts.getLocalName(i), atts.getValue(i)));
            }

            write(">");

            endElementHandlers.push(writeClose(localName));
        }

        private void writeSectionHeader(Attributes atts, String elementName)
        {
            String name = getAttribute(atts, "name");

            write(String.format("<%s>%s<%1$s>", elementName, name));

            endElementHandlers.push(NO_OP);
            return;
        }

        private String getAttribute(Attributes atts, String name)
        {
            for (int i = 0; i < atts.getLength(); i++)
            {
                if (atts.getLocalName(i).equals(name))
                    return atts.getValue(i);
            }

            throw new RuntimeException(String.format("No '%s' attribute present.", name));
        }

        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            endElementHandlers.pop().run();
        }

        public void characters(char[] ch, int start, int length) throws SAXException
        {
            if (state != ParserState.IGNORING)
            {
                try
                {
                    writer.write(ch, start, length);
                }
                catch (IOException ex)
                {
                    throw new SAXException(ex);
                }
            }
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
        {
        }

        public void processingInstruction(String target, String data) throws SAXException
        {
        }

        public void skippedEntity(String name) throws SAXException
        {
        }

    }

    /** Parse the file and write its transformed content to the Writer. */
    public void writeContent() throws SAXException
    {
        SaxHandler handler = new SaxHandler();

        XMLReader reader = XMLReaderFactory.createXMLReader();

        reader.setContentHandler(handler);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);

        try
        {
            InputStream is = new BufferedInputStream(new FileInputStream(xdoc));

            reader.parse(new InputSource(is));
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}