package org.makson.dto;

public class CurrencyResponseDto {
    private final Long id;
    private final String name;
    private final String code;
    private final String sign;

    public CurrencyResponseDto(Long id, String name, String code, String sign) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public Long getId() {
        return id;
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
