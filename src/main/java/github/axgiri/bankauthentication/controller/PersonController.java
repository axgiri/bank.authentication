package github.axgiri.bankauthentication.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.axgiri.bankauthentication.dto.request.LoginRequest;
import github.axgiri.bankauthentication.dto.request.PersonRequest;
import github.axgiri.bankauthentication.dto.response.AuthResponse;
import github.axgiri.bankauthentication.dto.response.PersonResponse;
import github.axgiri.bankauthentication.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    private final PersonService service;
    
    @PostMapping("/signup")
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest personRequest) {
        log.debug("creating person: {}", personRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(personRequest));
    }

    @PostMapping("/async/signup")
    public ResponseEntity<Void> createAsync(@Valid @RequestBody PersonRequest personRequest) {
        log.debug("creating person: {}", personRequest);
        service.createAsync(personRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        AuthResponse authResponse = service.authenticate(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<PersonResponse> findById(@PathVariable Long id) {
        log.debug("finding person with id: {}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/findByPhoneNumber/{phoneNumber}")
    public ResponseEntity<PersonResponse> findByPhoneNumber(@PathVariable String phoneNumber) {
        log.debug("finding person with phone number: {}", phoneNumber);
        return ResponseEntity.ok(service.findByPhoneNumber(phoneNumber));
    }

    @PostMapping("/async/update/{id}")
    public ResponseEntity<CompletableFuture<PersonResponse>> update(@PathVariable Long id, @Valid @RequestBody PersonRequest personRequest) {
        log.debug("updating person with id: {}", id);
        return ResponseEntity.ok(service.update(id, personRequest));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("deleting person with id: {}", id);
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate(@RequestHeader("Authorization") String token){
        log.debug("validating token: {}", token);
        service.validateToken(token);
        return ResponseEntity.ok("validation successful");
    }

    @GetMapping("/getRoleName")
    public ResponseEntity<String> getRoleName(@RequestHeader("Authorization") String token){
        log.debug("getting role from token: {}", token);
        String role = service.getRole(token);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/getMyColleagues")
    public ResponseEntity<List<PersonResponse>> getColleagues(@RequestHeader("Authorization") String token) {
        log.debug("getting colleagues for token: {}", token);
        service.validateToken(token);
        return ResponseEntity.ok(service.getColleaguesAsync(token));
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody LoginRequest loginRequest, @RequestParam String oldPassword) {
        log.debug("updating password for phone number: {}", loginRequest.getPhoneNumber());
        service.updatePasswordAsync(loginRequest, oldPassword);
        return ResponseEntity.ok().build();
    }
}