import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { ToDoListToDoListModule } from './to-do-list/to-do-list.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        ToDoListToDoListModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ToDoListEntityModule {}
