package com.spot.order.cache;

import com.spot.order.repo.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ListedPairsCache {

    @Autowired
    ListingRepository repository;
    public static Set<String> listedPairs;
    public void initCache(){
        listedPairs = new HashSet<>(repository.findAllByIsActivePairTrue());
    }
}
