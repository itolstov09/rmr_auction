package kz.itolstov.demo.repository;

import kz.itolstov.demo.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetRepository extends JpaRepository<Bet, Long> {
}
