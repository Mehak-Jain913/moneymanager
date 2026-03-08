package in.mehakj.moneymanager.controller;

import in.mehakj.moneymanager.dto.ProfileDTO;
import in.mehakj.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    // Register API
    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    // Activation API
    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token){
        profileService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully");
    }
}