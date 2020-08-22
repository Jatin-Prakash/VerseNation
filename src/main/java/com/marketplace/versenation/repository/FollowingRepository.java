package com.marketplace.versenation.repository;

import com.marketplace.versenation.models.Following.Following;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.userTypes.CreatorAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {
    //@Transactional
    //@Modifying
    //@Query("delete from u where u.from_user_fk = :from and u.to_creator_account_fk=:to")
    //void deleteByFromAndTo(@Param("from") long from , @Param("to") long to);
    Following findByFromAndTo(User from, CreatorAccount to);

    List<Following> findTop100ByTo(CreatorAccount to);
}
