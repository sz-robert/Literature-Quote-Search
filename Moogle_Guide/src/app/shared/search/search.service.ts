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

  constructor(private http: HttpClient) { }

  getAll() {
    return this.http.get('assets/data/people.json');
  }
}


export class Result {
  id: number;
  name: string;
  
  constructor(obj?: any) {
    this.id = obj && Number(obj.id) || null;
    this.name = obj && obj.name || null;
  }
}



