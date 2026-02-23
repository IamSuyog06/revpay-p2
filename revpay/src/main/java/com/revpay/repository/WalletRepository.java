package com.revpay.repository;

import com.revpay.entity.User;
import com.revpay.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {


    Optional<Wallet> findByUser(User user);
}
