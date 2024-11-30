package com.virtual.zone.module.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CardClickRequest {
    // Getters and Setters
    private String data;        // Use Object to accommodate any structure for 'data'
    private String cardId;      // Card ID

}
