package codeverse.com.web_be.service.MessageService;

import codeverse.com.web_be.entity.Message;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface IMessageService extends IGenericService<Message, Long> {
    List<Message> findBySenderId(Long senderId);
    List<Message> findByReceiverId(Long receiverId);
}