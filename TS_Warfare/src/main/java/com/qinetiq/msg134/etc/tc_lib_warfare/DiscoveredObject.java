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

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;

/**
 * Encapsulates the details of a discovered HLA object that has been received
 * from the RTI.
 * 
 * @author QinetiQ
 */
public class DiscoveredObject
{
    /**
     * The object name, as provided by the HLA RTI.
     */
    private final String objectName;
    
    /**
     * The object class handle, as provided by the HLA RTI.
     */
    private final ObjectClassHandle objectClass;
    
    /**
     * The object instance handle, as provided by the HLA RTI.
     */
    private final ObjectInstanceHandle theObject;
    
    /**
     * The AttributeHandleValueMap, as provided by the HLA RTI. This is typically
     * populated from a reflectAttributeValues federate ambassador callback.
     */
    private AttributeHandleValueMap theAttributes;
    
    /**
     * Creates an instance of this class given the following immutable values
     * 
     * @param objectClass
     *            The object class handle, as provided by the HLA RTI.
     * @param theObject
     *            The object instance handle, as provided by the HLA RTI.
     * @param objectName
     *            The object name, as provided by the HLA RTI.
     * @param theAttributes
     *            The attribute values provided for this object, which may be null.
     */
    public DiscoveredObject(final ObjectClassHandle objectClass, final ObjectInstanceHandle theObject,
            final String objectName, AttributeHandleValueMap theAttributes)
    {
        super();
        this.objectClass = objectClass;
        this.theObject = theObject;
        this.theAttributes = theAttributes;
        this.objectName = objectName;
    }
    
    /**
     * @return The object class handle
     */
    public ObjectClassHandle getObjectClass()
    {
        return objectClass;
    }
    
    /**
     * @return The object instance handle
     */
    public ObjectInstanceHandle getTheObject()
    {
        return theObject;
    }
    
    /**
     * @return The object name
     */
    public String getObjectName()
    {
        return objectName;
    }
    
    /**
     * @return The object instance attributes provided.
     */
    public AttributeHandleValueMap getTheAttributes()
    {
        return theAttributes;
    }
    
    /**
     * Sets / updates the value of the object instance attributes
     * 
     * @param theAttributes
     *            The attribute values which which to update this object
     */
    public void setTheAttributes(final AttributeHandleValueMap theAttributes)
    {
        this.theAttributes = theAttributes;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((theObject == null) ? 0 : theObject.hashCode());
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
        DiscoveredObject other = (DiscoveredObject) obj;
        if (theObject == null)
        {
            if (other.theObject != null)
                return false;
        }
        else if (!theObject.equals(other.theObject))
            return false;
        return true;
    }
    
}
