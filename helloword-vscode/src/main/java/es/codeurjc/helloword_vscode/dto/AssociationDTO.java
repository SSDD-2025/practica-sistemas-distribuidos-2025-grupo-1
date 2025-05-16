package es.codeurjc.helloword_vscode.dto;

import java.util.List;

public class AssociationDTO {
    
    private long id;
    private String name;
    private boolean hasImage;
    private List<Long> memberIds;
    private List<Long> minuteIds;

    // Constructors
    public AssociationDTO() {}

    public AssociationDTO(long id, String name, boolean hasImage, List<Long> memberIds, List<Long> minuteIds) {
        this.id = id;
        this.name = name;
        this.hasImage = hasImage;
        this.memberIds = memberIds;
        this.minuteIds = minuteIds;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }

    public List<Long> getMinuteIds() {
        return minuteIds;
    }

    public void setMinuteIds(List<Long> minuteIds) {
        this.minuteIds = minuteIds;
    }
}
