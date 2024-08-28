package com.spot.order.repo;

import com.spot.order.model.domain.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<Listing,Long> {

    Optional<Listing> findByPair(String listingPair);

    @Query("Select list.pair from listing list where isActivePair = true")
    List<String> findAllByIsActivePairTrue();
}
