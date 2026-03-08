package in.mehakj.moneymanager.service;

import in.mehakj.moneymanager.dto.ProfileDTO;
import in.mehakj.moneymanager.entity.ProfileEntity;
import in.mehakj.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;

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
                .password(profileDTO.getPassword())
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
}
//uuid->Universal Unigue Identifier
