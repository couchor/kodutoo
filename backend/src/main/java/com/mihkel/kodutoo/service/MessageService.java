package com.mihkel.kodutoo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.mihkel.kodutoo.model.Elevator;

@Service
public class MessageService {

  private final SimpMessagingTemplate simpMessagingTemplate;

  @Autowired
  public MessageService(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  // Send the elevator data to the frontend
  public void sendElevator(Elevator elevator) {
    // Save the elevator to Redis
    try {
      // Try to save to Redis
      redisTemplate.opsForValue().set("elevator" + elevator.getId(), elevator);
    } catch (RedisConnectionFailureException e) {
      // If Redis is not running, then do nothing
    }

    simpMessagingTemplate.convertAndSend("/topic/elevator/" + elevator.getId(), elevator);
  }
}