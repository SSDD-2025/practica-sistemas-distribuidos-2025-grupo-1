package es.codeurjc.helloword_vscode.mapper;

import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.dto.MinuteDTO;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MinuteMapper {

    MinuteDTO toDTO(Minute minute);

    List<MinuteDTO> toDTOs(Collection<Minute> minutes);

    Minute toDomain(MinuteDTO minuteDTO);
}
