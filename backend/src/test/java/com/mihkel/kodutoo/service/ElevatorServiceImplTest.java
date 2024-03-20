package com.mihkel.kodutoo.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mihkel.kodutoo.model.Direction;
import com.mihkel.kodutoo.model.Elevator;
import com.mihkel.kodutoo.model.ElevatorRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

public class ElevatorServiceImplTest {

  @Mock
  private MessageService messageService;

  // Inject @Mock annotated fields into the @InjectMocks annotated field
  @InjectMocks
  private ElevatorServiceImpl elevatorService;

  // Needed to mock messageService
  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  // Test the handleInternalRequest method
  // When the elevator is moving in the same direction as the request and new
  // request is higher than the current floor. Pressing 7 when on the 5th floor
  @Test
  void givenSameDirectionRequest_whenHandleInternalRequest_thenAddToQueue() {
    // Mock the messageService - do nothing since it doesn't matter in this test.
    // With @InjectMocks, this test gets elevatorService automatically
    doNothing().when(messageService).sendElevator(any(Elevator.class));

    // ElevatorServiceImpl elevatorService = new ElevatorServiceImpl();

    Elevator elevator = new Elevator(1);
    elevator.setCurrentFloor(5);
    elevator.setDirection(Direction.UP);
    ElevatorRequest request = new ElevatorRequest(7, Direction.UP, false);
    String result = elevatorService.handleInternalRequest(request, elevator);
    assertEquals("Added to end", result);
  }

  // When request is the same floor as the elevator. Pressing 1 when on the 1st
  // floor.
  @Test
  void givenSameFloorRequest_whenHandleInternalRequest_thenDoNothing() {
    ElevatorServiceImpl elevatorService = new ElevatorServiceImpl();
    Elevator elevator = new Elevator(1);
    elevator.setCurrentFloor(5);
    ElevatorRequest request = new ElevatorRequest(5, Direction.UP, false);
    String result = elevatorService.handleInternalRequest(request, elevator);
    assertEquals("Do nothing", result);
  }

  // When the elevator is moving in the same direction as the request and new
  // request is on the way to the current destination. Pressing 5 when on elevator
  // is on its way from the 3rd floor to the 7th.
  @Test
  void givenOnTheWayFloor_whenHandleInternalRequest_thenAddToQueue() {
    // Mock the messageService - do nothing since it doesn't matter in this test.
    // With @InjectMocks, this test gets elevatorService automatically
    doNothing().when(messageService).sendElevator(any(Elevator.class));

    Elevator elevator = new Elevator(1);
    elevator.setCurrentFloor(3);
    elevator.setDirection(Direction.UP);
    // Create a list of queued requests
    List<ElevatorRequest> queuedRequests = new ArrayList<>();
    ElevatorRequest existingRequest = new ElevatorRequest(7, Direction.UP, false);
    queuedRequests.add(existingRequest);
    elevator.setQueuedRequests(queuedRequests);
    ElevatorRequest request = new ElevatorRequest(5, Direction.UP, false);

    String result = elevatorService.handleInternalRequest(request, elevator);
    assertEquals("Added to middle", result);
  }

  // When the elevator is moving in the opposite direction as the request.
  // Pressing 7 when on the 5th floor and moving downwards
  @Test
  void givenOppositeDirectionRequest_whenHandleInternalRequest_thenChangeDirection() {
    // Mock the messageService - do nothing since it doesn't matter in this test.
    // With @InjectMocks, this test gets elevatorService automatically
    doNothing().when(messageService).sendElevator(any(Elevator.class));

    Elevator elevator = new Elevator(1);
    elevator.setCurrentFloor(5);
    elevator.setDirection(Direction.DOWN);
    ElevatorRequest request = new ElevatorRequest(7, Direction.UP, false);
    String result = elevatorService.handleInternalRequest(request, elevator);
    assertEquals("Did nothing", result);
  }

}