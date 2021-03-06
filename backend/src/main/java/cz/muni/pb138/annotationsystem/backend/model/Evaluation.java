package cz.muni.pb138.annotationsystem.backend.model;

import java.util.Objects;

/**
 * @author Ondrej Velisek <ondrejvelisek@gmail.com>
 */
public class Evaluation {

    private Long id;
    
    private Person person;
    private Answer answer;
    private Rating rating;
    private int elapsedTime;

    public Evaluation() {
    }

    public Evaluation(Person person, Answer answer, Rating rating, int elapsedTime) {
        this.person = person;
        this.answer = answer;
        this.rating = rating;
        this.elapsedTime = elapsedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
    

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public String toString() {
        return "Evaluation{" + "id=" + id + ", person id=" + person.getId() + 
                ", answer id=" + answer.getId() + ", rating=" + rating + 
                ", elapsedTime=" + elapsedTime + '}';
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Evaluation evalObj = (Evaluation) o;

        return getId() != null && getId().equals(evalObj.getId());
    }
    
}
