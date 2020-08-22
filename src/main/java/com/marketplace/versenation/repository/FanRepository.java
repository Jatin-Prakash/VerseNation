package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.userTypes.FanAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FanRepository extends JpaRepository<FanAccount, Long>  {

}
