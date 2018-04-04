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
package com.qinetiq.msg134.etc.tc_lib_warfare.decode;

import com.qinetiq.msg134.etc.tc_lib_warfare.types.VelocityVectorStruct;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Decoder for the VelocityVectorStructDecoder parameter type.
 * 
 * @author QinetiQ
 */
public class VelocityVectorStructDecoder implements Decoder<VelocityVectorStruct>
{
    /**
     * The HLA fixed record decoder to aggregate the data fields
     */
    private final HLAfixedRecord decoder;
    
    /**
     * The 32-bit representation of the XVelocity field
     */
    private final HLAfloat32BE xVelocity;
    
    /**
     * The 32-bit representation of the YVelocity field
     */
    private final HLAfloat32BE yVelocity;
    
    /**
     * The 32-bit representation of the ZVelocity field
     */
    private final HLAfloat32BE zVelocity;
    
    /**
     * Default constructor.
     * 
     * @throws RTIinternalError If an RTI error occurred
     */
    public VelocityVectorStructDecoder() throws RTIinternalError
    {
        EncoderFactory factory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        decoder = factory.createHLAfixedRecord();
        xVelocity = factory.createHLAfloat32BE();
        yVelocity = factory.createHLAfloat32BE();
        zVelocity = factory.createHLAfloat32BE();
        
        decoder.add(xVelocity);
        decoder.add(yVelocity);
        decoder.add(zVelocity);
    }
    
    /**
     * Decode the provided data to produce a VelocityVectorStruct object.
     */
    @Override
    public VelocityVectorStruct decode(final byte[] code) throws DecoderException
    {
        decoder.decode(code);
        return new VelocityVectorStruct(xVelocity.getValue(), yVelocity.getValue(), zVelocity.getValue());
    }
    
}
