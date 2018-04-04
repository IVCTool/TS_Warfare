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

import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * The base model for use in RPR_Warfare Test cases. As required by the IVCT
 * design pattern, this implements the IVCT_BaseModel. This also implements the
 * Federate Ambassador, configuring this federate's HLA interaction and object
 * subscriptions, and receiving and storing the data received from the RTI
 * accordingly.
 *
 * @author QinetiQ
 */
public class TC_Warfare_BaseModel extends IVCT_BaseModel
{
    /**
     * A reference to the RTI Ambassador, maintained here because the reference in
     * the superclass was declared private
     */
    private IVCT_RTIambassador rtiAmbassador;
    
    /**
     * Maintains the interaction classes to which this federate subscribes.
     */
    private Set<InteractionClassHandle> trackedInteractions = ConcurrentHashMap.newKeySet();
    
    /**
     * The queue of interactions received from the RTI
     */
    private Queue<ReceivedInteraction> receivedInteractionQueue = new ConcurrentLinkedQueue<>();
    
    /**
     * Maintains the object classes to which this federate subscribes along with the
     * attributes.
     */
    private Map<ObjectClassHandle, AttributeHandleSet> trackedObjectClasses = new ConcurrentHashMap<>();
    
    /**
     * The discovered objects, as received from the RTI, mapped against the object
     * instance handle. The attributes of each object instance will be updated on
     * reception of a reflectAttributeValues callback.
     */
    private Map<ObjectInstanceHandle, DiscoveredObject> discoveredObjects = new ConcurrentHashMap<>();
    
    /**
     * The logger. This is repeated here because there logger in the superclass
     * cannot be referenced herein because is is private. Although this therefore
     * hides the superclass's logger, it references the same instance.
     */
    private Logger logger;
    
    /**
     * @param logger
     *            reference to the logger
     * @param ivct_rti
     *            The IVCT RTIambassador
     * @param ivct_TcParam
     *            IVCT Test Case Parameters.
     */
    public TC_Warfare_BaseModel(final Logger logger, final IVCT_RTIambassador ivct_rti, final IVCT_TcParam ivct_TcParam)
    {
        super(ivct_rti, logger, ivct_TcParam);
        this.logger = logger;
        this.rtiAmbassador = ivct_rti;
    }
    
    /**
     * @return A reference to the queue of received interactions.
     */
    public Queue<ReceivedInteraction> getInteractionQueue()
    {
        return receivedInteractionQueue;
    }
    
    /**
     * Add an interaction class handle to the list of class that the federate
     * subscribes to.
     * 
     * @param ich
     *            The InteractionClassHandle of the subscribed interaction class
     */
    public void addTrackedInteractionClass(final InteractionClassHandle ich)
    {
        trackedInteractions.add(ich);
    }
    
    /**
     * Remove an interaction class handle from the list of class that the federate
     * no longer subscribes to.
     * 
     * @param ich
     *            The InteractionClassHandle of the unsubscribed interaction class
     */
    public void removeTrackedInteractionClass(final InteractionClassHandle ich)
    {
        trackedInteractions.remove(ich);
    }
    
    /**
     * Add an object class handle to the list of class that the federate longer
     * subscribes to.
     * 
     * @param och
     *            The ObjectClassHandle of the unsubscribed object class
     * @param fedAttributeHandleSet
     *            The attribute handle set of the tracked objects
     */
    public void addTrackedObjecClass(final ObjectClassHandle och, final AttributeHandleSet fedAttributeHandleSet)
    {
        trackedObjectClasses.put(och, fedAttributeHandleSet);
    }
    
    /**
     * @return The discovered objects, if any
     */
    public Set<DiscoveredObject> getDiscoveredObjects()
    {
        return new HashSet<>(discoveredObjects.values());
    }
    
    /**
     * Returns the discovered objects of a particular object class handle type
     * 
     * @param objectClass
     *            The class of the objects to be returned
     * @return The discovered objects pertaining to the give class handle, if any.
     */
    public Set<DiscoveredObject> getDiscoveredObjects(final ObjectClassHandle objectClass)
    {
        return discoveredObjects.values().stream().filter(obj -> obj.getObjectClass().equals(objectClass))
                .collect(Collectors.toSet());
    }
    
    /**
     * Request an attribute update from the RTI for all discovered objects of a
     * particular class
     * 
     * @param objectClass
     *            The object class handle of the discovered objects whose attributes
     *            are to be updated.
     * @throws TcInconclusive
     *             If problems were encountered
     */
    public void requestAttributeValueUpdate(final ObjectClassHandle objectClass) throws TcInconclusive
    {
        List<String> erroneousObjects = new ArrayList<>();
        
        discoveredObjects.values().stream().filter(obj -> obj.getObjectClass().equals(objectClass))
                .collect(Collectors.toList()).forEach(obj ->
                {
                    try
                    {
                        rtiAmbassador.requestAttributeValueUpdate(obj.getTheObject(),
                                trackedObjectClasses.get(obj.getObjectClass()), null);
                    }
                    catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress
                            | FederateNotExecutionMember | NotConnected | RTIinternalError e)
                    {
                        logger.error(
                                String.join(" ", "Exception requesting an attribute update for", obj.getObjectName()),
                                e);
                        erroneousObjects.add(obj.getObjectName());
                    }
                });
        
