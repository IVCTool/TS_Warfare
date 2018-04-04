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

import com.qinetiq.msg134.etc.tc_lib_warfare.types.FuseType;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Decoder for the EventIdentifierStruct enumeration type.
 * 
 * @author QinetiQ
 */
public class FuseTypeDecoder implements Decoder<FuseType>
{
    /**
     * The HLA object representing the 16-bit value
     */
    protected final HLAinteger16BE decoder;
    
    /**
     * Default constructor
     * 
     * @throws RTIinternalError If an RTI error occurred
     */
    public FuseTypeDecoder() throws RTIinternalError
    {
        decoder = RtiFactoryFactory.getRtiFactory().getEncoderFactory().createHLAinteger16BE();
    }
    
    /**
     * Decode the provided data to produce a FuseType value.
     */
    @Override
    public FuseType decode(final byte[] code) throws DecoderException
    {
        decoder.decode(code);
        int value = decoder.getValue();
        FuseType enumValue = null;
        
        for (FuseType thisEnumValue : FuseType.values())
        {
            if (thisEnumValue.val() == value)
            {
                enumValue = thisEnumValue;
                break;
            }
        }
        
        if (enumValue == null)
        {
            throw new DecoderException(String.join(" ", "Unrecognised FuseType value:", String.valueOf(value)));
        }
        
        return enumValue;
    }
    
}
