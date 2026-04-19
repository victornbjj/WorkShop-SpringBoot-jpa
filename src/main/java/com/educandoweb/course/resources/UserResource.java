package com.educandoweb.course.resources;

import com.educandoweb.course.entities.User;
import com.educandoweb.course.security.JwtService;
import com.educandoweb.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> insert(@RequestBody User obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        UUID requesterId = extractUserId(authHeader);

        // só o próprio user pode deletar a própria conta
        if (!requesterId.equals(id)) {
            return ResponseEntity.status(403).build();
        }

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable UUID id,
            @RequestBody User obj,
            @RequestHeader("Authorization") String authHeader) {

        UUID requesterId = extractUserId(authHeader);

        // só o próprio user pode atualizar os próprios dados
        if (!requesterId.equals(id)) {
            return ResponseEntity.status(403).build();
        }

        obj = service.update(id, obj);
        return ResponseEntity.ok(obj);
    }

    // extrai o UUID do token JWT que vem no header
    private UUID extractUserId(String authHeader) {
        String token = authHeader.substring(7); // remove "Bearer "
        return jwtService.extractUserId(token);
    }
}