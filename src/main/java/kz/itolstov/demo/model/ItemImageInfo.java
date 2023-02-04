package kz.itolstov.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor @Getter @Setter
public class ItemImageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String filePath;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}

