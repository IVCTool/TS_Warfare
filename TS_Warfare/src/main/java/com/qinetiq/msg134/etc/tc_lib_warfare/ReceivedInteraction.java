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

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;

/**
 * Encapsulates the details of an HLA interaction that has been received from
 * the RTI.
 * 
 * @author QinetiQ
 */
public class ReceivedInteraction
{
    /**
     * The interaction class handle of the received interaction.
     */
    private final InteractionClassHandle interactionClass;
    
    /**
     * The parameters of the received interaction.
     */
    private final ParameterHandleValueMap parameters;
    
    /**
     * The logical time stamp provided with the received interaction. This is
     * provided as a raw type.
     */
    @SuppressWarnings("rawtypes")
    private final LogicalTime sentTime;
    
    /**
     * The time stamp that the interaction was received by this federate.
     */
    private final long receivedTime;
    
    /**
     * Constructs a new instance of this class given the following parameters.
     * 
     * @param interactionClass
     *            The interaction class handle of the received interaction.
     * @param parameters
     *            The parameters of the received interaction.
     * @param sentTime
     *            The logical time stamp provided with the received interaction.
     * @param receivedTime
     *            The time stamp that the interaction was received by this federate.
     */
    public ReceivedInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap parameters,
            final LogicalTime<?, ?> sentTime, final long receivedTime)
    {
        super();
        this.interactionClass = interactionClass;
        this.parameters = parameters;
        this.sentTime = sentTime;
        this.receivedTime = receivedTime;
    }
    
    /**
     * @return The interaction class handle of the received interaction.
     */
    public InteractionClassHandle getInteractionClass()
    {
        return interactionClass;
    }
    
    /**
     * @return The parameters of the received interaction.
     */
    public ParameterHandleValueMap getParameters()
    {
        return parameters;
    }
    
    /**
     * @return The logical time stamp provided with the received interaction.
     */
    public LogicalTime<?, ?> getSentTime()
    {
        return sentTime;
    }
    
    /**
     * @return The time stamp that the interaction was received by this federate.
     */
    public long getReceivedTime()
    {
        return receivedTime;
    }
    
    /**
     * Returns the bytes for the given parameter handle
     * 
     * @param handle
     *            The handle of the parameter whose data
     * @return The data for the specified parameter
     */
    public byte[] getEncodedBytes(final ParameterHandle handle)
    {
        return parameters.get(handle);
    }
    
}
