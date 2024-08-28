package com.spot.order.service;

import com.spot.order.model.domain.Listing;
import com.spot.order.model.request.ListingPairRequest;
import com.spot.order.repo.ListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.spot.order.cache.ListedPairsCache.listedPairs;

@Slf4j
@Service
public class ListingServiceImpl implements ListingService{

    @Autowired
    ListingRepository repository;


    @Override
    public Listing addOrRemovePair(ListingPairRequest listingPairRequest) {
        log.info("The value of listingPair request is {}",listingPairRequest);
        if(listingPairRequest.isAddRequest() && !listedPairs.contains(listingPairRequest.getListingPair())) {
            Listing listing = Listing.builder().pair(listingPairRequest.getListingPair())
                    .marketType(listingPairRequest.getMarketType())
                    .createdTime(LocalDate.now()).isActivePair(true).build();
            log.info("The value of listing is {}",listing);
            listedPairs.add(listingPairRequest.getListingPair());
            return repository.save(listing);
        } else if (listingPairRequest.isRemoveRequest() && listedPairs.contains(listingPairRequest.getListingPair())) {
            Optional<Listing> listingOptional = repository.findByPair(listingPairRequest.getListingPair());
            listingOptional.ifPresent(listing -> {
                listing.setActivePair(false);
                repository.save(listing);
            });
            listedPairs.remove(listingPairRequest.getListingPair());
        }
        return Listing.builder().build();
    }

    @Override
    public List<String> getAllListings() {
        return  repository.findAllByIsActivePairTrue();
    }
}
