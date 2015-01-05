package com.cgz.capa.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by czarek on 04/01/15.
 */
//TODO extract common code from enums in com.cgz.capa.model.enums;
public enum ProtectionLevel {

    NORMAL("normal"),
    DANGEROUS("dangerous"),
    SIGNATURE("signature"),
    SIGNATURE_OR_SYSTEM("signature|system");

    private String name;

    private static Map<String, ProtectionLevel> valuesMap = new HashMap<String, ProtectionLevel>();

    private ProtectionLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ProtectionLevel getEnumValueByName(String name) {
        ProtectionLevel retVal = valuesMap.get(name);
        if (retVal == null) {
            return NORMAL;
        }
        return retVal;
    }


    static {
        for (ProtectionLevel enumVal : ProtectionLevel.values()) {
            valuesMap.put(enumVal.name, enumVal);
        }
    }
}
