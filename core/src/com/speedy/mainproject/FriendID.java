package com.speedy.mainproject;

/**
 * Created by test on 4/14/2018.
 */
public class FriendID {
    private String score;
    private String name;
    private String id;

    public FriendID(String m_name, String m_id, String m_score){
        score = m_score;
        name = m_name;
        id = m_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
