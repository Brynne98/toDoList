import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ToDoListSharedModule } from 'app/shared';
import {
    ToDoListComponent,
    ToDoListDetailComponent,
    ToDoListUpdateComponent,
    ToDoListDeletePopupComponent,
    ToDoListDeleteDialogComponent,
    toDoListRoute,
    toDoListPopupRoute
} from './';

const ENTITY_STATES = [...toDoListRoute, ...toDoListPopupRoute];

@NgModule({
    imports: [ToDoListSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        ToDoListComponent,
        ToDoListDetailComponent,
        ToDoListUpdateComponent,
        ToDoListDeleteDialogComponent,
        ToDoListDeletePopupComponent
    ],
    entryComponents: [ToDoListComponent, ToDoListUpdateComponent, ToDoListDeleteDialogComponent, ToDoListDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ToDoListToDoListModule {}
