import { Component, OnInit, Input } from '@angular/core';
import { /*Person*/ Quotes, SearchService } from '../shared';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})


export class SearchComponent implements OnInit {
   searchResults: Array< Quotes >;
  @Input() query: string;

  constructor(private searchService: SearchService) { }

  ngOnInit() {
  }

  search(): void {
    this.searchService.search(this.query).subscribe(
      //(data: any) => { this.searchResults = data; },
      (retriever: any) => { this.searchResults = retriever; },
      error => console.log(error)
    );
  }
  //change data to the name of the controller.
  
  
}
