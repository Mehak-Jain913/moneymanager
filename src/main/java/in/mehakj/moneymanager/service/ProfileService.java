package in.mehakj.moneymanager.service;

import in.mehakj.moneymanager.dto.AuthDTO;
import in.mehakj.moneymanager.dto.ProfileDTO;
import in.mehakj.moneymanager.entity.ProfileEntity;
import in.mehakj.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Map<String,Object> authenticateAndGenerateToken(AuthDTO authDTO){

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDTO.getEmail(),
                            authDTO.getPassword()
                    )
            );

            return Map.of(
                    "token","jwt-token-placeholder",
                    "user",getPublicProfile(authDTO.getEmail())
            );

        } catch (Exception e){
            throw new RuntimeException("Invalid email or password");
        }
    }

    public ProfileDTO registerProfile(ProfileDTO profileDTO){

        if(profileRepository.existsByEmail(profileDTO.getEmail())){
            throw new RuntimeException("Email already registered");
        }

        ProfileEntity newProfile = toEntity(profileDTO);

        newProfile.setActivationToken(UUID.randomUUID().toString());

        newProfile = profileRepository.save(newProfile);

        String activationLink =
                "http://localhost:8080/api/v1.0/profile/activate?token="
                        + newProfile.getActivationToken();

        String subject = "Activate your Money Manager account";

        String body = "Click the following link to activate your account:\n" + activationLink;

        emailService.sendEmail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);
    }

    public void activateAccount(String token){

        ProfileEntity profile = profileRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        profile.setIsActive(true);
        profile.setActivationToken(null);

        profileRepository.save(profile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .password(null)
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public ProfileEntity getCurrentProfile(){

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();

        return profileRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Profile not found with email: " + email));
    }

    public ProfileDTO getPublicProfile(String email){

        ProfileEntity currentUser = profileRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Profile not found with email: " + email));

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }
}
//uuid->Universal Unigue Identifier
