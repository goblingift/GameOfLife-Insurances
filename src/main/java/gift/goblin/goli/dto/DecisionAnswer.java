/*
 * Copyright (C) 2021 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.goli.dto;

/**
 * This class contains all fields to hold the response of an user to a specific question.
 * @author andre
 */
public class DecisionAnswer {
    
    private int level;
    private int answer;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "DecisionAnswer{" + "level=" + level + ", answer=" + answer + '}';
    }
    
}
