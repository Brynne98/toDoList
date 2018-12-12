/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ToDoListTestModule } from '../../../test.module';
import { ToDoListDeleteDialogComponent } from 'app/entities/to-do-list/to-do-list-delete-dialog.component';
import { ToDoListService } from 'app/entities/to-do-list/to-do-list.service';

describe('Component Tests', () => {
    describe('ToDoList Management Delete Component', () => {
        let comp: ToDoListDeleteDialogComponent;
        let fixture: ComponentFixture<ToDoListDeleteDialogComponent>;
        let service: ToDoListService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ToDoListTestModule],
                declarations: [ToDoListDeleteDialogComponent]
            })
                .overrideTemplate(ToDoListDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ToDoListDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ToDoListService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
