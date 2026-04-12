package com.romeogolf.residence.listing;

import com.romeogolf.residence.listing.dto.ListingRequest;
import com.romeogolf.residence.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    // ─── Public ──────────────────────────────────────────────────────────────
    public List<Listing> getPublished() {
        return listingRepository.findByStatus(ListingStatus.PUBLIE);
    }

    // ─── Admin ───────────────────────────────────────────────────────────────
    public List<Listing> getAll() {
        return listingRepository.findAll();
    }

    public Listing getById(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new ApiException("Annonce introuvable.", HttpStatus.NOT_FOUND));
    }

    public Listing create(ListingRequest req) {
        Listing listing = Listing.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .type(req.getType())
                .price(req.getPrice())
                .surface(req.getSurface())
                .floorNumber(req.getFloorNumber())
                .bedrooms(req.getBedrooms())
                .bathrooms(req.getBathrooms())
                .features(req.getFeatures())
                .photos(req.getPhotos())
                .status(req.getStatus())
                .build();
        return listingRepository.save(listing);
    }

    public Listing update(Long id, ListingRequest req) {
        Listing listing = getById(id);
        listing.setTitle(req.getTitle());
        listing.setDescription(req.getDescription());
        listing.setType(req.getType());
        listing.setPrice(req.getPrice());
        listing.setSurface(req.getSurface());
        listing.setFloorNumber(req.getFloorNumber());
        listing.setBedrooms(req.getBedrooms());
        listing.setBathrooms(req.getBathrooms());
        listing.setFeatures(req.getFeatures());
        listing.setPhotos(req.getPhotos());
        listing.setStatus(req.getStatus());
        return listingRepository.save(listing);
    }

    public void delete(Long id) {
        if (!listingRepository.existsById(id)) {
            throw new ApiException("Annonce introuvable.", HttpStatus.NOT_FOUND);
        }
        listingRepository.deleteById(id);
    }
}
