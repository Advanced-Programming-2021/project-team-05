package model;

public class ScoreboardItem {

    private String rank;
    private String nickname;
    private String score;


    public ScoreboardItem(String rank, String nickname, String score) {
        this.setRank(rank);
        this.setNickname(nickname);
        this.setScore(score);
    }


    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }


    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
