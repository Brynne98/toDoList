import { NgModule } from '@angular/core';

import { ToDoListSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [ToDoListSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [ToDoListSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class ToDoListSharedCommonModule {}
