package com.spot.order.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingPairRequest {

    private String listingPair;
    private String marketType;
    private boolean addRequest;
    private boolean removeRequest;
}
