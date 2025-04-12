package codeverse.com.web_be.service.MessageService;

import codeverse.com.web_be.entity.Message;
import codeverse.com.web_be.repository.MessageRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends GenericServiceImpl<Message, Long> implements IMessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        super(messageRepository);
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Message> findBySenderId(Long senderId) {
        return messageRepository.findBySenderId(senderId);
    }

    @Override
    public List<Message> findByReceiverId(Long receiverId) {
        return messageRepository.findByReceiverId(receiverId);
    }
}