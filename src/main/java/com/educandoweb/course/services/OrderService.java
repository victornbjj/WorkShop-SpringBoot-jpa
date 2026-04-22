package com.educandoweb.course.services;

import com.educandoweb.course.entities.Order;
import com.educandoweb.course.entities.User;
import com.educandoweb.course.repositories.OrderRepository;
import com.educandoweb.course.repositories.UserRepository;
import com.educandoweb.course.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;





@Service

public class OrderService {


    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    public Order findById(Long id){
         Optional <Order> obj = orderRepository.findById(id);
         return obj.get();
    }


    public List<Order> findAll(String token) {
        String email = jwtService.extractEmail(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole().equals("ADMIN")) {
            return orderRepository.findAll();
        } else {
            return orderRepository.findByClient(user);
        }
    }

}
