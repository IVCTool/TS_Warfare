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
package com.qinetiq.msg134.etc.tc_lib_warfare;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import com.qinetiq.msg134.etc.tc_lib_warfare.decode.Decoder;

import de.fraunhofer.iosb.tc_lib.TcInconclusive;

/**
 * Provides custom configuration from a JSON file.
 * 
 * @author QinetiQ
 */
public class TC_Warfare_Config
{
    /**
     * Specifies whether or not to test that the SuT federate joins the federation
     * correctly
     */
    protected boolean testSutFederateJoin = true;
    
    /**
     * Specifies whether or not to test that the SuT federate resigns from the
     * federation correctly
     */
    protected boolean testSutFederateResign = false;
    
    /**
     * Specifies whether or not to test the WeaponFire interaction
     */
    protected boolean testWeaponFire;
    
    /**
     * Specifies whether or not to test the MunitionDetonation interaction
     */
    protected boolean testMunitionDetonation;
    
    /**
     * Specifies whether or not to test if the munition object pertaining to the
     * warfare interactions is correctly created and removed.
     */
    protected boolean testMunitionInstance;
    
    /**
     * Test that, for each MunitionDetonation interaction, there was a corresponding
     * earlier WeaponFire interaction
     */
    protected boolean testForMatchingPair = true;
    
    /**
     * Test that the SuT loaded the FOMs correctly
     */
    protected boolean testFOMs = true;
    
    /**
     * Parameters that are optional. Each parameter may consist of the interaction
     * name and the parameter name, separated but a dot, or just the parameter name,
     * in which case it applies to any interaction.
     */
    protected Set<String> optionalParams = new HashSet<>();
    
    /**
     * Parameters that may be empty. Each parameter may consist of the interaction
     * name and the parameter name, separated but a dot, or just the parameter name,
     * in which case it applies to any interaction.
     */
    protected Set<String> optionallyEmptyParams = new HashSet<>();
    
    /**
     * Custom parameter decoder classes that may be specified.
     */
    protected Map<String, Class<? extends Decoder<?>>> paramDecoders = new HashMap<>();
    
