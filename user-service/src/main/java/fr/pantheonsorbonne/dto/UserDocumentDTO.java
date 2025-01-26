package fr.pantheonsorbonne.dto;

import java.util.List;

public record UserDocumentDTO (Long userId,List<String> medications,String address) {
}
