package fi.vm.sade.kaava

object Arvojoukot {

  val EI_KOOSTETUT = Array(
    "O" -> "Äidinkielen koe, ruotsi (O)",
    "O5" -> "Ruotsi toisena kielenä (O5)",
    "A" -> "Äidinkielen koe, suomi (A)",
    "A5" -> "Suomi toisena kielenä (A5)",
    "BA" -> "Ruotsi, pitkä oppimäärä (BA)",
    "BB" -> "Ruotsi, keskipitkä oppimäärä (BB)",
    "CA" -> "Suomi, pitkä oppimäärä (CA)",
    "CB" -> "Suomi, keskipitkä oppimäärä (CB)",
    "CC" -> "Suomi, lyhyt oppimäärä (CC)",
    "DC" -> "Pohjoissaame, lyhyt oppimäärä (DC)",
    "EA" -> "Englanti, pitkä oppimäärä (EA)",
    "EB" -> "Englanti, keskipitkä oppimäärä (EB)",
    "EC" -> "Englanti, lyhyt oppimäärä (EC)",
    "FA" -> "Ranska, pitkä oppimäärä (FA)",
    "FB" -> "Ranska, keskipitkä oppimäärä (FB)",
    "FC" -> "Ranska, lyhyt oppimäärä (FC)",
    "GA" -> "Portugali, pitkä oppimäärä (GA)",
    "GB" -> "Portugali, keskipitkä oppimäärä (GB)",
    "GC" -> "Portugali, lyhyt oppimäärä (GC)",
    "HA" -> "Unkari, pitkä oppimäärä (HA)",
    "HB" -> "Unkari, keskipitkä oppimäärä (HB)",
    "I" -> "Äidinkielen koe, inarinsaame (I)",
    "W" -> "Äidinkielen koe, koltansaame (W)",
    "IC" -> "Inarinsaame, lyhyt oppimäärä (IC)",
    "QC" -> "Koltan saame, lyhyt oppimäärä (QC)",
    "J" -> "Englanninkielinen kypsyyskoe (J)",
    "KC" -> "Kreikka, lyhyt oppimäärä (KC)",
    "L1" -> "Latina, lyhyt oppimäärä (L1)",
    "L7" -> "Latina, laajempi oppimäärä (L7)",
    "M" -> "Matematiikan koe, pitkä oppimäärä (M)",
    "N" -> "Matematiikan koe, lyhyt oppimäärä (N)",
    "PA" -> "Espanja, pitkä oppimäärä (PA)",
    "PB" -> "Espanja, keskipitkä oppimäärä (PB)",
    "PC" -> "Espanja, lyhyt oppimäärä (PC)",
    "RR" -> "Reaali, ev lut uskonnon kysymykset (RR)",
    "RO" -> "Reaali, ortod.uskonnon kysymykset (RO)",
    "RY" -> "Reaali, elämänkatsomustiedon kysymykset (RY)",
    "SA" -> "Saksa, pitkä oppimäärä (SA)",
    "SB" -> "Saksa, keskipitkä oppimäärä (SB)",
    "SC" -> "Saksa, lyhyt oppimäärä (SC)",
    "S9" -> "Saksalaisen koulun saksan kielen koe (S9)",
    "TA" -> "Italia, pitkä oppimäärä (TA)",
    "TB" -> "Italia, keskipitkä oppimäärä (TB)",
    "TC" -> "Italia, lyhyt oppimäärä (TC)",
    "VA" -> "Venäjä, pitkä oppimäärä (VA)",
    "VB" -> "Venäjä, keskipitkä oppimäärä (VB)",
    "VC" -> "Venäjä, lyhyt oppimäärä (VC)",
    "Z" -> "Äidinkielen koe, pohjoissaame (Z)",
    "UE" -> "Ev.lut. Uskonto (UE)",
    "UO" -> "Ortodoksiuskonto (UO)",
    "ET" -> "Elämänkatsomustieto (ET)",
    "FF" -> "Filosofia (FF)",
    "PS" -> "Psykologia (PS)",
    "HI" -> "Historia (HI)",
    "FY" -> "Fysiikka (FY)",
    "KE" -> "Kemia (KE)",
    "BI" -> "Biologia (BI)",
    "GE" -> "Maantiede (GE)",
    "TE" -> "Terveystieto (TE)",
    "YH" -> "Yhteiskuntaoppi (YH)"
  )

  val KOOSTETUT = Array(
    "AINEREAALI" -> "Ainereaali (UE, UO, ET, FF, PS, HI, FY, KE, BI, GE, TE, YH)",
    "REAALI" -> "Reaali (RR, RO, RY)",
    "PITKA_KIELI" -> "Kieli, pitkä oppimäärä (EA, FA, GA, HA, PA, SA, TA, VA, S9)",
    "KESKIPITKA_KIELI" -> "Kieli, keskipitkä oppimäärä (EB, FB, GB, HB, PB, SB, TB, VB)",
    "LYHYT_KIELI" -> "Kieli, lyhyt oppimäärä (EC, FC, GC, L1, PC, SC, TC, VC, KC, L7)",
    "AIDINKIELI" -> "Äidinkieli (O, A, I, W, Z, O5, A5)"
  )

  val YO_OPPIAINEET = EI_KOOSTETUT ++ KOOSTETUT

  val LUKUKAUDET = Array(
    "2" -> "Syksy",
    "1" -> "Kevät"
  )

  val OSAKOKEET = Array(
    "01" -> "Ev.lut. uskonto (01)",
    "02" -> "Ortodoksiuskonto (02)",
    "03" -> "Elämänkatsomustieto (03)",
    "04" -> "Filosofia (04)",
    "05" -> "Psykologia (05)",
    "06" -> "Historia ja yhteiskunta-oppi (06)",
    "07" -> "Fysiikka (07)",
    "08" -> "Kemia (08)",
    "09" -> "Biologia (09)",
    "10" -> "Maantiede (10)"
  ) ++ EI_KOOSTETUT

  val KOEROOLIT = Array(
    "11" -> "Äidinkieli (11)",
    "12" -> "Äidinkieli, saame (12)",
    "13" -> "Kypsyyskoe (13)",
    "14" -> "Äidinkielen tilalla suoritettava suomen tai ruotsin koe (14)",
    "21" -> "Pakollinen toisen kotimaisen kielen koe tai erivapaudella suoritettava muu kieli tai reaali tai matematiikka (21)",
    "22" -> "Pakollinen toisen kotimaisen sijaan suoritettu äidinkieli tässä kielessä (22)",
    "31" -> "Pakollisena kokeena suoritettu vieras kieli (31)",
    "32" -> "Pakollisena kokeen suoritettu vieras kieli (32)",
    "41" -> "Pakollisena kokeena suoritettu reaaliaine (41)",
    "42" -> "Pakollisena kokeena suoritettu matematiikka (42)",
    "60" -> "Ylimääräinen äidinkieli (60)",
    "61" -> "Ylimääräisenä kokeena suoritettu vieras kieli (61)",
    "62" -> "Ylimääräisenä suoritettu toisen kotimaisen kielen koe (62)",
    "71" -> "Ylimääräisenä kokeena suoritettu reaaliaine (71)",
    "81" -> "Ylimääräisenä kokeena suoritettu matematiikka (81)"
  )

  val TOTUUSARVOT = Array(
    "true" -> "true",
    "false" -> "false"
  )

}
