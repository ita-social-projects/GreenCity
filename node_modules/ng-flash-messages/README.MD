# NgFlashMessages

It is an angular2+ flash message library with bootstrap styling and native angular animation [Demo](https://ameerthehacker.github.io/ng-flash-messages-demo/).

![Demo Gif](https://raw.githubusercontent.com/ameerthehacker/ng-flash-messages-demo/master/src/assets/images/demo.gif)

## Installation

To install this library, run:

```bash
$ npm install ng-flash-messages --save
```

## Importing the necessary module

Once you have installed the library with npm, you can import the library in your Angular `AppModule`:

```typescript
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';

// Import the library
import { NgFlashMessagesModule } from 'ng-flash-messages';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,

    // Specify the library as an import
    NgFlashMessagesModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

Once the library is imported, you can use ng-flash-message components anywhere in the app:
```xml
<h1>
  {{ title }}
</h1>
<ng-flash-message></ng-flash-message>
```

## Showing the flash message

In any of your component inject the NgFlashMessageService:

```typescript
import { Component, OnInit } from '@angular/core';

import { NgFlashMessageService } from 'ng-flash-messages';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';

  constructor(private ngFlashMessageService: NgFlashMessageService) {
  }
  ngOnInit() {}
}
```
Then call the showFlashMessage method in the service to show the flash message:

```typescript
import { Component, OnInit } from '@angular/core';

import { NgFlashMessageService } from 'ng-flash-messages';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';

  constructor(private ngFlashMessageService: NgFlashMessageService) {
  }
  ngOnInit() {
    this.ngFlashMessageService.showFlashMessage({
      // Array of messages each will be displayed in new line
      messages: ["Yah! i'm alive"], 
      // Whether the flash can be dismissed by the user defaults to false
      dismissible: true, 
      // Time after which the flash disappears defaults to 2000ms
      timeout: false,
      // Type of flash message, it defaults to info and success, warning, danger types can also be used
      type: 'danger'
    });
  }
}
```


## License

MIT © [Ameer Jhan](mailto:ameerjhanprof@gmail.com)
