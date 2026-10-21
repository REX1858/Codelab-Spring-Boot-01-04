package com.denkitronik.clienteservice;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

public class MediaType extends org.springframework.http.MediaType implements Serializable {

    public MediaType(String type) {
        super(type);
    }

    public MediaType(String type, String subtype) {
        super(type, subtype);
    }

    public MediaType(String type, String subtype, Charset charset) {
        super(type, subtype, charset);
    }

    public MediaType(String type, String subtype, double qualityValue) {
        super(type, subtype, qualityValue);
    }

    public MediaType(org.springframework.http.MediaType other, Map<String, String> parameters) {
        super(other, parameters);
    }

    public MediaType(String type, String subtype, Map<String, String> parameters) {
        super(type, subtype, parameters);
    }
}
