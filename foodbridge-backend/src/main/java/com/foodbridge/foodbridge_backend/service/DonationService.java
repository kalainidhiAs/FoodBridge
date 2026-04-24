package com.foodbridge.foodbridge_backend.service;

import com.foodbridge.foodbridge_backend.model.*;
import com.foodbridge.foodbridge_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationService {

    @Autowired private DonationRepository donationRepository;
    @Autowired private FoodListingRepository foodListingRepository;
    @Autowired private NotificationService notificationService;

    public Donation requestCollection(User ngo, Long listingId) {
        if (!"APPROVED".equals(ngo.getStatus())) {
            throw new RuntimeException("Your NGO must be verified to collect food.");
        }
        FoodListing listing = foodListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Food listing not found"));
        if (!Boolean.TRUE.equals(listing.getIsFree())) {
            throw new RuntimeException("This food item is not a donation.");
        }
        if (donationRepository.findByNgoAndFoodListing(ngo, listing).isPresent()) {
            throw new RuntimeException("You have already requested this donation.");
        }
        Donation donation = new Donation();
        donation.setNgo(ngo);
        donation.setFoodListing(listing);
        donation.setStatus("PENDING");
        donation.setCollectionTime(LocalDateTime.now());
        Donation saved = donationRepository.save(donation);
        notificationService.send(listing.getHomemaker(),
                ngo.getName() + " has requested to collect your donated food: " + listing.getFoodName(), "DONATION");
        return saved;
    }

    public Donation confirmCollection(User ngo, Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        if (!donation.getNgo().getId().equals(ngo.getId())) {
            throw new RuntimeException("Not authorized.");
        }
        if (!"ACCEPTED".equals(donation.getStatus())) {
            throw new RuntimeException("Homemaker must accept the request first.");
        }
        donation.setStatus("COLLECTED");
        donation.setCollectionTime(LocalDateTime.now());
        Donation saved = donationRepository.save(donation);
        notificationService.send(donation.getFoodListing().getHomemaker(),
                "Your donated food '" + donation.getFoodListing().getFoodName() + "' has been successfully collected by " + ngo.getName() + ".", "DONATION");
        return saved;
    }

    public List<Donation> getNgoDonations(User ngo) {
        return donationRepository.findByNgo(ngo);
    }

    public Donation updateDonationStatus(User homemaker, Long donationId, String status) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        if (!donation.getFoodListing().getHomemaker().getId().equals(homemaker.getId())) {
            throw new RuntimeException("Not authorized.");
        }
        if (!"PENDING".equals(donation.getStatus())) {
            throw new RuntimeException("Only pending requests can be updated.");
        }
        if (!"ACCEPTED".equals(status) && !"REJECTED".equals(status)) {
            throw new RuntimeException("Invalid status.");
        }
        donation.setStatus(status);
        Donation saved = donationRepository.save(donation);
        notificationService.send(donation.getNgo(),
                homemaker.getName() + " has " + status.toLowerCase() + " your collection request for: " + donation.getFoodListing().getFoodName(), "DONATION");
        return saved;
    }

    public List<Donation> getHomemakerDonations(User homemaker) {
        return donationRepository.findByFoodListingHomemaker(homemaker);
    }
}
