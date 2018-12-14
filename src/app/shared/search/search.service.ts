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
    //return this.http.get('assets/data/people.json');
    //return this.http.get('assets/data/retriever.json');
    return this.http.get('assets/data/retriever2.json');
  }
}

/*export class Address {
  street: string;
  city: string;
  state: string;
  zip: string;

  constructor(obj?: any) {
    this.street = obj && obj.street || null;
    this.city = obj && obj.city || null;
    this.state = obj && obj.state || null;
    this.zip = obj && obj.zip || null;
  }
}*/

//export class Person {
  export class Quotes {
  //id: number;
  //result: string;
  quote: string;
  citation: string;
  //name: string;
  //phone: string;
  //address: Address;

  constructor(obj?: any) {
    //this.id = obj && Number(obj.id) || null;
    //this.result = obj && obj.result || null;
    this.quote = obj && obj.quote || null;
    this.citation = obj && obj.citation || null;
    //this.name = obj && obj.name || null;
    //this.phone = obj && obj.phone || null;
    //this.address = obj && obj.address || null;
  }
}




