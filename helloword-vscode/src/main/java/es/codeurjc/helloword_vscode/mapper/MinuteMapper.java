package es.codeurjc.helloword_vscode.mapper;


import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.dto.MinuteDTO;

public class MinuteMapper {

    // Convert Minute to MinuteDTO
    public static MinuteDTO toDTO(Minute minute) {
        if (minute == null) return null;
        return new MinuteDTO(
            minute.getId(),
            minute.getTitle(),
            minute.getContent(),
            minute.getDuration()
        );
    }

    // Convert MinuteDTO to Minute
    public static Minute toEntity(MinuteDTO dto) {
        if (dto == null) return null;
        Minute minute = new Minute();
        minute.setId(dto.getId());
        minute.setTitle(dto.getTitle());
        minute.setContent(dto.getContent());
        minute.setDuration(dto.getDuration());
        return minute;
    }
}
