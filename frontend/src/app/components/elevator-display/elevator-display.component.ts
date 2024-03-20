import { Component, Input } from '@angular/core';
import { Elevator } from '../../common/elevator';

@Component({
  selector: 'app-elevator-display',
  templateUrl: './elevator-display.component.html',
  styleUrl: './elevator-display.component.css'
})
export class ElevatorDisplayComponent {
  @Input() elevator!: Elevator;

  @Input() floorNumber!: number;

  // Get the time in seconds for the elevator to reach the floor
  getTimeToFloor(elevator: Elevator, floorNumber: number): number {
    // Check if the the elevator has queuedRequests with the same targetFloor as floorNumber, if it doesn't then return 0
    if (!elevator.queuedRequests.some(request => request.targetFloor === floorNumber)) {
      return 0;
    }
    // Go through the queuedRequests. Take note of the direction of the elevator and the direction of the request. Starting with the initial direction, add 3 seconds for each request. If the direction changes, then add 3 seconds times the difference between the curren requests targetFloor and the previous requests targetFloor
    let currentDirection = elevator.queuedRequests[0].direction;
    let timeRemaining = 0;
    for (let i = 0; i < elevator.queuedRequests.length; i++) {
      const request = elevator.queuedRequests[i];
      
      if (i === 0) {
        timeRemaining += Math.abs(elevator.currentFloor - request.targetFloor) * 3;
        currentDirection = request.direction;
      } else
      if (request.direction === currentDirection) {
        timeRemaining += 3;
      } else {
        timeRemaining += Math.abs(elevator.queuedRequests[i - 1].targetFloor - request.targetFloor) * 3;
        currentDirection = request.direction;
      }
      // If floorNumber is same as request targetFloor, then return the time remaining
      if (floorNumber === request.targetFloor) {
        return timeRemaining;
      }
    }

    return timeRemaining;
  }
}
