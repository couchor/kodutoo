import { Component, Input } from '@angular/core';
import { Elevator } from '../../common/elevator';
import { Direction, ElevatorRequest } from '../../common/elevator-request';
import { Floor } from '../../common/floor';
import { WebSocketService } from '../../services/web-socket-service.service';

@Component({
  selector: 'app-elevator',
  templateUrl: './elevator.component.html',
  styleUrl: './elevator.component.css'
})
export class ElevatorComponent {
  Direction = Direction;
  @Input() floorNumber!: number;

  @Input() floors!: Floor[];

  @Input() elevator!: Elevator;


  constructor(public webSocketService: WebSocketService) { }

  // Send a request to the server to move the elevator to the floor
  sendInternalRequest(targetFloor: number, direction: Direction) {
    // Only send the request if the elevator doesn't have a request with the same targetFloor
    if (!this.hasActiveCall(targetFloor, direction)) {
      this.webSocketService.sendInternalRequest(new ElevatorRequest(targetFloor, direction, false), this.elevator.id);
    }
  }

  // Check if the elevator has a request with the same targetFloor as floorNumber
  hasActiveCall(floor: number, direction: Direction): boolean {
    return this.elevator.queuedRequests.some(request => request.targetFloor === floor && Direction[request.direction].toString() === direction.toString())
  }
}
