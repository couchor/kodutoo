package com.mihkel.kodutoo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.mihkel.kodutoo.model.Elevator;
import com.mihkel.kodutoo.model.ElevatorRequest;
import com.mihkel.kodutoo.model.ElevatorRequestWrapper;

@Controller
public class WebSocketController {
  // Bring in the ElevatorController to get access to the elevators list
  @Autowired
  private ElevatorController elevatorController;

  // Get a single elevator
  @MessageMapping("/elevator/{id}")
  @SendTo("/topic/elevator/{id}")
  public Elevator getElevator(@DestinationVariable String id) {
    return elevatorController.getElevator(id);
  }

  // Add a request to the elevators list
  @MessageMapping("/addExternalRequest")
  public void addRequest(ElevatorRequest elevatorRequest) {
    elevatorController.handleExternalRequest(elevatorRequest);
  }

  // Add a request to the elevators list
  @MessageMapping("/addInternalRequest")
  public void addRequest(@Payload ElevatorRequestWrapper elevatorRequestWrapper) {
    elevatorController.handleInternalRequest(elevatorRequestWrapper.getElevatorRequest(),
        elevatorRequestWrapper.getElevatorId());
  }
}
