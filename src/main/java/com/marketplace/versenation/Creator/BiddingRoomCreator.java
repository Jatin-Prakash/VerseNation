package com.marketplace.versenation.Creator;

import com.marketplace.versenation.exception.ResourceNotFoundException;
import com.marketplace.versenation.models.User;
import com.marketplace.versenation.models.userTypes.CreatorAccount;
import com.marketplace.versenation.payload.JoinBidRequest;
import com.marketplace.versenation.repository.BidRepository;
import com.marketplace.versenation.repository.CreatorRepository;
import com.marketplace.versenation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequestMapping("createBidRoom")
public class BiddingRoomCreator implements CreatorRepository {



    @PostMapping("/createRoom")
    public ResponseEntity<?> createRoom(@Valid @RequestBody JoinBidRequest joinBidRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));

      /* if(existsById(user.getId()) && user.getAccountType()){


       }*/

      return null;
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    BidRepository bidRepository;

    @Override
    public List<CreatorAccount> findAll() {
        return null;
    }

    @Override
    public List<CreatorAccount> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<CreatorAccount> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<CreatorAccount> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(CreatorAccount entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends CreatorAccount> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends CreatorAccount> S save(S entity) {

        return null;
    }

    @Override
    public <S extends CreatorAccount> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<CreatorAccount> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends CreatorAccount> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<CreatorAccount> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public CreatorAccount getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends CreatorAccount> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends CreatorAccount> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends CreatorAccount> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends CreatorAccount> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends CreatorAccount> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends CreatorAccount> boolean exists(Example<S> example) {
        return false;
    }
}
