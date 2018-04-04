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
 * Encapsulation of the fields from the WorldLocationStruct fixed record type as
 * defined in the RPR- Base FOM. The location of an object in the world
 * coordinate system, as specified in IEEE Std 1278.1-1995 section 1.3.2
 * @author QinetiQ
 */
public class WorldLocationStruct
{
    /**
     * Distance from the origin along the X axis in metres.
     */
    private final double x;
    
    /**
     * Distance from the origin along the Y axis in metres.
     */
    private final double y;
    
    /**
     * Distance from the origin along the X axis in metres.
     */
    private final double z;
    
    /**
     * Constructs an immutable instance of this class.
     * @param x Distance from the origin along the X axis in metres.
     * @param y Distance from the origin along the Y axis in metres.
     * @param z Distance from the origin along the X axis in metres.
     */
    public WorldLocationStruct(final double x, final double y, final double z)
    {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * @return Distance from the origin along the X axis in metres.
     */
    public double getX()
    {
        return x;
    }
    
    /**
     * @return Distance from the origin along the Y axis in metres.
     */
    public double getY()
    {
        return y;
    }
    
    /**
     * @return Distance from the origin along the Z axis in metres.
     */
    public double getZ()
    {
        return z;
    }
    
    @Override
    public String toString()
    {
        return String.join(" ", "WorldLocationStruct[", "X=", String.valueOf(x), "Y=", String.valueOf(y), "X=",
                String.valueOf(z), "]");
    }
    
}
