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
 * Encapsulation of the fields from the VelocityVectorStruct fixed record type
 * as defined in the RPR- Base FOM.
 * 
 * @author QinetiQ
 */
public class VelocityVectorStruct
{
    /**
     * Velocity component along the X axis
     */
    private final float xVelocity;
    
    /**
     * Velocity component along the Y axis
     */
    private final float yVelocity;
    
    /**
     * Velocity component along the X axis
     */
    private final float zVelocity;
    
    /**
     * Constructs an immutable instance of this class.
     * 
     * @param xVelocity
     *            Velocity component along the X axis
     * @param yVelocity
     *            Velocity component along the Y axis
     * @param zVelocity
     *            Velocity component along the X axis
     */
    public VelocityVectorStruct(final float xVelocity, final float yVelocity, final float zVelocity)
    {
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.zVelocity = zVelocity;
        
    }
    
    /**
     * @return Velocity component along the X axis
     */
    public float getXVelocity()
    {
        return xVelocity;
    }
    
    /**
     * @return Velocity component along the Y axis
     */
    public float getYVelocity()
    {
        return yVelocity;
    }
    
    /**
     * @return Velocity component along the Z axis
     */
    public float getZVelocity()
    {
        return zVelocity;
    }
    
    @Override
    public String toString()
    {
        
        return String.join(" ", "VelocityVectorStruct[", "XVel=", String.valueOf(xVelocity), "YVel=",
                String.valueOf(yVelocity), "ZVel=", String.valueOf(zVelocity), "]");
    }
}