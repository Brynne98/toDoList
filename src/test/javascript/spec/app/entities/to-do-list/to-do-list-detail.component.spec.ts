/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ToDoListTestModule } from '../../../test.module';
import { ToDoListDetailComponent } from 'app/entities/to-do-list/to-do-list-detail.component';
import { ToDoList } from 'app/shared/model/to-do-list.model';

describe('Component Tests', () => {
    describe('ToDoList Management Detail Component', () => {
        let comp: ToDoListDetailComponent;
        let fixture: ComponentFixture<ToDoListDetailComponent>;
        const route = ({ data: of({ toDoList: new ToDoList(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ToDoListTestModule],
                declarations: [ToDoListDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(ToDoListDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ToDoListDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.toDoList).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
