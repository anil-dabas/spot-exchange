package com.spot.order.controller;

import com.spot.order.model.domain.Listing;
import com.spot.order.model.request.ListingPairRequest;
import com.spot.order.model.response.ListingDTO;
import com.spot.order.service.ListingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/instrument")
public class ListingController {


    @Autowired
    private ListingService listingService;
    @Autowired
    private ModelMapper mapper;

    @PostMapping()
    public ListingDTO addListing(@RequestBody ListingPairRequest listingPairRequest){
       Listing listing = listingService.addOrRemovePair(listingPairRequest);
        return mapper.map(listing,ListingDTO.class);
    }

    @GetMapping()
    public List<String> getAllListings(){
        return listingService.getAllListings();
    }
}
