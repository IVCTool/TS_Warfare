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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;

/**
 * Provides test case configuration from a JSON file in line with the IVCT
 * guidelines
 * 
 * @author QinetiQ
 */
public class TC_Warfare_TcParam implements IVCT_TcParam
{
    /**
     * The path to look for the FOMS where absolute paths were not specified and the
     * IVCT_TS_HOME environment variable was not configured or the FOM was not found
     * under that path.
     */
    private static String DEFAULT_BASE_PATH = "src/main/resources/";
    
    /**
     * The host of the HLA RTI
     */
    protected String rtiHost = "localhost";
    
    /**
     * The IVCT Federation name
     */
    protected String federationName = "IVCT";
    
    /**
     * The federate name of the test case
     */
    protected String tcFederateName = "TC_0001_Warfare";
    
    /**
     * The federate name of the SuT
     */
    protected String sutFederateName;
    
    /**
     * The federate type of the SuT
     */
    protected String sutFederateType;
    
    /**
     * The time in seconds for the SuT federate to join the federation
     */
    protected double sutFederateJoinTimeout;
    
    /**
     * The time in seconds for the SuT federate to resign from the federation
     */
    protected double sutFederateResignTimeout;
    
    /**
     * The test timeout in seconds
     */
    protected double testTimeout;
    
    /**
     * The sleep time in seconds
     */
    protected double sleepTime;
    
    /**
     * The urls of the FOM files to be used
     */
    protected URL[] urls;
    
    /**
     * Create a new instance of this class based on the following parameters.
     * 
     * @param tcParamJson
     *            The JSON file from which to read the values
     * @param logger
     *            The logger
     * @throws TcInconclusive
     *             If for any reason the initialisation fails
     */
    public TC_Warfare_TcParam(final String tcParamJson, final Logger logger) throws TcInconclusive
    {
        boolean errors = false;
        
        JSONParser jsonParser = new JSONParser();
        
        JSONObject jsonObject;
        try
        {
            jsonObject = (JSONObject) jsonParser.parse(tcParamJson);
        }
        catch (ParseException e)
        {
            throw new TcInconclusive("Unable to parse TcParam JSON string");
        }
        
        rtiHost = (String) jsonObject.get("rtiHost");
        federationName = (String) jsonObject.get("federationName");
        tcFederateName = (String) jsonObject.get("tcFederateName");
        sutFederateName = (String) jsonObject.get("sutFederateName");
        sutFederateType = (String) jsonObject.get("sutFederateType");
        sutFederateJoinTimeout = (Double) jsonObject.get("sutFederateJoinTimeout");
        sutFederateResignTimeout = (Double) jsonObject.get("sutFederateResignTimeout");
        
        testTimeout = (Double) jsonObject.get("testTimeout");
        sleepTime = (Double) jsonObject.get("sleepTime");
        
        // FOMS
        JSONArray fomsJSONArray = (JSONArray) jsonObject.get("urls");
        
        if (fomsJSONArray == null || fomsJSONArray.isEmpty())
        {
            logger.error("No urls were specified in the TcParam file");
            errors = true;
        }
        else
        {
            int size = fomsJSONArray.size();
            
            urls = new URL[size];
            for (int index = 0; index < size; index++)
            {
                String urlName = (String) fomsJSONArray.get(index);
                
                // is it a file?
                File file = new File(urlName);
                
                if (!file.exists())
                {
                    file = new File("", urlName);
                    
                    if (!file.exists())
                    {
                        file = new File("/", urlName);
                        
                        if (!file.exists())
                        {
                            file = new File("/resources", urlName);
                        }
                    }
                }
                
                if (file.exists())
                {
                    logger.info(file.getAbsolutePath() + " was specified in the TcParam file");
                    try
                    {
                        urls[index] = file.toURI().toURL();
                    }
                    catch (MalformedURLException e)
                    {
                        logger.error(
                                file.getAbsolutePath() + " was specified in the TcParam file but is not a valid URL");
                        errors = true;
                    }
                }
                else
                {
                    logger.error(String.join(" ", "URL", urlName,
                            "was specified in the TcParam file but cannot be resolved"));
                    errors = true;
                }
            }
            
        }
        
        if (errors)
        {
            String msg = "Unable to continute due to errors encoundered during TcParam configuration. Refer to log file";
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
        
    }
    
    @Override
    public String getFederationName()
    {
        
        return federationName;
    }
    
    @Override
    public String getSettingsDesignator()
    {
        
        return "crcAddress=" + rtiHost;
    }
    
    @Override
    public URL[] getUrls()
    {
        return Arrays.copyOf(urls, urls.length);
    }
    
    public String getDEFAULT_BASE_PATH()
    {
        return DEFAULT_BASE_PATH;
    }
    
    public String getRtiHost()
    {
        return rtiHost;
    }
    
    public String getTcFederateName()
    {
        return tcFederateName;
    }
    
    public String getSutFederateType()
    {
        return sutFederateType;
    }
    
    public String getSutFederateName()
    {
        return sutFederateName;
    }
    
    public double getSutFederateJoinTimeout()
    {
        return sutFederateJoinTimeout;
    }
    
    public double getSutFederateResignTimeout()
    {
        return sutFederateResignTimeout;
    }
    
    public double getSleepTime()
    {
        return sleepTime;
    }
    
    public double getTestTimeout()
    {
        return testTimeout;
    }
    
}