        if (!erroneousObjects.isEmpty())
        {
            erroneousObjects.add(0, "Problems encountered when requesting attribute updates for objects:");
            String msg = String.join(" ", erroneousObjects);
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
    }
    
    /**
     * Configure the necessary subscriptions to the given interaction,
     * 
     * @param interactionName
     *            The name of the interaction to which to subscribe.
     * @param paramNames
     *            The list of subscribed parameter names.
     * @return The interaction record containing the key interaction parameters/
     * @throws TcInconclusive
     *             if an error occurred
     */
    public InteractionRecord subscribeInteraction(final String interactionName, final List<String> paramNames)
            throws TcInconclusive
    {
        InteractionRecord interactionDetails;
        
        InteractionClassHandle interactionClassHandle;
        
        try
        {
            interactionClassHandle = rtiAmbassador.getInteractionClassHandle(interactionName);
            
            Map<String, ParameterHandle> paramMap = new HashMap<>();
            for (String paramName : paramNames)
            {
                ParameterHandle ph = rtiAmbassador.getParameterHandle(interactionClassHandle, paramName);
                paramMap.put(paramName, ph);
            }
            
            interactionDetails = new InteractionRecord(interactionName, interactionClassHandle, paramMap);
            
        }
        catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError
                | InvalidInteractionClassHandle ex1)
        {
            String msg = "Cannot get interaction class handle or parameter handle for " + interactionName;
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
        
        try
        {
            rtiAmbassador.subscribeInteractionClass(interactionClassHandle);
        }
        catch (Exception e)
        {
            String msg = "Cannot get interaction class handle or parameter handle for " + interactionName;
            logger.error(msg);
            throw new TcInconclusive(msg);
        }
        
        addTrackedInteractionClass(interactionClassHandle);
        
        return interactionDetails;
    }
    
    /**
     * Subscribe to an object class
     * 
     * @param className
     *            The class name of the subscribed object
     * @param attributeNames
     *            The names of the subscribed attributes
     * @param attributeHandles
     *            An empty list which this method will populate with the respective
     *            handles of the given attribute names. May be null.
     * @return The object class handle of the subscribed object
     * @throws TcInconclusive
     *             If an error occurred
     */
    public ObjectClassHandle subscribeObject(final String className, final List<String> attributeNames,
            final List<AttributeHandle> attributeHandles) throws TcInconclusive
    {
        ObjectClassHandle objectHandle;
        try
        {
            objectHandle = rtiAmbassador.getObjectClassHandle(className);
            
            AttributeHandleSet munitionAttributeHandleSet = rtiAmbassador.getAttributeHandleSetFactory().create();
            
            if (attributeHandles != null)
            {
                attributeHandles.clear();
            }
            
            for (String attributeName : attributeNames)
            {
                AttributeHandle attributeHandle = rtiAmbassador.getAttributeHandle(objectHandle, attributeName);
                munitionAttributeHandleSet.add(attributeHandle);
                
                if (attributeHandles != null)
                {
                    attributeHandles.add(attributeHandle);
                }
            }
            
            addTrackedObjecClass(objectHandle, munitionAttributeHandleSet);
            rtiAmbassador.subscribeObjectClassAttributes(objectHandle, munitionAttributeHandleSet);
        }
        catch (NameNotFound | FederateNotExecutionMember | NotConnected | RTIinternalError | InvalidObjectClassHandle
                | AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress e)
        {
            throw new TcInconclusive(
                    String.join(" ", "Cannot configure object class or attribute handle(s) for", className));
        }
        return objectHandle;
    }
    
