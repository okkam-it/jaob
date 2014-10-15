package com.yoshtec.owl.testclasses.enumt;

import com.yoshtec.owl.annotations.OwlClass;
import com.yoshtec.owl.annotations.OwlEnumValue;

@OwlClass // now this is the typical enumerated class case
public enum GlassColor {
    WHITE,
    @OwlEnumValue("Brown")
    BROWN,
    GREEN
}
