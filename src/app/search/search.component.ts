import { Component, OnInit, Input } from '@angular/core';
import { /*Person*/ Quotes, SearchService } from '../shared';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})


export class SearchComponent implements OnInit {
  //searchResults: Array< Person >;
  searchResults: Array< Quotes >;
  @Input() query: string;

  constructor(private searchService: SearchService) { }

  ngOnInit() {
  }

  search(): void {
    this.searchService.search(this.query).subscribe(
      (data: any) => { this.searchResults = data; },
      //(retrieverCont: any) => { this.searchResults = retrieverCont; },
      error => console.log(error)
    );
  }
  //change data to the name of the controller.
  
  
}
