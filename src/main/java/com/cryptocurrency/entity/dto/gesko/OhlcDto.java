package com.cryptocurrency.entity.dto.gesko;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.sql.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({ "date", "open", "high", "low", "close"})
public class OhlcDto {
    private Date date;
    private double open;
    private double high;
    private double low;
    private double close;
}
