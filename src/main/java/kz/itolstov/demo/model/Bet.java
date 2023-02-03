package kz.itolstov.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @NonNull
    private User author;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @NonNull
    private Item item;

    @NonNull
    private Integer amount;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
