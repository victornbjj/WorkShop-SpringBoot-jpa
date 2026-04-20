    package com.educandoweb.course.resources;

    import com.educandoweb.course.entities.Order;
    import com.educandoweb.course.entities.User;
    import com.educandoweb.course.repositories.UserRepository;
    import com.educandoweb.course.resources.exceptions.UserNotFoundException;
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
        private UserRepository userRepository;

        @Autowired
        private OrderService service;

        @GetMapping
        public ResponseEntity<List<Order>> findAll(
                @RequestHeader("Authorization") String authHender) {

            String email = jwtService.extractEmail(authHender.substring(7));
            String role = jwtService.extractRole(authHender.substring(7));


            List<Order> list;


            if(role.equals("ADMIN")){
                list = service.findAll();
            } else{
                User user= userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
                
                list = service.findAllByClient(user);
            } 
            return ResponseEntity.ok().body(list);
        }

        @GetMapping(value = "/{id}")
        public ResponseEntity<Order> findById(
                @PathVariable Long id,
                @RequestHeader("Authorization") String token) {

            Order obj = service.findById(id);
            String email = jwtService.extractEmail(token.replace("Bearer ", ""));

            boolean isAdmin = obj.getClient().getRole().equals("ADMIN");
            boolean isOwner = obj.getClient().getEmail().equals(email);

            if (!isOwner && !isAdmin) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok().body(obj);
        }

    }
