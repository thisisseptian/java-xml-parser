package com.efvitech.xmlprocessor.parser;

import com.efvitech.xmlprocessor.XmlProcessor;
import com.efvitech.xmlprocessor.model.Transaction;
import com.efvitech.xmlprocessor.model.TransactionList;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import java.io.File;
import java.util.List;

public class JaxbParser {

    public static void parseWithJAXB(String xmlFile, Schema schema, int batchSize) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Transaction.class, TransactionList.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);

        TransactionList transactionList = (TransactionList) unmarshaller.unmarshal(new File(xmlFile));
        List<Transaction> allTransactions = transactionList.getTransactionList();

        for (int i = 0; i < allTransactions.size(); i += batchSize) {
            int end = Math.min(i + batchSize, allTransactions.size());
            List<Transaction> batch = allTransactions.subList(i, end);
            XmlProcessor.processBatch(batch);
        }
    }

}
