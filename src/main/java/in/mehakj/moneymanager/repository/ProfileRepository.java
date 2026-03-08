package in.mehakj.moneymanager.repository;

import in.mehakj.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    // select * from tbl_profiles where email=?
    Optional<ProfileEntity> findByEmail(String email);

    // select * from tbl_profiles where activation_token=?
    Optional<ProfileEntity> findByActivationToken(String token);

    boolean existsByEmail(String email);
}