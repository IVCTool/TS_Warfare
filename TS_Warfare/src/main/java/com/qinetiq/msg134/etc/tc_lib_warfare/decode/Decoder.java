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

import hla.rti1516e.encoding.DecoderException;

/**
 * Interface to be implemented by concrete decoder classes to create an object
 * of the specified type from the raw data provided.
 * 
 * @author QinetiQ
 * @param <T>
 *            The type of object created by this decoder
 */
public interface Decoder<T>
{
    /**
     * Decode the raw data provided and creates an object of the specified type.
     * @param code The binary data from which to create the object.
     * @return The decoded object.
     * @throws DecoderException If the decode failed.
     */
    T decode(byte[] code) throws DecoderException;
}