    /**
     * Request an attribute update from the RTI for the given discovered object
     * 
     * @param theObject
     *            The discovered object whose attributes are to be updated.
     * @throws TcInconclusive
     *             If problems were encountered
     */
    public void requestAttributeValueUpdate(final DiscoveredObject theObject) throws TcInconclusive
    {
        try
        {
            rtiAmbassador.requestAttributeValueUpdate(theObject.getTheObject(),
                    trackedObjectClasses.get(theObject.getObjectClass()), null);
        }
        catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress
                | FederateNotExecutionMember | NotConnected | RTIinternalError e)
        {
            String msg = String.join(" ", "Exception requesting attribute update for", theObject.getObjectName());
            logger.error(msg, e);
            throw new TcInconclusive(msg);
        }
    }
    
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass,
            final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering,
            final TransportationTypeHandle theTransport, final SupplementalReceiveInfo receiveInfo)
    {
        this.doReceiveInteraction(interactionClass, theParameters, null);
    }
    
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass,
            final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering,
            final TransportationTypeHandle theTransport, @SuppressWarnings("rawtypes")
            final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError
    {
        this.doReceiveInteraction(interactionClass, theParameters, theTime);
    }
    
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass,
            final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering,
            final TransportationTypeHandle theTransport, @SuppressWarnings("rawtypes")
            final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle,
            final SupplementalReceiveInfo receiveInfo) throws FederateInternalError
    {
        this.doReceiveInteraction(interactionClass, theParameters, theTime);
    }
    
    @Override
    public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass,
            final String objectName)
    {
        if (trackedObjectClasses.containsKey(theObjectClass))
        {
            logger.debug(String.join(" ", "Discovered object", objectName, "Instance handle:", theObject.toString(),
                    "Class handle:", theObjectClass.toString()));
            discoveredObjects.computeIfAbsent(theObject,
                    k -> new DiscoveredObject(theObjectClass, theObject, objectName, null));
        }
        else
        {
            logger.warn(String.join(" ", "Object", objectName, "was 'discovered' but its class is not being tracked."));
        }
    }
    
    /**
     * Determines whether or not an object has been discovered
     * 
     * @param name
     *            The name of the object to check
     * @return True if an object with the name is present, otherwise false
     */
    public boolean isObjectInstanceDiscovered(final String name)
    {
        return discoveredObjects.values().stream().anyMatch(obj -> obj.getObjectName().equals(name));
    }
    
    /**
     * Determines whether or not an object of a given class has been discovered
     * 
     * @param objectClassHandle
     *            The class of the object to check
     * @return True if one or more objects of the class is present, otherwise false
     */
    public boolean isObjectClassDiscovered(final ObjectClassHandle objectClassHandle)
    {
        return discoveredObjects.values().stream().anyMatch(obj -> obj.getObjectClass().equals(objectClassHandle));
    }
    
    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
            SupplementalRemoveInfo removeInfo) throws FederateInternalError
    {
        discoveredObjects.remove(theObject);
    }
    
    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
            @SuppressWarnings("rawtypes")
            LogicalTime theTime, OrderType receivedOrdering, SupplementalRemoveInfo removeInfo)
            throws FederateInternalError
    {
        discoveredObjects.remove(theObject);
    }
    
    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
            @SuppressWarnings("rawtypes")
            LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle,
            SupplementalRemoveInfo removeInfo) throws FederateInternalError
    {
        discoveredObjects.remove(theObject);
    }
    
    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
            String objectName, FederateHandle producingFederate) throws FederateInternalError
    {
        discoverObjectInstance(theObject, theObjectClass, objectName);
    }
    
    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
            byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
            @SuppressWarnings("rawtypes")
            LogicalTime theTime, OrderType receivedOrdering, SupplementalReflectInfo reflectInfo)
            throws FederateInternalError
    {
        doReflectAttributeValues(theObject, theAttributes);
    }
    
    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
            byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
            @SuppressWarnings("rawtypes")
            LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle,
            SupplementalReflectInfo reflectInfo) throws FederateInternalError
    {
        doReflectAttributeValues(theObject, theAttributes);
    }
    
    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
            byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
            SupplementalReflectInfo reflectInfo)
    {
        doReflectAttributeValues(theObject, theAttributes);
    }
    
    /**
     * Called by the interface methods for receiving interactions.
     *
     * Adds the interaction details to a queue if the interaction is of the correct
     * type.
     * 
     * @param interactionClass
     * @param theParameters
     * @throws RTIinternalError
     * @throws NotConnected
     * @throws FederateNotExecutionMember
     * @throws InvalidInteractionClassHandle
     */
    private void doReceiveInteraction(final InteractionClassHandle interactionClass,
            final ParameterHandleValueMap theParameters, final LogicalTime<?, ?> theTime)
    {
        if (trackedInteractions.contains(interactionClass))
        {
            logger.debug(String.join(" ", "Received interaction", interactionClass.toString(), "with",
                    String.valueOf(theParameters.size()), "parameters. Queueing"));
            receivedInteractionQueue
                    .add(new ReceivedInteraction(interactionClass, theParameters, theTime, System.currentTimeMillis()));
        }
    }
    
    /**
     * Updates the attributes of the tracked object with the new values
     * 
     * @param theObject
     *            the object instance handle
     * @param theAttributes
     *            the map of attribute handle / value
     */
    private void doReflectAttributeValues(final ObjectInstanceHandle theObject,
            final AttributeHandleValueMap theAttributes)
    {
        DiscoveredObject object = discoveredObjects.get(theObject);
        
        if (object == null)
        {
            logger.warn(String.join(" ", "Object", theObject.toString(), "received but not tracked."));
        }
        else
        {
            logger.debug(String.join(" ", "Updating attribute values for", object.getObjectName(), "(",
                    String.valueOf(theAttributes.size()), "attributes)"));
            object.setTheAttributes(theAttributes);
        }
        
    }
    
}
