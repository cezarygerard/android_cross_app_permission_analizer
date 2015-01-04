package com.cgz.capa.model;

/**
 * Created by czarek on 04/01/15.
 */
public enum ProtectionLevel {

    NORMAL("normal"),
    DANGEROUS("dangerous"),
    SIGNATURE("signature"),
    SIGNATURE_OR_SYSTEM("signatureOrSystem");

    private String name;

    private ProtectionLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
