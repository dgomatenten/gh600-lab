package com.gh600.study.question;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * JPA Entity representing one choice (A, B, C, D...) for a Question.
 *
 * In a bidirectional @OneToMany / @ManyToOne relationship, the "many" side
 * (AnswerOption) is always the OWNING side because its table ("answer_option")
 * physically holds the foreign key column ("question_id").
 */
@Entity
public class AnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false, length = 1000)
    private String explanation;

    /**
     * Many AnswerOptions belong to one Question.
     *
     * IMPORTANT: @ManyToOne defaults to FetchType.EAGER in the JPA spec!
     * We explicitly set fetch = FetchType.LAZY so loading an AnswerOption does not
     * automatically trigger an extra query for Question unless we ask for it.
     *
     * @JoinColumn(name = "question_id") names the foreign key column in answer_option.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /**
     * Required by JPA.
     */
    protected AnswerOption() {
    }

    public AnswerOption(String text, boolean correct, String explanation) {
        this.text = text;
        this.correct = correct;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Question getQuestion() {
        return question;
    }

    void setQuestion(Question question) {
        this.question = question;
    }
}

