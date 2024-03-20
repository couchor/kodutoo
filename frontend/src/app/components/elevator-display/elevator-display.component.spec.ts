import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElevatorDisplayComponent } from './elevator-display.component';

describe('ElevatorDisplayComponent', () => {
  let component: ElevatorDisplayComponent;
  let fixture: ComponentFixture<ElevatorDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ElevatorDisplayComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ElevatorDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
