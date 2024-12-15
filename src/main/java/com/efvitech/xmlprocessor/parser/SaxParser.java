package com.efvitech.xmlprocessor.parser;

import com.efvitech.xmlprocessor.XmlProcessor;
import com.efvitech.xmlprocessor.model.Transaction;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaxParser {

    public static void parseWithSAX(String xmlFile, Schema schema, int batchSize) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setSchema(schema);

        SAXParser parser = factory.newSAXParser();
        parser.parse(new File(xmlFile), new DefaultHandler() {
            List<Transaction> batch = new ArrayList<>();
            Transaction currentTransaction;
            StringBuilder content = new StringBuilder();

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if ("transaction".equals(qName)) {
                    currentTransaction = new Transaction();
                }
                content.setLength(0);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (currentTransaction != null) {
                    switch (qName) {
                        case "id":
                            currentTransaction.setId(Integer.parseInt(content.toString()));
                            break;
                        case "amount":
                            currentTransaction.setAmount(Double.parseDouble(content.toString()));
                            break;
                        case "transaction":
                            batch.add(currentTransaction);
                            if (batch.size() == batchSize) {
                                XmlProcessor.processBatch(batch);
                                batch.clear();
                            }
                            break;
                    }
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                content.append(ch, start, length);
            }

            @Override
            public void endDocument() throws SAXException {
                if (!batch.isEmpty()) {
                    XmlProcessor.processBatch(batch);
                }
            }
        });
    }

}
