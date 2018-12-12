import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';

import { IToDoList } from 'app/shared/model/to-do-list.model';
import { ToDoListService } from './to-do-list.service';

@Component({
    selector: 'jhi-to-do-list-update',
    templateUrl: './to-do-list-update.component.html'
})
export class ToDoListUpdateComponent implements OnInit {
    toDoList: IToDoList;
    isSaving: boolean;
    dueDateDp: any;

    constructor(private toDoListService: ToDoListService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ toDoList }) => {
            this.toDoList = toDoList;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.toDoList.id !== undefined) {
            this.subscribeToSaveResponse(this.toDoListService.update(this.toDoList));
        } else {
            this.subscribeToSaveResponse(this.toDoListService.create(this.toDoList));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IToDoList>>) {
        result.subscribe((res: HttpResponse<IToDoList>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
