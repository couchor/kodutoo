package com.mihkel.kodutoo.model;

import lombok.Data;

@Data
public class Floor {
  private int floorNumber;

  public Floor(int floorNumber) {
    this.floorNumber = floorNumber;
  }
}
