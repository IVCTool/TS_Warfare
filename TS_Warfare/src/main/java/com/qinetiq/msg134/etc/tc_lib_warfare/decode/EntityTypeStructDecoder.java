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

import com.qinetiq.msg134.etc.tc_lib_warfare.types.EntityTypeStruct;
import com.qinetiq.msg134.etc.tc_lib_warfare.types.EntityTypeStruct.EntityKind;
import com.qinetiq.msg134.etc.tc_lib_warfare.types.EntityTypeStruct.MunitionDomain;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.exceptions.RTIinternalError;

/**
 * Decoder for the EntityTypeStruct parameter type.
 * @author QinetiQ
 */
public class EntityTypeStructDecoder implements Decoder<EntityTypeStruct>
{
    /**
     * The HLA fixed record decoder to aggregate the data fields
     */
    private final HLAfixedRecord decoder;
    
    /**
     * The EntityKind field
     */
    private final HLAoctet entityKind;
    
    /**
     * The Domain field pertaining to a Munition entity.
     */
    private final HLAoctet munitionDomain;
    
    /**
     * Default constructor.
     * @throws RTIinternalError If an RTI error occurred
     */
    public EntityTypeStructDecoder() throws RTIinternalError
    {
        EncoderFactory factory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
        decoder = factory.createHLAfixedRecord();
        entityKind = factory.createHLAoctet();
        munitionDomain = factory.createHLAoctet();
        decoder.add(entityKind);
        decoder.add(munitionDomain);
    }
    
    /**
     * Decode the provided data to produce a EntityTypeStruct object.
     */
    @Override
    public EntityTypeStruct decode(final byte[] code) throws DecoderException
    {
        decoder.decode(code);
        EntityKind entitykind;
        MunitionDomain munitiondomain;
        
        try
        {
            entitykind = EntityKind.values()[entityKind.getValue()];
            munitiondomain = MunitionDomain.values()[munitionDomain.getValue()];
        }
        catch (Exception e)
        {
            throw new DecoderException(
                    String.join(" ", "Error occurded trying to decode EntityKind and/or MunitionDomain"));
        }
        
        return new EntityTypeStruct(entitykind, munitiondomain);
    }
    
}
