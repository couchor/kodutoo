package com.mihkel.kodutoo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mihkel.kodutoo.model.Floor;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/floors")
public class FloorController {

  private static List<Floor> floors = new ArrayList<>();

  static {
    // Initialize the 9 floors
    for (int i = 8; i >= 0; i--) {
      floors.add(new Floor(i));
    }
  }

  @GetMapping("/all")
  public List<Floor> getFloors() {
    return floors;
  }
}
