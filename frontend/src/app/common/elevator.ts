import { ElevatorRequest } from "./elevator-request";

export class Elevator {
  constructor(public id: number, public currentFloor: number, public direction: Direction, public doorOpen: boolean, public queuedRequests: Array<ElevatorRequest>) {}
}

export enum Direction {
  UP,
  DOWN,
  NONE
}
