package com.spot.order.service;

import com.spot.order.model.domain.Listing;
import com.spot.order.model.request.ListingPairRequest;

import java.util.List;

public interface ListingService {
    Listing addOrRemovePair(ListingPairRequest listingPairRequest);
    List<String> getAllListings();
}
