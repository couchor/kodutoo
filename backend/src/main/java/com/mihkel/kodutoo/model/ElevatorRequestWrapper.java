package com.mihkel.kodutoo.model;

import lombok.Data;

// Wrapper class to get JSON data from the frontend for internal requests
@Data
public class ElevatorRequestWrapper {
  private ElevatorRequest elevatorRequest;
  private int elevatorId;
}
