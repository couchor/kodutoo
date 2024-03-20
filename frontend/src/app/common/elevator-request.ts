export class ElevatorRequest {
  constructor(public targetFloor: number, public direction: Direction, public external: boolean) {}
}

export enum Direction {
  UP,
  DOWN
}
