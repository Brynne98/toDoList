import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IToDoList } from 'app/shared/model/to-do-list.model';

type EntityResponseType = HttpResponse<IToDoList>;
type EntityArrayResponseType = HttpResponse<IToDoList[]>;

@Injectable({ providedIn: 'root' })
export class ToDoListService {
    public resourceUrl = SERVER_API_URL + 'api/to-do-lists';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/to-do-lists';

    constructor(private http: HttpClient) {}

    create(toDoList: IToDoList): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(toDoList);
        return this.http
            .post<IToDoList>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(toDoList: IToDoList): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(toDoList);
        return this.http
            .put<IToDoList>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IToDoList>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IToDoList[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IToDoList[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    protected convertDateFromClient(toDoList: IToDoList): IToDoList {
        const copy: IToDoList = Object.assign({}, toDoList, {
            dueDate: toDoList.dueDate != null && toDoList.dueDate.isValid() ? toDoList.dueDate.format(DATE_FORMAT) : null
        });
        return copy;
    }

    protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
        if (res.body) {
            res.body.dueDate = res.body.dueDate != null ? moment(res.body.dueDate) : null;
        }
        return res;
    }

    protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        if (res.body) {
            res.body.forEach((toDoList: IToDoList) => {
                toDoList.dueDate = toDoList.dueDate != null ? moment(toDoList.dueDate) : null;
            });
        }
        return res;
    }
}
