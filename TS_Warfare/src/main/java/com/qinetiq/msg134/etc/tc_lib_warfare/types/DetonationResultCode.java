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
 * The detonation result code enumeration from Sect.5.2
 * of the document Enumeration and Bit Encoded Values for
 * Use with Protocols for Distributed Interactive Simulation
 * Applications.
 * @author QinetiQ
 */
public enum DetonationResultCode
{
    Other( 0 ),
    EntityImpact( 1 ),
    EntityProximateDetonation( 2 ),
    GroundImpact( 3 ),
    GroundProximateDetonation( 4 ),
    Detonation( 5 ),
    None( 6 ),
    HE_hit_Small( 7 ),
    HE_hit_Medium( 8 ),
    HE_hit_Large( 9 ),
    ArmorPiercingHit( 10 ),
    DirtBlast_Small( 11 ),
    DirtBlast_Medium( 12 ),
    DirtBlast_Large( 13 ),
    WaterBlast_Small( 14 ),
    WaterBlast_Medium( 15 ),
    WaterBlast_Large( 16 ),
    AirHit( 17 ),
    BuildingHit_Small( 18 ),
    BuildingHit_Medium( 19 ),
    BuildingHit_Large( 20 ),
    MineClearingLineCharge( 21 ),
    EnvironmentObjectImpact( 22 ),
    EnvironmentObjectProximateDetonation( 23 ),
    WaterImpact( 24 ),
    AirBurst( 25 ),
    Kill_with_fragment_type_1( 26 ),
    Kill_with_fragment_type_2( 27 ),
    Kill_with_fragment_type_3( 28 ),
    Kill_with_fragment_type_1_after_fly_out_failure( 29 ),
    Kill_with_fragment_type_2_after_fly_out_failure( 30 ),
    Miss_due_to_fly_out_failure( 31 ),
    Miss_due_to_end_game_failure( 32 ),
    Miss_due_to_fly_out_and_end_game_failure( 33 );

  public final byte val;

  private DetonationResultCode(final int val)
  {
      this.val = (byte)val;
  }

  public byte val() { return val; }

}