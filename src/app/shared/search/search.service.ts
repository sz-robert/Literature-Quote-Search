import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class SearchService {

  search(q: string): Observable<any> {
    if (!q || q === '*') {
      q = '';
    } else {
      q = q.toLowerCase();
    }

    return this.getAll().pipe(
      map((data: any) => data
        .filter(item => JSON.stringify(item).toLowerCase().includes(q)))
    );
  }

  constructor(private http: HttpClient) {
  }


  //This points the search to the json file 
  getAll() {
   return this.http.get('assets/data/retriever2.json');
    //return this.http.get('src/app/retriever.js');
    /*point this section to the controller, this will be 
    dependent on how we set it up on the server.*/
   
  }
}

export class Quotes {
  quote: string;
  citation: string;

  constructor(obj?: any) {
    this.quote = obj && obj.quote || null;
    this.citation = obj && obj.citation || null;

  }
}




