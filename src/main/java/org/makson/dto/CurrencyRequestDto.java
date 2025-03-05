package org.makson.dto;

public class CurrencyRequestDto {
    private final String name;
    private final String code;
    private final String sign;

    public CurrencyRequestDto(String name, String code, String sign) {
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
