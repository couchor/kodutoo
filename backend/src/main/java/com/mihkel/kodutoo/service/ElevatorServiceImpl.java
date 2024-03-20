package com.mihkel.kodutoo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mihkel.kodutoo.model.Direction;
import com.mihkel.kodutoo.model.Elevator;
import com.mihkel.kodutoo.model.ElevatorRequest;

@Service
public class ElevatorServiceImpl implements ElevatorService {

  @Autowired
  private MessageService messageService;

  // Handle external requests to the elevator - load balance between the elevators
  @Override
  public void handleExternalRequest(ElevatorRequest elevatorRequest, List<Elevator> elevators) {
    // Check which elevators are available to receive the request - no active
    // requests or moving in the same direction already
    List<Elevator> availableElevators = new ArrayList<>();
    for (Elevator elevator : elevators) {
      // Update
      if (elevator.isAvailable(elevatorRequest)) {
        availableElevators.add(elevator);
      }
    }

    Elevator targetElevator = elevators.get(0);
    int closestDistance = Integer.MAX_VALUE;
    int minQueuedRequests = Integer.MAX_VALUE;
    // If there are no available elevators, then get the elevator whose last
    // queuedRequests targetFloor is the closest to the elevatorRequests
    // targetFloor. If distance is the same, then get the elevator with less
    // queuedRequests
    if (availableElevators.isEmpty()) {

      for (Elevator elevator : elevators) {
        int distance = Math
            .abs(elevator.getQueuedRequests().get(elevator.getQueuedRequests().size() - 1).getTargetFloor()
                - elevatorRequest.getTargetFloor());
        if (distance < closestDistance) {
          targetElevator = elevator;
          closestDistance = distance;
          minQueuedRequests = elevator.getQueuedRequests().size();
        } else if (distance == closestDistance) {
          if (elevator.getQueuedRequests().size() < minQueuedRequests) {
            targetElevator = elevator;
            closestDistance = distance;
            minQueuedRequests = elevator.getQueuedRequests().size();
          }
        }
      }
    }
    // If there are available elevators
    else {
      // If only one available elevator, then set it as the targetElevator
      if (availableElevators.size() == 1) {
        targetElevator = availableElevators.get(0);
      }

      // If there is more than one available elevator, then get the elevator which has
      // the closest currentFloor to the elevatorRequest targetFloor
      else {
        closestDistance = Integer.MAX_VALUE;
        minQueuedRequests = Integer.MAX_VALUE;

        for (Elevator elevator : availableElevators) {
          int distance = Math.abs(elevator.getCurrentFloor() - elevatorRequest.getTargetFloor());
          if (distance < closestDistance) {
            targetElevator = elevator;
            closestDistance = distance;
            minQueuedRequests = elevator.getQueuedRequests().size();
          } else if (distance == closestDistance) {
            if (elevator.getQueuedRequests().size() < minQueuedRequests) {
              targetElevator = elevator;
              closestDistance = distance;
              minQueuedRequests = elevator.getQueuedRequests().size();
            }
          }
        }
      }
    }

    // Target floor is the same as the current floor and
    if (elevatorRequest.getTargetFloor() == targetElevator.getCurrentFloor()) {

      // If request on floor where elevator currently MOVING, then add
      // the request to the elevator's queuedRequests list and don't open the door
      if (targetElevator.getDirection() != Direction.NONE) {
        addRequest(targetElevator, targetElevator.getQueuedRequests().size(), elevatorRequest);
        return;
      }

      targetElevator.setDoorOpen(true);
      targetElevator.setDirection(Direction.NONE);
      messageService.sendElevator(targetElevator);
      // Wait for 3 seconds before closing the door
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // If door is still open, close it
      if (targetElevator.isDoorOpen()) {
        targetElevator.setDoorOpen(false);
        messageService.sendElevator(targetElevator);
      }
      // }
    }
    // Target floor is not the same as the current floor
    else {
      // Check if the elevator is available to receive request
      // if (targetElevator.isAvailable(elevatorRequest)) {
      // no direction, then set it
      if (targetElevator.getDirection() == Direction.NONE) {
        targetElevator.setDirection(
            elevatorRequest.getTargetFloor() > targetElevator.getCurrentFloor() ? Direction.UP : Direction.DOWN);
      }

      int index = getListIndex(elevatorRequest, targetElevator);

      // If the index is not -1, then add the request to the index. Otherwise, add the
      // request to the end of the list.
      if (index != -1) {
        addRequest(targetElevator, index, elevatorRequest);
      } else {
        addRequest(targetElevator, targetElevator.getQueuedRequests().size(), elevatorRequest);
      }

    }
    messageService.sendElevator(targetElevator);
    return;
  }

