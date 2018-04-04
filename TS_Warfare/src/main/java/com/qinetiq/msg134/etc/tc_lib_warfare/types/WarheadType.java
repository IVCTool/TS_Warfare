package com.qinetiq.msg134.etc.tc_lib_warfare.types;

/**
 * The Warhead enumeration from Sect.5.1.1
 * of the document Enumeration and Bit Encoded Values for
 * Use with Protocols for Distributed Interactive Simulation
 * Applications.
 * @author QinetiQ
 */
public enum WarheadType
{
    Other( 0 ),
    CargoVariableSubmunitions( 10 ),
    FuelAirExplosive( 20 ),
    GlassBeads( 30 ),
    Warhead_1um( 31 ),
    Warhead_5um( 32 ),
    Warhead_10um( 33 ),
    HighExplosive( 1000 ),
    HE_Plastic( 1100 ),
    HE_Incendiary( 1200 ),
    HE_Fragmentation( 1300 ),
    HE_Antitank( 1400 ),
    HE_Bomblets( 1500 ),
    HE_ShapedCharge( 1600 ),
    HE_ContinuousRod( 1610 ),
    HE_TungstenBall( 1615 ),
    HE_BlastFragmentation( 1620 ),
    HE_SteerableDartswithHE( 1625 ),
    HE_Darts( 1630 ),
    HE_Flechettes( 1635 ),
    HE_DirectedFragmentation( 1640 ),
    HE_SemiArmorPiercing( 1645 ),
    HE_ShapedChargeFragmentation( 1650 ),
    HE_SemiArmorPiercingFragmentation( 1655 ),
    HE_HollowCharge( 1660 ),
    HE_DoubleHollowCharge( 1665 ),
    HE_GeneralPurpose( 1670 ),
    HE_BlastPenetrator( 1675 ),
    HE_RodPenetrator( 1680 ),
    HE_Antipersonnel( 1685 ),
    Smoke( 2000 ),
    Illumination( 3000 ),
    Practice( 4000 ),
    Kinetic( 5000 ),
    Mines( 6000 ),
    Nuclear( 7000 ),
    NuclearIMT( 7010 ),
    ChemicalGeneral( 8000 ),
    ChemicalBlisterAgent( 8100 ),
    HD_Mustard( 8110 ),
    ThickenedHD_Mustard( 8115 ),
    DustyHD_Mustard( 8120 ),
    ChemicalBloodAgent( 8200 ),
    AC_HCN( 8210 ),
    CK_CNCI( 8215 ),
    CG_Phosgene( 8220 ),
    ChemicalNerveAgent( 8300 ),
    VX( 8310 ),
    ThickenedVX( 8315 ),
    DustyVX( 8320 ),
    GA_Tabun( 8325 ),
    ThickenedGA_Tabun( 8330 ),
    DustyGA_Tabun( 8335 ),
    GB_Sarin( 8340 ),
    ThickenedGB_Sarin( 8345 ),
    DustyGB_Sarin( 8350 ),
    GD_Soman( 8355 ),
    ThickenedGD_Soman( 8360 ),
    DustyGD_Soman( 8365 ),
    GF( 8370 ),
    ThickenedGF( 8375 ),
    DustyGF( 8380 ),
    Biological( 9000 ),
    BiologicalVirus( 9100 ),
    BiologicalBacteria( 9200 ),
    BiologicalRickettsia( 9300 ),
    BiologicalGeneticallyModifiedMicroOrganisms( 9400 ),
    BiologicalToxin( 9500 );

  public final short val;

  private WarheadType(final int val)
  {
      this.val = (short) val;
  }

  public short val() { return val; }

}