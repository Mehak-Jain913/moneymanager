package in.mehakj.moneymanager.controller;

import in.mehakj.moneymanager.dto.AuthDTO;
import in.mehakj.moneymanager.dto.ProfileDTO;
import in.mehakj.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){

        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token){

        profileService.activateAccount(token);

        return ResponseEntity.ok("Account activated successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){

        try {

            Map<String,Object> response =
                    profileService.authenticateAndGenerateToken(authDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e){

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}