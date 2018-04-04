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

import com.qinetiq.msg134.etc.tc_lib_warfare.types.WorldLocationStruct;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Decoder for the WorldLocationStructDecoder parameter type.
 * 
 * @author QinetiQ
 */
public class WorldLocationStructDecoder implements Decoder<WorldLocationStruct>
{
    
    /**
     * The HLA fixed record decoder to aggregate the data fields
     */
    private final HLAfixedRecord decoder;
    
    /**
     * The 64-bit representation of the X field
     */
    private final HLAfloat64BE x;
    
    /**
     * The 64-bit representation of the Y field
     */
    private final HLAfloat64BE y;
    
    /**
     * The 64-bit representation of the Z field
     */
    private final HLAfloat64BE z;
    
    /**
     * Default constructor.
     * @throws RTIinternalError If an RTI error occurred
     */
    public WorldLocationStructDecoder() throws RTIinternalError
    {
        EncoderFactory factory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        decoder = factory.createHLAfixedRecord();
        x = factory.createHLAfloat64BE();
        y = factory.createHLAfloat64BE();
        z = factory.createHLAfloat64BE();
        
        decoder.add(x);
        decoder.add(y);
        decoder.add(z);
    }
    
    /**
     * Decode the provided data to produce a WorldLocationStruct object.
     */
    @Override
    public WorldLocationStruct decode(final byte[] code) throws DecoderException
    {
        decoder.decode(code);
        return new WorldLocationStruct(x.getValue(), y.getValue(), z.getValue());
    }
}
