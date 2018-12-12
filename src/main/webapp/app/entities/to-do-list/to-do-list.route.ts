import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ToDoList } from 'app/shared/model/to-do-list.model';
import { ToDoListService } from './to-do-list.service';
import { ToDoListComponent } from './to-do-list.component';
import { ToDoListDetailComponent } from './to-do-list-detail.component';
import { ToDoListUpdateComponent } from './to-do-list-update.component';
import { ToDoListDeletePopupComponent } from './to-do-list-delete-dialog.component';
import { IToDoList } from 'app/shared/model/to-do-list.model';

@Injectable({ providedIn: 'root' })
export class ToDoListResolve implements Resolve<IToDoList> {
    constructor(private service: ToDoListService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ToDoList> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<ToDoList>) => response.ok),
                map((toDoList: HttpResponse<ToDoList>) => toDoList.body)
            );
        }
        return of(new ToDoList());
    }
}

export const toDoListRoute: Routes = [
    {
        path: 'to-do-list',
        component: ToDoListComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'ToDoLists'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'to-do-list/:id/view',
        component: ToDoListDetailComponent,
        resolve: {
            toDoList: ToDoListResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ToDoLists'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'to-do-list/new',
        component: ToDoListUpdateComponent,
        resolve: {
            toDoList: ToDoListResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ToDoLists'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'to-do-list/:id/edit',
        component: ToDoListUpdateComponent,
        resolve: {
            toDoList: ToDoListResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ToDoLists'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const toDoListPopupRoute: Routes = [
    {
        path: 'to-do-list/:id/delete',
        component: ToDoListDeletePopupComponent,
        resolve: {
            toDoList: ToDoListResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ToDoLists'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
