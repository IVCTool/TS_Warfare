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
package com.qinetiq.msg134.etc.tc_lib_warfare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qinetiq.msg134.etc.tc_lib_warfare.types.EventIdentifierStruct;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.encoding.DataElement;

/**
 * Encapsulates the details of an HLA interaction class, including its class
 * name and handle, and expected interaction parameters. It may be also used to
 * store the outcomes of interaction parameters tested.
 * 
 * @author QinetiQ
 */
public class InteractionRecord
{
    /**
     * The interaction class name.
     */
    protected final String interactionName;
    
    /**
     * The interaction class handle.
     */
    protected final InteractionClassHandle interactionClassHandle;
    
    /**
     * The parameters expected from this interaction type, where the key is the
     * parameter name and the value is the corresponding HLA parameter handle
     */
    protected final Map<String, ParameterHandle> paramMap;
    
    /**
     * Parameters that decode successfully for a particular interaction based upon
     * its EventIdentifier.
     */
    protected final Map<EventIdentifierStruct, Set<DataElement>> decoded = new HashMap<>();
    
    /**
     * Parameters that failed to decode for a particular interaction based upon its
     * EventIdentifier.
     */
    protected final Map<EventIdentifierStruct, Set<String>> failedParams = new HashMap<>();
    
    /**
     * Parameters that were expected but not sent for a particular interaction based
     * upon its EventIdentifier.
     */
    protected final Map<EventIdentifierStruct, Set<String>> paramsNotSent = new HashMap<>();
    
    /**
     * Parameters that were not sent but are deemed optional for particular
     * interaction based upon its EventIdentifier.
     */
    protected final Map<EventIdentifierStruct, Set<String>> optionalParamsNotSent = new HashMap<>();
    
    /**
     * Interaction time stamps (time received) pertaining to each event.
     */
    protected final Map<EventIdentifierStruct, Long> receivedInteractionTimes = new HashMap<>();
    
    /**
     * Crates a new instance of this interaction record with the following
     * parameters.
     * 
     * @param interactionName
     *            The class name of the interaction
     * @param interactionClassHandle
     *            The interaction class handle
     * @param paramMap
     *            Map of key parameter name, value of parameter handle
     */
    public InteractionRecord(final String interactionName, final InteractionClassHandle interactionClassHandle,
            Map<String, ParameterHandle> paramMap)
    {
        this.interactionName = interactionName;
        this.interactionClassHandle = interactionClassHandle;
        this.paramMap = Collections.unmodifiableMap(new HashMap<>(paramMap));
    }
    
    /**
     * @return The interaction class name.
     */
    public String getInteractionName()
    {
        return interactionName;
    }
    
    /**
     * @return The interaction class handle.
     */
    public InteractionClassHandle getInteractionClassHandle()
    {
        return interactionClassHandle;
    }
    
    /**
     * @return The unmodifiable map of expected parameters, where the key is the
     *         parameter name and the value is the HLA parameter handle. Any attempt
     *         to modify the returned map will result in an exception being thrown,
     */
    public Map<String, ParameterHandle> getExpectedParamMap()
    {
        return Collections.unmodifiableMap(paramMap);
    }
    
    /**
     * Returns the HLA parameter handle for a given name.
     * 
     * @param paramName
     *            the name of the parameter whose handle to return.
     * @return The handle for the required parameter name.
     */
    public ParameterHandle getParameterHandle(final String paramName)
    {
        return paramMap.get(paramName);
    }
    
    /**
     * Determines whether a parameter handle is expected for this interaction.
     * 
     * @param parameterHandle
     *            The handle
     * @return True if this handle is expected, otherwise false.
     */
    public boolean isValidParameter(final ParameterHandle parameterHandle)
    {
        return paramMap.containsValue(parameterHandle);
    }
    
    /**
     * Test whether a particular class handle pertains to the interaction class of
     * this record.
     * 
     * @param handle
     *            The interaction class handle to test
     * @return True if it pertains to this record, otherwise false.
     */
    public boolean isValidClassHandle(final InteractionClassHandle handle)
    {
        return interactionClassHandle.equals(handle);
    }
    
    /**
     * Add a successfully decoded parameter to this record
     * 
     * @param id
     *            The EventIdentifier of this interaction
     * @param decodedElement
     *            The decoded parameter.
     */
    public void addDecoded(final EventIdentifierStruct id, final DataElement decodedElement)
    {
        Set<DataElement> decodedElements = decoded.get(id);
        
        if (decodedElements == null)
        {
            decodedElements = new HashSet<>();
            decoded.put(id, decodedElements);
        }
        
        decodedElements.add(decodedElement);
    }
    
    /**
     * Add the name of a parameter that failed to decode to this record
     * 
     * @param id
     *            The EventIdentifier of this interaction
     * @param failedParam
     *            The name of parameter that failed to decode.
     */
    public void addFailedParam(final EventIdentifierStruct id, final String failedParam)
    {
        Set<String> failed = failedParams.get(id);
        
        if (failed == null)
        {
            failed = new HashSet<>();
            failedParams.put(id, failed);
        }
        
        failed.add(failedParam);
    }
    
    /**
     * Add the name of a parameter that was expected but not received to this record
     * 
     * @param id
     *            The EventIdentifier of this interaction
     * @param paramNotSent
     *            The name of parameter that was not sent.
     */
    public void addParamNotSent(final EventIdentifierStruct id, final String paramNotSent)
    {
        Set<String> notSent = paramsNotSent.get(id);
        
        if (notSent == null)
        {
            notSent = new HashSet<>();
            paramsNotSent.put(id, notSent);
        }
        
        notSent.add(paramNotSent);
    }
    
