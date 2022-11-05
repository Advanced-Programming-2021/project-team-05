package model;

@SuppressWarnings("unused")
public class ScoreboardItem {

    private String rank;
    private String nickname;
    private String score;
    private boolean isCurrentUser;
    private boolean isOnline;


    public ScoreboardItem(String rank, String nickname, String score, boolean isCurrentUser, boolean isOnline) {
        this.setRank(rank);
        this.setNickname(nickname);
        this.setScore(score);
        this.setCurrentUser(isCurrentUser);
        this.setOnline(isOnline);
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


    public boolean isCurrentUser() {
        return this.isCurrentUser;
    }

    public void setCurrentUser(boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }


    public boolean isOnline() {
        return this.isOnline;
    }

    public void setOnline(boolean online) {
        this.isOnline = online;
    }
}
