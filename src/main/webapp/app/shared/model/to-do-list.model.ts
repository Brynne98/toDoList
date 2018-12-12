import { Moment } from 'moment';

export interface IToDoList {
    id?: number;
    title?: string;
    category?: string;
    description?: string;
    dueDate?: Moment;
    status?: boolean;
}

export class ToDoList implements IToDoList {
    constructor(
        public id?: number,
        public title?: string,
        public category?: string,
        public description?: string,
        public dueDate?: Moment,
        public status?: boolean
    ) {
        this.status = this.status || false;
    }
}
