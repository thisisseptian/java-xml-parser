package com.efvitech.xmlprocessor.parser;

import com.efvitech.xmlprocessor.XmlProcessor;
import com.efvitech.xmlprocessor.model.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DomParser {

    public static void parseWithDOM(String xmlFile, Schema schema, int batchSize) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setSchema(schema);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xmlFile));

        NodeList transactionNodes = document.getElementsByTagName("transaction");
        List<Transaction> batch = new ArrayList<>();

        for (int i = 0; i < transactionNodes.getLength(); i++) {
            Element element = (Element) transactionNodes.item(i);
            Transaction transaction = new Transaction();
            transaction.setId(Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()));
            transaction.setAmount(Double.parseDouble(element.getElementsByTagName("amount").item(0).getTextContent()));
            batch.add(transaction);

            if (batch.size() == batchSize || i == transactionNodes.getLength() - 1) {
                XmlProcessor.processBatch(batch);
                batch.clear();
            }
        }
    }

}
