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

import com.qinetiq.msg134.etc.tc_lib_warfare.types.DetonationResultCode;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Decoder for the DetonationResultCode enumeration.
 * @author QinetiQ
 */
public class DetonationResultCodeDecoder implements Decoder<DetonationResultCode>
{
    /**
     * The HLA object representing the byte value
     */
    protected final HLAoctet decoder;
    
    /**
     * Default constructor.
     * @throws RTIinternalError If an RTI error was encountered.
     */
    public DetonationResultCodeDecoder() throws RTIinternalError
    {
        EncoderFactory factory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        decoder = factory.createHLAoctet();
    }
    
    /**
     * Decode the provided data to produce a DetonationResultCode.
     */
    @Override
    public DetonationResultCode decode(final byte[] code) throws DecoderException
    {
        decoder.decode(code);
        int value = decoder.getValue();
        DetonationResultCode enumValue = null;
        for (DetonationResultCode thisEnumValue : DetonationResultCode.values())
        {
            if (thisEnumValue.val() == value)
            {
                enumValue = thisEnumValue;
                break;
            }
        }
        
        if (enumValue==null)
        {
            throw new DecoderException(String.join(" ", "Unrecognised DetonationResult value:",
                    String.valueOf(value)));
        }
        return enumValue;
    }
    
}
