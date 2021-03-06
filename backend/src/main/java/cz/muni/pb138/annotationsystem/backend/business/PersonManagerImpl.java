package cz.muni.pb138.annotationsystem.backend.business;

import cz.muni.pb138.annotationsystem.backend.api.PersonManager;
import cz.muni.pb138.annotationsystem.backend.common.BeanAlreadyExistsException;
import cz.muni.pb138.annotationsystem.backend.common.BeanNotExistsException;
import cz.muni.pb138.annotationsystem.backend.common.DaoException;
import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.dao.AnswerDao;
import cz.muni.pb138.annotationsystem.backend.dao.PersonDao;
import cz.muni.pb138.annotationsystem.backend.dao.PersonDaoImpl;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@Named
public class PersonManagerImpl implements PersonManager {

    @Inject
    private PersonDao personDao;

    @Override
    @Transactional
    public Person getOrCreatePersonByUsername(String username) throws DaoException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is null or empty");
        }

        Person person = personDao.getByUsername(username);
        if (person == null) {
            person = new Person(username);
            personDao.create(person);
        }
        return person;
    }

    @Override
    @Transactional
    public Person getPersonById(Long id) throws DaoException {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Id is null or negative");
        }

        Person p = personDao.getById(id);
        return p;
    }

    @Override
    @Transactional
    public List<Person> getAllPersons() throws DaoException {
        List<Person> all = personDao.getAll();
        return all;
    }
}
