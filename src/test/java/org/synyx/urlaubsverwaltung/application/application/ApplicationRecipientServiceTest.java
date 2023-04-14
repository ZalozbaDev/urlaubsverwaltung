package org.synyx.urlaubsverwaltung.application.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.synyx.urlaubsverwaltung.application.vacationtype.VacationTypeEntity;
import org.synyx.urlaubsverwaltung.department.Department;
import org.synyx.urlaubsverwaltung.department.DepartmentService;
import org.synyx.urlaubsverwaltung.person.Person;
import org.synyx.urlaubsverwaltung.person.PersonService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.synyx.urlaubsverwaltung.TestDataCreator.createApplication;
import static org.synyx.urlaubsverwaltung.TestDataCreator.createPerson;
import static org.synyx.urlaubsverwaltung.TestDataCreator.createVacationTypeEntity;
import static org.synyx.urlaubsverwaltung.application.vacationtype.VacationCategory.HOLIDAY;
import static org.synyx.urlaubsverwaltung.person.MailNotification.NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_ALL;
import static org.synyx.urlaubsverwaltung.person.MailNotification.NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_DEPARTMENT;
import static org.synyx.urlaubsverwaltung.person.Role.BOSS;
import static org.synyx.urlaubsverwaltung.person.Role.DEPARTMENT_HEAD;
import static org.synyx.urlaubsverwaltung.person.Role.OFFICE;
import static org.synyx.urlaubsverwaltung.person.Role.SECOND_STAGE_AUTHORITY;
import static org.synyx.urlaubsverwaltung.person.Role.USER;

@ExtendWith(MockitoExtension.class)
class ApplicationRecipientServiceTest {

    private ApplicationRecipientService sut;

    @Mock
    private PersonService personService;
    @Mock
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        sut = new ApplicationRecipientService(personService, departmentService);
    }

    @Test
    void getRecipientsOfInterestWithDepartments() {

        when(departmentService.getNumberOfDepartments()).thenReturn(1L);

        final Department department = new Department();
        department.setId(1);

        // given user application
        final Person normalUser = createPerson("normalUser", USER);
        normalUser.setId(1);
        final Application application = getHolidayApplication(normalUser);
        when(departmentService.getAssignedDepartmentsOfMember(normalUser)).thenReturn(List.of(department));

        // given boss all
        final Person bossAll = createPerson("boss", BOSS);
        bossAll.setId(2);
        bossAll.setNotifications(List.of(NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_ALL));
        when(personService.getActivePersonsByRole(BOSS)).thenReturn(List.of(bossAll));

        // given office all
        final Person officeAll = createPerson("office", OFFICE);
        officeAll.setId(2);
        officeAll.setNotifications(List.of(NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_ALL));
        when(personService.getActivePersonsByRole(OFFICE)).thenReturn(List.of(officeAll));

        // given boss department
        final Person bossDepartment = createPerson("boss", BOSS);
        bossDepartment.setId(3);
        bossDepartment.setNotifications(List.of(NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_DEPARTMENT));
        when(personService.getActivePersonsByRole(BOSS)).thenReturn(List.of(bossDepartment));
        when(departmentService.getAssignedDepartmentsOfMember(bossDepartment)).thenReturn(List.of(department));

        // given department head
        final Person departmentHead = createPerson("departmentHead", DEPARTMENT_HEAD);
        departmentHead.setId(4);
        departmentHead.setNotifications(List.of(NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_DEPARTMENT));
        when(personService.getActivePersonsByRole(DEPARTMENT_HEAD)).thenReturn(List.of(departmentHead));
        when(departmentService.isDepartmentHeadAllowedToManagePerson(departmentHead, normalUser)).thenReturn(true);

        // given second stage
        final Person secondStage = createPerson("secondStage", SECOND_STAGE_AUTHORITY);
        secondStage.setId(5);
        secondStage.setNotifications(List.of(NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_DEPARTMENT));
        when(personService.getActivePersonsByRole(SECOND_STAGE_AUTHORITY)).thenReturn(List.of(secondStage));
        when(departmentService.isSecondStageAuthorityAllowedToManagePerson(secondStage, normalUser)).thenReturn(true);

        final List<Person> recipientsForAllowAndRemind = sut.getRecipientsOfInterest(application.getPerson());
        assertThat(recipientsForAllowAndRemind).containsOnly(officeAll, bossAll, bossDepartment, departmentHead, secondStage);
    }

    @Test
    void getRecipientsOfInterestWithOutDepartments() {

        when(departmentService.getNumberOfDepartments()).thenReturn(0L);

        // given user application
        final Person normalUser = createPerson("normalUser", USER);
        normalUser.setId(1);
        final Application application = getHolidayApplication(normalUser);

        // given office all
        when(personService.getActivePersonsByRole(OFFICE)).thenReturn(List.of());

        // given boss all
        final Person bossAll = createPerson("boss", BOSS);
        bossAll.setId(2);
        bossAll.setNotifications(List.of(NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_ALL));
        when(personService.getActivePersonsByRole(BOSS)).thenReturn(List.of(bossAll));

        final List<Person> recipientsForAllowAndRemind = sut.getRecipientsOfInterest(application.getPerson());
        assertThat(recipientsForAllowAndRemind).containsOnly(bossAll);
    }

    @Test
    void getRecipientsOfInterestWithDepartmentsAndDistinctRecipients() {

        when(departmentService.getNumberOfDepartments()).thenReturn(1L);

        final Department department = new Department();
        department.setId(1);

        // given user application
        final Person normalUser = createPerson("normalUser", USER);
        normalUser.setId(1);
        final Application application = getHolidayApplication(normalUser);
        when(departmentService.getAssignedDepartmentsOfMember(normalUser)).thenReturn(List.of(department));

        // given boss all
        final Person bossAll = createPerson("boss", BOSS);
        bossAll.setId(2);
        bossAll.setNotifications(List.of(NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_ALL, NOTIFICATION_EMAIL_APPLICATION_MANAGEMENT_DEPARTMENT));
        when(personService.getActivePersonsByRole(BOSS)).thenReturn(List.of(bossAll));

        // given office all
        when(personService.getActivePersonsByRole(OFFICE)).thenReturn(List.of());

        // given boss department
        when(personService.getActivePersonsByRole(BOSS)).thenReturn(List.of(bossAll));
        when(departmentService.getAssignedDepartmentsOfMember(bossAll)).thenReturn(List.of(department));

        final List<Person> recipientsForAllowAndRemind = sut.getRecipientsOfInterest(application.getPerson());
        assertThat(recipientsForAllowAndRemind).containsOnly(bossAll);
    }

    private Application getHolidayApplication(Person normalUser) {
        final VacationTypeEntity vacationType = createVacationTypeEntity(HOLIDAY, "application.data.vacationType.holiday");
        return createApplication(normalUser, vacationType);
    }
}
