package de.htwberlin.f4.ai.ma.indoorroutefinder.node;

import org.junit.Test;

import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.FingerprintFactory;

import static org.junit.Assert.*;

/**
 * Created by Johann Winter
 */
public class NodeFactoryTest {

    /**
     * Test for the successful creation of a Node
     */
    @Test
    public void createInstance() throws Exception {

        String testNodeID = "TestNode";
        String testDescription = "TestDescription";
        Fingerprint testFingerprint = FingerprintFactory.createInstance("testWifi", null);
        String testCoordinates = "testCoordinates";
        String testPicturePath = "/test/test.jpg";
        String testAdditionalInfo = "-PLACEHOLDER-";

        Node input = new NodeImpl(testNodeID, testDescription, testFingerprint, testCoordinates, testPicturePath, testAdditionalInfo);
        Node output;

        output = NodeFactory.createInstance(testNodeID, testDescription, testFingerprint, testCoordinates, testPicturePath, testAdditionalInfo);

        assertEquals(input.getId(), output.getId());
        assertEquals(input.getDescription(), output.getDescription());
        assertEquals(input.getFingerprint(), output.getFingerprint());
        assertEquals(input.getCoordinates(), output.getCoordinates());
        assertEquals(input.getPicturePath(), output.getPicturePath());
        assertEquals(input.getAdditionalInfo(), output.getAdditionalInfo());

    }
}