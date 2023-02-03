package kz.itolstov.demo.service;

import kz.itolstov.demo.exception.AuctionException;
import kz.itolstov.demo.model.User;
import kz.itolstov.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new AuctionException("user with id not found")
        );
    }
}
