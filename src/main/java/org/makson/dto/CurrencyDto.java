package org.makson.dto;

public class CurrencyDto {
    private final String name;
    private final String code;
    private final String sign;

    public CurrencyDto(String name, String code, String sign) {
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSign() {
        return sign;
    }
}
