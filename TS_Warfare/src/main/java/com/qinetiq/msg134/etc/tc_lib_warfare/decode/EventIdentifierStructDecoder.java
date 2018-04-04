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
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.exceptions.RTIinternalError;
import com.qinetiq.msg134.etc.tc_lib_warfare.types.EventIdentifierStruct;

/**
 * Decoder for the EventIdentifierStruct parameter type.
 * 
 * @author QinetiQ
 */
public class EventIdentifierStructDecoder implements Decoder<EventIdentifierStruct>
{
    /**
     * The HLA fixed record decoder to aggregate the data fields
     */
    private final HLAfixedRecord decoder;
    
    /**
     * The EventCount field
     */
    private final HLAinteger16BE eventCount;
    
    /**
     * The IssuingObjectIdentifier field
     */
    private final RTIobjectIdDecoder issuingObjectIdentifier;
    
    /**
     * Default constructor.
     * 
     * @throws RTIinternalError
     *             If an RTI error occurred
     */
    public EventIdentifierStructDecoder() throws RTIinternalError
    {
        EncoderFactory factory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        decoder = factory.createHLAfixedRecord();
        eventCount = factory.createHLAinteger16BE();
        issuingObjectIdentifier = new RTIobjectIdDecoder();
        decoder.add(eventCount);
        decoder.add(issuingObjectIdentifier);
    }
    
    /**
     * Decode the provided data to produce a EventIdentifierStruct object.
     */
    @Override
    public EventIdentifierStruct decode(final byte[] code) throws DecoderException
    {
        decoder.decode(code);
        return new EventIdentifierStruct(eventCount.getValue(), issuingObjectIdentifier.getValue().trim());
    }
}
