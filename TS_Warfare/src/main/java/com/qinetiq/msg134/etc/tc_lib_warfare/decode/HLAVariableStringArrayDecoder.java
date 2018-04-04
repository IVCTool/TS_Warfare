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

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Decoder for the HLAVariableStringArray type to create an
 * HLAVariableStringArray of HLAunicodeString elements.
 * 
 * @author QinetiQ
 */
public class HLAVariableStringArrayDecoder implements Decoder<HLAvariableArray<HLAunicodeString>>
{
    /**
     * HLA Encoder factory.
     */
    EncoderFactory factory;
    
    /**
     * Data element factory for the creation of the individual HLAunicodeString
     * array elements.
     */
    DataElementFactory<HLAunicodeString> elementFactory;
    
    /**
     * Default constructor.
     * 
     * @throws RTIinternalError If an RTI error occurred
     */
    public HLAVariableStringArrayDecoder() throws RTIinternalError
    {
        factory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        elementFactory = new DataElementFactory<HLAunicodeString>()
        {
            @Override
            public HLAunicodeString createElement(int index)
            {
                return factory.createHLAunicodeString();
            }
        };
        
    }
    
    /**
     * Decode the provided data to produce a HLAvariableArray containing
     * HLAunicodeString elements.
     */
    @Override
    public HLAvariableArray<HLAunicodeString> decode(final byte[] code) throws DecoderException
    {
        
        ByteWrapper byteWrapper = new ByteWrapper(code);
        int elementCount = byteWrapper.getInt();
        byteWrapper.reset();
        HLAunicodeString[] HLAunicodeStringArray = new HLAunicodeString[elementCount];
        for (int index = 0; index < elementCount; index++)
        {
            HLAunicodeStringArray[index] = factory.createHLAunicodeString();
        }
        HLAvariableArray<HLAunicodeString> hlaVvariableArray = factory.createHLAvariableArray(elementFactory,
                HLAunicodeStringArray);
        hlaVvariableArray.decode(byteWrapper);
        return hlaVvariableArray;
    }
    
}
