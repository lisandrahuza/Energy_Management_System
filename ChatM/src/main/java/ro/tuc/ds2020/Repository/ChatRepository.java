package ro.tuc.ds2020.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.entities.Chat;

import java.util.List;
import java.util.UUID;
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("SELECT sender FROM Chat WHERE receiver=:receiver")
    List<UUID> getNecitite(@Param("receiver") UUID receiver);

    @Query("SELECT content FROM Chat WHERE receiver=:receiver AND sender=:sender")
    List<String> getMesaje(@Param("receiver")UUID receiver,@Param("sender") UUID sender);

    @Modifying
    @Transactional
    @Query("DELETE FROM Chat c WHERE c.receiver = :receiver AND c.sender = :sender")
    void deleteByReceiverAndSender(@Param("receiver") UUID receiver, @Param("sender") UUID sender);



}
