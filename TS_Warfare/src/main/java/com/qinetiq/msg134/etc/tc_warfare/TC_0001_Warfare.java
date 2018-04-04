/**
 * Copyright 2017, UK (QinetiQ)
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
package com.qinetiq.msg134.etc.tc_warfare;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qinetiq.msg134.etc.tc_lib_warfare.DecoderGenerator;
import com.qinetiq.msg134.etc.tc_lib_warfare.DiscoveredObject;
import com.qinetiq.msg134.etc.tc_lib_warfare.InteractionRecord;
import com.qinetiq.msg134.etc.tc_lib_warfare.ReceivedInteraction;
import com.qinetiq.msg134.etc.tc_lib_warfare.TC_Warfare_BaseModel;
import com.qinetiq.msg134.etc.tc_lib_warfare.TC_Warfare_Config;
import com.qinetiq.msg134.etc.tc_lib_warfare.TC_Warfare_TcParam;
import com.qinetiq.msg134.etc.tc_lib_warfare.decode.Decoder;
import com.qinetiq.msg134.etc.tc_lib_warfare.decode.EntityTypeStructDecoder;
import com.qinetiq.msg134.etc.tc_lib_warfare.decode.EventIdentifierStructDecoder;
import com.qinetiq.msg134.etc.tc_lib_warfare.decode.HLAVariableStringArrayDecoder;
import com.qinetiq.msg134.etc.tc_lib_warfare.decode.RTIobjectIdDecoder;
import com.qinetiq.msg134.etc.tc_lib_warfare.types.EventIdentifierStruct;
import com.qinetiq.msg134.etc.tc_lib_warfare.types.EntityTypeStruct;
import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidParameterHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Implements the Warfare test cases, namely WeaponFire and MunitionDetonation.
 * 
 * @author QinetiQ
 */
public class TC_0001_Warfare extends AbstractTestCase
{
    private static final String CONFIG_FILE = "/TC_0001_Warfare_Config.json";
    
    /**
     * The HLAFederate object, as declared in the MIM.
     */
    private static final String HLA_FEDERATE_OBJECT = "HLAmanager.HLAfederate";
    
    /**
     * The HLAfederateName attribute of the HLAFederate object, as declared in the
     * MIM.
     */
    private static final String HLA_FEDERATE_NAME = "HLAfederateName";
    
    /**
     * The HLAfederateType attribute of the HLAFederate object, as declared in the
     * MIM.
     */
    private static final String HLA_FEDERATE_TYPE = "HLAfederateType";
    
    /**
     * The HLAFOMmoduleDesignatorList attribute of the HLAFederate object, as
     * declared in the MIM.
     */
    private static final String HLA_FEDERATE_FOMS = "HLAFOMmoduleDesignatorList";
    
    /**
     * The Munition object class name.
     */
    private static final String MUNITION_OBJECT_NAME = "BaseEntity.PhysicalEntity.Munition";
    
    /**
     * The MunitionObjectIdentifier parameter name.
     */
    private static final String MUNITION_OBJECT_ID_PARAM = "MunitionObjectIdentifier";
    
    /**
     * The WeaponFire interaction, as declared in the FOM.
     */
    private static final String WEAPONFIRE = "WeaponFire";
    
    /**
     * The MunitionDetonation interaction, as declared in the FOM.
     */
    private static final String MUNITIONDETONATION = "MunitionDetonation";
    
    /**
     * The EventIdentifier parameter, as declared in the FOM for both WeaponFire and
     * MunitionDetonation interactions.
     */
    private static final String EVENT_ID_PARAM = "EventIdentifier";
    
    /**
     * The EntityTypeStruct parameter, as declared in the FOM for both WeaponFire
     * and MunitionDetonation interactions.
     */
    private static final String MUNITION_TYPE_PARAM = "MunitionType";
    
    /**
     * Reference to the TcParams configuration object which is loaded on startup.
     */
    private TC_Warfare_TcParam tcWarfareParam;
    
    /**
     * Reference to the custom configuration object which is loaded on startup.
     */
    private TC_Warfare_Config tcWarfareConfig;
    
    /**
     * The handle of the HLAmanager HLAfederate object
     */
    private ObjectClassHandle federateHandle;
    
    /**
     * The handle of the HLAfederateName of the HLAmanager HLAfederate object
     */
    private AttributeHandle federateNameNameHandle;
    
    /**
     * The handle of the HLAfederateType of the HLAmanager HLAfederate object
     */
    private AttributeHandle federateTypeHandle;
    
    /**
     * The handle of the HLAFOMmoduleDesignatorList of the HLAmanager HLAfederate
     * object
     */
    private AttributeHandle federateFOMSHandle;
    
    /**
     * The IVCT RTIambassador.
     */
    private IVCT_RTIambassador ivctRTI;
    
    /**
     * The IVCT Base Model, which is the federate ambassador for this test case
     * federate..
     */
    private TC_Warfare_BaseModel tcWarfareBaseModel;
    
    /**
     * The decoder generator
     */
    private DecoderGenerator decoderGenerator;
    
    /**
     * The interaction record associated with the WeaponFire interaction. This will
     * be set to null if the WeaponFire interaction is not tested, as defined in the
     * configuration.
     */
    protected InteractionRecord weaponFireRecord;
    
    /**
     * The interaction record associated with the MunitionDetonation interaction.
     * This will be set to null if the MunitionDetonation interaction is not tested,
     * as defined in the configuration.
     */
    protected InteractionRecord munitionDetonationRecord;
    
    /**
     * The custom decoders (if any) configured for specific interaction parameters.
     */
    private Map<String, Decoder<?>> decoders = new HashMap<>();
    
    /**
     * The interaction parameters (if any) configured as being optional.
     */
    private Set<String> optionalParameters = new HashSet<>();
    
    /**
     * The interaction parameters (if any) that may be empty.
     */
    private Set<String> optionallyEmptyParams = new HashSet<>();
    
    /**
     * Default constructor
     */
    public TC_0001_Warfare()
    {
        
    }
    
