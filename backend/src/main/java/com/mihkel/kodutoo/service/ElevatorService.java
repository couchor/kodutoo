package com.mihkel.kodutoo.service;

import java.util.List;

import com.mihkel.kodutoo.model.Elevator;
import com.mihkel.kodutoo.model.ElevatorRequest;

public interface ElevatorService {
  public void handleExternalRequest(ElevatorRequest elevatorRequest, List<Elevator> elevators);

  public String handleInternalRequest(ElevatorRequest elevatorRequest, Elevator elevator);
}
