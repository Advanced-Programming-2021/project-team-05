package model;

import java.util.UUID;

@SuppressWarnings("unused")
public class Message {

    private final String id;
    private String nickname;
    private String profilePicName;
    private String content;


    public Message(User user, String content) {
        this.id = UUID.randomUUID().toString();
        this.setNickname(user.getNickname());
        this.setProfilePicName(user.getProfilePictureName());
        this.setContent(content);
    }


    public String getId() {
        return id;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getProfilePicName() {
        return profilePicName;
    }

    public void setProfilePicName(String profilePicName) {
        this.profilePicName = profilePicName;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
