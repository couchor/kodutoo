package com.mihkel.kodutoo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mihkel.kodutoo.model.Elevator;
import com.mihkel.kodutoo.service.ElevatorService;

import jakarta.annotation.PostConstruct;

import com.mihkel.kodutoo.model.ElevatorRequest;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/elevators")
public class ElevatorController {

  private static List<Elevator> elevators = new ArrayList<>();

  @Autowired
  private ElevatorService elevatorService;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  // Wait for dependency injection to be done before initializing the elevators
  @PostConstruct
  public void init() {
    // Initialize the 2 elevators
    for (int i = 0; i < 2; i++) {
      try {
        // Try to get the elevator from Redis if it exists. Get as object initially and
        // map
        // over
        // values to get the Elevator class
        Object objectFromRedis = redisTemplate.opsForValue().get("elevator" + (i + 1));
        if (objectFromRedis instanceof Map) {
          Elevator elevatorFromRedis = objectMapper.convertValue(objectFromRedis, Elevator.class);
          elevators.add(elevatorFromRedis);
        } else {
          elevators.add(new Elevator(i + 1));
        }
      } catch (RedisConnectionFailureException e) {
        // If Redis is not running, just initialize the elevator normally
        elevators.add(new Elevator(i + 1));
      }
    }
  }

  // Get a single elevator
  public Elevator getElevator(String id) {
    return elevators.get(Integer.parseInt(id) - 1);
  }

  // Next ones were HTTP initially, but changed to Websocket so I wouldn't have to
  // assign new
  // values on front end based on return object after every request and rather
  // just subscribe to elevator
  // updates.

  // Handle external requests to the elevator - load balance between the elevators
  public void handleExternalRequest(ElevatorRequest elevatorRequest) {
    elevatorService.handleExternalRequest(elevatorRequest, elevators);
  }

  // Handle internal requests from the elevator
  public void handleInternalRequest(ElevatorRequest elevatorRequest, int elevatorId) {
    elevatorService.handleInternalRequest(elevatorRequest, elevators.get(elevatorId - 1));
  }

}
