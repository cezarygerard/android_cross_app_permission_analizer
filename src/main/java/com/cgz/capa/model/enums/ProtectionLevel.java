package com.cgz.capa.model.enums;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by czarek on 04/01/15.
 */
//TODO extract common code from enums in com.cgz.capa.model.enums;
public enum ProtectionLevel {

    NORMAL("normal"),
    DANGEROUS("dangerous"),
    SIGNATURE("signature"),
    SIGNATURE_OR_SYSTEM("signatureOrSystem");

    private String name;

    private static Map<String, ProtectionLevel> valuesMap = new Hashtable<String, ProtectionLevel> ();

    private ProtectionLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ProtectionLevel getEnumValueByName(String name){
        return valuesMap.get(name);
    }

    static{
        for(ProtectionLevel enumVal : ProtectionLevel.values()){
            valuesMap.put(enumVal.name, enumVal);
        }
    }
}
