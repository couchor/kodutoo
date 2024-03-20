import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FloorListComponent } from './components/floor-list/floor-list.component';
import { HttpClientModule } from '@angular/common/http';
import { ElevatorDisplayComponent } from './components/elevator-display/elevator-display.component';
import { ElevatorComponent } from './components/elevator/elevator.component';

@NgModule({
  declarations: [
    AppComponent,
    FloorListComponent,
    ElevatorDisplayComponent,
    ElevatorComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  
})
export class AppModule { }