    /**
     * Create a new instance of this class based on the following parameters.
     * 
     * @param jsonFile
     *            The JSON file from which to read the values
     * @param logger
     *            The logger
     * @throws TcInconclusive
     *             If for any reason the initialisation fails
     */
    public TC_Warfare_Config(final String jsonFile, final Logger logger) throws TcInconclusive
    {
        String paramJson;
        try (Scanner scanner = new Scanner(getClass().getResourceAsStream(jsonFile)))
        {
            scanner.useDelimiter("\\Z");
            paramJson = scanner.next();
        }
        catch (Exception e)
        {
            throw new TcInconclusive(String.join(" ", "Error initialising from config file", jsonFile));
        }
        
        boolean errors = false;
        
        JSONParser jsonParser = new JSONParser();
        
        JSONObject jsonObject;
        try
        {
            jsonObject = (JSONObject) jsonParser.parse(paramJson);
        }
        catch (ParseException e)
        {
            throw new TcInconclusive("Unable to parse configuration JSON string");
        }
        
        testSutFederateJoin = (Boolean) jsonObject.get("testSutFederateJoin");
        testSutFederateResign = (Boolean) jsonObject.get("testSutFederateResign");
        testWeaponFire = (Boolean) jsonObject.get("testWeaponFire");
        testMunitionDetonation = (Boolean) jsonObject.get("testMunitionDetonation");
        testMunitionInstance = (Boolean) jsonObject.get("testMunitionInstance");
        testForMatchingPair = (Boolean) jsonObject.get("testForMatchingPair");
        testFOMs = (Boolean) jsonObject.get("testFOMs");
        
        JSONArray optionalParamsJSONArray = (JSONArray) jsonObject.get("optionalParams");
        
        if (optionalParamsJSONArray == null || optionalParamsJSONArray.isEmpty())
        {
            logger.info("No optionalParams were specified in the config file");
        }
        else
        {
            for (Object obj : optionalParamsJSONArray)
            {
                optionalParams.add((String) obj);
            }
        }
        
        JSONArray optionallyEmptyParamsJSONArray = (JSONArray) jsonObject.get("optionallyEmptyParams");
        
        if (optionallyEmptyParamsJSONArray == null || optionallyEmptyParamsJSONArray.isEmpty())
        {
            logger.info("No optionallyEmptyParams were specified in the config file");
        }
        else
        {
            for (Object obj : optionallyEmptyParamsJSONArray)
            {
                optionallyEmptyParams.add((String) obj);
            }
        }
        
        JSONObject paramDecodersJSONObject = (JSONObject) jsonObject.get("paramDecoders");
        
        if (paramDecodersJSONObject == null || paramDecodersJSONObject.isEmpty())
        {
            logger.info("No custom parameter decoders were specified in the config file");
        }
        else
        {
            for (Iterator<?> i = paramDecodersJSONObject.entrySet().iterator(); i.hasNext();)
            {
                Map.Entry<?, ?> e = (Entry<?, ?>) i.next();
                
                String param = (String) e.getKey();
                String decoderClassname = (String) e.getValue();
                
                try
                {
                    Class<?> clazz = Class.forName(decoderClassname);
                    if (Decoder.class.isAssignableFrom(clazz))
                    {
                        @SuppressWarnings("unchecked")
                        Class<? extends Decoder<?>> decoderClass = (Class<? extends Decoder<?>>) clazz;
                        paramDecoders.put(param, decoderClass);
                    }
                }
                catch (ClassNotFoundException ex)
                {
                    logger.error(String.join(" ", "Decoder class for parameter", param, "is", decoderClassname,
                            "which is not found on the classpath"));
                    errors = true;
                }
                
            }
            
        }
        
        if (errors)
        {
            throw new TcInconclusive(
                    "Unable to continue due to errors encountered during custom configuration. Refer to log file");
        }
        
    }
    
    /**
     * @return True if the FOMs should be tested
     */
    public boolean isTestFOMs()
    {
        return testFOMs;
    }
    
    /**
     * @return True if correct joining of the SuT federate to the federation should
     *         be tested
     */
    public boolean isTestSutFederateJoin()
    {
        return testSutFederateJoin;
    }
    
    /**
     * @return True if correct resigning of the SuT federate from the federation
     *         should be tested
     */
    public boolean isTestSutFederateResign()
    {
        return testSutFederateResign;
    }
    
    /**
     * @return True if the WeaponFire interaction should be tested
     */
    public boolean isTestWeaponFire()
    {
        return testWeaponFire;
    }
    
    /**
     * @return True if the MunitionDetonation interaction should be tested
     */
    public boolean isTestMunitionDetonation()
    {
        return testMunitionDetonation;
    }
    
    /**
     * @return True if MunitionDetonation interactions are to be tested for prior
     *         matching WeaponFire interactions
     */
    public boolean isTestForMatchingPair()
    {
        return testForMatchingPair;
    }
    
    /**
     * @return True if the munition object pertaining to the interactions should be
     *         tested
     */
    public boolean isTestMunitionInstance()
    {
        return testMunitionInstance;
    }
    
    /**
     * @return The optional parameters as an unmodifiable collection, if any
     */
    public Collection<String> getOptionalParams()
    {
        return Collections.unmodifiableCollection(optionalParams);
    }
    
    /**
     * @return The optionally empty parameters as an unmodifiable collection, if any
     */
    public Collection<String> getOptionallyEmptyParams()
    {
        return Collections.unmodifiableCollection(optionallyEmptyParams);
    }
    
    /**
     * @return The custom parameter decoders as an unmodifiable collection, if any,
     *         whereby the key is the parameter name and the value is the decoder
     *         class
     */
    public Map<String, Class<? extends Decoder<?>>> getParamDecoders()
    {
        return Collections.unmodifiableMap(paramDecoders);
    }
}
