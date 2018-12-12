import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IToDoList } from 'app/shared/model/to-do-list.model';

@Component({
    selector: 'jhi-to-do-list-detail',
    templateUrl: './to-do-list-detail.component.html'
})
export class ToDoListDetailComponent implements OnInit {
    toDoList: IToDoList;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ toDoList }) => {
            this.toDoList = toDoList;
        });
    }

    previousState() {
        window.history.back();
    }
}
