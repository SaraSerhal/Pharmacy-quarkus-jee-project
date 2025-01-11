package fr.pantheonsorbonne.dto;

import java.util.List;

public class UserDocumentDTO {
    private Long userId;
    private List<String> medications;

    public UserDocumentDTO(Long userId, List<String> medications) {
        this.userId = userId;
        this.medications = medications;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getMedications() {
        return medications;
    }

    public void setMedications(List<String> medications) {
        this.medications = medications;
    }
}
