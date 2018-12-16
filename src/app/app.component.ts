import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']

})
export class AppComponent {
  title = 'Moogle!';

  //new
constructor(private http: HttpClient){
  
}
//new
ngOnInit(){
  let obs = this.http.get('http://api.github.com/users/MA2637')
  //let obs = this.http.get('http://ec2-34-210-26-240.us-west-2.compute.amazonaws.com:8080/retreiver')
  obs.subscribe(() => console.log('Got the response'));
}

}


