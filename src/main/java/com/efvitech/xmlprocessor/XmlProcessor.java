package com.efvitech.xmlprocessor;

import com.efvitech.xmlprocessor.model.Transaction;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import java.io.File;
import java.util.List;

import static com.efvitech.xmlprocessor.parser.DomParser.parseWithDOM;
import static com.efvitech.xmlprocessor.parser.JaxbParser.parseWithJAXB;
import static com.efvitech.xmlprocessor.parser.SaxParser.parseWithSAX;

public class XmlProcessor {

    private static final String XML_FILE = XmlProcessor.class.getClassLoader().getResource("transactions.xml").getPath();
    private static final String XSD_FILE = XmlProcessor.class.getClassLoader().getResource("transactions.xsd").getPath();
    private static final int BATCH_SIZE = 2;

    public static void main(String[] args) throws Exception {
        System.out.println("Starting XML Processor...\n");

        Schema schema = loadSchema();

        System.out.println("DOM Parser:");
        measurePerformance(() -> parseWithDOM(XML_FILE, schema, BATCH_SIZE));

        System.out.println("JAXB Parser:");
        measurePerformance(() -> parseWithJAXB(XML_FILE, schema, BATCH_SIZE));

        System.out.println("SAX Parser:");
        measurePerformance(() -> parseWithSAX(XML_FILE, schema, BATCH_SIZE));
    }

    private static Schema loadSchema() throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return factory.newSchema(new File(XSD_FILE));
    }

    public static void processBatch(List<Transaction> batch) {
        System.out.println("Processing batch:");
        for (Transaction transaction : batch) {
            System.out.println(transaction != null ? transaction.toString() : "null");
        }
    }

    @FunctionalInterface
    interface CheckedRunnable {
        void run() throws Exception;
    }

    private static void measurePerformance(CheckedRunnable parserMethod) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long startTime = System.currentTimeMillis();
        long startMemory = runtime.totalMemory() - runtime.freeMemory();

        try {
            parserMethod.run();
        } catch (Exception e) {
            System.err.println("An error occurred during parsing: " + e.getMessage());
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long endMemory = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Time: " + (endTime - startTime) + " ms");
        System.out.println("Memory: " + (endMemory - startMemory) / 1024 + " KB\n");
    }
}