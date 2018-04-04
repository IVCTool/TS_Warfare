/**
 * Copyright 2017, QinetiQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qinetiq.msg134.warfare;

import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.*;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinetiq.msg134.etc.tc_lib_warfare.DecoderGenerator;



/**
 * Default Template: de.fraunhofer.iosb.tc_lib
 *
 * @author rjjones3
 * @since 17/10/2016
 */
public class DecoderGeneratorTest {
    private static String basePath = "src/test/resources/";//"build/resources/main/";
    private static URL[] urls;
    private static DecoderGenerator decoder;
    private static EncoderFactory encoder;
    private static Logger logger =LoggerFactory.getLogger(DecoderGeneratorTest.class);
   
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    public void cleanUpStreams() {
        System.setOut(System.out);
        System.setErr(System.err);
    }

    /**
     *
     * @throws Exception
     */
    @org.junit.BeforeClass
    public static void setupDecoderAndRti() throws Exception{
        try{
            urls = new URL[]{
                    new File(basePath + "RPR-Warfare_v2.0.xml").toURI().toURL(),
                    new File(basePath + "RPR-Foundation_v2.0.xml").toURI().toURL(),
                    new File(basePath + "RPR-Enumerations_v2.0.xml").toURI().toURL(),
                    new File(basePath + "RPR-Base_v2.0.xml").toURI().toURL()
            };
        }catch(MalformedURLException mue){
            fail("Failed to prepare Decoder.");
        }

        try {
            final RtiFactory rtiFactory = RtiFactoryFactory.getRtiFactory();
            encoder = rtiFactory.getEncoderFactory();
        }
        catch (final Exception e) {
        }

        decoder = new DecoderGenerator(urls, encoder, logger);
    }

    /**
     * Attributes are appropriately built (by comparison to a manually built object)  according to the RPR 2.0 FOM definition.
     * @throws Exception
     */
//    @org.junit.Test
//    public void generateAttributeDecoderTest() throws Exception {
//        DataElement data = decoder.generateAttributeDecoder("EmbeddedSystem","RelativePosition");
//
//        HLAfixedRecord expected = encoder.createHLAfixedRecord();
//        HLAfloat32BE BodyXDistance = encoder.createHLAfloat32BE();
//        HLAfloat32BE BodyYDistance = encoder.createHLAfloat32BE();
//        HLAfloat32BE BodyZDistance = encoder.createHLAfloat32BE();
//
//        expected.add(BodyXDistance);
//        expected.add(BodyYDistance);
//        expected.add(BodyZDistance);
//
//        assertEquals(data,expected);
//    }

    /**
     * List of attributes under an interaction is appropriately built (by comparison to a manually built object)   according to the RPR 2.0 FOM definition.
     * @throws Exception
     */
    @org.junit.Test
    public void generateAttributeListTest() throws Exception {
        List<String> attributeList = decoder.generateAttributeList("EmbeddedSystem");
        List<String> expected = new ArrayList<>();
        expected.add("EntityIdentifier");
        expected.add("HostObjectIdentifier");
        expected.add("RelativePosition");

        assertEquals(attributeList, expected);
    }

    /**
     *Parameters are appropriately built (by comparison to a manually built object)  according to the RPR 2.0 FOM definition.
     * @throws Exception
     */
//    @org.junit.Test
//    public void generateParameterDecoderTest() throws Exception {
//        DataElement data = decoder.generateParameterDecoder("MunitionDetonation","DetonationLocation");
//
//        HLAfixedRecord expected = encoder.createHLAfixedRecord();
//        HLAfloat64BE X = encoder.createHLAfloat64BE();
//        HLAfloat64BE Y = encoder.createHLAfloat64BE();
//        HLAfloat64BE Z = encoder.createHLAfloat64BE();
//
//        expected.add(X);
//        expected.add(Y);
//        expected.add(Z);
//
//        assertEquals(data,expected);
//    }

