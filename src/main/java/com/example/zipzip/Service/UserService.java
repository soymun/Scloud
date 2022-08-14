package com.example.zipzip.Service;

import com.example.zipzip.Entity.User;
import com.example.zipzip.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findUserByEmail(username);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }


        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getRole());
    }

    @Transactional
    public void save(User user){
        userRepo.save(user);
    }

    @Transactional
    public User findUserByEmail(String email){
        return userRepo.findUserByEmail(email);
    }

    @Transactional
    public User findUserById(Long id){
        return userRepo.findUserById(id);
    }

    @Transactional
    public void saveFilm(User user){
        userRepo.flush();
        userRepo.save(user);
    }
}
