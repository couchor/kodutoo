package com.mihkel.kodutoo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Elevator implements Serializable {
  private int id;
  private int currentFloor;
  private Direction direction;
  private boolean doorOpen;
  private boolean activeRequest;

  private List<ElevatorRequest> queuedRequests = new ArrayList<>();

  // Initialize the elevators state.
  public Elevator(int id) {
    this.id = id;
    this.currentFloor = 0;
    this.direction = Direction.NONE;
    this.doorOpen = false;
    this.activeRequest = false;
  }

  public Elevator(Elevator elevator) {
    this.id = elevator.getId();
    this.currentFloor = elevator.getCurrentFloor();
    this.direction = elevator.getDirection();
    this.doorOpen = elevator.isDoorOpen();
    this.activeRequest = elevator.isActiveRequest();
    this.queuedRequests = elevator.getQueuedRequests();
  }

  // Check if elevator can take on the request
  public boolean isAvailable(ElevatorRequest request) {
    // If the elevator is not moving
    if (this.direction == Direction.NONE)
      return true;

    // If the elevator is moving in the same direction as the request and the
    // targetFloor is in the same direction
    if (this.direction == request.direction
        && ((this.direction == Direction.UP && this.currentFloor <= request.targetFloor)
            || (this.direction == Direction.DOWN && this.currentFloor >= request.targetFloor)))
      return true;

    return false;
  }
}
