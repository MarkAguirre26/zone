package com.virtual.zone.module.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class AppResponse {
    private ZoneResponse zoneResponse;
    private List<DivElement> divElement;
}
