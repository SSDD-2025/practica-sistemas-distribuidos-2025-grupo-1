package es.codeurjc.helloword_vscode.dto;

import java.util.List;

public class MinuteDTO {

    private long id;
    private String date;
    private List<Long> participantIds;
    private String content;
    private double duration;
    private long associationId;

    // Constructors
    public MinuteDTO() {}

    public MinuteDTO(long id, String date, List<Long> participantIds, String content, double duration, long associationId) {
        this.id = id;
        this.date = date;
        this.participantIds = participantIds;
        this.content = content;
        this.duration = duration;
        this.associationId = associationId;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(long associationId) {
        this.associationId = associationId;
    }
}