    /**
     * Add the name of an optional parameter that was not received to this record
     * 
     * @param id
     *            The EventIdentifier of this interaction
     * @param paramNotSent
     *            The name of parameter that was not sent.
     */
    public void addOptionalParamNotSent(final EventIdentifierStruct id, final String paramNotSent)
    {
        Set<String> notSent = optionalParamsNotSent.get(id);
        
        if (notSent == null)
        {
            notSent = new HashSet<>();
            optionalParamsNotSent.put(id, notSent);
        }
        
        notSent.add(paramNotSent);
    }
    
    /**
     * @return True if errors were found (missing or failed parameters), otherwise
     *         false.
     */
    public boolean isErroneous()
    {
        return !failedParams.isEmpty() || !paramsNotSent.isEmpty();
    }
    
    /**
     * Test whether a particular interaction based upon its EventIdentifier has been
     * processed.
     * 
     * @param eventIdentifier
     *            The eventIdentifier to check.
     * @return True if this interaction has been processed, otherwise false.
     */
    public boolean isEventPresent(final EventIdentifierStruct eventIdentifier)
    {
        return receivedInteractionTimes.containsKey(eventIdentifier);
    }
    
    /**
     * Test whether a particular interaction based upon the EventIdentifier has
     * issuingObjectIdentifier parameter been processed.
     * 
     * @param issuingObjectId
     *            The issuingObjectId to check.
     * @return True if this interaction has been processed, otherwise false.
     */
    public boolean isIssuingObjectIdPresent(final String issuingObjectId)
    {
        return receivedInteractionTimes.keySet().stream()
                .anyMatch((e) -> e.getIssuingObjectIdentifier().equals(issuingObjectId));
    }
    
    /**
     * Test if a prior event has been stored. For this to return true, there must
     * exist an event that matches the issuingObjectIdentifier of the parameter
     * whose eventCount and time stamp must be lower
     * 
     * @param event
     *            The event against which to test if there exists a prior event
     * @param time
     *            The time stamp against which to test if there exists a prior event
     * @return True of a prior matching event exists, otherwise false
     */
    public boolean isPriorEventPresent(final EventIdentifierStruct event, final long time)
    {
        String issuingId = event.getIssuingObjectIdentifier();
        return receivedInteractionTimes.entrySet().stream()
                .anyMatch((e) -> e.getKey().getIssuingObjectIdentifier().equals(issuingId)
                        && e.getKey().getEventCount() < event.getEventCount() && e.getValue() < time);
    }
    
    /**
     * Retrieves the parameters previously reported as having been decoded
     * successfully.
     * 
     * @param id
     *            EventIdentifier identifying the fire event whose parameters were
     *            deemed as successfully decoded.
     * 
     * @return A collection of the successfully decoded parameters, which may be
     *         empty.
     */
    public Collection<DataElement> getDecodedParams(final EventIdentifierStruct id)
    {
        Collection<DataElement> params;
        
        if (decoded.containsKey(id))
        {
            params = new ArrayList<>(decoded.get(id));
        }
        else
        {
            params = new ArrayList<>(0);
        }
        
        return params;
    }
    
    /**
     * Retrieves the parameters previously reported as having failed to decode.
     * 
     * @param id
     *            EventIdentifier identifying the fire event whose parameters were
     *            deemed to have failed.
     * 
     * @return A collection of the failed parameters, which may be empty.
     */
    public Collection<String> getFailedParams(final EventIdentifierStruct id)
    {
        Collection<String> params;
        
        if (failedParams.containsKey(id))
        {
            params = new ArrayList<>(failedParams.get(id));
        }
        else
        {
            params = new ArrayList<>(0);
        }
        
        return params;
    }
    
    /**
     * Retrieves the parameters previously reported as having not been sent by the
     * Sut.
     * 
     * @param id
     *            EventIdentifier identifying the fire event whose parameters were
     *            not sent.
     * 
     * @return A collection of the unsent parameters, which may be empty.
     */
    public Collection<String> getParamsNotSent(final EventIdentifierStruct id)
    {
        Collection<String> params;
        
        if (paramsNotSent.containsKey(id))
        {
            params = new ArrayList<>(paramsNotSent.get(id));
        }
        else
        {
            params = new ArrayList<>(0);
        }
        
        return params;
    }
    
    /**
     * Retrieves the optional parameters previously reported as having not been sent
     * by the Sut.
     * 
     * @param id
     *            EventIdentifier identifying the fire event whose parameters were
     *            not sent.
     * 
     * @return A collection of the unsent parameters, which may be empty.
     */
    public Collection<String> getOptionalParamsNotSent(final EventIdentifierStruct id)
    {
        Collection<String> params;
        
        if (optionalParamsNotSent.containsKey(id))
        {
            params = new ArrayList<>(optionalParamsNotSent.get(id));
        }
        else
        {
            params = new ArrayList<>(0);
        }
        
        return params;
    }
    
    /**
     * Called to record the time stamps of any interactions pertaining to the given
     * fire event
     * 
     * @param id
     *            The EventIdentifier identifying the fire event whose time stamp is
     *            to be recorded.
     * @param interactionTimestamp
     *            The time stamp provided
     */
    public void addEvent(final EventIdentifierStruct id, final long interactionTimestamp)
    {
        receivedInteractionTimes.put(id, interactionTimestamp);
    }
    
    /**
     * @return A list of event EventIdentifier whose details have been recorded.
     */
    public List<EventIdentifierStruct> getEvents()
    {
        return new ArrayList<>(receivedInteractionTimes.keySet());
    }
    
    /**
     * @return A list of event EventIdentifier whose details have been recorded.
     */
    public Map<EventIdentifierStruct, Long> getEventsAndTimes()
    {
        return Collections.unmodifiableMap(receivedInteractionTimes);
    }
    
}
