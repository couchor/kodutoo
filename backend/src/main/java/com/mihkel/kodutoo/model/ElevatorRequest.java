package com.mihkel.kodutoo.model;

import lombok.Data;

@Data
public class ElevatorRequest {
  public Direction direction;
  public int targetFloor;
  public boolean external;

  public ElevatorRequest(int targetFloor, Direction direction, boolean external) {
    this.targetFloor = targetFloor;
    this.direction = direction;
    this.external = external;
  }
}
