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
 * Encapsulation of the relevant the fields from the EntityTypeStruct
 * fixed record type as defined in the RPR- Base FOM.
 * @author QinetiQ
 */
public class EntityTypeStruct 
{

	/**
     * The Entity Kind enumeration from Sect.4.2.1
     * of the document Enumeration and Bit Encoded Values for
     * Use with Protocols for Distributed Interactive Simulation
     * Applications.
     */
	public enum EntityKind
	{
		OTHER,
		PLATFORM,
		MUNITION,
		LIFE_FORM,
		ENVIRONMENTAL,
		CULTURAL_FEATURE,
		SUPPLY,
		RADIO,
		EXPENDABLE,
		SENSOR,
		EMITTER
	}
	
	/**
     * The Munition Domain enumeration from Sect.4.2.1.2.1
     * of the document Enumeration and Bit Encoded Values for
     * Use with Protocols for Distributed Interactive Simulation
     * Applications.
	 */
	public enum MunitionDomain
	{
		OTHER,
		ANTI_AIR,
		ANTI_ARMOR,
		ANTI_GUIDED_WEAPON,
		ANTIRADAR,
		ANTI_SATELLITE,
		ANTI_SHIP,
		ANTI_SUBMARINE,
		ANTI_PERSONEL,
		BATTLEFIELD_SUPPORT,
		STRATEGIC,
		TACTICAL
	}
	
	/**
	 * The Entity Kind
	 */
	private final EntityKind entityKind;
	
	/**
	 * The Domain value pertaining to a Munition entity.
	 */
	private final MunitionDomain domain;
	
	/**
	 * Constructs an immutable instance of this class.
	 * @param entityKind The Entity Kind value
	 * @param domain The Munition Domain value
	 */
	public EntityTypeStruct(final EntityKind entityKind, final MunitionDomain domain) 
	{
		super();
		this.entityKind = entityKind;
		this.domain = domain;
	}
	
	/**
	 * @return The entityKind
	 */
	public EntityKind getEntityKind() 
	{
		return entityKind;
	}
	
	/**
	 * @return The domain
	 */
	public MunitionDomain getDomain() 
	{
		return domain;
	}
	
	@Override
	public String toString() {
		return String.join(" ", "EntityTypeStruct[","EntityKind=",entityKind.name(),"MunitionDomain=",domain.name(),"]");
	}
	


}
