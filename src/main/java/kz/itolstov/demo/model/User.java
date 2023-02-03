package kz.itolstov.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @RequiredArgsConstructor @NoArgsConstructor @ToString
@Table(name = "auction_users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NonNull
    private String email;

    @NonNull
    private String password;

}
