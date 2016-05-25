package cz.muni.pb138.annotationsystem.backend.api;

import cz.muni.pb138.annotationsystem.backend.common.ValidationException;
import cz.muni.pb138.annotationsystem.backend.config.TestConfig;
import cz.muni.pb138.annotationsystem.backend.model.Answer;
import cz.muni.pb138.annotationsystem.backend.model.Evaluation;
import cz.muni.pb138.annotationsystem.backend.model.Pack;
import cz.muni.pb138.annotationsystem.backend.model.Person;
import cz.muni.pb138.annotationsystem.backend.model.Rating;
import cz.muni.pb138.annotationsystem.backend.model.Subpack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EvaluationManagerTest {

    @Inject
    private DataSource dataSource;

    @Inject
    private EvaluationManager evaluationManager;

    @Inject
    private AnswerManager answerManager;

    @Inject
    private PackManager packManager;

    @Inject
    private PersonManager personManager;

    @Inject
    private SubpackManager subpackManager;

    @Before
    public void setUp() throws Exception {
        Resource create = new ClassPathResource("createTables.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), create);
    }

    @After
    public void tearDown() throws Exception {
        Resource drop = new ClassPathResource("dropTables.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), drop);
    }



    @Test
    public void eval() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);

        assertNotNull(eval.getId());
        assertEquals(eval, evaluationManager.getEvaluationById(eval.getId()));
        assertTrue(evaluationManager.getEvaluationsOfPerson(persons[1]).contains(eval));
    }
    @Test
    public void evalCorrect() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);

        Evaluation correction = new Evaluation(persons[1], answerManager.getAnswerById(answer.getId()), Rating.NEGATIVE, 20);

        evaluationManager.eval(correction);

        assertNotNull(correction.getId());
        assertEquals(eval.getId(), correction.getId());

        Evaluation result = evaluationManager.getEvaluationById(eval.getId());

        assertEquals(eval.getId(), result.getId());
        assertEquals(Rating.NEGATIVE, result.getRating());
        assertEquals(correction.getElapsedTime(), result.getElapsedTime());
        assertTrue(evaluationManager.getEvaluationsOfPerson(persons[1]).contains(correction));
        assertTrue(evaluationManager.getEvaluationsOfPerson(persons[1]).contains(result));
    }
    @Test(expected = IllegalStateException.class)
    public void evalNotAssigned() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        subpackManager.updatePersonsAssignment(persons[1], new ArrayList<Subpack>());

        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);
        evaluationManager.eval(eval);
    }
    @Test(expected = IllegalArgumentException.class)
    public void evalNullEval() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        evaluationManager.eval(null);
    }
    @Test(expected = ValidationException.class)
    public void evalNullAnswer() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);

        Evaluation eval = new Evaluation(persons[1], null, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);
    }
    @Test(expected = ValidationException.class)
    public void evalNullPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));

        Evaluation eval = new Evaluation(null, answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);
    }
    @Test(expected = ValidationException.class)
    public void evalNonExistsAnswer() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        answer.setId((long) 99);
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);
    }
    @Test(expected = ValidationException.class)
    public void evalNonExistsPerson() throws Exception {
        Pack[] packs = TestUtils.createPacks(packManager);
        List<Subpack> subpacks = subpackManager.getSubpacksInPack(packs[1]);
        Person[] persons = TestUtils.createPersons(personManager);
        subpackManager.updatePersonsAssignment(persons[1], subpacks);
        Answer answer = answerManager.nextAnswer(persons[1], subpacks.get(1));
        persons[1].setId((long) 99);
        Evaluation eval = new Evaluation(persons[1], answer, Rating.POSITIVE, 10);

        evaluationManager.eval(eval);
    }



    @Test
    public void getEvaluationById() throws Exception {
        fail("TODO");
    }



    @Test
    public void getEvaluationsOfPerson() throws Exception {
        fail("TODO");
    }

}