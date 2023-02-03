package kz.itolstov.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @RequiredArgsConstructor @NoArgsConstructor @ToString
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @NonNull
    private User owner;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private Integer minimalPrice;

    private Integer currentPrice;

    @OneToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private Status status;

    private LocalDateTime auctionEndsAt;


    public enum Status {
        ACTIVE,
        DISCARD
    }
}