  // Handle internal requests from the elevator. Return string for testing
  // purposes
  @Override
  public String handleInternalRequest(ElevatorRequest elevatorRequest, Elevator elevator) {
    // If the elevator is not moving
    if (elevator.getDirection() == Direction.NONE) {
      // If the elevator is on the same floor as the request
      if (elevator.getCurrentFloor() == elevatorRequest.getTargetFloor()) {
        // Do nothing
        return "Do nothing";
      }
      // If the elevator is not moving and the request is on a different floor, then
      // set the
      // direction of the elevator to the direction of the request
      elevator
          .setDirection(elevatorRequest.getTargetFloor() > elevator.getCurrentFloor() ? Direction.UP : Direction.DOWN);

      elevator.setDoorOpen(false);
      // Add the request to the elevator's queuedRequests list
      addRequest(elevator, elevator.getQueuedRequests().size(), elevatorRequest);
    }

    // If the elevator is moving in the same direction as the request
    if (elevator.getDirection() == elevatorRequest.getDirection()) {
      int index = getListIndex(elevatorRequest, elevator);

      // If the index is not -1, then add the request to the index. Otherwise, add the
      // request to the end of the list.
      elevator.setDoorOpen(false);
      if (index != -1) {
        addRequest(elevator, index, elevatorRequest);
        return "Added to middle";
      } else {
        addRequest(elevator, elevator.getQueuedRequests().size(), elevatorRequest);
        return "Added to end";
      }

    }
    return "Did nothing";
  }

  // Logic to deal with requests and update the elevator
  public void dealWithRequests(Elevator elevator) {
    messageService.sendElevator(elevator);

    if (!elevator.isActiveRequest()) {
      // If the elevator has no active requests then continue
      if (elevator.getQueuedRequests().isEmpty()) {
        return;
      }
      ElevatorRequest nextRequest = elevator.getQueuedRequests().get(0);

      // If the elevator is on the same floor as the request
      if (elevator.getCurrentFloor() == nextRequest.targetFloor) {
        elevator.getQueuedRequests().remove(0);

        // If there are no more requests, set the direction to none
        if (elevator.getQueuedRequests().isEmpty()) {
          elevator.setDirection(Direction.NONE);
        }

        openAndCloseDoor(elevator);
        setActiveRequest(elevator, 0, -1);
        return;
      }

      // Check which direction the elevator should move and change floor
      if (nextRequest.targetFloor > elevator.getCurrentFloor()) {
        elevator.setDirection(Direction.UP);
        messageService.sendElevator(elevator);
        setActiveRequest(elevator, 3000, elevator.getCurrentFloor() + 1);
        return;
      } else {
        elevator.setDirection(Direction.DOWN);
        ;
        messageService.sendElevator(elevator);
        setActiveRequest(elevator, 3000, elevator.getCurrentFloor() - 1);
        return;
      }

    }
  }

  // Add a request to the elevator at index - and then run function to deal with
  public void addRequest(Elevator elevator, int index, ElevatorRequest request) {
    elevator.getQueuedRequests().add(index, request);

    dealWithRequests(elevator);
  }

  // Set the elevator to active for a certain amount of time - mostly for the 3
  // seconds to move between floors. Delay change of currentFloor as well to deal
  // with other issues.
  public void setActiveRequest(Elevator elevator, int delay, int newFloor) {
    // Delay the elevator for delay milliseconds
    elevator.setActiveRequest(true);
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // If newFloor is not -1, then set the current floor to newFloor
    if (newFloor != -1) {
      elevator.setCurrentFloor(newFloor);
    }
    elevator.setActiveRequest(false);
    messageService.sendElevator(elevator);
    dealWithRequests(elevator);
  }

  // Open the door and close it automatically after 2 seconds if it's still open.
  public void openAndCloseDoor(Elevator elevator) {
    elevator.setDoorOpen(true);
    messageService.sendElevator(elevator);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (elevator.isDoorOpen()) {
      elevator.setDoorOpen(false);
      messageService.sendElevator(elevator);
    }
  }

  // Get the index of the request in queuedRequests list where the request
  // direction is the same as the elevator direction and the targetFloor is
  // greater than elevator.currentFloor if the direction is UP and the targetFloor
  // is less than elevator.currentFloor if the direction is DOWN
  public int getListIndex(ElevatorRequest elevatorRequest, Elevator elevator) {
    int index = -1;
    for (int i = 0; i < elevator.getQueuedRequests().size(); i++) {
      ElevatorRequest nextRequest = elevator.getQueuedRequests().get(i);
      if (nextRequest.getDirection() == elevatorRequest.getDirection()
          && (nextRequest.getTargetFloor() > elevatorRequest.getTargetFloor()
              && elevatorRequest.getDirection() == Direction.UP)
          || (nextRequest.getTargetFloor() < elevatorRequest.getTargetFloor()
              && elevatorRequest.getDirection() == Direction.DOWN)) {
        index = i;
        break;
      }
    }
    return index;
  }
}
