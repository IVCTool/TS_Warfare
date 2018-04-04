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
package com.qinetiq.msg134.etc.tc_lib_warfare.types;

/**
 * Encapsulation of the fields from the EventIdentifierStruct fixed
 * record type as defined in the RPR- Base FOM.
 * @author QinetiQ
 */
public class EventIdentifierStruct
{
    /**
     * The EventCount field.
     */
    private final short eventCount;
    
    /**
     * The IssuingObjectIdentifier field.
     */
    private final String issuingObjectIdentifier;
    
    /**
     * Constructs an immutable instance of this class.
     * @param eventCount The EventCount value.
     * @param issuingObjectIdentifier The IssuingObjectIdentifier value.
     */
    public EventIdentifierStruct(final short eventCount, final String issuingObjectIdentifier)
    {
        this.eventCount = eventCount;
        this.issuingObjectIdentifier = issuingObjectIdentifier;
    }
    
    /**
     * @return The EventCount value.
     */
    public short getEventCount()
    {
        return eventCount;
    }
    
    /**
     * @return The IssuingObjectIdentifier value.
     */
    public String getIssuingObjectIdentifier()
    {
        return issuingObjectIdentifier;
    }
    
    @Override
    public String toString()
    {
        
        return String.join("", "EventIdentifierStruct[", "IssuingObjectIdentifier=", issuingObjectIdentifier,
                " EventCount=", String.valueOf(eventCount), "]");
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + eventCount;
        result = prime * result + ((issuingObjectIdentifier == null) ? 0 : issuingObjectIdentifier.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventIdentifierStruct other = (EventIdentifierStruct) obj;
        if (eventCount != other.eventCount)
            return false;
        if (issuingObjectIdentifier == null)
        {
            if (other.issuingObjectIdentifier != null)
                return false;
        }
        else if (!issuingObjectIdentifier.equals(other.issuingObjectIdentifier))
            return false;
        return true;
    }
    
}
