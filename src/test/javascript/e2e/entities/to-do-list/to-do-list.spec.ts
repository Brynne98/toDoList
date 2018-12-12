/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { ToDoListComponentsPage, ToDoListDeleteDialog, ToDoListUpdatePage } from './to-do-list.page-object';

const expect = chai.expect;

describe('ToDoList e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let toDoListUpdatePage: ToDoListUpdatePage;
    let toDoListComponentsPage: ToDoListComponentsPage;
    let toDoListDeleteDialog: ToDoListDeleteDialog;

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load ToDoLists', async () => {
        await navBarPage.goToEntity('to-do-list');
        toDoListComponentsPage = new ToDoListComponentsPage();
        expect(await toDoListComponentsPage.getTitle()).to.eq('To Do Lists');
    });

    it('should load create ToDoList page', async () => {
        await toDoListComponentsPage.clickOnCreateButton();
        toDoListUpdatePage = new ToDoListUpdatePage();
        expect(await toDoListUpdatePage.getPageTitle()).to.eq('Create or edit a To Do List');
        await toDoListUpdatePage.cancel();
    });

    it('should create and save ToDoLists', async () => {
        const nbButtonsBeforeCreate = await toDoListComponentsPage.countDeleteButtons();

        await toDoListComponentsPage.clickOnCreateButton();
        await promise.all([
            toDoListUpdatePage.setTitleInput('title'),
            toDoListUpdatePage.setCategoryInput('category'),
            toDoListUpdatePage.setDescriptionInput('description'),
            toDoListUpdatePage.setDueDateInput('2000-12-31')
        ]);
        expect(await toDoListUpdatePage.getTitleInput()).to.eq('title');
        expect(await toDoListUpdatePage.getCategoryInput()).to.eq('category');
        expect(await toDoListUpdatePage.getDescriptionInput()).to.eq('description');
        expect(await toDoListUpdatePage.getDueDateInput()).to.eq('2000-12-31');
        const selectedStatus = toDoListUpdatePage.getStatusInput();
        if (await selectedStatus.isSelected()) {
            await toDoListUpdatePage.getStatusInput().click();
            expect(await toDoListUpdatePage.getStatusInput().isSelected()).to.be.false;
        } else {
            await toDoListUpdatePage.getStatusInput().click();
            expect(await toDoListUpdatePage.getStatusInput().isSelected()).to.be.true;
        }
        await toDoListUpdatePage.save();
        expect(await toDoListUpdatePage.getSaveButton().isPresent()).to.be.false;

        expect(await toDoListComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });

    it('should delete last ToDoList', async () => {
        const nbButtonsBeforeDelete = await toDoListComponentsPage.countDeleteButtons();
        await toDoListComponentsPage.clickOnLastDeleteButton();

        toDoListDeleteDialog = new ToDoListDeleteDialog();
        expect(await toDoListDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this To Do List?');
        await toDoListDeleteDialog.clickOnConfirmButton();

        expect(await toDoListComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
