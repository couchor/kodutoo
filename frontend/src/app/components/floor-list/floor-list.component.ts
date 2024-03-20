import { Component, OnInit } from '@angular/core';
import { Floor } from '../../common/floor';
import { HttpClient } from '@angular/common/http';
import { Elevator } from '../../common/elevator';
import { ElevatorRequest } from '../../common/elevator-request';
import { WebSocketService } from '../../services/web-socket-service.service';
import { Direction } from '../../common/elevator-request';
import { Direction as ElevatorDirection } from '../../common/elevator';
@Component({
  selector: 'app-floor-list',
  templateUrl: './floor-list.component.html',
  styleUrl: './floor-list.component.css',
})
export class FloorListComponent implements OnInit{
  floors: Floor[] = [];
  elevator1: Elevator = new Elevator(1, 0, ElevatorDirection.NONE, false, []);
  elevator2: Elevator = new Elevator(2, 0, ElevatorDirection.NONE, false, []);
  
  constructor(private httpClient: HttpClient, public webSocketService: WebSocketService) {}

  Direction = Direction;

  // Init the component by getting the floors and elevators
  ngOnInit(): void {
    this.getFloors();
    // Subscribe to the websocket to get the up to date elevators at all times
    this.webSocketService.getElevator('1').subscribe((elevator: Elevator) => {
      this.elevator1 = elevator;
    });
    this.webSocketService.getElevator('2').subscribe((elevator: Elevator) => {
      this.elevator2 = elevator;
    });
  }

  // Send a request to the server to move the elevator to the floor
  sendRequest(targetFloor: number, direction: Direction, external: boolean) {
    this.webSocketService.sendRequest(new ElevatorRequest(targetFloor, direction, external));
  }

  // Check if the elevator has a request with external = true and same targetFloor as floor
  hasActiveCall(floor: number, direction: Direction): boolean {
    return this.elevator1.queuedRequests.some(request => request.targetFloor === floor && Direction[request.direction].toString() === direction.toString() && request.external) ||
      this.elevator2.queuedRequests.some(request => request.targetFloor === floor && Direction[request.direction].toString() === direction.toString() && request.external);
  }



  // Get the floors from the server
  getFloors() {
    this.httpClient.get<Floor[]>('http://localhost:8085/api/floors/all').subscribe(
      data => {
        this.floors = data;
      }
    );
  }
}