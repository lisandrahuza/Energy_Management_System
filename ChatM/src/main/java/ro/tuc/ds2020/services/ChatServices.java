package ro.tuc.ds2020.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import ro.tuc.ds2020.entities.Chat;
import ro.tuc.ds2020.repositories.ChatRepository;
@Service
public class ChatServices {
    private ChatRepository chatRepository;

    @Autowired
    public ChatServices(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void addMessage(UUID clientId, UUID userId, String content) {
        Chat chat=new Chat(clientId,userId,content);
        chatRepository.save(chat);
    }

    public List<UUID> getNecitite(UUID receiver) {
        return chatRepository.getNecitite(receiver);
    }

    public List<String> getMesaje(UUID receiver, UUID sender) {
        List<String> result=chatRepository.getMesaje(receiver,sender);
        chatRepository.deleteByReceiverAndSender(receiver, sender);
        return result;
    }


    public void delete(UUID receiver,UUID sender) {
     chatRepository.deleteByReceiverAndSender(receiver,sender);
    }
}