    /**
     * Perform pre-test initialisation and configuration checking.
     * 
     * @param logger
     *            The logger to use
     * @throws TcInconclusive
     *             if errors were encountered during initialisation
     */
    private void initialise(final Logger logger) throws TcInconclusive
    {
        tcWarfareConfig = new TC_Warfare_Config(CONFIG_FILE, logger);
        
        // Set up any configured custom decoders, throwing an exception if a decoder
        // could not be created.
        for (Map.Entry<String, Class<? extends Decoder<?>>> e : tcWarfareConfig.getParamDecoders().entrySet())
        {
            try
            {
                decoders.put(e.getKey(), e.getValue().newInstance());
            }
            catch (IllegalAccessException | InstantiationException ex)
            {
                String msg = "Error creating decoder for parameter" + e.getKey();
                logger.error(msg);
                throw new TcInconclusive(msg, ex);
            }
        }
        
        // Set up any configured optional parameters.
        optionalParameters.addAll(tcWarfareConfig.getOptionalParams());
        
        // Set up any optionally empty parameters
        optionallyEmptyParams.addAll(tcWarfareConfig.getOptionallyEmptyParams());
        
        // The event Id parameter is mandatory and may not be declared as optional
        if (optionalParameters.contains(EVENT_ID_PARAM))
        {
            String msg = "Interaction parameter EventIdentifier is always mandatory but has been declared in the configuration as optional.";
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
        
        // Perform some configuration validation. If it is specified that matching
        // WeaponFire and MunitionDetonation
        // interactions pairs should be verified, deem the test inconclusive if only one
        // interaction is configured to be tested.
        if (tcWarfareConfig.isTestForMatchingPair()
                && (!tcWarfareConfig.isTestWeaponFire() || !tcWarfareConfig.isTestMunitionDetonation()))
        {
            String msg = "Configuration parameter testForMatchingPair is set but only one of the warfare interactions is set to be tested.";
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
    }
    
    /**
     * Called by the IVCT framework to create the base model and establish the
     * interface to the HLA RTI
     * 
     * @param tcParamJson
     *            test case parameters
     * @param logger
     *            The logger to use
     * @return the verdict
     */
    @Override
    public IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive
    {
        // Create the configuration object from the JSON string provided
        logger.debug("Parsing TcParam");
        tcWarfareParam = new TC_Warfare_TcParam(tcParamJson, logger);
        
        // Create the HLA RTI ambassador
        ivctRTI = IVCT_RTI_Factory.getIVCT_RTI(logger);
        if (ivctRTI == null)
        {
            String msg = "Unable to create RTI ambassador";
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
        
        // Create the IVCT base model, which is the HLA federate ambassador
        try
        {
            tcWarfareBaseModel = new TC_Warfare_BaseModel(logger, ivctRTI, tcWarfareParam);
            decoderGenerator = new DecoderGenerator(tcWarfareParam.getUrls(), ivctRTI.getEncoderFactory(), logger);
        }
        catch (Exception ex)
        {
            String msg = "Error occurred during initialisation of the base model";
            logger.error(msg, ex);
            throw new TcInconclusive(msg, ex);
        }
        
        return tcWarfareBaseModel;
    }
    
    /**
     * Called by the IVCT framework in order to establish a connection with the RTI
     * to configure the necessary object and interaction subscriptions, and to
     * prepare any tests to be run.
     *
     *
     * @throws TcInconclusive
     *             if something fails in retrieving handles from the RTI.
     */
    @Override
    protected void preambleAction(final Logger logger) throws TcInconclusive
    {
        initialise(logger);
        
        boolean isTestSutFederateJoin = tcWarfareConfig.isTestSutFederateJoin();
        String sutFederateName = tcWarfareParam.getSutFederateName();
        
        if (isTestSutFederateJoin && sutFederateName == null || sutFederateName.trim().isEmpty())
        {
            String msg = "The parameter testSutFederateJoin is set to true but the sutFederateName is not present or not valid";
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
        
        logger.debug("Initiating RTI");
        tcWarfareBaseModel.initiateRti(tcWarfareParam.getTcFederateName(), tcWarfareBaseModel);
        
        // Configure the test to verify whether SuT joins the federation correctly.
        if (tcWarfareConfig.isTestSutFederateJoin())
        {
            logger.debug(String.join(" ", "Subscribing to object", HLA_FEDERATE_OBJECT));
            final List<String> listOfAttributeNames = Arrays.asList(HLA_FEDERATE_NAME, HLA_FEDERATE_TYPE,
                    HLA_FEDERATE_FOMS);
            List<AttributeHandle> handles = new ArrayList<>(3);
            federateHandle = tcWarfareBaseModel.subscribeObject(HLA_FEDERATE_OBJECT, listOfAttributeNames, handles);
            federateNameNameHandle = handles.get(0);
            federateTypeHandle = handles.get(1);
            federateFOMSHandle = handles.get(2);
        }
        
        // Configure the WeaponFire subscription.
        logger.debug(String.join(" ", "Subscribing to interaction", WEAPONFIRE));
        weaponFireRecord = tcWarfareBaseModel.subscribeInteraction(WEAPONFIRE,
                decoderGenerator.generateParameterList(WEAPONFIRE));
        
        // Configure the MunitionDetonation subscription.
        logger.debug(String.join(" ", "Subscribing to interaction", MUNITIONDETONATION));
        munitionDetonationRecord = tcWarfareBaseModel.subscribeInteraction(MUNITIONDETONATION,
                decoderGenerator.generateParameterList(MUNITIONDETONATION));
        
        // Configure the test to verify whether the munition object is correctly created
        // and removed
        if (tcWarfareConfig.isTestMunitionInstance())
        {
            // The subscribed attributes are not evaluated in this test case, but at least
            // one attribute needs to
            // be subscribed to receive an update from the object instance
            logger.debug(String.join(" ", "Subscribing to object", MUNITION_OBJECT_NAME));
            final List<String> listOfAttributeNames = decoderGenerator.generateAttributeList("Munition");
            tcWarfareBaseModel.subscribeObject(MUNITION_OBJECT_NAME, listOfAttributeNames, null);
        }
        
    }
    
    /**
     * Called by the IVCT framework to Perform the tests.
     *
     * @throws TcInconclusive
     *             if an expected object update interaction message was never
     *             received.
     * @throws TcFailed
     *             if an interaction message failed to decode properly.
     */
    @Override
    protected void performTest(final Logger logger) throws TcInconclusive, TcFailed
    {
        // Test that the SuT has joined the federation if configured to do so
        // If the test is deemed inconclusive, this will throw the relevant exception as
        // appropriate.
        if (tcWarfareConfig.isTestSutFederateJoin())
        {
            testSuTConnected(logger);
        }
        
        // Run the main warfare interaction tests
        // If the test fails or is deemed inconclusive, this will throw the relevant
        // exception as appropriate.
        if (tcWarfareConfig.isTestWeaponFire() || tcWarfareConfig.isTestMunitionDetonation())
        {
            testWarfareInteractions(logger);
        }
        
        // Test for missing or failed parameters. This method will throw the relevant
        // exception as appropriate.
        testReceivedParams(logger);
        
        // If configured, test for matching pairs of received WeaponFire and
        // MunitionDetonation interactions.
        // If the test fails or is deemed inconclusive this will throw the relevant
        // exception as appropriate.
        if (tcWarfareConfig.isTestForMatchingPair())
        {
            testMatchingPairs(logger);
        }
        
        if (tcWarfareConfig.isTestSutFederateResign())
        {
            testSuTResigned(logger);
        }
        
    }
    
    /**
     * Validate that the received interaction is expected and is testable.
     * 
     * @param receivedInteraction
     *            The interaction received.
     * @param logger
     *            The logger to use
     * @return True if the received interaction is valid, otherwise false.
     * @throws TcFailed
     *             If a required value is missing
     * @throws TcInconclusive
     */
    private boolean isValidInteractionType(final ReceivedInteraction receivedInteraction, final Logger logger)
            throws TcFailed, TcInconclusive
    {
        boolean valid;
        // Verify that the received interaction is from an expected interaction class
        // type
        InteractionRecord interactionRecord = determineInteractionRecord(receivedInteraction, logger);
        
        if (interactionRecord == null)
        {
            valid = false;
        }
        else
        {
            final String interactionName = interactionRecord.getInteractionName();
            
            // Check that the received interaction pertains to a munition
            EntityTypeStruct entityType = determineMunitionType(receivedInteraction, logger);
            
            if (entityType == null)
            {
                String msg = String.join(" ", interactionName, "Unable to establish value of parameter",
                        MUNITION_TYPE_PARAM);
                
                if (isOptionalParameter(interactionName, MUNITION_TYPE_PARAM))
                {
                    logger.warn(String.join(" ", msg, "but parameter is optional so continuing."));
                    valid = true;
                }
                else
                {
                    msg = String.join(" ", msg, ". This value is expected and is required to continue.");
                    logger.error(msg);
                    throw new TcFailed(msg);
                }
            }
            else
            {
                if (entityType.getEntityKind() == EntityTypeStruct.EntityKind.MUNITION)
                {
                    logger.info(String.join(" ", "Munition type is", entityType.getDomain().name()));
                    valid = true;
                }
                else
                {
                    logger.info(String.join(" ", "Interaction Entity Kind is ", entityType.getEntityKind().name(),
                            "which is not valid for this test."));
                    valid = false;
                }
            }
        }
        
        return valid;
    }
    
    /**
     * Determines whether or not this interaction is a WeaponFire
     * 
     * @param interaction
     *            The interaction to test
     * @param logger
     *            The logger to use
     * @return True if the interaction is a WeaponFire, otherwise false
     */
    private boolean isWeaponFireInteraction(final ReceivedInteraction interaction)
    {
        return weaponFireRecord.isValidClassHandle(interaction.getInteractionClass());
    }
    
    /**
     * Determines whether or not this interaction is a MunitionDetonation
     * 
     * @param interaction
     *            The interaction to test
     * @param logger
     *            The logger to use
     * @return True if the interaction is a MunitionDetonation, otherwise false
     */
    private boolean isMunitionDetonationInteraction(final ReceivedInteraction interaction)
    {
        return munitionDetonationRecord.isValidClassHandle(interaction.getInteractionClass());
    }
    
    /**
     * Perform a check on the received interactions to verify that they are present
     * and correct. Report any missing of failed parameters to the log and throw an
     * exception if the test failed.
     * 
     * @param logger
     *            The logger to use
     * @throws TcFailed
     *             If the test failed
     */
    private void testReceivedParams(final Logger logger) throws TcFailed
    {
        // Look at all interactions received and test that all parameters were present
        // and correct
        Arrays.asList(weaponFireRecord, munitionDetonationRecord).forEach(record ->
        {
            if (record != null)
            {
                record.getEvents().forEach(id ->
                {
                    logger.info(String.join(" ", record.getInteractionName(), "parameter analysis for EventIdentifier:",
                            id.toString()));
                    
                    // Log failed parameters
                    final StringJoiner failedString = new StringJoiner(",", "[", "]");
                    failedString.setEmptyValue("None");
                    
                    record.getFailedParams(id).forEach(failedParam ->
                    {
                        failedString.add(failedParam);
                    });
                    
                    logger.info(String.join(" ", "Failed params:", failedString.toString()));
                    
                    // Log parameters not sent
                    final StringJoiner notSentString = new StringJoiner(",", "[", "]");
                    notSentString.setEmptyValue("None");
                    
                    record.getParamsNotSent(id).forEach(param ->
                    {
                        notSentString.add(param);
                    });
                    
                    logger.info(String.join(" ", "Params not sent :", notSentString.toString()));
                    
                    // Log optional parameters not sent
                    final StringJoiner optionalNotSentString = new StringJoiner(",", "[", "]");
                    optionalNotSentString.setEmptyValue("None");
                    
                    record.getOptionalParamsNotSent(id).forEach(param ->
                    {
                        optionalNotSentString.add(param);
                    });
                    
                    logger.info(String.join(" ", "Optional Params not sent :", optionalNotSentString.toString()));
                });
            }
        });
        
        if (weaponFireRecord.isErroneous() || munitionDetonationRecord.isErroneous())
        {
            String msg = "Failed or missing parameters were encountered. Refer to the log file for details.";
            logger.error(msg);
            throw new TcFailed(msg);
        }
    }
    
    /**
     * Test that the SuT has connected and that the relevant attributes match those
     * defined in the configuration.
     * 
     * @param logger
     *            The logger to use
     * @throws TcInconclusive
     *             If the test is deemed inconclusive
     * @throws TcFailed
     *             If the test fails
     */
    private void testSuTConnected(final Logger logger) throws TcInconclusive, TcFailed
    {
        // Test that the SuT has joined the federation if configured to do so
        if (tcWarfareConfig.isTestSutFederateJoin())
        {
            boolean sutConnected = false;
            boolean correctFedNameType = false;
            boolean fomsLoadedCorrectly = false;
            
            final String expectedFedName = tcWarfareParam.getSutFederateName();
            final String expectedFedType = tcWarfareParam.getSutFederateType();
            
            // Wait for it to join until the timeout occurs
            // Test for Federate Name and Federate Type of the discovered HLAFederate Object
            // Instances
            
            int timeoutMillis = (int) (tcWarfareParam.getSutFederateJoinTimeout() * 1000);
            int sleepTimeMillis = (int) (tcWarfareParam.getSleepTime() * 1000);
            long startTime = System.currentTimeMillis(); // fetch starting time
            
            logger.info(String.join(" ", "Testing for SuT", expectedFedName, "to join the federation within",
                    String.valueOf(timeoutMillis), "ms"));
            
            // Test for correct value of Federate Name and Federate Type as defined in the
            // configuration
            HLAunicodeString stringCoder = ivctRTI.getEncoderFactory().createHLAunicodeString();
            
            WAIT_FOR_SUT_FEDERATE_LOOP:
            while (timeoutMillis < 0 || (System.currentTimeMillis() - startTime) < timeoutMillis)
            {
                try
                {
                    // Read the received object attributes
                    for (DiscoveredObject fedObject : tcWarfareBaseModel.getDiscoveredObjects(federateHandle))
                    {
                        AttributeHandleValueMap receivedAttributeHandleValueMap = fedObject.getTheAttributes();
                        
                        // If an object instance has not yet been received, request it and decode it on
                        // the next cycle
                        if (receivedAttributeHandleValueMap == null)
                        {
                            tcWarfareBaseModel.requestAttributeValueUpdate(fedObject);
                        }
                        else
                        {
                            stringCoder.decode(receivedAttributeHandleValueMap.get(federateNameNameHandle));
                            String fedName = stringCoder.getValue();
                            
                            sutConnected = fedName.equals(expectedFedName);
                            
                            if (sutConnected)
                            {
                                stringCoder.decode(receivedAttributeHandleValueMap.get(federateTypeHandle));
                                String fedType = stringCoder.getValue();
                                correctFedNameType = fedType.equals(expectedFedType);
                                HLAVariableStringArrayDecoder hlaVariableStringArrayDecoder = new HLAVariableStringArrayDecoder();
                                HLAvariableArray<HLAunicodeString> foms = hlaVariableStringArrayDecoder
                                        .decode(receivedAttributeHandleValueMap.get(federateFOMSHandle));
                                logger.info(String.join(" ", "SuT federate joined.", HLA_FEDERATE_NAME, "=", fedName,
                                        " ", HLA_FEDERATE_TYPE, "=", fedType));
                                
                                if (tcWarfareConfig.isTestFOMs())
                                {
                                    fomsLoadedCorrectly = testFOMs(foms, logger);
                                }
                                else
                                {
                                    fomsLoadedCorrectly = true;
                                }
                                
                                break WAIT_FOR_SUT_FEDERATE_LOOP;
                            }
                        }
                    }
                    
                    Thread.sleep(sleepTimeMillis);
                }
                catch (Exception e)
                {
                    String msg = "Exception thrown trying to ascertain the SuT federate details.";
                    logger.error(msg);
                    throw new TcInconclusive(msg, e);
                }
            }
            
            if (!sutConnected || !correctFedNameType || !fomsLoadedCorrectly)
            {
                StringJoiner sj = new StringJoiner(" ");
                if (!sutConnected)
                {
                    String msg = String.join(" ", "SuT federate", expectedFedName, "failed to join federation after",
                            String.valueOf(timeoutMillis), "ms.");
                    sj.add(msg);
                }
                else
                {
                    sj.add(String.join(" ", "SuT federate", expectedFedName, "joined the federation but"));
                    
                    if (!correctFedNameType)
                    {
                        sj.add("the federate type name was wrong");
                    }
                    
                    if (!fomsLoadedCorrectly)
                    {
                        if (!correctFedNameType)
                        {
                            sj.add("and");
                        }
                        sj.add("it did not load the expected FOMs");
                    }
                    
                }
                
                logger.error(sj.toString());
                throw new TcFailed(sj.toString());
            }
        }
    }
    
    /**
     * Tests that the expected FOMs were loaded by the SuT
     * 
     * @param foms
     *            The FOMs loaded by the SuT
     * @param logger
     *            The logger to use
     * @return True of the test passes, otherwise False
     */
    private boolean testFOMs(final HLAvariableArray<HLAunicodeString> foms, final Logger logger)
    {
        // Delimiter to represent path characters in order that the simple FOM file
        // name may be extracted from the returned value
        final String pathDelim = "[\\\\/]";
        
        logger.info("FOMS declared in the SuT are as follows:");
        
        Set<String> sutFOMNames = new HashSet<>();
        
        foms.forEach(fom ->
        {
            logger.info(fom.getValue());
            String path = fom.getValue();
            String[] pathElements = path.split(pathDelim);
            
            if (pathElements != null && pathElements.length > 0)
            {
                sutFOMNames.add(pathElements[pathElements.length - 1]);
            }
            
        });
        
        Set<String> missingFOMs = new HashSet<>();
        Arrays.asList(tcWarfareParam.getUrls()).forEach(url ->
        {
            String path = url.getPath();
            String[] pathElements = path.split(pathDelim);
            
            if (pathElements != null && pathElements.length > 0)
            {
                String name = pathElements[pathElements.length - 1];
                if (!sutFOMNames.contains(name))
                {
                    missingFOMs.add(name);
                }
            }
        });
        
        boolean testPassed;
        if (missingFOMs.isEmpty())
        {
            testPassed = true;
        }
        else
        {
            logger.error("The following FOM declarations were missing from the SuT:");
            missingFOMs.forEach(fom -> logger.error(fom));
            testPassed = false;
        }
        
        return testPassed;
    }
    
    /**
     * Tests that the SuT has resigned before the configured timeout
     * 
     * @param logger
     *            The logger to use
     * @throws TcFailed
     *             If the SuT is still connected after the timeout
     * @throws TcInconclusive
     *             If something goes wrong
     */
    private void testSuTResigned(final Logger logger) throws TcFailed, TcInconclusive
    {
        String sutFederateName = tcWarfareParam.getSutFederateName();
        int timeoutMillis = (int) (tcWarfareParam.getSutFederateResignTimeout() * 1000);
        int sleepTimeMillis = (int) (tcWarfareParam.getSleepTime() * 1000);
        long startTime = System.currentTimeMillis(); // fetch starting time
        
        logger.info(String.join(" ", "Testing for SuT", sutFederateName, "to resign from the federation within",
                String.valueOf(timeoutMillis), "ms ..."));
        
        boolean sutHasResigned = false;
        WAIT_FOR_SUT_FEDERATE_LOOP:
        while (timeoutMillis < 0 || (System.currentTimeMillis() - startTime) < timeoutMillis)
        {
            if (isFederateConnected(sutFederateName, logger))
            {
                try
                {
                    Thread.sleep(sleepTimeMillis);
                }
                catch (InterruptedException e)
                {
                    String msg = "Test interrupted whilst waiting for SuT to resign";
                    logger.error(msg);
                    throw new TcInconclusive(msg, e);
                }
            }
            else
            {
                sutHasResigned = true;
                break WAIT_FOR_SUT_FEDERATE_LOOP;
            }
        }
        
        if (!sutHasResigned)
        {
            String msg = String.join(" ", "SuT federate", sutFederateName, "had not resigned after",
                    String.valueOf(timeoutMillis), "ms");
            logger.error(msg);
            throw new TcFailed(msg);
        }
    }
    
    /**
     * Test the warfare interactions. This will wait until a configured timeout
     * occurs for relevant warfare interaction(s) to be received. If it is
     * configured that a single interaction, i.e. WeaponFire or MunitionDetonation
     * message (but not both) is to be tested, this will stop waiting when one has
     * been received. If it is configured that both interactions be tested, then
     * this will wait until both have been received. If it is configured that
     * matching pairs are tested, then it will wait for both interactions to be
     * received bearing the same EventIdentifier.
     * 
     * @param logger
     *            The logger to use
     * @throws TcFailed
     *             If the test fails
     * @throws TcInconclusive
     *             If something goes wrong
     */
    private void testWarfareInteractions(final Logger logger) throws TcFailed, TcInconclusive
    {
        boolean weaponFireReceived = false;
        boolean munitionDetonationReceived = false;
        /**
         * Keep trying as long as we haven't taken longer than the configured timeout
         */
        final boolean testWeaponFire = tcWarfareConfig.isTestWeaponFire();
        final boolean testMunitionDetonation = tcWarfareConfig.isTestMunitionDetonation();
        
        final String sutFederateName = tcWarfareParam.getSutFederateName();
        
        logWarfareTestStart(testWeaponFire, testMunitionDetonation, logger);
        
        Queue<ReceivedInteraction> interactionQueue = tcWarfareBaseModel.getInteractionQueue();
        
        int timeoutMillis = (int) (tcWarfareParam.getTestTimeout() * 1000);
        int sleepTimeMillis = (int) (tcWarfareParam.getSleepTime() * 1000);
        long startTime = System.currentTimeMillis(); // fetch starting time
        
        WARFARE_TEST_LOOP:
        while (timeoutMillis < 0 || (System.currentTimeMillis() - startTime) < timeoutMillis)
        {
            // Check that the SuT federate is still connected
            if (isFederateConnected(sutFederateName, logger))
            {
                if (!interactionQueue.isEmpty())
                {
                    // Get the interaction
                    final ReceivedInteraction received = interactionQueue.remove();
                    
                    boolean testThisInteraction = false;
                    
                    if (isWeaponFireInteraction(received))
                    {
                        testThisInteraction = testWeaponFire;
                        weaponFireReceived = true;
                    }
                    
                    if (isMunitionDetonationInteraction(received))
                    {
                        testThisInteraction = testMunitionDetonation;
                        munitionDetonationReceived = true;
                    }
                    
                    if (testThisInteraction)
                    {
                        // Verify that the received interaction is from an expected interaction class
                        // type
                        if (isValidInteractionType(received, logger))
                        {
                            logReceivedInteractionParams(received, logger);
                            logger.info("...processing...");
                            
                            // Decode the received interaction
                            EventIdentifierStruct eventIdentifier = decodeReceivedInteraction(received, logger);
                            
                            // Break if a IssuingObjectIdentifier was not determinable from the received
                            // interaction
                            if (eventIdentifier == null)
                            {
                                String msg = "EventIdentifier parameter of received interaction was invalid";
                                logger.error(msg);
                                throw new TcFailed(msg);
                            }
                            
                            // Test that the munition instance has been created or removed depending upon
                            // whether a WeaponFire or MunitionDetonation interaction has been received
                            if (tcWarfareConfig.isTestMunitionInstance())
                            {
                                testMunitionObject(received, logger);
                            }
                            
                            // Break if only WeaponFire or MunitionDetonation (but not both) are being
                            // tested
                            if (testWeaponFire != testMunitionDetonation)
                            {
                                break WARFARE_TEST_LOOP;
                            }
                            
                            // If testing for matching WeaponFire/MunitionDetonation pairs, break if
                            // both types of interaction has been received from the same entity as defined
                            // by the IssuingObjectIdentifier parameter. If not testing for matching pairs, just
                            // break when both types of interaction have been received from anywhere.
                            if (tcWarfareConfig.isTestForMatchingPair())
                            {
                                // Break if we have received both a WeaponFire or MunitionDetonation interaction
                                // with matching EventIdentifier IssuingObjectId values.
                                if (weaponFireRecord
                                        .isIssuingObjectIdPresent(eventIdentifier.getIssuingObjectIdentifier())
                                        && munitionDetonationRecord
                                                .isIssuingObjectIdPresent(eventIdentifier.getIssuingObjectIdentifier()))
                                {
                                    logger.info(String.join(" ",
                                            "Received WeaponFire and MunitionDetonation interactions with EventIdentifier.IssuingObjectIdentifier:",
                                            eventIdentifier.getIssuingObjectIdentifier()));
                                    break WARFARE_TEST_LOOP;
                                }
                            }
                            else
                            {
                                // Not testing for matching pairs, so break if we have received both a
                                // WeaponFire or MunitionDetonation interaction
                                // without necessarily having matching EventIdentifier values.
                                if (!weaponFireRecord.getEvents().isEmpty()
                                        && !munitionDetonationRecord.getEvents().isEmpty())
                                {
                                    logger.info("Received WeaponFire and MunitionDetonation interactions");
                                    break WARFARE_TEST_LOOP;
                                }
                            }
                        }
                        else
                        {
                            logger.info("Unexpected interaction received:");
                            logReceivedInteractionParams(received, logger);
                            logger.info("...ignoring...");
                        }
                    }
                }
                try
                {
                    Thread.sleep(sleepTimeMillis);
                }
                catch (InterruptedException e)
                {
                    logger.warn("Test interrupted", e);
                }
            }
            else
            {
                // SuT Federate isn't connected
                String msg = String.join(" ", "SuT federate", sutFederateName, "is not connected");
                logger.error(msg);
                throw new TcInconclusive(msg);
            }
        }
        
        // If there were any missing interactions, fail the test
        boolean weaponFireMissing = testWeaponFire && !weaponFireReceived;
        boolean munitionDetonationMissing = testMunitionDetonation && !munitionDetonationReceived;
        
        if (weaponFireMissing || munitionDetonationMissing)
        {
            StringJoiner msg = new StringJoiner(" ");
            
            msg.add("Did not receive expected interaction(s)");
            
            if (weaponFireMissing)
            {
                msg.add("WeaponFire");
            }
            if (munitionDetonationMissing)
            {
                msg.add("MunitionDetonation");
            }
            
            msg.add("after the timeout period of").add(String.valueOf(timeoutMillis)).add("ms");
            logger.error(msg.toString());
            throw new TcFailed(msg.toString());
        }
        
    }
    
    /**
     * Convenience method to log the start of the warfare test depending upon the
     * configuration.
     * 
     * @param testWeaponFire
     *            True if WeaponFire is being tested
     * @param testMunitionDetection
     *            True if MunitionDetection is being tested
     * @param logger
     *            The logger to use
     */
    private void logWarfareTestStart(final boolean testWeaponFire, final boolean testMunitionDetection,
            final Logger logger)
    {
        StringJoiner msg = new StringJoiner(" ");
        msg.add("Testing for");
        if (testWeaponFire)
        {
            msg.add("WeaponFire");
        }
        if (testWeaponFire && testMunitionDetection)
        {
            msg.add("and");
        }
        if (testMunitionDetection)
        {
            msg.add("MunitionDetonation");
        }
        msg.add("interactions. Waiting...");
        logger.info(msg.toString());
    }
    
    /**
     * Convenience method to log the details of the received interaction.
     * 
     * @param interaction
     *            The interaction to log
     * @param logger
     *            The logger to use
     */
    private void logReceivedInteractionParams(final ReceivedInteraction interaction, final Logger logger)
    {
        InteractionClassHandle handle = interaction.getInteractionClass();
        
        try
        {
            ParameterHandleValueMap params = interaction.getParameters();
            StringJoiner sj = new StringJoiner(" ", "[", "]");
            for (ParameterHandle param : params.keySet())
            {
                sj.add(ivctRTI.getParameterName(handle, param));
            }
            logger.info(String.join(" ", ivctRTI.getInteractionClassName(handle), sj.toString()));
            
        }
        catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError
                | InteractionParameterNotDefined | InvalidParameterHandle e)
        {
            logger.warn(String.join(" ", "Unable to determine class and/or param names for interaction handle",
                    handle.toString()));
        }
        
    }
    
    /**
     * Tests for matching WeaponFire / MunitionDetonation pairs. Report the results
     * to the logger, and throw the relevant failed or inconclusive exception as
     * appropriate if any anomalies are found. This tests that for each
     * MunitionDetonation interaction, there is an earlier WeaponFire interaction
     * with the same EventIdentifier.
     * 
     * @param logger
     *            The logger to use
     * @throws TcFailed
     *             If the test fails
     */
    private void testMatchingPairs(final Logger logger) throws TcFailed
    {
        // Check that, for any MunitionDetonation, a matching prior WeaponFire was received.
        // A match means that the WeaponFire was received before the MunitionDetonation, the
        // IssuingObjectIdentifier values match, and the EventCount value of the WeaponFire is lower.
        logger.info("Testing matching WeaponFire / MuntionDetonation pairs");
        
        if (munitionDetonationRecord.getEventsAndTimes().entrySet().stream()
                .allMatch((e) -> weaponFireRecord.isPriorEventPresent(e.getKey(), e.getValue())))
        {
            logger.info("Matching WeaponFire / MunitionDetonation pairs found");
        }
        else
        {
            String msg = "Testing matching pairs but could not find a MunitionDetection interaction with a prior matching WeaponFire.";
            logger.error(msg);
            throw new TcFailed(msg);
        }
    }
    
    /**
     * Test for the presence and removal of a munitions object instance associated
     * with the received interaction. If the received interaction is a WeaponFire,
     * then it is verified that a munition object instance, identified by the
     * MunitionObjectIdentifier parameter, was duly created in the environment. If
     * the received interaction is a MunitionDetonation, it is verified that the
     * object was duly deleted.
     * 
     * @param received
     *            The received interaction
     * @param logger
     *            The logger to use
     * @throws TcFailed
     *             If the test fails
     */
    private void testMunitionObject(final ReceivedInteraction received, final Logger logger) throws TcFailed
    {
        String munitionObjectIdentifier = determineMunitionObjectIdentifier(received, logger);
        
        if (munitionObjectIdentifier == null || munitionObjectIdentifier.trim().isEmpty())
        {
            String msg = "Text case is configured to test for the Munition object instance but the MunitionObjectIdentifier parameter of received interaction was invalid";
            logger.error(msg);
            throw new TcFailed(msg);
        }
        else
        {
            if (isWeaponFireInteraction(received))
            {
                if (tcWarfareBaseModel.isObjectInstanceDiscovered(munitionObjectIdentifier))
                {
                    logger.info(String.join(" ", "Munition object instance", munitionObjectIdentifier,
                            "pertaining to the WeaponFire interaction was discovered as expected"));
                }
                else
                {
                    String msg = String.join(" ", "Munition object instance", munitionObjectIdentifier,
                            "pertaining to the WeaponFire interaction was expected but never discovered");
                    logger.error(msg);
                    throw new TcFailed(msg);
                }
            }
            else if (isMunitionDetonationInteraction(received))
            {
                if (tcWarfareBaseModel.isObjectInstanceDiscovered(munitionObjectIdentifier))
                {
                    String msg = String.join(" ", "Munition object instance", munitionObjectIdentifier,
                            "should have been removed following the MunitionDetonation interaction");
                    logger.error(msg);
                    throw new TcFailed(msg);
                }
                else
                {
                    logger.info(String.join(" ", "Munition object instance", munitionObjectIdentifier,
                            "was removed following the MunitionDetonation interaction as expected"));
                }
            }
            else
            {
                // Do nothing - this shouldn't happen because of previous validation
            }
        }
    }
    
    /**
     * Takes a parameter handle value map, and iterates over it using the decoder
     * class to generate HLA objects which are used to decode values in the map.
     *
     * Every correctly decoded parameter (i.e. a parameter that decodes without
     * throwing a decoder error) is stored in the decoded list. Parameters that
     * don't decode successfully are stored in failedParams and parameters that
     * weren't sent are stored in a paramsNotSent list.
     *
     * @param receivedInteraction
     *            The received interaction to decode
     * @param logger
     *            The logger to use
     * @return The value of the EventIdentifier parameter of the recieved
     *         interaction
     * @throws TcFailed
     *             If the test failed
     * @throws TcInconclusive
     *             If the test was deemed inconclusive
     */
    protected EventIdentifierStruct decodeReceivedInteraction(final ReceivedInteraction receivedInteraction,
            final Logger logger) throws TcFailed, TcInconclusive
    {
        EventIdentifierStruct eventIdentifier;
        
        InteractionRecord interactionRecord = determineInteractionRecord(receivedInteraction, logger);
        
        if (interactionRecord == null)
        {
            // Unable to determine which interaction record to use (this should not happen
            // due to previous checks!).
            String msg = "Received interaction type could not be determined";
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
        else
        {
            final String interactionName = interactionRecord.getInteractionName();
            
            eventIdentifier = determineEventIdentifier(interactionName, receivedInteraction, logger);
            
            if (eventIdentifier == null)
            {
                String msg = String.join(" ", "eventIdentifier was not provided for this", interactionName);
                logger.error(msg);
                throw new TcInconclusive(msg);
            }
            
            interactionRecord.addEvent(eventIdentifier, receivedInteraction.getReceivedTime());
            
            // First, get the MunitionObjectId value
            interactionRecord.getExpectedParamMap().forEach((name, handle) ->
            {
                byte[] encoded = receivedInteraction.getParameters().get(handle);
                
                // Was this parameter sent?
                if (encoded == null)
                {
                    // The parameter was not sent, so record it accordingly
                    if (isOptionalParameter(interactionName, name))
                    {
                        logger.info(String.join(" ", interactionName, "Optional parameter not sent:", name));
                        interactionRecord.addOptionalParamNotSent(eventIdentifier, name);
                    }
                    else
                    {
                        logger.error(String.join(" ", interactionName, "Parameter not sent:", name));
                        interactionRecord.addParamNotSent(eventIdentifier, name);
                    }
                }
                else
                {
                    // The parameter was sent, so attempt to decode it
                    DataElement decoder = decoderGenerator.generateParameterDecoder(interactionName, name);
                    
                    // Could a valid decoder be found?
                    if (decoder == null)
                    {
                        // A decoder could not be found, so record this as a failed parameter
                        logger.error(String.join(" ", interactionName, "Cannot find decoder for param", name,
                                "Recording as a failed param."));
                        interactionRecord.addFailedParam(eventIdentifier, name);
                    }
                    else
                    {
                        // Attempt to decode the parameter
                        try
                        {
                            // If this is a parameter that requires a full decode
                            if (!decodeFully(interactionName, name, encoded, logger))
                            {
                                // Just make sure it decodes without throwing an exception
                                decoder.decode(encoded);
                                logger.info(String.join(" ", "Decoded",interactionName,"param", name, "Value=", decoder.toString()));
                            }
                            
                            // If this point is reached without an exception being thrown, record this as a
                            // successfully received parameter
                            logger.info(String.join(" ", interactionName, "Successful decode for param", name));
                            interactionRecord.addDecoded(eventIdentifier, decoder);
                        }
                        catch (final DecoderException de)
                        {
                            // If the parameter failed to decode because it was empty, check if empty
                            // parameters are allowed
                            if (encoded.length == 0 && isParameterValidWhenEmpty(interactionName, name))
                            {
                                logger.info(String.join(" ", interactionName, "param", name,
                                        "was empty but deemed acceptable"));
                                interactionRecord.addDecoded(eventIdentifier, decoder);
                            }
                            else
                            {
                                logger.error(String.join(" ", interactionName, "Parameter", name,
                                        "failed to decode. Reason:", de.getMessage()));
                                interactionRecord.addFailedParam(eventIdentifier, name);
                            }
                        }
                    }
                }
            });
            
        }
        return eventIdentifier;
    }
    
    /**
     * Determines whether or not a parameter is optional.
     * 
     * @param interactionName
     *            The interaction whose parameter name is to be tested.
     * @param paramName
     *            The parameter name to test.
     * @return True if the parameter is optional, otherwise false
     */
    private boolean isOptionalParameter(final String interactionName, final String paramName)
    {
        
        boolean optional;
        if (optionalParameters == null || optionalParameters.isEmpty())
        {
            optional = false;
        }
        else
        {
            // If the optional set contains just the parameter name, then it doesn't matter
            // which interaction it came from.
            optional = optionalParameters.contains(paramName)
                    || optionalParameters.contains(String.join("", interactionName, ".", paramName));
        }
        
        return optional;
    }
    
    /**
     * Determines whether or not a parameter is valid if it is empty.
     * 
     * @param interactionName
     *            The interaction whose parameter name is to be tested.
     * @param paramName
     *            The parameter name to test.
     * @return True if the parameter is valid when empty, otherwise false
     */
    private boolean isParameterValidWhenEmpty(final String interactionName, final String paramName)
    {
        boolean validIfEmpty;
        
        if (optionallyEmptyParams == null || optionallyEmptyParams.isEmpty())
        {
            validIfEmpty = false;
        }
        else
        {
            // If the optionallyEmptyParams set contains just the parameter name, then it
            // doesn't matter
            // which interaction it came from.
            validIfEmpty = optionallyEmptyParams.contains(paramName)
                    || optionallyEmptyParams.contains(String.join("", interactionName, ".", paramName));
        }
        
        return validIfEmpty;
    }
    
    /**
     * Determine the type of munition from the EntityType parameter of the received
     * interaction.
     * 
     * @param receivedInteraction
     *            The received interaction.
     * @param logger
     *            The logger to use
     * @return The value of the EntityType parameter which holds details about the
     *         munition type, or null if the parameter was not present.
     * @throws TcInconclusive
     *             If an exception occurred whilst trying to ascertain the value of
     *             MunitionType
     */
    private EntityTypeStruct determineMunitionType(final ReceivedInteraction receivedInteraction, final Logger logger)
            throws TcInconclusive
    {
        EntityTypeStruct entityType = null;
        
        InteractionClassHandle classHandle = receivedInteraction.getInteractionClass();
        
        String interactionClassName = getInteractionClassName(receivedInteraction.getInteractionClass(), logger);
        
        if (interactionClassName != null)
        {
            byte[] encoded;
            
            ParameterHandle paramHandle;
            try
            {
                paramHandle = ivctRTI.getParameterHandle(classHandle, MUNITION_TYPE_PARAM);
                
                encoded = receivedInteraction.getEncodedBytes(paramHandle);
                
                if (encoded != null)
                {
                    EntityTypeStructDecoder decoder = new EntityTypeStructDecoder();
                    entityType = decoder.decode(encoded);
                }
                
            }
            catch (NameNotFound | InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected
                    | RTIinternalError | DecoderException e)
            {
                String msg = String.join(" ", "Exception whilst trying to determine the value of parameter: ",
                        MUNITION_TYPE_PARAM);
                logger.error(msg);
                throw new TcInconclusive(msg, e);
            }
            
        }
        
        return entityType;
    }
    
    /**
     * Convenience method to determine which interaction record to update based upon
     * the interaction class of the received interaction.
     * 
     * @param receivedInteraction
     *            The received interaction.
     * @param logger
     *            The logger to use
     * @return The interaction record that was created earlier, or null.
     */
    private InteractionRecord determineInteractionRecord(final ReceivedInteraction receivedInteraction,
            final Logger logger)
    {
        InteractionRecord interactionRecord;
        
        if (isWeaponFireInteraction(receivedInteraction))
        {
            interactionRecord = weaponFireRecord;
        }
        else if (isMunitionDetonationInteraction(receivedInteraction))
        {
            interactionRecord = munitionDetonationRecord;
        }
        else
        {
            interactionRecord = null;
        }
        return interactionRecord;
    }
    
    /**
     * Decodes the received interaction in order to determine the
     * MunitionObjectIdentifier
     * 
     * @param receivedInteraction
     *            The interaction to decode
     * @param logger
     *            The logger to use
     * @return the value of MunitionObjectIdentifier, or null if the value could not
     *         be determined
     */
    private String determineMunitionObjectIdentifier(final ReceivedInteraction receivedInteraction, final Logger logger)
    {
        String munitionObjectIdentifier;
        InteractionClassHandle classHandle = receivedInteraction.getInteractionClass();
        
        String interactionClassName = getInteractionClassName(classHandle, logger);
        byte[] encoded;
        try
        {
            ParameterHandle paramHandle = ivctRTI.getParameterHandle(classHandle, MUNITION_OBJECT_ID_PARAM);
            encoded = receivedInteraction.getEncodedBytes(paramHandle);
        }
        catch (NameNotFound | InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected
                | RTIinternalError e)
        {
            logger.error(String.join(" ", interactionClassName, "error retrieving parameter handle",
                    MUNITION_OBJECT_ID_PARAM), e);
            encoded = null;
        }
        
        if (encoded == null)
        {
            logger.error(String.join(" ", interactionClassName, "does not contain a", MUNITION_OBJECT_ID_PARAM,
                    "parameter"));
            munitionObjectIdentifier = null;
        }
        else
        {
            RTIobjectIdDecoder rtiObjectIdDecoder = new RTIobjectIdDecoder();
            rtiObjectIdDecoder.decode(encoded);
            munitionObjectIdentifier = rtiObjectIdDecoder.getValue();
        }
        
        return munitionObjectIdentifier;
    }
    
    /**
     * Determine the value of the EventIdentifier parameter of the received
     * interaction.
     * 
     * @param interactionClassName
     *            The class name of the received interaction.
     * @param receivedInteraction
     *            The received interaction.
     * @param logger
     *            The logger to use
     * @return The value of the EventIdentifier parameter, or null if one wasn't
     *         provided or is invalid.
     */
    private EventIdentifierStruct determineEventIdentifier(final String interactionClassName,
            final ReceivedInteraction receivedInteraction, final Logger logger)
    {
        EventIdentifierStruct eventIdentifier;
        InteractionClassHandle classHandle = receivedInteraction.getInteractionClass();
        
        byte[] encoded;
        try
        {
            ParameterHandle paramHandle = ivctRTI.getParameterHandle(classHandle, EVENT_ID_PARAM);
            encoded = receivedInteraction.getEncodedBytes(paramHandle);
        }
        catch (NameNotFound | InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected
                | RTIinternalError e)
        {
            logger.error(String.join(" ", interactionClassName, "error retrieving parameter handle"), e);
            encoded = null;
        }
        
        if (encoded == null)
        {
            logger.error(String.join(" ", interactionClassName, "does not contain a", EVENT_ID_PARAM, "parameter"));
            eventIdentifier = null;
        }
        else
        {
            try
            {
                EventIdentifierStructDecoder decoder = new EventIdentifierStructDecoder();
                eventIdentifier = decoder.decode(encoded);
            }
            catch (Exception e)
            {
                logger.error(String.join(" ", interactionClassName, "contains the", EVENT_ID_PARAM,
                        "parameter but it could not be decoded"), e);
                eventIdentifier = null;
            }
        }
        
        return eventIdentifier;
    }
    
    /**
     * Called by the framework after the test has been performed. Closes the
     * connection to the RTI.
     * 
     * @param logger
     *            The logger to use
     * @throws TcInconclusive
     *             if terminating the connection fails.
     */
    @Override
    protected void postambleAction(final Logger logger) throws TcInconclusive
    {
        // Terminate rti
        tcWarfareBaseModel.terminateRti();
    }
    
    /**
     * Convenience method to return the interaction class name for the given handle
     * 
     * @param classHandle
     *            The interaction class handle of the class whose name should be
     *            returned
     * @param logger
     *            The logger to use
     * @return The class name for the provided interaction class handle
     */
    private String getInteractionClassName(final InteractionClassHandle classHandle, final Logger logger)
    {
        String className;
        try
        {
            className = ivctRTI.getInteractionClassName(classHandle);
        }
        catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e)
        {
            logger.warn(String.join(" ", "Unable to determine class name for interactionClassHandle",
                    classHandle.toString()));
            className = null;
        }
        return className;
    }
    
    /**
     * Logs the test purpose to the logger provided.
     */
    @Override
    protected void logTestPurpose(final Logger logger)
    {
        logger.info("\n------------------------------------------------------------------------\n");
        logger.info("TEST PURPOSE\n");
        logger.info("Tests that WeaponFire and/or MuntionDetonation interactions are received\n");
        logger.info("and that all parameters are present and correct.\n");
        logger.info("If configured to do so, it is also verified that the interactions from a \n");
        logger.info("particular entity, as determined by the EventIdentifier parameter\n");
        logger.info("element IssuingObjectIdentifier received in the correct order, i.e. \n");
        logger.info("WeaponFire before MunitionDetonation. \n");
        logger.info("--------------------------------------------------------------------------\n");
    }
    
    /**
     * Entry point to run the test case as a standalone application.
     * 
     * @param args
     *            The full path of the JSON configuration file.
     */
    public static void main(final String[] args)
    {
        Logger logger = LoggerFactory.getLogger(TC_0001_Warfare.class);
        
        if (args.length >= 1)
        {
            File file = new File(args[0]);
            try (Scanner scanner = new Scanner(file))
            {
                scanner.useDelimiter("\\Z");
                String paramJson = scanner.next();
                TC_0001_Warfare warfareTC = new TC_0001_Warfare();
                warfareTC.execute(paramJson, logger);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.err.append("TC_0001_Warfare. Expecting JSON filename");
        }
    }
    
    /**
     * Decodes the given parameter using the custom decoder configured for that
     * particular parameter name. If a custom decoder has been configured for the
     * parameter name, it is decoded and printed to the logger. If the parameter
     * fails to decoder, a DecoderException is thrown. If a custom decoder has not
     * been configured for this parameter, this method returns false.
     * 
     * @param interactionName
     *            The name of the interaction.
     * @param paramName
     *            The parameter to decode.
     * @param bytes
     *            The parameter binary data.
     * @param logger
     *            The logger to which to print the decoded value.
     * @return True if a a custom decoder has been configured for the parameter
     *         name, otherwise false.
     * @throws DecoderException
     *             If the data failed to decode
     */
    private boolean decodeFully(final String interactionName, final String paramName, final byte[] bytes,
            final Logger logger) throws DecoderException
    {
        Decoder<?> decoder = decoders.get(String.join("", interactionName, ".", paramName));
        
        if (decoder == null)
        {
            decoder = decoders.get(paramName);
        }
        
        if (decoder != null)
        {
            // In the absence of specific validation criteria, just Just decode the value
            // and print the toString to the log.
            // If the decode fails, a DecoderException is thrown at this point which is
            // propagated to the caller.
            logger.info(String.join(" ", "Decoded", interactionName,"param",paramName, "Value=", decoder.decode(bytes).toString()));
        }
        
        return decoder != null;
    }
    
    /**
     * Determines whether or not a federate with the name defined by sutFederateName
     * is connected to the federation
     * 
     * @param sutFederateName
     *            The name of the federate whose connection status is to be
     *            determined
     * @param logger
     *            The logger to use
     * @return True if the federate of the given name is connected, otherwise false
     * @throws TcInconclusive
     *             If something went wrong during the test
     */
    private boolean isFederateConnected(final String sutFederateName, final Logger logger) throws TcInconclusive
    {
        boolean sutConnected;
        
        // Check if the SuT is already connected
        try
        {
            // If this does not throw an exception, it means that the SuT is present in the
            // federation
            ivctRTI.getFederateHandle(sutFederateName);
            sutConnected = true;
        }
        catch (NameNotFound e)
        {
            // If a NameNotFound or NotConnected exception is thrown it might just mean that
            // the SuT has not yet joined the federation
            logger.info("Unknown federate exception ignored");
            sutConnected = false;
        }
        catch (FederateNotExecutionMember | RTIinternalError | NotConnected e2)
        {
            // If any of these exceptions are thrown, there has been an error, so render the
            // test inconclusive
            String msg = String.join(" ", "Error occurred whilst trying to ascertain if SuT federate", sutFederateName,
                    "is connected to federation");
            logger.error(msg);
            throw new TcInconclusive(msg, e2);
        }
        return sutConnected;
    }
    
}
