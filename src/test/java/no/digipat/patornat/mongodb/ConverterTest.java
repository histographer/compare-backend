package no.digipat.patornat.mongodb;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Test;

import no.digipat.patornat.mongodb.dao.Converter;
import no.digipat.patornat.mongodb.models.image.BestImageChoice;
import no.digipat.patornat.mongodb.models.image.ImageChoice;

public class ConverterTest {

    @Test
    public void testDbDocumentToBestImageChoice_invalid() {
        final List<Document> documents = new ArrayList<>();
        // Null tests
        Document doc1 = null;
        documents.add(doc1);
        Document doc2 = new Document();
        documents.add(doc2);
        Document doc3 = new Document();
        Document nullIdImage = new Document();
        nullIdImage.put("comment", "fun comment");
        doc3.put("chosen", nullIdImage);
        doc3.put("other", nullIdImage);
        documents.add(doc3);
        // Class cast tests
        Document doc4 = new Document();
        Document choiceDocument = new Document();
        choiceDocument.put("id", 1);
        choiceDocument.put("comment", "a comment");
        doc4.put("chosen", "this is not a document");
        doc4.put("other", choiceDocument);
        documents.add(doc4);
        Document doc5 = new Document();
        doc5.put("other", "this is also not a document");
        doc5.put("chosen", choiceDocument);
        documents.add(doc5);
        Document doc6 = new Document();
        doc6.put("user", new Object());
        documents.add(doc6);
        Document doc7 = new Document();
        Document invalidChosenOrOther = new Document();
        invalidChosenOrOther.put("id", "this is not an int");
        invalidChosenOrOther.put("comment", "but this is a valid string");
        doc7.put("chosen", invalidChosenOrOther);
        doc7.put("other", invalidChosenOrOther);
        documents.add(doc7);
        Document doc8 = new Document();
        Document anotherInvalidChosenOrOther = new Document();
        anotherInvalidChosenOrOther.put("id", 1);
        anotherInvalidChosenOrOther.put("comment", new Object());
        documents.add(doc8);
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            try {
                Converter.dbDocumentToBestImageChoice(document);
                fail("Expected IllegalArgumentException for document " + (i + 1) + ", but got nothing");
            } catch (IllegalArgumentException e) {
                // Nothing to see here
            } catch (Exception e) {
                fail("Expected IllegalArgumentException for document " + (i + 1) + ", but got " + e.getClass().getName());
            }
        }
    }
    
    @Test
    public void testDbDocumentToBestImageChoice_valid() {
        Document bestImageChoiceDoc = new Document();
        Document chosenImageDoc = new Document();
        Document otherImageDoc = new Document();
        chosenImageDoc.put("id", 1);
        chosenImageDoc.put("comment", "this is a comment");
        otherImageDoc.put("id", 2);
        // Other image's comment is null
        bestImageChoiceDoc.put("chosen", chosenImageDoc);
        bestImageChoiceDoc.put("other", otherImageDoc);
        bestImageChoiceDoc.put("user", "cool_username");
        BestImageChoice choice = Converter.dbDocumentToBestImageChoice(bestImageChoiceDoc);
        assertEquals("cool_username", choice.getUser());
        ImageChoice chosen = choice.getChosen();
        assertEquals(1, chosen.getId());
        assertEquals("this is a comment", chosen.getComment());
        ImageChoice other = choice.getOther();
        assertEquals(2, other.getId());
        assertNull(other.getComment());
    }

}
