/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ToDoListTestModule } from '../../../test.module';
import { ToDoListUpdateComponent } from 'app/entities/to-do-list/to-do-list-update.component';
import { ToDoListService } from 'app/entities/to-do-list/to-do-list.service';
import { ToDoList } from 'app/shared/model/to-do-list.model';

describe('Component Tests', () => {
    describe('ToDoList Management Update Component', () => {
        let comp: ToDoListUpdateComponent;
        let fixture: ComponentFixture<ToDoListUpdateComponent>;
        let service: ToDoListService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ToDoListTestModule],
                declarations: [ToDoListUpdateComponent]
            })
                .overrideTemplate(ToDoListUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(ToDoListUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ToDoListService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new ToDoList(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.toDoList = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new ToDoList();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.toDoList = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
