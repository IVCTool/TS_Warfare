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

import com.qinetiq.msg134.etc.tc_lib_warfare.types.WarheadType;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Decoder for the WarheadType parameter type.
 * @author QinetiQ
 */
public class WarheadTypeDecoder implements Decoder<WarheadType>
{
    /**
     * The 16-bit decoder
     */
    protected final HLAinteger16BE decoder;
    
    /**
     * Default constructor
     * @throws RTIinternalError If an RTI error occurred
     */
    public WarheadTypeDecoder() throws RTIinternalError
    {
        EncoderFactory factory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        decoder = factory.createHLAinteger16BE();
    }
    
    /**
     * Decode the provided data to produce a WarheadType value.
     */
    @Override
    public WarheadType decode(final byte[] code) throws DecoderException
    {
        decoder.decode(code);
        short value = decoder.getValue();
        WarheadType enumValue = null;
        for (WarheadType thisEnumValue : WarheadType.values())
        {
            if (thisEnumValue.val() == value)
            {
                enumValue = thisEnumValue;
                break;
            }
        }
        
        if (enumValue==null)
        {
            throw new DecoderException(String.join(" ", "Unrecognised WarheadType value:",
                    String.valueOf(value)));
        }
        return enumValue;
    }
    
}
