USE recruitment;

INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Менеджер1 Менеджер Менеджер", "mngr1@mail.ru", "manager1", "manager1");
INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Менеджер2 Менеджер Менеджер", "mngr2@mail.ru", "manager2", "manager2");
INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Менеджер3 Менеджер Менеджер", "mngr3@mail.ru", "manager3", "manager3");
INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Работодатель1 Работодатель Работодатель", "emplr1@mail.ru", "emplr1", "emplr1");
INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Работодатель2 Работодатель Работодатель", "emplr2@mail.ru", "emplr2", "emplr2");
INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Работодатель3 Работодатель Работодатель", "emplr3@mail.ru", "emplr3", "emplr3");
INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Соискатель1 Соискатель Соискатель", "aplcnt1@mail.ru", "aplcnt1", "aplcnt1");
INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Соискатель2 Соискатель Соискатель", "aplcnt2@mail.ru", "aplcnt2", "aplcnt2");
INSERT INTO `recruitment`.`person` (name, email, login, password) VALUES ("Соискатель3 Соискатель Соискатель", "aplcnt3@mail.ru", "aplcnt3", "aplcnt3");

INSERT INTO `recruitment`.`manager` (person_id) VALUES (1);
INSERT INTO `recruitment`.`manager` (person_id) VALUES (2);
INSERT INTO `recruitment`.`manager` (person_id) VALUES (3);

INSERT INTO `recruitment`.`employer` (company_name, description, site, person_id) VALUES ("Company1", "The 1 best company!", "www.company1.com",  4);
INSERT INTO `recruitment`.`employer` (company_name, description, site, person_id) VALUES ("Company2", "The 2 best company!", "www.company2.com", 5);
INSERT INTO `recruitment`.`employer` (company_name, description, site, person_id) VALUES ("Company3", "The 3 best company!", "www.company3.com", 6);

INSERT INTO `recruitment`.`applicant` (person_id) VALUES (7);
INSERT INTO `recruitment`.`applicant` (person_id) VALUES (8);
INSERT INTO `recruitment`.`applicant` (person_id) VALUES (9);

INSERT INTO `recruitment`.`mark` (manager_id, evaluated_person_id, mark, comment) VALUES (1, 5, 5, "очень хорошо!");
INSERT INTO `recruitment`.`mark` (manager_id, evaluated_person_id, mark, comment) VALUES (2, 7, 3, "так себе!");
INSERT INTO `recruitment`.`mark` (manager_id, evaluated_person_id, mark, comment) VALUES (3, 6, 1, "ужасно!");
INSERT INTO `recruitment`.`mark` (manager_id, evaluated_person_id, mark) VALUES (1, 7, 4);

INSERT INTO `recruitment`.`vacancy` (employer_id, position, requirments, salary) VALUES (1, "Должность1", "Какие-то требования", 100000);
INSERT INTO `recruitment`.`vacancy` (employer_id, position, requirments, salary) VALUES (2, "Должность2", "Какие-то очень жесткие требования", 999999);
INSERT INTO `recruitment`.`vacancy` (employer_id, position, requirments, salary, status, applicant_id, closeDate) VALUES (3, "Должность3", "Нет требований", 3.99, 1, 3, "2016.02.01");

INSERT INTO `recruitment`.`interview` (applicant_id, vacancy_id, date) VALUES (1, 1, "2016.06.11");
INSERT INTO `recruitment`.`interview` (applicant_id, vacancy_id, date) VALUES (2, 2, "2016.06.07");
INSERT INTO `recruitment`.`interview` (applicant_id, vacancy_id, date, employer_result, applicant_result) VALUES (3, 3, "2016.05.13", 1, 1);

INSERT INTO `recruitment`.`resume` (applicant_id, experience, skills, education, description) VALUES (1, "10 лет опыта в конторе рога и копыта", "скилл1, скилл2, скилл3", "3 Высших образования", "Какая-то доп информация.");
INSERT INTO `recruitment`.`resume` (applicant_id, experience, skills, education, description) VALUES (2, "1 год опыта работы дворником", "скилл - подметать", "среднее дворниковое", "я очень хороший дворник");
INSERT INTO `recruitment`.`resume` (applicant_id, experience, skills, education, description, in_search, vacancy_id) VALUES (3, "опыта нет", "навыков нет", "еще в школе учусь", "просто хочу работать", false, 3);