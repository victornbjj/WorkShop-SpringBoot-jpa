package com.educandoweb.course.resources;


import com.educandoweb.course.entities.Order;
import com.educandoweb.course.security.JwtService;
import com.educandoweb.course.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/orders")
public class OrderResource {

    @Autowired
    private JwtService jwtService;

   @Autowired
    private OrderService service;

    @GetMapping
    public ResponseEntity<List<Order>> findAll(@RequestHeader("Authorization") String token) {
        List<Order> list = service.findAll(token);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Order> findById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Order obj = service.findById(id);
        String email = jwtService.extractEmail(token.replace("Bearer ", ""));

        boolean isAdmin = obj.getClient().getRole().equals("ROLE_ADMIN");
        boolean isOwner = obj.getClient().getEmail().equals(email);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok().body(obj);
    }




}
