import { Injectable } from '@angular/core';
import { Client, Stomp } from '@stomp/stompjs';
import SockJS from "sockjs-client/dist/sockjs"
import { BehaviorSubject } from 'rxjs';
import { Elevator } from '../common/elevator';
import { ElevatorRequest } from '../common/elevator-request';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  // Define the stompClient and the elevatorListSubject
  private stompClient: Client = new Client;
  // Create elevator1Subject
  private elevator1Subject: BehaviorSubject<Elevator> = new BehaviorSubject<Elevator>(new Elevator(1, 0, 0, false, []));
  private elevator2Subject: BehaviorSubject<Elevator> = new BehaviorSubject<Elevator>(new Elevator(2, 0, 0, false, []));
  constructor() { 
    this.connect();
  }

  // Connect to the server and subscribe to the topic
  connect(): void {
    const socket = new SockJS('http://localhost:8085/ws');
    this.stompClient = Stomp.over(socket);
    // Disable debug messages
    this.stompClient.debug = () => {};

    
    this.stompClient.onConnect = (frame) => {
      // Fetch individual elevators
      this.stompClient.publish({destination: `/app/elevator/1`});
      this.subscribeToElevatorUpdates('1', this.elevator1Subject);
      this.stompClient.publish({destination: `/app/elevator/2`});
      this.subscribeToElevatorUpdates('2', this.elevator2Subject);
    };

    // Handle connection errors - try to reconnect every 5 seconds
    this.stompClient.onStompError = (frame) => {
      console.error(`Error in connection: ${frame.headers['message']}`);
      // Implement reconnection logic here
      setTimeout(() => this.connect(), 5000);
    }

    // Handle initial connection refusal - try to reconnect every 5 seconds
    this.stompClient.onWebSocketClose = (event) => {
      console.error(`WebSocket connection closed with code: ${event.code}`);
      setTimeout(() => this.connect(), 5000);
    };


    this.stompClient.activate();
  }

  // Subscribe to the topic to get a single elevator
  private subscribeToElevatorUpdates(id: string, elevatorSubject: BehaviorSubject<Elevator>): void {
    this.stompClient.subscribe(`/topic/elevator/${id}`, (message) => {
      elevatorSubject.next(JSON.parse(message.body));
    });
  }

  // Send an external request to the server to move the elevator. If handled, then server will send a websocket message to update the elevator
  sendRequest(elevatorRequest: ElevatorRequest) {
    this.stompClient.publish({destination: "/app/addExternalRequest", body: JSON.stringify(elevatorRequest)});
  }

  // Send an internal request to the server to move the elevator. If handled, then server will send a websocket message to update the elevator
  sendInternalRequest(elevatorRequest: ElevatorRequest, elevatorId: number) {
    this.stompClient.publish({destination: "/app/addInternalRequest", body: JSON.stringify({elevatorRequest, elevatorId})});
  }

  // Get a single elevator
  getElevator(id: string): BehaviorSubject<Elevator> {
    return id === '1' ? this.elevator1Subject : this.elevator2Subject;
  }
}