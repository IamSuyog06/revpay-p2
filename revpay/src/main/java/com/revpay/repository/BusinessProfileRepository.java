package com.revpay.repository;

import com.revpay.entity.BusinessProfile;
import com.revpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessProfileRepository  extends JpaRepository<BusinessProfile,Long> {
    Optional<BusinessProfile> findByUser(User user);

    boolean existsByTaxId(String taxId);

}
