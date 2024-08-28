package com.spot.order.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingDTO {
    private String pair;
    private String marketType;
    private LocalDate createdTime;
    private LocalDate updatedTime;
    private boolean isActivePair;
}
