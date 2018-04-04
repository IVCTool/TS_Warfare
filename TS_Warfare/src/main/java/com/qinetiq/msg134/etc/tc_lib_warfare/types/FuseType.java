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
 * The FuseType enumeration from Sect.5.1.2
 * of the document Enumeration and Bit Encoded Values for
 * Use with Protocols for Distributed Interactive Simulation
 * Applications.
 * @author QinetiQ
 */
public enum FuseType
{
    Other( 0 ),
    IntelligentInfluence( 10 ),
    Sensor( 20 ),
    SelfDestruct( 30 ),
    UltraQuick( 40 ),
    Body( 50 ),
    DeepIntrusion( 60 ),
    Multifunction( 100 ),
    PointDetonation_PD( 200 ),
    BaseDetonation_BD( 300 ),
    Contact( 1000 ),
    ContactInstantImpact( 1100 ),
    ContactDelayed( 1200 ),
    Contact10msDelay( 1201 ),
    Contact20msDelay( 1202 ),
    Contact50msDelay( 1205 ),
    Contact60msDelay( 1206 ),
    Contact100msDelay( 1210 ),
    Contact125msDelay( 1212 ),
    Contact250msDelay( 1225 ),
    ContactElectronicObliqueContact( 1300 ),
    ContactGraze( 1400 ),
    ContactCrush( 1500 ),
    ContactHydrostatic( 1600 ),
    ContactMechanical( 1700 ),
    ContactChemical( 1800 ),
    ContactPiezoelectric( 1900 ),
    ContactPointInitiating( 1910 ),
    ContactPointInitiatingBaseDetonating( 1920 ),
    ContactBaseDetonating( 1930 ),
    ContactBallisticCapAndBase( 1940 ),
    ContactBase( 1950 ),
    ContactNose( 1960 ),
    ContactFittedInStandoffProbe( 1970 ),
    ContactNonAligned( 1980 ),
    Timed( 2000 ),
    TimedProgrammable( 2100 ),
    TimedBurnout( 2200 ),
    TimedPyrotechnic( 2300 ),
    TimedElectronic( 2400 ),
    TimedBaseDelay( 2500 ),
    TimedReinforcedNoseImpactDelay( 2600 ),
    TimedShortDelayImpact( 2700 ),
    Timed10msDelay( 2701 ),
    Timed20msDelay( 2702 ),
    Timed50msDelay( 2705 ),
    Timed60msDelay( 2706 ),
    Timed100msDelay( 2710 ),
    Timed125msDelay( 2712 ),
    Timed250msDelay( 2725 ),
    TimedNoseMountedVariableDelay( 2800 ),
    TimedLongDelaySide( 2900 ),
    TimedSelectableDelay( 2910 ),
    TimedImpact( 2920 ),
    TimedSequence( 2930 ),
    Proximity( 3000 ),
    ProximityActiveLaser( 3100 ),
    ProximityMagneticMagpolarity( 3200 ),
    ProximityActiveDopplerRadar( 3300 ),
    ProximityRadioFrequencyRF( 3400 ),
    ProximityProgrammable( 3500 ),
    ProximityProgrammablePrefragmented( 3600 ),
    ProximityInfrared( 3700 ),
    Command( 4000 ),
    CommandElectronicRemotelySet( 4100 ),
    Altitude( 5000 ),
    AltitudeRadioAltimeter( 5100 ),
    AltitudeAirBurst( 5200 ),
    Depth( 6000 ),
    Acoustic( 7000 ),
    Pressure( 8000 ),
    PressureDelay( 8010 ),
    Inert( 8100 ),
    Dummy( 8110 ),
    Practice( 8120 ),
    PlugRepresenting( 8130 ),
    Training( 8150 ),
    Pyrotechnic( 9000 ),
    PyrotechnicDelay( 9010 ),
    ElectroOptical( 9100 ),
    ElectroMechanical( 9110 ),
    ElectroMechanicalNose( 9120 ),
    Strikerless( 9200 ),
    StrikerlessNoseImpact( 9210 ),
    StrikerlessCompressionIgnition( 9220 ),
    CompressionIgnition( 9300 ),
    CompressionIgnitionStrikerlessNoseImpact( 9310 ),
    Percussion( 9400 ),
    PercussionInstantaneous( 9410 ),
    Electronic( 9500 ),
    ElectronicInternallyMounted( 9510 ),
    ElectronicRangeSetting( 9520 ),
    ElectronicProgrammed( 9530 ),
    Mechanical( 9600 ),
    MechanicalNose( 9610 ),
    MechanicalTail( 9620 );

  public final short val;


  private FuseType(final int val)
  {
      this.val = (short)val;
  }

  public short val() { return val; }

}