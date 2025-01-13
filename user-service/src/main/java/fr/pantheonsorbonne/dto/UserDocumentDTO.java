package fr.pantheonsorbonne.dto;

import java.util.List;

public class UserDocumentDTO {
    private Long userId;
    private List<String> medications;

    private String address;

    public UserDocumentDTO(Long userId, List<String> medications, String address) {
        this.userId = userId;
        this.medications = medications;
        this.address = address;
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

    public String getAddress(){
       return this.address;
    }
    public void setAddress(String address){
         this.address = address;
    }
}
