package es.codeurjc.helloword_vscode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.repository.MinuteRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import es.codeurjc.helloword_vscode.entities.Minute;

@Service
public class MinuteService {
    @Autowired
	private MinuteRepository minuteRepository;

    public List<Minute> findAll() {
		return minuteRepository.findAll();
	}

	public void save (Minute minute) throws IOException{
		minuteRepository.save(minute);
	}

}