    /**
     *  List of parameters under an object is appropriately built (by comparison to a manually built object)  according to the RPR 2.0 FOM definition.
     * @throws Exception
     */
    @org.junit.Test
    public void generateParameterListTest() throws Exception {
        List<String> attributeList = decoder.generateParameterList("MunitionDetonation");
        List<String> expected = new ArrayList<>();
        expected.add("ArticulatedPartData");
        expected.add("DetonationLocation");
        expected.add("DetonationResultCode");
        expected.add("EventIdentifier");
        expected.add("FiringObjectIdentifier");
        expected.add("FinalVelocityVector");
        expected.add("FuseType");
        expected.add("MunitionObjectIdentifier");
        expected.add("MunitionType");
        expected.add("QuantityFired");
        expected.add("RateOfFire");
        expected.add("RelativeDetonationLocation");
        expected.add("TargetObjectIdentifier");
        expected.add("WarheadType");

        assertEquals(attributeList, expected);
    }

    /**
     *  Error messages are provided for invalid input (interactions, objects, attributes or parameters that do not exist within the defined FOM files)
     * @throws Exception
     */
    @org.junit.Test
    public void generateAttributeDecoderInvalid_WrongObject() throws Exception {
        setUpStreams();
        decoder.generateAttributeDecoder("INVALID_OBJECT","EntityIdentifier");
        String output = outContent.toString().toLowerCase();
        String[] lines = output.split("\n");
        String finalOutput = lines[lines.length - 1];
        assertTrue(finalOutput.contains("debug") && finalOutput.contains("failed while searching for object"));
        cleanUpStreams();
    }

    /**
     *  Error messages are provided for invalid input (interactions, objects, attributes or parameters that do not exist within the defined FOM files)
     * @throws Exception
     */
    @org.junit.Test
    public void generateAttributeDecoderInvalid_WrongAttribute() throws Exception {
        setUpStreams();
        decoder.generateAttributeDecoder("EmbeddedSystem","INVALID_ATTRIBUTE");
        String output = outContent.toString().toLowerCase();
        String[] lines = output.split("\n");
        String finalOutput = lines[lines.length - 1];
        assertTrue(finalOutput.contains("debug") && finalOutput.contains("failed while searching for attribute"));
        cleanUpStreams();
    }

    /**
     * Error messages are provided for invalid input (interactions, objects, attributes or parameters that do not exist within the defined FOM files)
     * @throws Exception
     */
    @org.junit.Test
    public void generateParameterDecoderInvalid_WrongInteraction() throws Exception {
        setUpStreams();
        decoder.generateParameterDecoder("INVALID_INTERACTION","ArticulatedPartData");
        String output = outContent.toString().toLowerCase();
        String[] lines = output.split("\n");
        String finalOutput = lines[lines.length - 1];
        assertTrue(finalOutput.contains("debug") && finalOutput.contains("failed while searching for interaction"));
        cleanUpStreams();
    }

    /**
     * Error messages are provided for invalid input (interactions, objects, attributes or parameters that do not exist within the defined FOM files)
     * @throws Exception
     */
    @org.junit.Test
    public void generateParameterDecoderInvalid_WrongParameter() throws Exception {
        setUpStreams();
        decoder.generateParameterDecoder("MunitionDetonation","INVALID_PARAMETER");
        String output = outContent.toString().toLowerCase();
        String[] lines = output.split("\n");
        String finalOutput = lines[lines.length - 1];
        assertTrue(finalOutput.contains("debug") && finalOutput.contains("failed while searching for parameter"));
        cleanUpStreams();
    }

    /**
     *  Error messages are provided for invalid input (interactions, objects, attributes or parameters that do not exist within the defined FOM files)
     * @throws Exception
     */
    @org.junit.Test
    public void generateAttributeListInvalid() throws Exception {
        setUpStreams();
        decoder.generateAttributeList("INVALID_OBJECT");
        String output = outContent.toString().toLowerCase();
        String[] lines = output.split("\n");
        String finalOutput = lines[lines.length - 1];
        assertTrue(finalOutput.contains("debug") && finalOutput.contains("failed while searching for object"));
        cleanUpStreams();
    }

    /**
     * Error messages are provided for invalid input (interactions, objects, attributes or parameters that do not exist within the defined FOM files)
     * @throws Exception
     */
    @org.junit.Test
    public void generateParameterListInvalid() throws Exception {
        setUpStreams();
        decoder.generateParameterList("INVALID_INTERACTION");
        String output = outContent.toString().toLowerCase();
        String[] lines = output.split("\n");
        String finalOutput = lines[lines.length - 1];
        assertTrue(finalOutput.contains("debug") && finalOutput.contains("failed while searching for interaction"));
        cleanUpStreams();
    }
}
